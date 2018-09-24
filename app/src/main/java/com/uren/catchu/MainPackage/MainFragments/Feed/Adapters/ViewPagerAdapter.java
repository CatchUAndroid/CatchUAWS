package com.uren.catchu.MainPackage.MainFragments.Feed.Adapters;


import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.MediaSerializable;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.ImageFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.VideoFragment;
import com.uren.catchu.R;

import java.util.List;

import catchu.model.Media;

import static com.uren.catchu.Constants.StringConstants.IMAGE_TYPE;
import static com.uren.catchu.Constants.StringConstants.VIDEO_TYPE;

public class ViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<String> imageList;
    private List<String> videoList;
    private int imageCounter;
    private int videoCounter;
    MediaPlayer mediaPlayer;
    ImageView playVideoImgv;
    int mediaLen = 0;

    public ViewPagerAdapter(Context context, List<String> imageList, List<String> videoList) {
        this.mContext = context;
        this.imageList = imageList;
        this.videoList = videoList;
        this.imageCounter = 0;
        this.videoCounter = 0;
    }


    @Override
    public Object instantiateItem(ViewGroup collection, int position) {

        View itemView = null;
        PlayerView playerView;
        SimpleExoPlayer player;

        if (videoCounter < videoList.size()) {

            itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.viewpager_video, collection, false);
            playerView = itemView.findViewById(R.id.playerView);
            collection.addView(itemView);
            player = ExoPlayerFactory.newSimpleInstance(mContext, new DefaultTrackSelector());
            playerView.setPlayer(player);
            DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(mContext,
                    Util.getUserAgent(mContext,"feed-demo"));
            ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(defaultDataSourceFactory)
                    .createMediaSource(Uri.parse(videoList.get(videoCounter)));
            player.prepare(mediaSource);
            player.setPlayWhenReady(false);
            player.seekTo(3);


            videoCounter++;
        } else if (imageCounter < imageList.size()) {
            //sonra imagelar bitene kadar eklenir
            itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.view_red, collection, false);

            ImageView imgFeedItem = (ImageView) itemView.findViewById(R.id.imgFeedItem);

            collection.addView(itemView);

            Glide.with(mContext)
                    .load(imageList.get(imageCounter))
                    .apply(RequestOptions.centerCropTransform())
                    .into(imgFeedItem);
            imageCounter++;
        } else {
            //do nothing
            itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.view_red, collection, false);
        }
        //collection.addView(layout);


        return itemView;

    }


    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
        // NT: kaydırma esnasında view'lar kayboldugu icin kapattım
        //collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        //return ModelObject.values().length;
        return imageList.size() + videoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        String pageTitle = "Title";
        return pageTitle;
    }


}


