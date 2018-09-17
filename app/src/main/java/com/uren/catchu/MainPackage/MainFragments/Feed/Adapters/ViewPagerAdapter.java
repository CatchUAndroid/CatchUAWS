package com.uren.catchu.MainPackage.MainFragments.Feed.Adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.MediaSerializable;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.ImageFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.VideoFragment;

import java.util.List;

import catchu.model.Media;

import static com.uren.catchu.Constants.StringConstants.IMAGE_TYPE;
import static com.uren.catchu.Constants.StringConstants.VIDEO_TYPE;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<Media> mediaList;
    private int bindedItems;

    public ViewPagerAdapter(FragmentManager fragmentManager, List<Media> mediaList) {
        super(fragmentManager);

        this.mediaList= mediaList;
        this.bindedItems=0;
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return mediaList.size();
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {

        MediaSerializable mediaSerializable = new MediaSerializable(mediaList.get(position));

        if(mediaList.get(position).getType().equals(VIDEO_TYPE)){
            return VideoFragment.newInstance(mediaSerializable);
        }else if(mediaList.get(position).getType().equals(IMAGE_TYPE)){
            return ImageFragment.newInstance(mediaSerializable);
        }else{
            Log.e("mediaTypeError ", "unknown_media_type");
        }

        return null;
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }

}


