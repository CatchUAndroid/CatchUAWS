package com.uren.catchu.SharePackage.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.MediaSerializable;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.MyVideoModel;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.ImageFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.VideoFragment;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.Share.ShareItems;
import com.uren.catchu.VideoPlay.VideoPlay;

import java.util.ArrayList;
import java.util.List;

import catchu.model.Media;

import static com.uren.catchu.Constants.StringConstants.IMAGE_TYPE;
import static com.uren.catchu.Constants.StringConstants.VIDEO_TYPE;
import static com.uren.catchu.Constants.StringConstants.VIEWPAGER_IMAGE;
import static com.uren.catchu.Constants.StringConstants.VIEWPAGER_VIDEO;

public class ShareItemsDisplayAdapter extends PagerAdapter {
    private Activity mActivity;
    private Context mContext;
    private List<Media> attachments;
    private List<MyVideoModel> videoList = new ArrayList<>();
    private List<String> imageList = new ArrayList<>();
    private int imageCounter;
    private int videoCounter;
    private VideoPlay videoPlay;


    public ShareItemsDisplayAdapter(Activity activity, Context context, List<Media> attachments) {
        this.mActivity = activity;
        this.mContext = context;
        this.attachments = attachments;
        this.imageCounter = 0;
        this.videoCounter = 0;
        seperateAttachmentsByTypes();
    }

    private void seperateAttachmentsByTypes() {

        for (int i = 0; i < attachments.size(); i++) {

            switch (attachments.get(i).getType()) {
                case VIDEO_TYPE:
                    MyVideoModel myVideoModel = new MyVideoModel(attachments.get(i).getUrl(), attachments.get(i).getThumbnail(), "video");
                    videoList.add(myVideoModel);
                    break;
                case IMAGE_TYPE:
                    imageList.add(attachments.get(i).getUrl());
                    break;
                default:
                    DialogBoxUtil.showErrorDialog(mContext, "ShareItemsDisplayAdapter:Unknown media item!", new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {
                        }
                    });
                    break;
            }
        }
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {

        View itemView;
        if (videoCounter < videoList.size()) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.viewpager_video, collection, false);

            loadVideo(itemView);
            loadImage(itemView, VIEWPAGER_VIDEO);
            videoCounter++;
        } else if (imageCounter < imageList.size()) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.view_red, collection, false);

            loadImage(itemView, VIEWPAGER_IMAGE);
            imageCounter++;
        } else {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.view_red, collection, false);
        }

        collection.addView(itemView);
        return itemView;

    }

    private void loadVideo(View itemView) {
        String videoUrl = "";
        if (videoList.get(videoCounter).getVideo_url() != null && !videoList.get(videoCounter).getVideo_url().isEmpty()) {
            videoUrl = videoList.get(videoCounter).getVideo_url();
        }

        videoPlay = new VideoPlay(itemView, videoUrl, mActivity);
    }

    private void loadImage(View itemView, String TAG) {

        String loadUrl = "";
        if (TAG.equals(VIEWPAGER_VIDEO)) {

            if (videoList.get(videoCounter).getImage_url() != null && !videoList.get(videoCounter).getImage_url().isEmpty()) {
                loadUrl = videoList.get(videoCounter).getImage_url();
            }

            Glide.with(mContext)
                    .load(loadUrl)
                    .apply(RequestOptions.centerInsideTransform())
                    .into(videoPlay.getImageView());

        } else if (TAG.equals(VIEWPAGER_IMAGE)) {

            ImageView imgFeedItem = itemView.findViewById(R.id.imgFeedItem);

            if (imageList.get(imageCounter) != null && !imageList.get(imageCounter).isEmpty()) {
                loadUrl = imageList.get(imageCounter);
            }

            Glide.with(mContext)
                    .load(loadUrl)
                    .apply(RequestOptions.fitCenterTransform())
                    .into(imgFeedItem);
        }
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return attachments.size();
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

