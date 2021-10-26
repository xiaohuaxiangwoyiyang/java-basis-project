package com.example.dao;

import com.example.pojo.OrderItem;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface OrderItemMapper {
    int batchInsert(List<OrderItem> orderItemList);
    List<OrderItem> selectByOrderNoSet(@Param("orderNoSets") Set<Long> orderNoSets);
    List<OrderItem> selectByOrderNo(Long orderNo);
    int deleteByOrderNo(Long orderNo);
}
