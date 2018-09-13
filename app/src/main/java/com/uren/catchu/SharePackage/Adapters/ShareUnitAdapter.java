package com.uren.catchu.SharePackage.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class ShareUnitAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> mFragmentList = new ArrayList<>();

    public ShareUnitAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment){
        mFragmentList.add(fragment);
    }

    public void updateFragment(int position, Fragment fragment){
        mFragmentList.set(position, fragment);
    }

  /*  @Override
    public int getItemPosition(Object object) {
        // POSITION_NONE makes it possible to reload the PagerAdapter
        return POSITION_NONE;
    }*/
}
