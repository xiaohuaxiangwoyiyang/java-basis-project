package com.example.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class Category {
    private Integer id;
    private Integer parentId;
    private String name;
    private String desc;
    private Date createTime;
    private Date updateTime;

    public Category() {}

    public Category(Integer parentId, String name, String desc) {
        this.parentId = parentId;
        this.name = name;
        this.desc = desc;
    }
}
