package com.uren.catchu.UgurDeneme;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.uren.catchu.R;

import java.util.ArrayList;
import java.util.List;

import eu.long1.spacetablayout.SpaceTabLayout;

public class Main2Activity extends AppCompatActivity {

    SpaceTabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //add the fragments you want to display in a List
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new FragmentXXX());
        fragmentList.add(new FragmentXXX());
        fragmentList.add(new FragmentXXX());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (SpaceTabLayout) findViewById(R.id.spaceTabLayout);

        //we need the savedInstanceState to get the position
        tabLayout.initialize(viewPager, getSupportFragmentManager(),
                fragmentList, savedInstanceState);
    }

    //we need the outState to save the position
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        tabLayout.saveState(outState);
        super.onSaveInstanceState(outState);
    }
}
