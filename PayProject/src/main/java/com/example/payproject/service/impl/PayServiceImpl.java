package com.example.payproject.service.impl;

import com.example.payproject.dao.PayInfoMapper;
import com.example.payproject.enums.PayPlatformEnum;
import com.example.payproject.mapper.PayMapper;
import com.example.payproject.pojo.PayInfo;
import com.google.gson.Gson;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;

import com.lly835.bestpay.service.BestPayService;
import com.lly835.bestpay.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;


@Slf4j
@Service
public class PayServiceImpl implements PayMapper {
    private final static String QUEUE_PAY_NOTIFY = "payNotify";

    @Autowired
    BestPayService bestPayService;

    @Autowired
    PayInfoMapper payInfoMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public PayResponse create(@RequestParam("orderNo") String orderNo,
                              @RequestParam("amount") BigDecimal amount,
                              @RequestParam("bestPayTypeEnum") BestPayTypeEnum bestPayTypeEnum) {
        // 写入数据库
        PayInfo payInfo = new PayInfo(
                Long.parseLong(orderNo),
                PayPlatformEnum.getByBestPayTypeEnum(bestPayTypeEnum).getCode(),
                OrderStatusEnum.NOTPAY.name(),
                amount
        );
        log.info("payInfo = {}", JsonUtil.toJson(payInfo));

        payInfoMapper.insertSelective(payInfo);

        PayRequest request = new PayRequest();

        request.setPayTypeEnum(bestPayTypeEnum);
        request.setOrderId(orderNo);
        request.setOrderAmount(amount.doubleValue());
        request.setOrderName("6904480-最好的支付2");
        request.setAttach("这里是附加的信息");
        // request.setOpenid("wxd898fcb01713c658");

        log.info("【发起支付】request={}", JsonUtil.toJson(request));
        PayResponse payResponse = bestPayService.pay(request);
        log.info("【发起支付】response={}", JsonUtil.toJson(payResponse));

        return payResponse;
    }

    @Override
    public String asyncNotify(String notifyData) {
        // 1.签名校验
        PayResponse payResponse = bestPayService.asyncNotify(notifyData);
        log.info("payResponse={}", payResponse);
        // 2. 金额校验（从数据库查订单）
        PayInfo payInfo = payInfoMapper.selectByOrderNo(Long.parseLong(payResponse.getOrderId()));
        if (payInfo == null) {
            // 告警
            throw new RuntimeException("通过orderNo查询到的结果为null" + payResponse.getOrderId());
        }
        // 如果订单状态不是"已支付"
        if (!payInfo.getPlatformStatus().equals(OrderStatusEnum.SUCCESS.name())) {
            if (payInfo.getPayAmount().compareTo(BigDecimal.valueOf(payResponse.getOrderAmount())) != 0) {
                throw new RuntimeException("异步通知中的金额和数据库里的不一致，orderNo=" + payResponse.getOrderId());
            }
            // 3. 修改订单状态
            payInfo.setPlatformStatus(OrderStatusEnum.SUCCESS.name());
            payInfo.setPlatformNumber(payResponse.getOutTradeNo());
            payInfoMapper.updateByPrimaryKeySelective(payInfo);
        }

        log.info("notifyData");

        //TODO pay发送MQ消息，mall接受MQ消息
        amqpTemplate.convertAndSend(QUEUE_PAY_NOTIFY, new Gson().toJson(payInfo));

        // 两种支付方式

        if(payResponse.getPayPlatformEnum() == BestPayPlatformEnum.WX) {
            // 4. 告诉微信不要在通知
            return "<xml>\n" +
                    "<return_code><![CDATA[SUCCESS]]></return_code>\n" +
                    "<return_msg><![CDATA[OK]]></return_msg>\n" +
                    "</xml>";
        } else if(payResponse.getPayPlatformEnum() == BestPayPlatformEnum.ALIPAY) {
            return "success";
        }

        throw new RuntimeException("异步通知中错误的支付平台");

    }

    @Override
    public PayInfo queryByOrderId(String orderId) {

        PayInfo payInfo = payInfoMapper.selectByOrderNo(Long.parseLong(orderId));
        log.info("payInfo = {}", payInfo);
        return payInfo;
    }
}
