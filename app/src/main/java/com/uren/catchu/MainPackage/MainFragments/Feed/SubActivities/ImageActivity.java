package com.uren.catchu.MainPackage.MainFragments.Feed.SubActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostItem;
import com.uren.catchu.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.Media;

public class ImageActivity extends AppCompatActivity {

    @BindView(R.id.imgFeedItem)
    ImageView imgFeedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);

        setImage();

    }

    private void setImage() {

        Media media = PostItem.getInstance().getMedia();

        Glide.with(this)
                .load(media.getUrl())
                .apply(RequestOptions.centerInsideTransform())
                .into(imgFeedItem);

    }

}
