package com.dahuntun.wxshop.service;

import com.dahuntun.wxshop.WxshopApplication;
import com.dahuntun.wxshop.controller.ShoppingCartController;
import com.dahuntun.wxshop.entity.PageResponse;
import com.dahuntun.wxshop.entity.Response;
import com.dahuntun.wxshop.entity.ShoppingCartData;
import com.dahuntun.wxshop.entity.ShoppingCartGoods;
import com.dahuntun.wxshop.generate.Goods;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WxshopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.config.location=classpath:test-application.yml")
public class ShoppingCartIntegrationTest extends AbstractIntegrationTest{
    @Test
    public void canQueryShoppingCartData() throws JsonProcessingException {
        UserLoginResponse userLoginResponse = loginAndGetCookie();

        PageResponse<ShoppingCartData> response = testHttpRequest("/api/shoppingCart?pageNum=2&pageSize=1",
                "GET", null, userLoginResponse.cookie).asJsonObject(new TypeReference<PageResponse<ShoppingCartData>>() {
        });

        Assertions.assertEquals(2, response.getPageNum());
        Assertions.assertEquals(1, response.getPageSize());
        Assertions.assertEquals(2, response.getTotalPage());
        Assertions.assertEquals(1, response.getData().size());
        Assertions.assertEquals(2, response.getData().get(0).getShop().getId());
        Assertions.assertEquals(Arrays.asList(4L, 5L),
                response.getData().get(0).getGoods().stream()
                        .map(Goods::getId).collect(Collectors.toList()));
        Assertions.assertEquals(Arrays.asList(100L, 200L),
                response.getData().get(0).getGoods().stream()
                        .map(ShoppingCartGoods::getPrice).collect(Collectors.toList()));
        Assertions.assertEquals(Arrays.asList(200, 300),
                response.getData().get(0).getGoods().stream()
                        .map(ShoppingCartGoods::getNumber).collect(Collectors.toList()));

    }

    @Test
    public void canAddShoppingCartData() throws Exception {
        UserLoginResponse loginResponse = loginAndGetCookie();

        ShoppingCartController.AddToShoppingCartRequest request = new ShoppingCartController.AddToShoppingCartRequest();
        ShoppingCartController.AddToShoppingCartItem item = new ShoppingCartController.AddToShoppingCartItem();
        item.setId(2L);
        item.setNumber(2);

        request.setGoods(Collections.singletonList(item));

        Response<ShoppingCartData> response = testHttpRequest("/api/shoppingCart",
                "POST", request, loginResponse.cookie).asJsonObject(new TypeReference<Response<ShoppingCartData>>() {
        });

        Assertions.assertEquals(1L, response.getData().getShop().getId());
        Assertions.assertEquals(Arrays.asList(1L, 2L),
                response.getData().getGoods().stream().map(Goods::getId).collect(Collectors.toList()));
        Assertions.assertEquals(Sets.newHashSet(2, 100),
                response.getData().getGoods().stream().map(ShoppingCartGoods::getNumber).collect(Collectors.toSet()));
        Assertions.assertTrue(response.getData().getGoods().stream().allMatch(
                goods -> goods.getShopId() == 1L
        ));
    }
}
