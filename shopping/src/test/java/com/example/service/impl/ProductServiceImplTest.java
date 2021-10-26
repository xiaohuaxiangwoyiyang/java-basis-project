package com.example.service.impl;

import com.example.ShoppingApplicationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class ProductServiceImplTest extends ShoppingApplicationTests {
    @Autowired
    private ProductServiceImpl projectService;

    @Test
    public void selectProductPageByCategoryId() {
        projectService.selectProductPageByCategoryId(null, 1, 10);
    }
}