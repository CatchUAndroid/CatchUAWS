package com.uren.catchu.MainPackage.MainFragments.Feed.SubActivities;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.MyVideoModel;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostItem;
import com.uren.catchu.R;
import com.uren.catchu._Libraries.VideoPlay.VideoPlay;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.Media;

public class VideoActivity extends AppCompatActivity {

    private VideoPlay videoPlay;

    @BindView(R.id.progressLoading)
    ProgressBar progressLoading;

    @BindView(R.id.llProgress)
    LinearLayout llProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);

        setVideo();

    }

    private void setVideo() {

        Media media = PostItem.getInstance().getMedia();

        MyVideoModel myVideoModel = new MyVideoModel(media.getUrl(), media.getThumbnail(), "video");

        String videoUrl = "";

        if (myVideoModel.getVideo_url() != null && !myVideoModel.getVideo_url().isEmpty()) {
            videoUrl = myVideoModel.getVideo_url();
        }

        LinearLayout root_view = findViewById(R.id.root_view);

        videoPlay = new VideoPlay(root_view, videoUrl, this);
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
