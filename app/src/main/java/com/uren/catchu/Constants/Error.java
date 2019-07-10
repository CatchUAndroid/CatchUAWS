package com.uren.catchu.Constants;

public enum Error {
    NO_NETWORK_CONN("No Network Connection", 0),
    AWS_SERVER_ERR("Amazon Webservis Error", 1);


    private String stringValue;

    Error(String toString, int value) {
        stringValue = toString;
        int intValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}