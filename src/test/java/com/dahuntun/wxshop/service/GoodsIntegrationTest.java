package com.dahuntun.wxshop.service;

import com.dahuntun.wxshop.WxshopApplication;
import com.dahuntun.wxshop.entity.Response;
import com.dahuntun.wxshop.generate.Goods;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static javax.servlet.http.HttpServletResponse.SC_CREATED;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WxshopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.config.location=classpath:test-application.yml")
public class GoodsIntegrationTest extends AbstractIntegrationTest{
    @Test
    public void testCreateGoods() throws JsonProcessingException {
        String cookie = loginAndGetCookie();

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
        goods.setShopId(12345L);

        TestHttpResponse testHttpResponse = testHttpRequest(
                "/api/goods",
                false,
                goods,
                cookie);

        Response<Goods> responseData = objectMapper.readValue(testHttpResponse.body,
                new TypeReference<Response<Goods>>() {
        });
        Assertions.assertEquals(SC_CREATED, testHttpResponse.code);
        Assertions.assertEquals("肥皂", responseData.getData().getName());
    }

    @Test
    public void testDeleteGoods() {

    }
}
