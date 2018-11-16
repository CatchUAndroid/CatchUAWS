package com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.UserPostGridViewFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.UserPostListViewFragment;


public class ProfilePostPagerAdapter extends FragmentPagerAdapter {

    private int numOfTabs;
    private String catchType;

    public ProfilePostPagerAdapter(FragmentManager fm, int numOfTabs, String catchType) {
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