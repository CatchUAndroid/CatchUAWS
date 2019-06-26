package com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses;

/*import androidx.recyclerview.widget.RecyclerView;*/

import java.io.Serializable;

import androidx.recyclerview.widget.RecyclerView;
import catchu.model.User;

public class UserInfoListItem {

    User user;
    int clickedPosition;
    RecyclerView.Adapter adapter;

    public UserInfoListItem(User user) {
        this.user = user;
    }

    public User getUser() {return user; }

    public void setUser(User user) { this.user = user; }

    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    public int getClickedPosition() {
        return clickedPosition;
    }

    public void setClickedPosition(int clickedPosition) {
        this.clickedPosition = clickedPosition;
    }
}
