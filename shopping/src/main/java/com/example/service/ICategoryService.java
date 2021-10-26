package com.example.service;

import com.example.pojo.Category;
import com.example.vo.CategoryVo;
import com.example.vo.ResponseVo;

import java.util.Set;

public interface ICategoryService {
    /**
     * 插入
     */
    ResponseVo insertCategory(Category category);

    /**
     * 通过id查找相应的类目及children
     */
    CategoryVo selectCategoryById(Integer id);

    /**
     * 查找详情
     */
    Category selectCategoryDetail(Integer id);

    /**
     * 查找子集id
     */
    void findSubCategoryId(Integer id, Set<Integer> resultSet);

}
