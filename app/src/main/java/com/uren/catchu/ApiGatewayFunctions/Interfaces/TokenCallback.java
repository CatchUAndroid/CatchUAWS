package com.uren.catchu.ApiGatewayFunctions.Interfaces;

public interface TokenCallback{

    void onTokenTaken(String token);
    void onTokenFail(String message);
}


