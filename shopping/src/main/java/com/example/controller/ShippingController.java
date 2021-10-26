package com.example.controller;

import com.example.consts.MallConst;
import com.example.form.ShippingForm;
import com.example.pojo.User;
import com.example.service.IShippingService;
import com.example.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@RestController
public class ShippingController {
    @Autowired
    private IShippingService shippingService;

    @PostMapping("/shipping")
    public ResponseVo add(@Valid @RequestBody ShippingForm form, HttpSession session) {
        log.info("form = {}", form);
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        log.info("user = {}", user);
        return shippingService.add(user.getId(), form);
    }

    @PostMapping("/shipping/{shippingId}")
    public ResponseVo update(@PathVariable Integer shippingId, @Valid @RequestBody ShippingForm form, HttpSession session) {
        // User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return shippingService.update(1, shippingId, form);
    }

    @DeleteMapping("/shipping/{shippingId}")
    public ResponseVo delete(@PathVariable Integer shippingId) {
        log.info("shippingId = {}", shippingId);
        return shippingService.delete(1, shippingId);
    }

    @GetMapping("/shipping")
    public ResponseVo list(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                           @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return shippingService.list(1, pageNum, pageSize);
    }
}
