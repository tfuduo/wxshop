package com.dahuntun.order.service;

import com.dahuntun.api.DataStatus;
import com.dahuntun.api.data.OrderInfo;
import com.dahuntun.api.generate.Order;
import com.dahuntun.api.rpc.RpcOrderService;
import com.dahuntun.order.generate.OrderMapper;
import com.dahuntun.order.mapper.MyOrderMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.function.BooleanSupplier;

@Service(version = "${wxshop.rpcorderservice.version}")
public class RpcRpcOrderServiceImpl implements RpcOrderService {
    private OrderMapper orderMapper;

    private MyOrderMapper myOrderMapper;

    @Autowired
    public RpcRpcOrderServiceImpl(OrderMapper orderMapper, MyOrderMapper myOrderMapper) {
        this.orderMapper = orderMapper;
        this.myOrderMapper = myOrderMapper;
    }

    @Override
    public Order createOrder(OrderInfo orderInfo, Order order) {
        insertOrder(order);
        orderInfo.setOrderId(order.getId());
        myOrderMapper.inertOrders(orderInfo);
        return order;
    }

    private void insertOrder(Order order) {
        order.setStatus(DataStatus.PENDING.getName());

        verify(() -> order.getUserId() == null, "userId不能为空");
        verify(() -> order.getTotalPrice() == null || order.getTotalPrice().doubleValue() < 0, "totalPrice非法！");
        verify(() -> order.getAddress() == null, "address不能为空！");

        order.setExpressCompany(null);
        order.setExpressId(null);
        order.setCreatedAt(new Date());
        order.setUpdatedAt(new Date());

        orderMapper.insert(order);
    }

    private void verify(BooleanSupplier supplier, String message) {
        if (supplier.getAsBoolean()) {
            throw new IllegalArgumentException(message);
        }
    }
}
