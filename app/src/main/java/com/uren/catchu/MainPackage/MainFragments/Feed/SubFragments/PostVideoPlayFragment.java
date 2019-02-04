package com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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
public class PostVideoPlayFragment extends BaseFragment {


    View mView;

    private VideoPlay videoPlay;

    @BindView(R.id.progressLoading)
    ProgressBar progressLoading;
    @BindView(R.id.llProgress)
    LinearLayout llProgress;
    @BindView(R.id.root_view)
    LinearLayout root_view;

    Media media;

    public PostVideoPlayFragment(Media media) {
        this.media = media;
    }

    @Override
    public void onStart() {
        getActivity().findViewById(R.id.tabMainLayout).setVisibility(View.GONE);
        ((NextActivity) getActivity()).ANIMATION_TAG = null;
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_post_video_play, container, false);
        ButterKnife.bind(this, mView);
        setVideo();
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void setVideo() {

        MyVideoModel myVideoModel = new MyVideoModel(media.getUrl(), media.getThumbnail(), "video");

        String videoUrl = "";

        if (myVideoModel.getVideo_url() != null && !myVideoModel.getVideo_url().isEmpty()) {
            videoUrl = myVideoModel.getVideo_url();
        }

        videoPlay = new VideoPlay(root_view, videoUrl, (Activity) getContext());
        videoPlay.playVideo();
        videoPlay.videoStarted();

        Glide.with(this)
                .load(media.getThumbnail())
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
                .into(videoPlay.getImageView());
    }
}
