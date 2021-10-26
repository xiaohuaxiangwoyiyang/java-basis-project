package com.example.service.impl;

import com.example.dao.OrderItemMapper;
import com.example.dao.OrderMapper;
import com.example.dao.ProductMapper;
import com.example.dao.ShippingMapper;
import com.example.enums.OrderStatusEnum;
import com.example.enums.PaymentTypeEnum;
import com.example.enums.ProductStatusEnum;
import com.example.enums.ResponseEnum;
import com.example.pojo.*;
import com.example.service.ICartService;
import com.example.service.IOrderService;
import com.example.service.IShippingService;
import com.example.vo.CartVo;
import com.example.vo.OrderItemVo;
import com.example.vo.OrderVo;
import com.example.vo.ResponseVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.sun.org.apache.xpath.internal.operations.Or;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ICartService cartService;

    @Override
    @Transactional
    public ResponseVo create(Integer uid, Integer shippingId) {

        // 1. 查询收获地址， 并校验
        Shipping shipping = shippingMapper.selectByPrimaryKey(shippingId);
        if (shipping == null) {
            return ResponseVo.error(ResponseEnum.SHIPPING_NOT_EXIST);
        }
        // 2. 查询购物车,并校验

        List<Cart> cartList = cartService.listForCart(uid).stream()
                .filter(Cart::getProductSelected)
                .collect(Collectors.toList());

        if(CollectionUtils.isEmpty(cartList)) {
            return ResponseVo.error(ResponseEnum.CART_SELECTED_IS_EMPTY);
        }

        //获取cartList里的productIds
        Set<Integer> productIdSet = cartList.stream()
                .map(Cart::getProductId)
                .collect(Collectors.toSet());
        List<Product> productList = productMapper.selectByProductIdSet(productIdSet);

        Map<Integer, Product> map = productList.stream()
                .collect(Collectors.toMap(Product::getId, product -> product));
        // 通过购物车中的商品id, 查找商品详情
        List<OrderItem> orderItemList = new ArrayList<>();
        Long orderNo = generateOrderNo();

        for(Cart cart: cartList) {
            //根据productId查数据库
            Product product = map.get(cart.getProductId());

            //构建orderItem，并计算每个Item的总价
            OrderItem orderItem = buildOrderItem(uid, orderNo, cart.getQuantity(), product);
            orderItemList.add(orderItem);

            // 校验商品
            // 1. 是否有商品
            if (product == null) {
                return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST,
                        "商品不存在， productId =" + cart.getProductId());
            }
            // 2. 商品上下架状态
            if (!ProductStatusEnum.ON_SALE.getCode().equals(product.getStatus())) {
                return ResponseVo.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE,
                        "商品不是在售状态." + product.getName());
            }
            // 3. 库存是否充足
            if (product.getStock() <= cart.getQuantity()) {
                return ResponseVo.error(ResponseEnum.PRODUCT_STOCK_ERROR,
                            "库存不正确." + product.getName());
            }

            // 减库存
            product.setStock(product.getStock() - cart.getQuantity());
            int row = productMapper.updateByPrimaryKeySelective(product);
            if (row <= 0) {
                return ResponseVo.error(ResponseEnum.ERROR);
            }
        }

        log.info("orderItemList = {}", orderItemList);
        // 计算总价，并生成订单，入库
        Order order = buildOrder(uid, orderNo, shippingId, orderItemList);


        // 4. 写入数据库
        // 入库订单
        int rowForOrder = orderMapper.insertSelective(order);
        if (rowForOrder <= 0) {
            return ResponseVo.error(ResponseEnum.ERROR);
        }

        // 入库订单item
        int rowForOrderItem = orderItemMapper.batchInsert(orderItemList);
        if (rowForOrderItem <= 0) {
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        // 5. 更新购物车
        // Redis有事务(打包命令)，不能回滚
        for(Cart cart: cartList) {
            cartService.delete(uid, cart.getProductId());
        }

        // 构造orderVo
        OrderVo orderVo = buildOrderVo(order, orderItemList, shipping);

        return ResponseVo.success(orderVo);
    }

    @Override
    public ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        // 1. 查找该用户下所有订单
        List<Order> orderList = orderMapper.selectOrderByUserId(uid);
        // 2. 根据订单列表找出所有订单id集合
        Set<Long> orderNoSets = orderList.stream()
                .map(Order::getOrderNo)
                .collect(Collectors.toSet());
        // 3. 找到符合订单id的item
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoSet(orderNoSets);
        // 4. 将orderItemList转为map
        Map<Long, List<OrderItem>> orderItemMap = orderItemList.stream()
                                .collect(Collectors.groupingBy(OrderItem::getOrderNo));
        log.info("orderItemList = {}", orderItemMap);
        // 5. 根据order中的shippingId查找shipping信息
        Set<Integer> shippingIdSet = orderList.stream()
                        .map(Order::getShippingId)
                        .collect(Collectors.toSet());
        log.info("shippingIdSet = {}", shippingIdSet);
        List<Shipping> shippingList = shippingMapper.selectByIdSet(shippingIdSet);

        Map<Integer, Shipping> shippingMap= shippingList.stream()
                        .collect(Collectors.toMap(Shipping::getId, shipping -> shipping));

        log.info("orderItemList = {}", shippingMap);
        List<OrderVo> orderVoList = new ArrayList<>();

        for(Order order: orderList) {

            OrderVo orderVo = buildOrderVo(order,
                    orderItemMap.get(order.getOrderNo()),
                    shippingMap.get(order.getShippingId()));
            orderVoList.add(orderVo);
        }

        PageInfo pageInfo = new PageInfo<>(orderList);
        pageInfo.setList(orderVoList);

        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<OrderVo> detail(Integer uid, Long orderNo) {
        Order order = orderMapper.selectOrderByOrderNo(orderNo);
        log.info("order = {}", order);
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(orderNo);
        log.info("orderItem", orderItemList);
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        log.info("shipping = {}", shipping);
        OrderVo orderVo = buildOrderVo(order, orderItemList, shipping);
        log.info("orderVo = {}", orderVo);
        return ResponseVo.success(orderVo);
    }

//    @Override  错误，此为删除订单的逻辑
//    public ResponseVo cancel(Integer uid, Long orderNo) {
//
//        // 3. 删除orderItem及order
//
//        int rowForOrderItem = orderItemMapper.deleteByOrderNo(orderNo);
//        if (rowForOrderItem <= 0) {
//            return ResponseVo.error(ResponseEnum.ERROR);
//        }
//
//        int rowForOrder = orderMapper.deleteByOrderNo(orderNo);
//        if (rowForOrder <= 0) {
//            return ResponseVo.error(ResponseEnum.ERROR);
//        }
//
//        // 4. 返回结果
//        return ResponseVo.success(orderNo + "订单取消成功");
//    }

    @Override
    public ResponseVo cancel(Integer uid, Long orderNo) {
        Order order = orderMapper.selectOrderByOrderNo(orderNo);
        if (order == null && !order.getUserId().equals(uid)) {
            return ResponseVo.error(ResponseEnum.ORDER_NOT_EXIST);
        }
        // 只有[未付款]订单可以取消，看自己公司业务
        if (!order.getStatus().equals(OrderStatusEnum.NO_PAY.getCode())) {
            return  ResponseVo.error(ResponseEnum.ORDER_STATUS_ERROR);
        }

        order.setStatus(OrderStatusEnum.TRADE_CLOSE.getCode());
        order.setCloseTime(new Date());
        int row = orderMapper.updateByPrimaryKeySelective(order);
        if (row <= 0) {
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        return ResponseVo.success();
    }

    @Override
    public void paid(Long orderNo) {
        Order order = orderMapper.selectOrderByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException(ResponseEnum.ORDER_NOT_EXIST.getDesc() + "订单id: " + orderNo);
        }
        if (!order.getPaymentType().equals(OrderStatusEnum.NO_PAY.getCode())) {
            throw new RuntimeException(ResponseEnum.ORDER_STATUS_ERROR.getDesc() + "订单id: " + orderNo);
        }

        order.setStatus(OrderStatusEnum.PAID.getCode());
        order.setPaymentTime(new Date());
        int row = orderMapper.updateByPrimaryKeySelective(order);
        if (row <= 0) {
            throw new RuntimeException("将订单更新为已支付状态失败，订单id:" + orderNo);
        }
    }

    private Long generateOrderNo() {
        return System.currentTimeMillis() + new Random().nextInt(999);
    }

    private OrderItem buildOrderItem(Integer uid, Long orderNo, Integer quantity, Product product) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderNo(orderNo);
        orderItem.setUserId(uid);
        orderItem.setQuantity(quantity);
        orderItem.setProductId(product.getId());
        orderItem.setProductName(product.getName());
        orderItem.setProductImage(product.getMainImage());
        orderItem.setCurrentUnitPrice(product.getPrice());
        orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        return orderItem;
    }

    private Order buildOrder(Integer uid,
                             Long orderNo,
                             Integer shippingId,
                             List<OrderItem> orderItemList
                             ) {
        BigDecimal payment = orderItemList.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Order order = new Order();
        order.setUserId(uid);
        order.setOrderNo(orderNo);
        order.setPayment(payment);
        order.setShippingId(shippingId);
        order.setPaymentType(PaymentTypeEnum.PAY_ONLINE.getCode());
        order.setPostage(0);
        order.setStatus(OrderStatusEnum.NO_PAY.getCode());
        return order;
    }

    private OrderVo buildOrderVo(Order order, List<OrderItem> orderItemList, Shipping shipping) {
        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(order, orderVo);
        List<OrderItemVo> orderItemVoList = orderItemList.stream()
                .map(e -> {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    BeanUtils.copyProperties(e, orderItemVo);
                    return orderItemVo;
                })
                .collect(Collectors.toList());
        orderVo.setOrderItemVoList(orderItemVoList);
        if (shipping != null) {
            orderVo.setShippingId(shipping.getId());
            orderVo.setShippingVo(shipping);
        }
        return orderVo;
    }
}
