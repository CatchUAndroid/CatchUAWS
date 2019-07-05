package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople.Models;

import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Models.Contact;

import catchu.model.User;

public class ContactFriendModel {

    Contact contact;
    User user;

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
