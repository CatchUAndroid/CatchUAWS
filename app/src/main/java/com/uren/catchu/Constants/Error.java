package com.uren.catchu.Constants;

public enum Error {
    NO_NETWORK_CONN("No Network Connection", 0),
    AWS_SERVER_ERR("Amazon Webservis Error", 1);


    private String stringValue;
    private int intValue;
    Error(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}