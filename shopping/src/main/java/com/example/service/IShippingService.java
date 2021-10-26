package com.example.service;

import com.example.form.ShippingForm;
import com.example.vo.ResponseVo;
import com.github.pagehelper.PageInfo;

import java.util.Map;

public interface IShippingService {
    ResponseVo<Map<String, Integer>> add(Integer uid, ShippingForm form);
    ResponseVo update(Integer uid, Integer shippingId, ShippingForm form);
    ResponseVo delete(Integer uid, Integer shippingId);
    ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize);
}
