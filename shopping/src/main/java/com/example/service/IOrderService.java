package com.example.service;

import com.example.pojo.Order;
import com.example.pojo.PayInfo;
import com.example.vo.OrderVo;
import com.example.vo.ResponseVo;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;

public interface IOrderService {
    ResponseVo create(Integer uid, Integer shippingId);
    ResponseVo<PageInfo> list(@Param("uid") Integer uid,
                              @Param("pageNum") Integer PageNum,
                              @Param("pageSize") Integer PageSize
                                );
    ResponseVo<OrderVo> detail(@Param("uid") Integer uid, @Param("orderNo") Long orderNo);
    ResponseVo cancel(Integer uid, Long orderNo);
    void paid(Long orderNo);
}
