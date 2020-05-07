package com.dahuntun.wxshop.service;

import com.dahuntun.api.data.GoodsInfo;
import com.dahuntun.api.data.OrderInfo;
import com.dahuntun.api.generate.Order;
import com.dahuntun.api.rpc.RpcOrderService;
import com.dahuntun.wxshop.entity.GoodsWithNumber;
import com.dahuntun.wxshop.entity.HttpException;
import com.dahuntun.wxshop.entity.OrderResponse;
import com.dahuntun.wxshop.generate.*;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Reference(version = "${wxshop.rpcorderservice.version}")
    private final RpcOrderService rpcOrderService;

    private final UserMapper userMapper;

    private final ShopMapper shopMapper;

    private final GoodsMapper goodsMapper;

    private final GoodsService goodsService;

    @Inject
    public OrderService(RpcOrderService rpcOrderService,
                        UserMapper userMapper,
                        ShopMapper shopMapper,
                        GoodsMapper goodsMapper,
                        GoodsService goodsService) {
        this.userMapper = userMapper;
        this.shopMapper = shopMapper;
        this.goodsMapper = goodsMapper;
        this.rpcOrderService = rpcOrderService;
        this.goodsService = goodsService;
    }

    public OrderResponse createOrder(OrderInfo orderInfo, Long userId) {
        List<Long> goodsId = orderInfo.getGoods()
                .stream()
                .map(GoodsInfo::getId)
                .collect(Collectors.toList());
        Map<Long, Goods> idToGoodsMap = goodsService.getIdToGoodsMap(goodsId);
        Order order = new Order();
        order.setUserId(userId);
        order.setAddress(userMapper.selectByPrimaryKey(userId).getAddress());
        order.setTotalPrice(caculateTotalPrice(orderInfo, idToGoodsMap));

        Order createdOrder = rpcOrderService.createOrder(orderInfo, order);

        OrderResponse orderResponse = new OrderResponse(createdOrder);
        Long shopId = new ArrayList<>(idToGoodsMap.values()).get(0).getShopId();
        orderResponse.setShop(shopMapper.selectByPrimaryKey(shopId));
        orderResponse.setGoods(orderInfo.getGoods()
                .stream()
                .map(goods -> toGoodsWithNumber(goods, idToGoodsMap))
                .collect(Collectors.toList()));
        return orderResponse;
    }

    private GoodsWithNumber toGoodsWithNumber(GoodsInfo goodsInfo, Map<Long, Goods> idToGoodsMap) {
        GoodsWithNumber ret = new GoodsWithNumber(idToGoodsMap.get(goodsInfo.getId()));
        ret.setNumber(goodsInfo.getNumber());
        return ret;
    }

    private Long caculateTotalPrice(OrderInfo orderInfo, Map<Long, Goods> idToGoodsMap) {
        long result = 0L;

        for (GoodsInfo goodsInfo:orderInfo.getGoods()) {
            Goods goods = idToGoodsMap.get(goodsInfo.getId());
            if (goods == null) {
                throw HttpException.badRequest("goodsId非法: " + goodsInfo.getId());
            }
            if (goodsInfo.getNumber() <= 0) {
                throw HttpException.badRequest("number非法: " + goodsInfo.getNumber());
            }
            result += goods.getPrice()*goodsInfo.getNumber();
        }
        return result;
    }
}
