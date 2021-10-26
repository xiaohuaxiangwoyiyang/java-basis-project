package com.example.service;

import com.example.form.CartAddForm;
import com.example.form.CartUpdateForm;
import com.example.pojo.Cart;
import com.example.vo.CartVo;
import com.example.vo.ResponseVo;

import java.util.List;

public interface ICartService {
    ResponseVo<CartVo> add(Integer id, CartAddForm form);
    ResponseVo<CartVo> list(Integer id);
    ResponseVo<CartVo> update(Integer uid, Integer productId, CartUpdateForm form);

    ResponseVo<CartVo> delete(Integer uid, Integer productId);
    ResponseVo<CartVo> select(Integer uid, Boolean productSelected);
    Integer sum(Integer uid);

    List<Cart> listForCart(Integer uid);
}
