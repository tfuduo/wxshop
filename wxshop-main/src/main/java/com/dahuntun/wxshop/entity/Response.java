package com.dahuntun.wxshop.entity;

public class Response<T> {
    private String message;
    private T data;

    public static <T> Response<T> of(String message, T data) {
        return new Response<T>(message, data);
    }

    public static <T> Response<T> of(T data) {
        return new Response<T>(null, data);
    }

    private Response() {
    }

    private Response(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public T getData() {
        return data;
    }

    //巨坑无比 一定要有Getter
    public String getMessage() {
        return message;
    }
}
