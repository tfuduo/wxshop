package com.dahuntun.api.rpc;

import com.dahuntun.api.data.OrderInfo;
import com.dahuntun.api.generate.Order;
import org.springframework.stereotype.Component;

public interface RpcOrderService {
    Order createOrder(OrderInfo orderInfo, Order order);
}
