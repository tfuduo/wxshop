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

    public static DataStatus fromStatus(String name) {
        try {
            if (name == null) {
                return null;
            }
            return DataStatus.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
