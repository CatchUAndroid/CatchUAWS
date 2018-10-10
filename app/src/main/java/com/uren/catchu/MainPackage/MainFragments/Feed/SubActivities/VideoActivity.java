package com.uren.catchu.MainPackage.MainFragments.Feed.SubActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.MyVideoModel;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostItem;
import com.uren.catchu.R;
import com.uren.catchu.VideoPlay.VideoPlay;

import catchu.model.Media;

public class VideoActivity extends AppCompatActivity {

    private VideoPlay videoPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

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
                .into(videoPlay.getImageView());



    }
}
