package com.example.service.impl;


import com.example.ShoppingApplicationTests;
import com.example.form.CartAddForm;
import com.example.form.CartUpdateForm;
import com.example.service.ICartService;
import com.example.vo.CartProductVo;
import com.example.vo.CartVo;
import com.example.vo.ResponseVo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sun.jvm.hotspot.utilities.Assert;

@Slf4j
public class CartServiceImplTest extends ShoppingApplicationTests {

    @Autowired
    private ICartService cartService;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private Integer uid = 1;
    private Integer productId = 26;

    @Test
    public void add() {
        CartAddForm cartAddForm = new CartAddForm();
        cartAddForm.setProductId(27);
        cartAddForm.setSelected(true);
        cartService.add(1, cartAddForm);
    }

    @Test
    public void list() {
        ResponseVo<CartVo> responseVo = cartService.list(uid);
        log.info("list={}", gson.toJson(responseVo));
    }

    @Test
    public void update() {
        CartUpdateForm form = new CartUpdateForm();
        form.setQuantity(5);
        form.setSelected(false);
        ResponseVo<CartVo> responseVo = cartService.update(uid, productId, form);
        log.info("result = {}", gson.toJson(responseVo));

    }

    @Test
    public void delete() {
        ResponseVo<CartVo> responseVo = cartService.delete(uid, productId);
        log.info("result = {}", gson.toJson(responseVo));
    }

    @Test
    public void selectAll() {
        ResponseVo<CartVo> responseVo = cartService.select(uid, true);
        log.info("result = {}", gson.toJson(responseVo));
    }

    @Test
    public void unselSctAll() {
        ResponseVo<CartVo> responseVo = cartService.select(uid, false);
        log.info("result = {}", gson.toJson(responseVo));
    }

    @Test
    public void sum() {
        Integer sum = cartService.sum(uid);
        log.info("result = {}", sum);
    }
}