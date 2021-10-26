package com.example.dao;

import com.example.pojo.Shipping;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface ShippingMapper {
    int insertSelective(Shipping record);
    int updateByPrimaryKeySelective(Shipping record);
    int deleteByIdAndUid(@Param("uid") Integer uid, @Param("shippingId") Integer shippingId);
    List<Shipping> selectByUid(Integer uid);
    Shipping selectByPrimaryKey(Integer id);
    List<Shipping> selectByIdSet(@Param("shippingIdSet") Set<Integer> shippingIdSet);
}
