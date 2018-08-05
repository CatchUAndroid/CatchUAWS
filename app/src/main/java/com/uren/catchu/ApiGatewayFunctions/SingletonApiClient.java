package com.uren.catchu.ApiGatewayFunctions;

import android.util.Log;

import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;

import catchu.CatchUMobileAPIClient;

public class SingletonApiClient {

    // static variable myInstance of type SingletonApiClient
    private static SingletonApiClient myInstance = null;

    public CatchUMobileAPIClient client;

    // private constructor restricted to this class itself
    public SingletonApiClient()
    {
        // create a client
        
        ApiClientFactory factory = new ApiClientFactory();
        client = factory.build(CatchUMobileAPIClient.class);

    }

    // static method to create instance of SingletonApiClient class
    public static SingletonApiClient getInstance()
    {
        if (myInstance == null)
            myInstance = new SingletonApiClient();

        return myInstance;
    }





}
