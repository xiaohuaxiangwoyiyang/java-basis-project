package com.example.form;

import lombok.Data;

@Data
public class CartAddForm {
    private Integer productId;
    private Boolean selected = true;
}
