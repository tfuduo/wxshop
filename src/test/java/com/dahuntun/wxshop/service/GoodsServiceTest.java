package com.dahuntun.wxshop.service;

import com.dahuntun.wxshop.entity.DataStatus;
import com.dahuntun.wxshop.entity.PageResponse;
import com.dahuntun.wxshop.generate.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    public void setUp() {
        User user = new User();
        user.setId(1L);
        UserContext.setCurrentUser(user);

        lenient().when(shopMapper.selectByPrimaryKey(anyLong())).thenReturn(shop);
    }

    @Test
    public void createGoodsSucceedIfUserIsOwner() {
        when(shop.getOwnerUserId()).thenReturn(1L);
        when(goodsMapper.insert(goods)).thenReturn(123);

        assertEquals(goods, goodsService.createGoods(goods));
        verify(goods).setId(123L);
    }

    @Test
    public void createGoodsFailedIfUserNotOwner() {
        when(shop.getOwnerUserId()).thenReturn(2L);

        assertThrows(GoodsService.NotAuthorizedForShopException.class, () -> {
            goodsService.createGoods(goods);
        });
    }

    @Test
    public void deleteGoodsThrowExceptionIfGoodsNotFound() {
        long goodsToBeDeleted = 123L;
        when(shop.getOwnerUserId()).thenReturn(1L);

        when(goodsMapper.selectByPrimaryKey(goodsToBeDeleted)).thenReturn(null);
        assertThrows(GoodsService.ResourceNotFoundException.class, () -> {
            goodsService.deleteGoodsById(goodsToBeDeleted);
        });
    }

    @Test
    public void deleteGoodsSucceed() {
        long goodsToBeDeleted = 123L;
        when(shop.getOwnerUserId()).thenReturn(1L);
        when(goodsMapper.selectByPrimaryKey(goodsToBeDeleted)).thenReturn(goods);
        goodsService.deleteGoodsById(goodsToBeDeleted);

        verify(goods).setStatus(DataStatus.DELETED.getName());
    }

    @Test
    public void deleteGoodsThrowExceptionIfUserNotOwner() {
        long goodsToBeDeleted = 123L;
        when(shop.getOwnerUserId()).thenReturn(2L);
        assertThrows(GoodsService.NotAuthorizedForShopException.class, () -> {
           goodsService.deleteGoodsById(123L);
        });
    }

    @Test
    public void getGoodsSucceedWithNullShopId() {
        int pageNumber = 5;
        int pageSize = 10;

        List<Goods> mockData = Mockito.mock(List.class);

        when(goodsMapper.countByExample(any())).thenReturn(55L);
        when(goodsMapper.selectByExample(any())).thenReturn(mockData);
        PageResponse<Goods> result = goodsService.getGoods(pageNumber, pageSize, null);

        assertEquals(5, result.getPageNum());
        assertEquals(10, result.getPageSize());
        assertEquals(6, result.getTotalPage());
        assertEquals(mockData, result.getData());
    }

    @Test
    public void getGoodsSucceedWithShopId() {
        int pageNumber = 5;
        int pageSize = 10;

        List<Goods> mockData = Mockito.mock(List.class);

        when(goodsMapper.countByExample(any())).thenReturn(100L);
        when(goodsMapper.selectByExample(any())).thenReturn(mockData);
        PageResponse<Goods> result = goodsService.getGoods(pageNumber, pageSize, 456);

        assertEquals(5, result.getPageNum());
        assertEquals(10, result.getPageSize());
        assertEquals(10, result.getTotalPage());
        assertEquals(mockData, result.getData());
    }

    @Test
    public void updateGoodsThrowExceptionIfGoodsNotFound() {
        long goodsToBeUpdated = 123L;
        when(shop.getOwnerUserId()).thenReturn(1L);

        lenient().when(goodsMapper.selectByPrimaryKey(goodsToBeUpdated)).thenReturn(null);
        assertThrows(GoodsService.ResourceNotFoundException.class, () -> {
            goodsService.updateGoods(goods, 123L);
        });
    }

    @Test
    public void updateGoodsThrowExceptionIfUserNotOwner() {
        long goodsToBeUpdated = 123L;

        when(shop.getOwnerUserId()).thenReturn(2L);
        assertThrows(GoodsService.NotAuthorizedForShopException.class, () -> {
            goodsService.updateGoods(goods,123L);
        });
    }

    @Test
    public void updateGoodsSucceed() {
        long goodsToBeUpdated = 123L;
        when(shop.getOwnerUserId()).thenReturn(1L);
        when(goodsMapper.updateByExample(any(), any())).thenReturn(1);

        assertEquals(goods, goodsService.updateGoods(goods, goodsToBeUpdated));
    }
}
