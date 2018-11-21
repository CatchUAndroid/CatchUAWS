package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople.Interfaces;

import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Models.Contact;

import catchu.model.User;

public interface ContactFriendSelectCallback {
    void contactSelected(Contact contact);
    void appUserSelected(User user);
}
