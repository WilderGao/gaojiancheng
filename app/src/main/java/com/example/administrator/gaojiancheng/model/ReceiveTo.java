package com.example.administrator.gaojiancheng.model;

/**
 * @Author:高键城
 * @time：
 * @Discription：
 */
public class ReceiveTo<T> {
    private int method;
    private T requestBody;

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public T getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(T requestBody) {
        this.requestBody = requestBody;
    }

    @Override
    public String toString() {
        return "ReceiveTo{" +
                "method=" + method +
                ", requestBody=" + requestBody +
                '}';
    }
}
