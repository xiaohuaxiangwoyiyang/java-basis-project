package com.example.controller;

import com.example.form.CategoryForm;
import com.example.pojo.Category;
import com.example.service.impl.CategoryServiceImpl;
import com.example.vo.CategoryVo;
import com.example.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class CategoryController {

    @Autowired
    private CategoryServiceImpl categoryService;

    /**
     * 插入类目数据
     * @param categoryForm
     * @return
     */
    @PostMapping("/category/insert")
    public ResponseVo insertCategory(@Valid @RequestBody CategoryForm categoryForm) {
            Category category = new Category(categoryForm.getParentId(),
                    categoryForm.getName(),categoryForm.getDesc() );

        log.info("category = {}", category );
        ResponseVo categoryResponseVo = categoryService.insertCategory(category);
        return categoryResponseVo;
    }

    /**
     * 通过id找到相应的类目及children
     * @param id
     * @return
     */
    @GetMapping("/category/getById")
    public ResponseVo getCategoryById(@RequestParam("id") Integer id) {
        CategoryVo categoryVoList = categoryService.selectCategoryById(id);
        return ResponseVo.success(categoryVoList);
    }

    /**
     * 查找详情
     * @param id
     * @return
     */
    @GetMapping("/category/detail")
    public ResponseVo getCategoryDetail(@RequestParam("id") Integer id) {
        Category category = categoryService.selectCategoryDetail(id);
        return ResponseVo.success(category);
    }
}
