package com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses;

import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters.FollowAdapter;

import java.io.Serializable;

import catchu.model.FollowInfoResultArrayItem;

public class FollowInfoRowItem implements Serializable {

    FollowInfoResultArrayItem resultArrayItem;
    FollowAdapter followAdapter;
    int clickedPosition;

    public FollowInfoRowItem(FollowInfoResultArrayItem resultArrayItem) {
        this.resultArrayItem = resultArrayItem;
    }

    public FollowInfoResultArrayItem getResultArrayItem() {
        return resultArrayItem;
    }

    public void setResultArrayItem(FollowInfoResultArrayItem resultArrayItem) {
        this.resultArrayItem = resultArrayItem;
    }

    public FollowAdapter getFollowAdapter() {
        return followAdapter;
    }

    public void setFollowAdapter(FollowAdapter followAdapter) {
        this.followAdapter = followAdapter;
    }
    public int getClickedPosition() {
        return clickedPosition;
    }

    public void setClickedPosition(int clickedPosition) {
        this.clickedPosition = clickedPosition;
    }
}
