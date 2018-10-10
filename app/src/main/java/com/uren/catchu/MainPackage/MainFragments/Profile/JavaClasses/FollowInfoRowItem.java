package com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses;

import android.support.v7.widget.RecyclerView;

import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters.FollowAdapter;

import java.io.Serializable;

import catchu.model.FollowInfoResultArrayItem;

public class FollowInfoRowItem implements Serializable {

    FollowInfoResultArrayItem resultArrayItem;
    int clickedPosition;
    RecyclerView.Adapter adapter;

    public FollowInfoRowItem(FollowInfoResultArrayItem resultArrayItem) {
        this.resultArrayItem = resultArrayItem;
    }

    public FollowInfoResultArrayItem getResultArrayItem() {
        return resultArrayItem;
    }

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
