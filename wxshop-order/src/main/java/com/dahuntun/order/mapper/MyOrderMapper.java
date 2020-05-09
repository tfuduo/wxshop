package com.dahuntun.order.mapper;

import com.dahuntun.api.data.GoodsInfo;
import com.dahuntun.api.data.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MyOrderMapper {
    void inertOrders(OrderInfo orderInfo);

    List<GoodsInfo> getGoodsInfoOfOrder(long orderId);
}
