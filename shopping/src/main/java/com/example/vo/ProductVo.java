package com.example.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ProductVo {
    private Integer id;
    private Integer categoryId;
    private String name;
    private String subTitle;
    private String mainImage;
    private String detail;
    private BigDecimal price;
    private Integer status;
}
