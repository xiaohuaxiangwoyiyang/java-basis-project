package com.example.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Product {
    private Integer id;
    private Integer categoryId;
    private String name;
    private String subTitle;
    private String mainImage;
    private String subImages;
    private String subtitle;
    private String detail;
    private BigDecimal price;
    private Integer stock;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
