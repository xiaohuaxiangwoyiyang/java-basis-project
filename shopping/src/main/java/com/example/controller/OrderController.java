package com.example.controller;

import com.example.service.IOrderService;
import com.example.vo.OrderVo;
import com.example.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @PostMapping("/order")
    ResponseVo create(@RequestParam(required = true) Integer uid,
                      @RequestParam(required = true) Integer shippingId ) {
        return orderService.create(uid, shippingId);
    }

    @GetMapping("/order")
    ResponseVo list(@RequestParam(required = true) Integer uid,
                    @RequestParam(required = true) Integer pageNum,
                    @RequestParam(required = true) Integer pageSize) {

        return orderService.list(uid, pageNum, pageSize);
    }

    @GetMapping("/order/{orderNo}")
    ResponseVo<OrderVo> detail(@RequestParam(required = true) Integer uid, @PathVariable Long orderNo) {
        return orderService.detail(uid, orderNo);
    }
}
