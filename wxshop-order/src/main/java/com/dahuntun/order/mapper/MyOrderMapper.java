package com.dahuntun.order.mapper;

import com.dahuntun.api.data.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MyOrderMapper {
    void inertOrders(OrderInfo orderInfo);
}
