package com.example.payproject.config;

import com.lly835.bestpay.config.AliPayConfig;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.service.BestPayService;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BestPayConfig {

    @Autowired
    public AliAccountConfig aliAccountConfig;

    @Autowired
    public WxConfig wxConfig;

    @Bean
    public BestPayService bestPayService(WxPayConfig wxPayConfig) {

        AliPayConfig aliPayConfig = new AliPayConfig();
        aliPayConfig.setAppId(aliAccountConfig.getAppId());
        aliPayConfig.setPrivateKey(aliAccountConfig.getPrivateKey());
        aliPayConfig.setAliPayPublicKey(aliAccountConfig.getPublicKey());
        aliPayConfig.setNotifyUrl(aliAccountConfig.getNotifyUrl());
        aliPayConfig.setReturnUrl(aliAccountConfig.getReturnUrl());

        BestPayServiceImpl bestPayService = new BestPayServiceImpl();
        bestPayService.setWxPayConfig(wxPayConfig);
        bestPayService.setAliPayConfig(aliPayConfig);
        log.info("wxPayConfig = {}", wxPayConfig);
        return bestPayService;
    }

    @Bean
    public WxPayConfig wxPayConfig() {
        WxPayConfig wxPayConfig = new WxPayConfig();
        wxPayConfig.setAppId(wxConfig.getAppId());
        wxPayConfig.setMchId(wxConfig.getMchId());
        wxPayConfig.setMchKey(wxConfig.getMchKey());
        wxPayConfig.setNotifyUrl(wxConfig.getNotifyUrl());
        wxPayConfig.setReturnUrl(wxConfig.getReturnUrl());
        return wxPayConfig;
    }
}
