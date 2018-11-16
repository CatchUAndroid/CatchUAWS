package com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.UserPostGridViewFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.UserPostListViewFragment;


public class ProfilePostPagerAdapter extends FragmentPagerAdapter {

    private int numOfTabs;

    public ProfilePostPagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new UserPostGridViewFragment();
            case 1:
                return new UserPostListViewFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

}