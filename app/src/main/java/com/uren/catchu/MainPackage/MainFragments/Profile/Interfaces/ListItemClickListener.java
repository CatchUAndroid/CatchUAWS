package com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces;

import android.view.View;

import catchu.model.FollowInfoResultArrayItem;

public interface ListItemClickListener {

    void onClick(View view, FollowInfoResultArrayItem rowItem, int clickedPosition);
}