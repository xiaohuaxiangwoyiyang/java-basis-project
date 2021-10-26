package com.example.dao;

import com.example.pojo.Order;
import com.example.vo.OrderItemVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {
    int insertSelective(Order order);
    List<Order> selectOrderByUserId(Integer uid);
    Order selectOrderByOrderNo(Long orderNo);
    int deleteByOrderNo(Long orderNo);
    int updateByPrimaryKeySelective(Order order);
}
