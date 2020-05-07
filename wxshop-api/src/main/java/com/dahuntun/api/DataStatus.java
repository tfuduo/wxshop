package com.dahuntun.api;

public enum  DataStatus {
    OK,
    DELETED,

    //订单状态
    PENDING,
    PAID,
    DELIVERED,
    RECEIVED;

    public String getName() {
        return name().toLowerCase();
    }
}
