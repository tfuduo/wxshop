package com.dahuntun.wxshop.mock;

import com.dahuntun.api.data.OrderInfo;
import com.dahuntun.api.generate.Order;
import com.dahuntun.api.rpc.RpcOrderService;
import org.apache.dubbo.config.annotation.Service;
import org.mockito.Mock;

@Service(version = "${wxshop.rpcorderservice.version}")
public class MockRpcOrderService implements RpcOrderService {
    @Mock
    private RpcOrderService rpcOrderService;

    @Override
    public Order createOrder(OrderInfo orderInfo, Order order) {
        return rpcOrderService.createOrder(orderInfo, order);
    }
}
