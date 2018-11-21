package com.uren.catchu.MainPackage.MainFragments.Feed.SubActivities;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.SingletonPostItem;
import com.uren.catchu.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.Media;

public class ImageActivity extends AppCompatActivity {

    @BindView(R.id.imgFeedItem)
    ImageView imgFeedItem;

    @BindView(R.id.progressLoading)
    ProgressBar progressLoading;

    @BindView(R.id.llProgress)
    LinearLayout llProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);

        setImage();

    }

    private void setImage() {

        Media media = SingletonPostItem.getInstance().getMedia();

        Glide.with(this)
                .load(media.getUrl())
                .apply(RequestOptions.centerInsideTransform())
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
                .into(imgFeedItem);

    }

}
