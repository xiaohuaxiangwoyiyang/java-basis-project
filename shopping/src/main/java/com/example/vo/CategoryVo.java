package com.example.vo;

import lombok.Data;

import java.util.List;

@Data
public class CategoryVo {
    private Integer id;
    private Integer parentId;
    private String name;
    private String desc;
    private List<CategoryVo> children;
}
