package com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.UserPostGridViewFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.UserPostListViewFragment;


public class UserPostPagerAdapter extends FragmentStatePagerAdapter {

    private int numOfTabs;
    private String catchType, targetUid;

    public UserPostPagerAdapter(FragmentManager fm, int numOfTabs, String catchType, String targetUid) {
        super(fm);
        this.numOfTabs = numOfTabs;
        this.catchType = catchType;
        this.targetUid = targetUid;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return UserPostGridViewFragment.newInstance(catchType, targetUid);
            case 1:
                return UserPostListViewFragment.newInstance(catchType, targetUid);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

}