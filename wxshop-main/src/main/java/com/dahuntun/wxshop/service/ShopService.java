package com.dahuntun.wxshop.service;

import com.dahuntun.api.DataStatus;
import com.dahuntun.wxshop.entity.HttpException;
import com.dahuntun.wxshop.entity.PageResponse;
import com.dahuntun.wxshop.generate.Shop;
import com.dahuntun.wxshop.generate.ShopExample;
import com.dahuntun.wxshop.generate.ShopMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class ShopService {
    private final ShopMapper shopMapper;

    public ShopService(ShopMapper shopMapper) {
        this.shopMapper = shopMapper;
    }

    public PageResponse<Shop> getShopByUserId(Integer pageNum, Integer pageSize, Long id) {
        //找出总数
        ShopExample countByStatus = new ShopExample();
        countByStatus.createCriteria().andStatusEqualTo(DataStatus.OK.getName());
        int totalNumber = (int) shopMapper.countByExample(countByStatus);
        //找出页数
        int totalPage = totalNumber % pageSize == 0 ? totalNumber / pageSize : totalNumber / pageSize + 1;
        //分页
        ShopExample pageCondition = new ShopExample();
        pageCondition.createCriteria().andStatusEqualTo(DataStatus.OK.getName());
        pageCondition.setLimit(pageSize);
        pageCondition.setOffset((pageNum - 1) * pageSize);

        List<Shop> pageShops = shopMapper.selectByExample(pageCondition);
        return PageResponse.pageData(pageNum, pageSize, totalPage, pageShops);
    }

    public Shop createShop(Shop shop, Long creatorId) {
        shop.setOwnerUserId(creatorId);
        shop.setCreatedAt(new Date());
        shop.setUpdatedAt(new Date());
        shop.setStatus(DataStatus.OK.getName());
        long shopId = shopMapper.insert(shop);
        shop.setId(shopId);
        return shop;
    }

    public Shop updateShop(Shop shop, Long userId) {
        Shop shopInDatabase = shopMapper.selectByPrimaryKey(shop.getId());
        if (shopInDatabase == null) {
            throw HttpException.notFound("店铺未找到！");
        }

        if (!Objects.equals(shopInDatabase.getOwnerUserId(), userId)) {
            throw HttpException.forbidden("无权访问！");
        }

        shopMapper.updateByPrimaryKey(shop);

        return shop;
    }

    public Shop deleteShopById(Long shopId, Long userId) {
        Shop shopInDataBase = shopMapper.selectByPrimaryKey(shopId);
        if (shopInDataBase == null) {
            throw HttpException.notFound("店铺未找到！");
        }

        if (!Objects.equals(shopInDataBase.getOwnerUserId(), userId)) {
            throw HttpException.forbidden("无权访问！");
        }

        shopInDataBase.setStatus(DataStatus.DELETED.getName());
        shopMapper.updateByPrimaryKey(shopInDataBase);
        return shopInDataBase;
    }
}
