package com.uren.catchu.MainPackage.MainFragments.Feed.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.uren.catchu.MainPackage.MainFragments.Feed.FeedCatchedFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.FeedPublicFragment;


public class FeedPagerAdapter extends FragmentPagerAdapter {

    private int numOfTabs;

    public FeedPagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FeedPublicFragment();
            case 1:
                return new FeedCatchedFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

}