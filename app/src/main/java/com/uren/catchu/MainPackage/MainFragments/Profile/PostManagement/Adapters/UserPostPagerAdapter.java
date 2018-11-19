package com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.UserPostGridViewFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.UserPostListViewFragment;


public class UserPostPagerAdapter extends FragmentStatePagerAdapter {

    private int numOfTabs;
    private String catchType;

    public UserPostPagerAdapter(FragmentManager fm, int numOfTabs, String catchType) {
        super(fm);
        this.numOfTabs = numOfTabs;
        this.catchType = catchType;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return UserPostGridViewFragment.newInstance(catchType);
            case 1:
                return UserPostListViewFragment.newInstance(catchType);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

}