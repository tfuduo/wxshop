package com.dahuntun.wxshop.service;

import com.dahuntun.wxshop.WxshopApplication;
import com.dahuntun.wxshop.entity.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WxshopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.config.location=classpath:test-application.yml")
public class GoodsIntegrationTest extends AbstractIntegrationTest{
    @Test
    public void testCreateGoods() throws JsonProcessingException {
        UserLoginResponse userLoginResponse = loginAndGetCookie();

        Shop shop = new Shop();
        shop.setName("我的微店");
        shop.setDescription("开张了！");
        shop.setImgUrl("http://shopurl");

        TestHttpResponse shopResponse = testHttpRequest(
                "/api/shop",
                "POST",
                shop,
                userLoginResponse.cookie);

        Response<Shop> shopData = objectMapper.readValue(shopResponse.body,
                new TypeReference<Response<Shop>>() {
                });
        assertEquals(SC_CREATED, shopResponse.code);
        assertEquals("我的微店", shopData.getData().getName());
        assertEquals("开张了！", shopData.getData().getDescription());
        assertEquals("http://shopurl", shopData.getData().getImgUrl());
        assertEquals("ok", shopData.getData().getStatus());
        assertEquals(shopData.getData().getOwnerUserId(), userLoginResponse.user.getId());
        //{
        //    "name": "肥皂",
        //    "description": "纯天然无污染肥皂",
        //    "details": "这是一块好肥皂",
        //    "imgUrl": "https://img.url",
        //    "price": 500,
        //    "stock": 10,
        //    "shopId": 12345
        //}
        Goods goods = new Goods();
        goods.setName("肥皂");
        goods.setDescription("纯天然无污染肥皂");
        goods.setDetails("这是一块好肥皂");
        goods.setImgUrl("https://img.url");
        goods.setPrice(500L);
        goods.setStock(10);
        goods.setShopId(shopData.getData().getId());

        TestHttpResponse goodsResponse = testHttpRequest(
                "/api/goods",
                "POST",
                goods,
                userLoginResponse.cookie);

        Response<Goods> goodsData = objectMapper.readValue(goodsResponse.body,
                new TypeReference<Response<Goods>>() {
        });
        assertEquals(SC_CREATED, goodsResponse.code);
        assertEquals("肥皂", goodsData.getData().getName());
        assertEquals(shopData.getData().getId(), goodsData.getData().getShopId());
        assertEquals("ok", goodsData.getData().getStatus());
    }

    @Test
    public void return404IfGoodsNotExist() throws JsonProcessingException {
        String cookie = loginAndGetCookie().cookie;
        TestHttpResponse response = testHttpRequest("/api/goods/12345678",
                "DELETE",
                null,
                cookie);
        assertEquals(SC_NOT_FOUND, response.code);
    }
}
