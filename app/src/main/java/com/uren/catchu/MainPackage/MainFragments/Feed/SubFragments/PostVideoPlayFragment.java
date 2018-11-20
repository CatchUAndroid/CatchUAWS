package com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.MyVideoModel;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostItem;
import com.uren.catchu.MainPackage.MainFragments.Share.Adapters.ColorPaletteAdapter;
import com.uren.catchu.MainPackage.MainFragments.Share.Interfaces.BrushCompleteCallback;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.ModelViews.PaintView;
import com.uren.catchu.R;
import com.uren.catchu._Libraries.VideoPlay.VideoPlay;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.Media;

public class PostVideoPlayFragment extends BaseFragment {


    View mView;

    private VideoPlay videoPlay;

    @BindView(R.id.progressLoading)
    ProgressBar progressLoading;
    @BindView(R.id.llProgress)
    LinearLayout llProgress;
    @BindView(R.id.root_view)
    LinearLayout root_view;

    public PostVideoPlayFragment() {

    }

    @Override
    public void onStart() {
        NextActivity.bottomTabLayout.setVisibility(View.GONE);
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

        Media media = PostItem.getInstance().getMedia();

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
