package com.uren.catchu.MainPackage.MainFragments.Feed.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.uren.catchu.MainPackage.MainFragments.Feed.FeedCaughtFragment;
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
                return new FeedCaughtFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

}