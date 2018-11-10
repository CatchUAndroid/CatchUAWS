package com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces;

import android.view.View;

import catchu.model.User;

public interface ListItemClickListener {

    void onClick(View view, User user, int clickedPosition);
}