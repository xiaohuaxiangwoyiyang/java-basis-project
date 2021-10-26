package com.example.dao;

import com.example.pojo.Category;
import com.example.vo.CategoryVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    int insertCategory(Category category);
    CategoryVo selectCategoryById(Integer id);
    List<Category> selectAll();
    Category selectCategoryDetail(Integer id);

}
