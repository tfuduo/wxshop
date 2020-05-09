package com.dahuntun.wxshop.mock;

import com.dahuntun.api.DataStatus;
import com.dahuntun.api.data.OrderInfo;
import com.dahuntun.api.data.PageResponse;
import com.dahuntun.api.data.RpcOrderGoods;
import com.dahuntun.api.generate.Order;
import com.dahuntun.api.rpc.RpcOrderService;
import org.apache.dubbo.config.annotation.Service;
import org.mockito.Mock;

@Service(version = "${wxshop.rpcorderservice.version}")
public class MockRpcOrderService implements RpcOrderService {
    @Mock
    public RpcOrderService rpcOrderService;

    @Override
    public Order createOrder(OrderInfo orderInfo, Order order) {
        return rpcOrderService.createOrder(orderInfo, order);
    }

    @Override
    public RpcOrderGoods deleteOrder(long orderId, long userId) {
        return rpcOrderService.deleteOrder(orderId, userId);
    }

    @Override
    public PageResponse<RpcOrderGoods> getOrder(Long userId, Integer pageNum, Integer pageSize, DataStatus dataStatus) {
        return rpcOrderService.getOrder(userId, pageNum, pageSize, dataStatus);
    }

    @Override
    public RpcOrderGoods updateOrder(Order order) {
        return rpcOrderService.updateOrder(order);
    }

    @Override
    public RpcOrderGoods getOrderById(long orderId) {
        return rpcOrderService.getOrderById(orderId);
    }
}
