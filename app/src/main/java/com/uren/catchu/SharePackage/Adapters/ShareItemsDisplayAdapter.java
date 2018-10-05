package com.uren.catchu.SharePackage.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.Models.ImageShareItemBox;
import com.uren.catchu.SharePackage.Models.VideoShareItemBox;
import com.uren.catchu.Singleton.Share.ShareItems;
import com.uren.catchu.VideoPlay.VideoPlay;

import static com.uren.catchu.Constants.StringConstants.VIEWPAGER_IMAGE;
import static com.uren.catchu.Constants.StringConstants.VIEWPAGER_VIDEO;

public class ShareItemsDisplayAdapter extends PagerAdapter {
    private Activity mActivity;
    private Context mContext;
    private int imageCounter;
    private int videoCounter;
    private VideoPlay videoPlay;

    public ShareItemsDisplayAdapter(Activity activity, Context context) {
        this.mActivity = activity;
        this.mContext = context;
        this.imageCounter = 0;
        this.videoCounter = 0;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {

        View itemView = null;

        if (videoCounter < ShareItems.getInstance().getVideoShareItemBoxes().size()) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.viewpager_video, collection, false);

            VideoShareItemBox videoShareItemBox = ShareItems.getInstance().getVideoShareItemBoxes().get(videoCounter);
            loadVideo(itemView, videoShareItemBox);
            loadImage(itemView, VIEWPAGER_VIDEO, videoShareItemBox);
            videoCounter++;
        } else if (imageCounter < ShareItems.getInstance().getImageShareItemBoxes().size()) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.viewpager_image, collection, false);

            ImageShareItemBox imageShareItemBox = ShareItems.getInstance().getImageShareItemBoxes().get(imageCounter);
            loadImage(itemView, VIEWPAGER_IMAGE, imageShareItemBox);
            imageCounter++;
        }

        collection.addView(itemView);
        return itemView;
    }

    private void loadVideo(View itemView, VideoShareItemBox videoShareItemBox) {
        String videoUrl = "";
        if (videoShareItemBox.getVideoSelectUtil().getVideoRealPath() != null && !videoShareItemBox.getVideoSelectUtil().getVideoRealPath().isEmpty()) {
            videoUrl = videoShareItemBox.getVideoSelectUtil().getVideoRealPath();
        }
        videoPlay = new VideoPlay(itemView, videoUrl, mActivity);
    }

    private void loadImage(View itemView, String TAG, Object object) {
        if (TAG.equals(VIEWPAGER_VIDEO)) {

            VideoShareItemBox videoShareItemBox = (VideoShareItemBox) object;
            Bitmap videoBitmap = videoShareItemBox.getVideoSelectUtil().getVideoBitmap();

            if (videoBitmap != null) {
                Glide.with(mContext)
                        .load(videoBitmap)
                        .apply(RequestOptions.centerInsideTransform())
                        .into(videoPlay.getImageView());
            }

        } else if (TAG.equals(VIEWPAGER_IMAGE)) {

            ImageView imgFeedItem = itemView.findViewById(R.id.imgFeedItem);
            ImageShareItemBox imageShareItemBox = (ImageShareItemBox) object;
            Bitmap imageBitmap;

            if(imageShareItemBox.getPhotoSelectUtil().getScreeanShotBitmap() != null)
                imageBitmap = imageShareItemBox.getPhotoSelectUtil().getScreeanShotBitmap();
            else
                imageBitmap = imageShareItemBox.getPhotoSelectUtil().getBitmap();

            if (imageBitmap != null)
                Glide.with(mContext)
                        .load(imageBitmap)
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
        return ShareItems.getInstance().getTotalMediaCount();
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

