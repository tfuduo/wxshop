package com.dahuntun.wxshop.service;

import com.dahuntun.wxshop.dao.GoodsDAO;
import com.dahuntun.wxshop.generate.Goods;
import org.springframework.stereotype.Service;

@Service
public class GoodsService {
    private GoodsDAO goodsDAO;

    public Goods createGoods(Goods goods) {
        return goodsDAO.insertGoods(goods);
    }
}
