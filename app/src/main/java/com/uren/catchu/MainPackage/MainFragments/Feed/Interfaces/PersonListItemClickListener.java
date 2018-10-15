package com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces;

import android.view.View;

import catchu.model.User;

public interface PersonListItemClickListener {

    void onClick(View view, User user, int clickedPosition);
}