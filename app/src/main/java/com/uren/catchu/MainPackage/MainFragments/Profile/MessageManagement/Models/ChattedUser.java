package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models;

import java.io.Serializable;

import catchu.model.User;

public class ChattedUser implements Serializable{

    User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
