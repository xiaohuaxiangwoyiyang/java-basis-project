package com.example.service;

import com.example.vo.ProductDetailVo;
import com.example.vo.ResponseVo;
import com.github.pagehelper.PageInfo;

public interface IProjectService {
    /**
     * 根据categoryId过滤数据做分页
     */
    void selectProductPageByCategoryId(Integer id, Integer pageNumber, Integer pageSize);

    ResponseVo<PageInfo> list(Integer categoryId, Integer pageNumber, Integer pageSize);
    ResponseVo<ProductDetailVo> detail(Integer productId);

}
