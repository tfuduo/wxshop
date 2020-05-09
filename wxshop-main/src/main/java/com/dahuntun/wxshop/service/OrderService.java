package com.dahuntun.wxshop.service;

import com.dahuntun.api.DataStatus;
import com.dahuntun.api.data.GoodsInfo;
import com.dahuntun.api.data.OrderInfo;
import com.dahuntun.api.generate.Order;
import com.dahuntun.api.rpc.RpcOrderService;
import com.dahuntun.wxshop.dao.GoodsStockMapper;
import com.dahuntun.wxshop.entity.GoodsWithNumber;
import com.dahuntun.wxshop.entity.HttpException;
import com.dahuntun.wxshop.entity.OrderResponse;
import com.dahuntun.wxshop.generate.Goods;
import com.dahuntun.wxshop.generate.ShopMapper;
import com.dahuntun.wxshop.generate.UserMapper;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    @Reference(version = "${wxshop.rpcorderservice.version}")
    private RpcOrderService rpcOrderService;

    private UserMapper userMapper;

    private ShopMapper shopMapper;

    private GoodsStockMapper goodsStockMapper;

    private GoodsService goodsService;

    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    public OrderService(UserMapper userMapper,
                        ShopMapper shopMapper,
                        GoodsStockMapper goodsStockMapper,
                        GoodsService goodsService,
                        SqlSessionFactory sqlSessionFactory) {
        this.userMapper = userMapper;
        this.shopMapper = shopMapper;
        this.goodsStockMapper = goodsStockMapper;
        this.goodsService = goodsService;
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public OrderResponse createOrder(OrderInfo orderInfo, Long userId) {
        //获取goodsId到商品的映射
        Map<Long, Goods> idToGoodsMap = getIdToGoodsMap(orderInfo);

        //创建订单
        Order createdOrder = crateOrderViaRpc(orderInfo, userId, idToGoodsMap);

        //返回响应
        return generateResponse(orderInfo, idToGoodsMap, createdOrder);
    }

    private OrderResponse generateResponse(OrderInfo orderInfo, Map<Long, Goods> idToGoodsMap, Order createdOrder) {
        OrderResponse orderResponse = new OrderResponse(createdOrder);
        Long shopId = new ArrayList<>(idToGoodsMap.values()).get(0).getShopId();
        orderResponse.setShop(shopMapper.selectByPrimaryKey(shopId));
        orderResponse.setGoods(orderInfo.getGoods()
                .stream()
                .map(goods -> toGoodsWithNumber(goods, idToGoodsMap))
                .collect(Collectors.toList()));
        return orderResponse;
    }

    private Map<Long, Goods> getIdToGoodsMap(OrderInfo orderInfo) {
        List<Long> goodsId = orderInfo.getGoods()
                .stream()
                .map(GoodsInfo::getId)
                .collect(Collectors.toList());
        return goodsService.getIdToGoodsMap(goodsId);
    }

    private Order crateOrderViaRpc(OrderInfo orderInfo, Long userId, Map<Long, Goods> idToGoodsMap) {
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(DataStatus.PENDING.getName());
        order.setAddress(userMapper.selectByPrimaryKey(userId).getAddress());
        order.setTotalPrice(caculateTotalPrice(orderInfo, idToGoodsMap));

        return rpcOrderService.createOrder(orderInfo, order);
    }

    @Transactional
    public void deductStock(OrderInfo orderInfo) {
        for (GoodsInfo goodsInfo : orderInfo.getGoods()) {
            if (goodsStockMapper.deductStock(goodsInfo) <= 0) {
                LOGGER.error("扣减库存失败，商品id：" + goodsInfo.getId() + "，数量：" + goodsInfo.getNumber());
                throw HttpException.gone("扣减库存失败！");
            }
        }
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
