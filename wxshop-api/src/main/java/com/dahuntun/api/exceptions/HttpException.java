package com.dahuntun.api.exceptions;

import javax.servlet.http.HttpServletResponse;

public class HttpException extends RuntimeException {
    private int statusCode;
    private String message;

    public static HttpException forbidden(String message) {
        return new HttpException(HttpServletResponse.SC_FORBIDDEN, message);
    }

    public static HttpException notFound(String message) {
        return new HttpException(HttpServletResponse.SC_NOT_FOUND, message);
    }

    public static HttpException badRequest(String message) {
        return new HttpException(HttpServletResponse.SC_BAD_REQUEST, message);
    }

    public static HttpException gone(String message) {
        return new HttpException(HttpServletResponse.SC_GONE, message);
    }

    private HttpException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
