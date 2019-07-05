package com.uren.catchu.ApiGatewayFunctions.Interfaces;

public interface OnEventListener<T> {

    void onSuccess(T object);
    void onFailure(Exception e);
    void onTaskContinue();

}


