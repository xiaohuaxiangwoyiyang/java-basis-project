package com.example.service.impl;

import com.example.dao.ProductMapper;
import com.example.enums.ResponseEnum;
import com.example.pojo.Product;
import com.example.service.IProjectService;
import com.example.vo.ProductDetailVo;
import com.example.vo.ProductVo;
import com.example.vo.ResponseVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.enums.ProductStatusEnum.*;
import static com.example.enums.ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE;

@Slf4j
@Service
public class ProductServiceImpl implements IProjectService {

    @Autowired
    private ProductMapper projectMapper;

    @Autowired
    private CategoryServiceImpl categoryService;


    @Override
    public void selectProductPageByCategoryId(Integer id, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        List<Product> productList = projectMapper.selectProductPageByCategoryId(id, pageNumber, pageSize);
        log.info("projects = {}", productList);
    }

    @Override
    public ResponseVo<PageInfo> list(Integer categoryId, Integer pageNumber, Integer pageSize) {
        Set<Integer> categoryIdSet = new HashSet<>();
        if (categoryId != null) {
            categoryService.findSubCategoryId(categoryId, categoryIdSet);
            categoryIdSet.add(categoryId);
        }
        PageHelper.startPage(pageNumber, pageSize);
        List<Product> productList = projectMapper.selectByCategoryIdSet(categoryIdSet);
        List<ProductVo> productVoList = productList.stream()
                .map(e -> {
                    ProductVo productVo = new ProductVo();
                    BeanUtils.copyProperties(e, productVo);
                    return productVo;
                })
                .collect(Collectors.toList());
        PageInfo pageInfo = new PageInfo<>(productList);
        pageInfo.setList(productVoList);
        return ResponseVo.success(pageInfo);

    }

    @Override
    public ResponseVo<ProductDetailVo> detail(Integer productId) {
        Product product = projectMapper.selectByPrimaryKey(productId);
        // 只对确定性条件判断
        if(product.getStatus().equals(OFF_SALE.getCode()) || product.getStatus().equals(DELETE.getCode())) {
            return ResponseVo.error(PRODUCT_OFF_SALE_OR_DELETE);
        }
        ProductDetailVo productDetailVo = new ProductDetailVo();
        BeanUtils.copyProperties(product, productDetailVo);
        // 敏感数据处理
        productDetailVo.setStock(product.getStock() > 100 ? 100 : product.getStock());
        return ResponseVo.success(productDetailVo);
    }

}
