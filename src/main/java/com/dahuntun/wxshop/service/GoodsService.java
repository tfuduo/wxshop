package com.dahuntun.wxshop.service;

import com.dahuntun.wxshop.entity.DataStatus;
import com.dahuntun.wxshop.entity.PageResponse;
import com.dahuntun.wxshop.generate.*;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class GoodsService {
    private final GoodsMapper goodsMapper;
    private final ShopMapper shopMapper;

    @Autowired
    public GoodsService(GoodsMapper goodsMapper, ShopMapper shopMapper) {
        this.goodsMapper = goodsMapper;
        this.shopMapper = shopMapper;
    }

    public Goods createGoods(Goods goods) {
        Shop shop = getShop(goods);

        if (Objects.equals(shop.getOwnerUserId(), UserContext.getCurrentUser().getId())) {
            long goodsId = goodsMapper.insert(goods);
            goods.setId(goodsId);
            return goods;
        } else {
            throw new NotAuthorizedForShopException("无权访问！");
        }
    }

    public Goods deleteGoodsById(long goodsId) {
        Shop shop = shopMapper.selectByPrimaryKey(goodsId);

        if (Objects.equals(shop.getOwnerUserId(), UserContext.getCurrentUser().getId())) {
            Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
            if (goods == null) {
                throw new ResourceNotFoundException("商品未找到！");
            }
            goods.setStatus(DataStatus.DELETED.getName());
            goodsMapper.updateByPrimaryKey(goods);
            return goods;
        } else {
            throw new NotAuthorizedForShopException("无权访问！");
        }
    }

    public Goods updateGoods(Goods goods, long goodsId) {
        Shop shop = getShop(goods);

        if (Objects.equals(shop.getOwnerUserId(), UserContext.getCurrentUser().getId())) {
            GoodsExample byId = new GoodsExample();
            byId.createCriteria().andIdEqualTo(goodsId);
            int affectedRows = goodsMapper.updateByExample(goods, byId);
            if (affectedRows == 0) {
                throw new ResourceNotFoundException("商品未找到");
            }
            return goods;
        } else {
            throw new NotAuthorizedForShopException("无权访问！");
        }
    }

    public PageResponse<Goods> getGoods(Integer pageNum, Integer pageSize, Integer shopId) {
        //找出总数
        int totalNumber = countAll(shopId);
        //找出页数
        int totalPage = totalNumber % pageSize == 0 ? totalNumber / pageSize : totalNumber / pageSize + 1;
        //分页
        List<Goods> pageGoods = getPageGoods(pageNum, pageSize);
        return PageResponse.pageData(pageNum, pageSize, totalPage, pageGoods);
    }

    private Shop getShop(Goods goods) {
        return shopMapper.selectByPrimaryKey(goods.getShopId());
    }
    private int countAll(Integer shopId) {
        GoodsExample goodsExample = new GoodsExample();
        if (shopId == null) {
            goodsExample.createCriteria().andStatusEqualTo(DataStatus.OK.getName());
        } else {
            goodsExample.createCriteria()
                    .andStatusEqualTo(DataStatus.OK.getName())
                    .andShopIdEqualTo(Long.valueOf(shopId));
        }
        return (int) goodsMapper.countByExample(goodsExample);
    }

    private List<Goods> getPageGoods(Integer pageNum, Integer pageSize) {
        GoodsExample page = new GoodsExample();
        page.setLimit(pageSize);
        page.setOffset((pageNum - 1) * pageSize);
        return goodsMapper.selectByExample(page);
    }

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    public static class NotAuthorizedForShopException extends RuntimeException {
        public NotAuthorizedForShopException(String message) {
            super(message);
        }
    }
}
