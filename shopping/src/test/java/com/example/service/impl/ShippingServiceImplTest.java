package com.example.service.impl;

import com.example.ShoppingApplicationTests;
import com.example.form.ShippingForm;
import com.example.service.IShippingService;
import com.example.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;


@Slf4j
public class ShippingServiceImplTest extends ShoppingApplicationTests {

    @Autowired
    private IShippingService shippingService;

    private Integer uid = 1;
    private ShippingForm form;
    private Integer shippingId;

//    @Before
//    public void before() {
//        ShippingForm form = new ShippingForm();
//        form.setReceiverName("廖师兄");
//        form.setReceiverAddress("慕课网");
//        form.setReceiverCity("北京");
//        form.setReceiverMobile("18812345678");
//        form.setReceiverPhone("010123456");
//        form.setReceiverProvince("北京");
//        form.setReceiverDistrict("海淀区");
//        form.setReceiverZip("000000");
//        this.form = form;
//        add();
//    }

    @Test
    public void add() {
        log.info("shippingService = {}", shippingService);
        ShippingForm form = new ShippingForm();
        form.setReceiverName("廖师兄");
        form.setReceiverAddress("慕课网");
        form.setReceiverCity("北京");
        form.setReceiverMobile("18812345678");
        form.setReceiverPhone("010123456");
        form.setReceiverProvince("北京");
        form.setReceiverDistrict("海淀区");
        form.setReceiverZip("000000");
        this.form = form;
        ResponseVo<Map<String, Integer>> responseVo =  shippingService.add(uid, form);
        log.info("response = {}", responseVo);
        this.shippingId = responseVo.getData().get("shippingId");

    }

    @Test
    public void update() {
        ShippingForm form = new ShippingForm();
        form.setReceiverName("小明");
        ResponseVo responseVo = shippingService.update(uid, shippingId, form);
    }

//    @After
//    public void delete() {
//        ResponseVo responseVo = shippingService.delete(uid, shippingId);
//    }

    @Test
    public void list() {
        ResponseVo responseVo = shippingService.list(uid, 1, 10);
    }
}