package com.dahuntun.api.rpc;

import com.dahuntun.api.DataStatus;
import com.dahuntun.api.data.OrderInfo;
import com.dahuntun.api.data.PageResponse;
import com.dahuntun.api.data.RpcOrderGoods;
import com.dahuntun.api.generate.Order;

public interface RpcOrderService {
    Order createOrder(OrderInfo orderInfo, Order order);

    RpcOrderGoods deleteOrder(long orderId, long userId);

    PageResponse<RpcOrderGoods> getOrder(Long userId, Integer pageNum, Integer pageSize, DataStatus dataStatus);

    RpcOrderGoods updateOrder(Order order);

    RpcOrderGoods getOrderById(long orderId);
}
