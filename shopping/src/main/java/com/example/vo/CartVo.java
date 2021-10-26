package com.example.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartVo {
    private List<CartProductVo> cartProdoctVoList;
    private Boolean selectedAll;
    private BigDecimal cartTotalPrice;
    private Integer cartTotalQuantity;
}
