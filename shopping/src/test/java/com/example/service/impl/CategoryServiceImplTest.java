package com.example.service.impl;

import com.example.ShoppingApplicationTests;
import com.example.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class CategoryServiceImplTest extends ShoppingApplicationTests {

    @Autowired
    private CategoryServiceImpl categoryService;

    @Test
    public void selectCategoryById() {
        categoryService.selectCategoryById(1);
    }

}