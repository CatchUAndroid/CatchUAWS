package com.uren.catchu.SharePackage.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.R;

import java.util.List;

public class ShareItemsDisplayAdapter extends PagerAdapter{

    private Context mContext;
    private List<Uri> imageList;
    private List<Uri> videoList;
    private List<Bitmap> textBitmapList;
    private int imageCounter;
    private int videoCounter;
    private int textCounter;
    MediaPlayer mediaPlayer;
    ImageView playVideoImgv;

    public ShareItemsDisplayAdapter(Context context, List<Bitmap> textBitmapList, List<Uri> imageList, List<Uri> videoList) {
        this.mContext = context;
        this.imageList = imageList;
        this.videoList = videoList;
        this.textBitmapList = textBitmapList;
        this.imageCounter = 0;
        this.videoCounter = 0;
        this.textCounter = 0;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {

        View itemView;

        if(textCounter < textBitmapList.size()){
            itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.view_green, collection, false);
            ImageView imgFeedItem = (ImageView) itemView.findViewById(R.id.imgFeedItem);
            collection.addView(itemView);

            Glide.with(mContext)
                    .load(textBitmapList.get(textCounter))
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgFeedItem);

            textCounter++;
        }

        if (imageCounter < imageList.size()) {
            itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.view_green, collection, false);
            ImageView imgFeedItem = (ImageView) itemView.findViewById(R.id.imgFeedItem);
            collection.addView(itemView);

            Glide.with(mContext)
                    .load(imageList.get(imageCounter))
                    .apply(RequestOptions.centerInsideTransform())
                    .into(imgFeedItem);

            imageCounter++;
        }

        if (videoCounter < videoList.size()) {
            itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.view_blue, collection, false);
            VideoView videoFeedItem = (VideoView) itemView.findViewById(R.id.videoFeedItem);
            collection.addView(itemView);

            playVideoImgv = itemView.findViewById(R.id.playVideoImgv);
            videoFeedItem.setVideoURI(videoList.get(videoCounter));
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
