package com.example.dao;

import com.example.pojo.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface ProductMapper {
    List<Product> selectProductPageByCategoryId(Integer id, Integer pageNumber, Integer pageSize);
    List<Product> selectByCategoryIdSet(@Param("categoryIdSet") Set<Integer> categoryIdSet);
    Product selectByPrimaryKey(Integer id);
    List<Product> selectByProductIdSet(Set<Integer> productIdSet);
    int updateByPrimaryKeySelective(Product product);
}
