package com.example.service.impl;

import com.example.dao.ProductMapper;
import com.example.enums.ProductStatusEnum;
import com.example.enums.ResponseEnum;
import com.example.form.CartAddForm;
import com.example.form.CartUpdateForm;
import com.example.pojo.Cart;
import com.example.pojo.Product;
import com.example.service.ICartService;
import com.example.vo.CartProductVo;
import com.example.vo.CartVo;
import com.example.vo.ResponseVo;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CartServiceImpl implements ICartService {
    private final static String CART_REDIS_KEY_TEMPLATE = "cart_%d";

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private Gson gson = new Gson();


    @Override
    public ResponseVo<CartVo> add(Integer uid, CartAddForm form) {
        Integer quantity = 1;
        Product product = productMapper.selectByPrimaryKey(form.getProductId());
        // 商品是否存在


        if (product == null) {
            return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST);
        }

        // 商品是否正常在售
        if (!product.getStatus().equals(ProductStatusEnum.ON_SALE.getCode())) {
            return ResponseVo.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE);
        }

        // 商品库存是否充足

        if(product.getStock() <= 0) {
            return ResponseVo.error(ResponseEnum.PRODUCT_STOCK_ERROR);
        }

        // 写到redis
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Cart cart;
        String value = opsForHash.get(redisKey, String.valueOf(product.getId()));

        if (!StringUtils.hasText(value)) {
            // 没有该商品，新增
            cart = new Cart(product.getId(), quantity, form.getSelected());
        } else {
            // 已经有了，数量+1
            cart = gson.fromJson(value, Cart.class);
            log.info("cart = {}", cart);
            cart.setQuantity(cart.getQuantity() + quantity);
        }
        opsForHash.put(redisKey, String.valueOf(product.getId()), gson.toJson(cart));
        return list(uid);
        // return ResponseVo.success();
    }

    @Override
    public ResponseVo<CartVo> list(Integer uid) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Map<String, String> entries = opsForHash.entries(redisKey);
        boolean selectAll = true;
        Integer cartTotalQuantity = 0;
        BigDecimal cartTotalPrice = BigDecimal.ZERO;
        CartVo cartVo = new CartVo();
        List<CartProductVo> cartProductVoList = new ArrayList<>();
        for(Map.Entry<String, String> entry: entries.entrySet()) {
            Integer productId = Integer.valueOf(entry.getKey());
            Cart cart = gson.fromJson(entry.getValue(), Cart.class);

            Product product = productMapper.selectByPrimaryKey(productId);
            if(product != null) {
                CartProductVo cartProductVo = new CartProductVo(productId,
                        cart.getQuantity(),
                        product.getName(),
                        product.getSubTitle(),
                        product.getMainImage(),
                        product.getPrice(),
                        product.getStatus(),
                        product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())),
                        product.getStock(),
                        cart.getProductSelected()
                        );
                cartProductVoList.add(cartProductVo);
                if(!cart.getProductSelected()) {
                    selectAll = false;
                }

                // 计算总价
                if (cart.getProductSelected()) {
                    cartTotalPrice = cartTotalPrice.add(cartProductVo.getProductTotalPrice());
                }

            }

            cartTotalQuantity += cart.getQuantity();
        }
        cartVo.setSelectedAll(selectAll);
        cartVo.setCartTotalQuantity(cartTotalQuantity);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProdoctVoList(cartProductVoList);
        return ResponseVo.success(cartVo);

    }

    @Override
    public ResponseVo<CartVo> update(Integer uid, Integer productId, CartUpdateForm form) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        String value = opsForHash.get(redisKey, String.valueOf(productId));
        log.info("value = {}", StringUtils.hasText(value));
        if (!StringUtils.hasText(value)) {
            // 没有该商品，报错
            return ResponseVo.error(ResponseEnum.CART_PRODUCT_NOT_EXIST);
        }

        // 已经有了，修改内容
        Cart cart = gson.fromJson(value, Cart.class);
        if (form.getQuantity() != null && form.getQuantity() >= 0) {
            cart.setQuantity(form.getQuantity());
        }
        if (form.getSelected() != null) {
            cart.setProductSelected(form.getSelected());
        }

        opsForHash.put(redisKey, String.valueOf(productId), gson.toJson(cart));
        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> delete(Integer uid, Integer productId) {
        // 读取product信息
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        opsForHash.delete(redisKey, String.valueOf(productId));
        return list(uid);
    }

    public ResponseVo<CartVo> select(Integer uid, Boolean productSelected) {
        // 读取product信息
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Map<String, String> entries = opsForHash.entries(redisKey);

        for(Map.Entry<String, String> entry: entries.entrySet()) {
           Cart cart = gson.fromJson(entry.getValue(),Cart.class);
           cart.setProductSelected(productSelected);
           opsForHash.put(redisKey, String.valueOf(cart.getProductId()), gson.toJson(cart));
        }
        return list(uid);
    }

    @Override
    public Integer sum(Integer uid) {
        // 读取product信息
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Map<String, String> entries = opsForHash.entries(redisKey);
        Integer sum = 0;
        for(Map.Entry<String, String> entry: entries.entrySet()) {
            Cart cart = gson.fromJson(entry.getValue(),Cart.class);
            sum += cart.getQuantity();
        }
        return sum;
    }

    @Override
    public List<Cart> listForCart(Integer uid) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Map<String, String> entries = opsForHash.entries(redisKey);
        List<Cart> cartList = new ArrayList<>();
        for(Map.Entry<String, String> entry: entries.entrySet()) {
            cartList.add(gson.fromJson(entry.getValue(), Cart.class));
        }
        return cartList;
    }
}
