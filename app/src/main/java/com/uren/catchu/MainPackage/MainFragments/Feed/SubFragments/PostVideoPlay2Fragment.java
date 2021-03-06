package com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.MyVideoModel;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu._Libraries.VideoPlay.VideoPlay;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.Media;

@SuppressLint("ValidFragment")
public class PostVideoPlay2Fragment extends BaseFragment {

    View mView;

    @BindView(R.id.progressLoading)
    ProgressBar progressLoading;
    @BindView(R.id.llProgress)
    LinearLayout llProgress;
    @BindView(R.id.root_view)
    LinearLayout root_view;
    @BindView(R.id.videoView)
    VideoView videoView;

    Media media;

    public PostVideoPlay2Fragment(Media media) {
        this.media = media;
    }

    @Override
    public void onStart() {
        getActivity().findViewById(R.id.tabMainLayout).setVisibility(View.GONE);
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_post_video_play2, container, false);
        ButterKnife.bind(this, mView);
        setVideo();
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void setVideo() {

        MediaController mediaController = new MediaController(getContext());
        mediaController.setAnchorView(videoView);
        Uri video = Uri.parse(media.getUrl());
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(video);
        videoView.start();


    }
}
