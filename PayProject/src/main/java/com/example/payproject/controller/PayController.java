package com.example.payproject.controller;

import com.example.payproject.pojo.PayInfo;
import com.example.payproject.service.impl.PayServiceImpl;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class PayController {

    @Autowired
    private PayServiceImpl payService;

    @Autowired
    private WxPayConfig wxPayConfig;


    @GetMapping ("/pay")
    public ModelAndView create(@RequestParam("orderNo") String orderNo,
                               @RequestParam("amount") BigDecimal amount,
                               @RequestParam("payType") BestPayTypeEnum bestPayTypeEnum) {
        PayResponse payResponse = payService.create(orderNo, amount, bestPayTypeEnum);
        Map<String, String> map = new HashMap<>();

        // 支付方式不同，渲染就不同
        if(bestPayTypeEnum == BestPayTypeEnum.WXPAY_NATIVE) {
            map.put("codeUrl", payResponse.getCodeUrl());
            map.put("orderId", orderNo);
            map.put("returnUrl", wxPayConfig.getReturnUrl());
            return new ModelAndView("createForWxNative", map);
        } else if(bestPayTypeEnum == BestPayTypeEnum.ALIPAY_PC) {
            map.put("body", payResponse.getBody());
            return new ModelAndView("createForAliPay", map);
        }
        throw new RuntimeException("暂不支持该支付类型");
    }

    @PostMapping("/notify")
    @ResponseBody
    public String asyncNotify(@RequestBody String notifyData) {
        // log.info("notify={}", notifyData);
        return payService.asyncNotify(notifyData);
    }

    @GetMapping("/queryByOrderId")
    @ResponseBody
    public PayInfo queryOrderId(@RequestParam String orderId) {
        log.info("支付订单查询中。。。");
        return payService.queryByOrderId(orderId);
    }
}

