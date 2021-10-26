package com.example.service.impl;

import com.example.ShoppingApplicationTests;
import com.example.form.CartAddForm;
import com.example.service.ICartService;
import com.example.service.IOrderService;
import com.example.vo.CartVo;
import com.example.vo.OrderVo;
import com.example.vo.ResponseVo;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class OrderServiceImplTest extends ShoppingApplicationTests {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private ICartService cartService;

    private Integer uid = 1;
    private Integer shippingId = 3;
    private Integer productId = 26;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Before
    public void before() {
        CartAddForm form = new CartAddForm();
        form.setProductId(productId);
        form.setSelected(true);
        ResponseVo<CartVo> responseVo = cartService.add(uid, form);
    }

    @Test
    private ResponseVo<OrderVo> create() {
        ResponseVo<OrderVo> responseVo = orderService.create(uid, shippingId);
        log.info("result={}", gson.toJson(responseVo));
        return responseVo;
    }

    @Test
    public void list() {
        ResponseVo<PageInfo> responseVo = orderService.list(uid, 1, 10);
        log.info("responseVo = {}", responseVo);
    }

    @Test
    public void detail() {
        ResponseVo<OrderVo> vo = create();
        ResponseVo<OrderVo> orderVoResponseVo = orderService.detail(uid, vo.getData().getOrderNo());
        log.info("orderVoResponseVo = {}", orderVoResponseVo);
    }

    @Test
    public void cancel() {
        ResponseVo<OrderVo> vo = create();
        ResponseVo responseVo = orderService.cancel(uid, vo.getData().getOrderNo());
        log.info("responseVo = {}", responseVo);
    }
}
