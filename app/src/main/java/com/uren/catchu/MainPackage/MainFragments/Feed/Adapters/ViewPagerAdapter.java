package com.uren.catchu.MainPackage.MainFragments.Feed.Adapters;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.MyVideoModel;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostHelper;
import com.uren.catchu.R;
import com.uren.catchu._Libraries.VideoPlay.VideoPlay;

import java.util.ArrayList;
import java.util.List;

import catchu.model.Media;

import static com.uren.catchu.Constants.StringConstants.IMAGE_TYPE;
import static com.uren.catchu.Constants.StringConstants.VIDEO_TYPE;
import static com.uren.catchu.Constants.StringConstants.VIEWPAGER_IMAGE;
import static com.uren.catchu.Constants.StringConstants.VIEWPAGER_VIDEO;

public class ViewPagerAdapter extends PagerAdapter {

    private Activity mActivity;
    private Context mContext;
    private List<Media> attachments;
    private List<Media> orderedAttachments;
    private List<MyVideoModel> videoList = new ArrayList<>();
    private List<String> imageList = new ArrayList<>();
    private int imageCounter;
    private int videoCounter;
    private VideoPlay videoPlay;

    public ViewPagerAdapter(Activity activity, Context context, List<Media> attachments) {
        this.mActivity = activity;
        this.mContext = context;
        this.attachments = attachments;
        this.imageCounter = 0;
        this.videoCounter = 0;

        orderMedia();
        seperateAttachmentsByTypes();
    }

    private void orderMedia() {

        orderedAttachments = new ArrayList<>();

        for (int i = 0; i < attachments.size(); i++) {
            if (attachments.get(i).getType().equals(VIDEO_TYPE)) {
                orderedAttachments.add(attachments.get(i));
            }
        }

        for (int i = 0; i < attachments.size(); i++) {
            if (attachments.get(i).getType().equals(IMAGE_TYPE)) {
                orderedAttachments.add(attachments.get(i));
            }
        }

    }

    private void seperateAttachmentsByTypes() {

        String tempImagePath = "http://res.cloudinary.com/krupen/video/upload/w_300,h_150,c_crop,q_70,so_0/v1491561340/hello_cuwgcb.jpg";

        for (int i = 0; i < orderedAttachments.size(); i++) {

            switch (orderedAttachments.get(i).getType()) {
                case VIDEO_TYPE:
                    String thumbnailPath;
                    if (orderedAttachments.get(i).getThumbnail() != null && !orderedAttachments.get(i).getThumbnail().isEmpty()) {
                        thumbnailPath = orderedAttachments.get(i).getThumbnail();
                    } else {
                        thumbnailPath = tempImagePath;
                    }

                    MyVideoModel myVideoModel = new MyVideoModel(orderedAttachments.get(i).getUrl(), thumbnailPath, "video");
                    videoList.add(myVideoModel);
                    break;
                case IMAGE_TYPE:
                    imageList.add(orderedAttachments.get(i).getUrl());
                    break;
                default:
                    Log.i("Warning ", "Unknown media type detected. Media type :" + orderedAttachments.get(i).getType());
                    break;

            }
        }
    }


    @Override
    public Object instantiateItem(ViewGroup collection, final int position) {

        View itemView;
        final String clickedItemType;

        if (videoCounter < videoList.size()) {
            clickedItemType = VIDEO_TYPE;

            /*Ana sayfadan video oynatma özelliği kaldırıldı*/
            /*
            itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.viewpager_video, collection, false);

            loadVideo(itemView);
            loadImage(itemView, VIEWPAGER_VIDEO);
            videoCounter++;
            */

            itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.viewpager_image, collection, false);

            loadImage(itemView, VIEWPAGER_VIDEO);
            videoCounter++;


        } else if (imageCounter < imageList.size()) {
            clickedItemType = IMAGE_TYPE;
            //sonra imagelar bitene kadar eklenir
            itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.viewpager_image, collection, false);

            loadImage(itemView, VIEWPAGER_IMAGE);
            imageCounter++;

        } else {
            //do nothing
            itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.viewpager_image, collection, false);
        }


        itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //this will log the page number that was click
                PostHelper.ViewPagerItemClicked.startProcess(mActivity, mContext, orderedAttachments.get(position));
                //viewPagerClickCallback.onViewPagerItemClicked(orderedAttachments.get(position));
            }
        });

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

            //load image into imageview
            if (videoList.get(videoCounter).getImage_url() != null && !videoList.get(videoCounter).getImage_url().isEmpty()) {
                loadUrl = videoList.get(videoCounter).getImage_url();
            }

            //Bitmap bm =  getVideoImageUrl(videoList.get(videoCounter).getVideo_url().toString());
            /* Ana sayfadan video oynatma özelliği kaldırıldı
            Glide.with(mContext)
                    .load(loadUrl)
                    .apply(RequestOptions.centerInsideTransform())
                    .into(videoPlay.getImageView());
                    */

            ImageView imgFeedItem = (ImageView) itemView.findViewById(R.id.imgFeedItem);
            ImageView iconPlay = (ImageView) itemView.findViewById(R.id.iconPlay);
            iconPlay.setVisibility(View.VISIBLE);
            final LinearLayout llProgress = (LinearLayout) itemView.findViewById(R.id.llProgress);
            final ProgressBar progressLoading = (ProgressBar) itemView.findViewById(R.id.progressLoading);

            Glide.with(mContext)
                    .load(loadUrl)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressLoading.setVisibility(View.GONE);
                            llProgress.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .apply(RequestOptions.fitCenterTransform())
                    .into(imgFeedItem);


        } else if (TAG.equals(VIEWPAGER_IMAGE)) {

            ImageView imgFeedItem = (ImageView) itemView.findViewById(R.id.imgFeedItem);
            ImageView iconPlay = (ImageView) itemView.findViewById(R.id.iconPlay);
            iconPlay.setVisibility(View.GONE);
            final LinearLayout llProgress = (LinearLayout) itemView.findViewById(R.id.llProgress);
            final ProgressBar progressLoading = (ProgressBar) itemView.findViewById(R.id.progressLoading);


            if (imageList.get(imageCounter) != null && !imageList.get(imageCounter).isEmpty()) {
                loadUrl = imageList.get(imageCounter);
            }

            Glide.with(mContext)
                    .load(loadUrl)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressLoading.setVisibility(View.GONE);
                            llProgress.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .apply(RequestOptions.fitCenterTransform())
                    .into(imgFeedItem);


        } else {
            //do nothing
        }

    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
        // NT: kaydırma esnasında view'lar kayboldugu icin kapattım
        //collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return orderedAttachments.size();
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


