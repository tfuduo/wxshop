package com.dahuntun.wxshop.service;

import com.dahuntun.wxshop.generate.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoodsServiceTest {
    @Mock
    private GoodsMapper goodsMapper;
    @Mock
    private ShopMapper shopMapper;
    @Mock
    private Shop shop;
    @Mock
    private Goods goods;

    @InjectMocks
    private GoodsService goodsService;

    @AfterEach
    public void clearUserContext() {
        UserContext.setCurrentUser(null);
    }

    @BeforeEach
    public void initUserContext() {
        User user = new User();
        user.setId(1L);
        UserContext.setCurrentUser(user);
    }

    @Test
    public void createGoodsSucceedIfUserIsOwner() {
        when(shopMapper.selectByPrimaryKey(anyLong())).thenReturn(shop);
        when(shop.getOwnerUserId()).thenReturn(1L);
        when(goodsMapper.insert(goods)).thenReturn(123);

        Assertions.assertEquals(goods, goodsService.createGoods(goods));
        verify(goods).setId(123L);
    }

    @Test
    public void createGoodsFailedIfUserNotOwner() {
        when(shopMapper.selectByPrimaryKey(anyLong())).thenReturn(shop);
        when(shop.getOwnerUserId()).thenReturn(2L);

        Assertions.assertThrows(GoodsService.NotAuthorizedForShopException.class, () -> {
            goodsService.createGoods(goods);
        });
    }
}
