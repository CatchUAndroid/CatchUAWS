package com.uren.catchu._Libraries.VideoPlay;

import android.app.Activity;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.util.concurrent.Callable;


/**
 * Created by krupenghetiya on 03/02/17.
 */

public class CustomViewHolder extends RecyclerView.ViewHolder {
    private VideoImage videoImage;
    private String imageUrl;
    private String videoUrl;
    private boolean isLooping = true;
    private boolean isPaused = false;


    public CustomViewHolder(View x) {
        super(x);
        videoImage = (VideoImage) x.findViewWithTag("videoImage");
    }

    public void playVideo() {
        this.videoImage.getCustomVideoView().setPaused(false);
        this.videoImage.getCustomVideoView().startVideo();
    }

    public void videoStarted() {
        this.videoImage.getImageView().setVisibility(View.GONE);
    }
    public void showThumb() {
        this.videoImage.getImageView().setVisibility(View.VISIBLE);
    }

    public void initVideoView(String url, Activity _act) {
        this.videoImage.getCustomVideoView().setVisibility(View.VISIBLE);
        Uri uri = Uri.parse(url);
        this.videoImage.getCustomVideoView().setSource(uri);
        this.videoImage.getCustomVideoView().setLooping(isLooping);
        this.videoImage.getCustomVideoView().set_act(_act);
        this.videoImage.getCustomVideoView().setMyFuncIn(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                videoStarted();
                return null;
            }
        });

        this.videoImage.getCustomVideoView().setShowThumb(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                showThumb();
                return null;
            }
        });
    }

    public void setLooping(boolean looping) {
        isLooping = looping;
    }

    public void pauseVideo() {
        this.videoImage.getCustomVideoView().pauseVideo();
        this.videoImage.getCustomVideoView().setPaused(true);
    }

    public void muteVideo() {
        this.videoImage.getCustomVideoView().muteVideo();
    }

    public void unmuteVideo() {
        this.videoImage.getCustomVideoView().unmuteVideo();
    }

    public VideoImage getAah_vi() {
        return videoImage;
    }

    public ImageView getAAH_ImageView() {
        return videoImage.getImageView();
    }

    public String getImageUrl() {
        return imageUrl + "";
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        this.videoImage.getImageView().setVisibility(View.VISIBLE);
        this.videoImage.getCustomVideoView().setVisibility(View.GONE);
    }

    public void setAah_vi(VideoImage aah_vi) {
        this.videoImage = aah_vi;
    }

    public String getVideoUrl() {
        return videoUrl + "";
    }

    public boolean isPlaying() {
        return this.videoImage.getCustomVideoView().isPlaying();
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isLooping() {
        return isLooping;
    }
}