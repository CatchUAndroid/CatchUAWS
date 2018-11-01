package com.uren.catchu._Libraries.VideoPlay;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.uren.catchu.R;
import java.util.concurrent.Callable;
import butterknife.ButterKnife;

public class VideoPlay {

    View itemView;
    VideoImage aah_vi;
    TextView tv;
    ImageView img_vol;
    ImageView img_playback;

    Activity act;
    String imageUrl;
    String videoUrl;
    boolean isLooping = true;
    boolean isPaused = false;
    boolean isMuted;

    public VideoPlay(View itemView, String videoUrl, Activity act) {
        this.itemView = itemView;
        this.videoUrl= videoUrl;
        this.act=act;
        initItems();
        initVideoView();
    }

    private void initItems() {

        this.aah_vi = (VideoImage) itemView.findViewById(R.id.videoImage);
        this.tv = ButterKnife.findById(itemView, R.id.tv);
        this.img_vol = ButterKnife.findById(itemView, R.id.img_vol);
        this.img_playback = ButterKnife.findById(itemView, R.id.img_playback);

        setLooping(true);

        img_playback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying()) {
                    pauseVideo();
                    setPaused(true);
                } else {
                    playVideo();
                    setPaused(false);
                }
            }
        });

        img_vol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMuted) {
                    unmuteVideo();
                    img_vol.setImageResource(R.drawable.ic_unmute);
                } else {
                    muteVideo();
                    img_vol.setImageResource(R.drawable.ic_mute);
                }
                isMuted = !isMuted;
            }
        });

        img_vol.setVisibility(View.VISIBLE);
        img_playback.setVisibility(View.VISIBLE);

    }

    public void initVideoView() {
        this.aah_vi.getCustomVideoView().setVisibility(View.VISIBLE);
        Uri uri = Uri.parse(videoUrl);
        this.aah_vi.getCustomVideoView().setSource(uri);
        this.aah_vi.getCustomVideoView().setLooping(isLooping);
        this.aah_vi.getCustomVideoView().set_act(act);
        this.aah_vi.getCustomVideoView().setMyFuncIn(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                videoStarted();
                return null;
            }
        });

        this.aah_vi.getCustomVideoView().setShowThumb(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                showThumb();
                return null;
            }
        });
    }

    public void playVideo() {
        this.aah_vi.getCustomVideoView().setPaused(false);
        this.aah_vi.getCustomVideoView().startVideo();


        img_playback.setImageResource(R.drawable.ic_pause);
        if (isMuted) {
            muteVideo();
            img_vol.setImageResource(R.drawable.ic_mute);
        } else {
            unmuteVideo();
            img_vol.setImageResource(R.drawable.ic_unmute);
        }


    }

    public void videoStarted() {
        this.aah_vi.getImageView().setVisibility(View.GONE);
    }
    public void showThumb() {
        this.aah_vi.getImageView().setVisibility(View.VISIBLE);
    }



    public void setLooping(boolean looping) {
        isLooping = looping;
    }

    public void pauseVideo() {
        this.aah_vi.getCustomVideoView().pauseVideo();
        this.aah_vi.getCustomVideoView().setPaused(true);

        img_playback.setImageResource(R.drawable.ic_play);
    }

    public void muteVideo() {
        this.aah_vi.getCustomVideoView().muteVideo();
    }

    public void unmuteVideo() {
        this.aah_vi.getCustomVideoView().unmuteVideo();
    }

    public VideoImage getAah_vi() {
        return aah_vi;
    }

    public ImageView getAAH_ImageView() {
        return aah_vi.getImageView();
    }

    public String getImageUrl() {
        return imageUrl + "";
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        this.aah_vi.getImageView().setVisibility(View.VISIBLE);
        this.aah_vi.getCustomVideoView().setVisibility(View.GONE);
    }

    public void setAah_vi(VideoImage aah_vi) {
        this.aah_vi = aah_vi;
    }

    public String getVideoUrl() {
        return videoUrl + "";
    }

    public boolean isPlaying() {
        return this.aah_vi.getCustomVideoView().isPlaying();
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

    public ImageView getImageView() {
        return aah_vi.getImageView();
    }





}