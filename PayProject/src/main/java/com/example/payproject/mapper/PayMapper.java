package com.example.payproject.mapper;


import com.example.payproject.pojo.PayInfo;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;

@Mapper
public interface PayMapper {
    /**
     * 创建支付
     */
    PayResponse create(String orderId, BigDecimal amount, BestPayTypeEnum bestPayTypeEnum);

    /**
     * 异步通知处理
     * @param notifyData
     * @return
     */

    String asyncNotify(String notifyData);

    /**
     * 查询支付记录
     * @param orderId
     */
    PayInfo queryByOrderId(String orderId);

}
