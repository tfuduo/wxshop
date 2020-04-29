package com.dahuntun.wxshop.entity;

import com.dahuntun.wxshop.generate.Shop;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ShoppingCartData {
    @JsonProperty("shop")
    private Shop shop;

    @JsonProperty("goods")
    private List<ShoppingCartGoods> goods;

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public List<ShoppingCartGoods> getGoods() {
        return goods;
    }

    public void setGoods(List<ShoppingCartGoods> goods) {
        this.goods = goods;
    }
}
