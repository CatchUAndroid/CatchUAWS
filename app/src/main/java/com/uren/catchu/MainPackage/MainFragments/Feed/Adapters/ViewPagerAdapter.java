package com.uren.catchu.MainPackage.MainFragments.Feed.Adapters;


import android.content.Context;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.ModelObject;
import com.uren.catchu.R;

import java.util.List;
import java.util.Random;

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
        //ModelObject modelObject = ModelObject.values()[position];
        //LayoutInflater inflater = LayoutInflater.from(mContext);
        //ViewGroup layout = (ViewGroup) inflater.inflate(modelObject.getLayoutResId(), collection, false);

        View itemView;

        if (videoCounter < videoList.size()) {
            //önce videolar bitene kadar eklenir

            itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.view_blue, collection, false);
            VideoView videoFeedItem = (VideoView) itemView.findViewById(R.id.videoFeedItem);
            collection.addView(itemView);

            playVideoImgv = itemView.findViewById(R.id.playVideoImgv);
            videoFeedItem.setVideoURI(Uri.parse(videoList.get(videoCounter)));
            videoFeedItem.requestFocus();

            videoFeedItem.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer = mp;
                    mediaPlayer.start();
                    playVideoImgv.setVisibility(View.GONE);
                }
            });

            videoFeedItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (playVideoImgv.getVisibility() == View.GONE) {
                        playVideoImgv.setVisibility(View.VISIBLE);
                        mediaPlayer.pause();
                    } else {
                        playVideoImgv.setVisibility(View.GONE);
                        mediaPlayer.start();
                    }
                }
            });

            videoCounter++;

        } else if (imageCounter < imageList.size()) {
            //sonra imagelar bitene kadar eklenir

            itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.view_green, collection, false);
            ImageView imgFeedItem = (ImageView) itemView.findViewById(R.id.imgFeedItem);
            collection.addView(itemView);

            Glide.with(mContext)
                    .load(imageList.get(imageCounter))
                    .apply(RequestOptions.centerInsideTransform())
                    .into(imgFeedItem);

            imageCounter++;

        } else {
            //do nothing
            itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.view_red, collection, false);
        }

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        // NT: kaydırma esnasında view'lar kayboldugu icin kapattım
        //collection.removeView((View) view);
    }

    @Override
    public int getCount() {
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


