package com.example.form;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
public class CategoryForm {


    private Integer parentId;
    @NotBlank
    private String name;
    @NotBlank
    private String desc;
}
