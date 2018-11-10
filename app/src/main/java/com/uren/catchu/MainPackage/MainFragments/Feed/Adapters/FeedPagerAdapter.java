package com.uren.catchu.MainPackage.MainFragments.Feed.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FeedPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> fragList = new ArrayList<>();
    List<String> titleList = new ArrayList<>();

    public FeedPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFrag(Fragment f, String title) {
        fragList.add(f);
        titleList.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return fragList.get(position);
    }

    @Override
    public int getCount() {
        return fragList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }
}