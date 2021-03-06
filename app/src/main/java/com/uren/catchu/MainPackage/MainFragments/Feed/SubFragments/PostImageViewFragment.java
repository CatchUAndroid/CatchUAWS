package com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments;

import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.Media;
import uk.co.senab.photoview.PhotoViewAttacher;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;

@SuppressLint("ValidFragment")
public class PostImageViewFragment extends BaseFragment {


    View mView;

    @BindView(R.id.imgFeedItem)
    ImageView imgFeedItem;

    @BindView(R.id.progressLoading)
    ProgressBar progressLoading;

    @BindView(R.id.llProgress)
    LinearLayout llProgress;

    Matrix initMatrix;
    Media media;

    public PostImageViewFragment(Media media) {
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
        mView = inflater.inflate(R.layout.fragment_post_image_view, container, false);
        ButterKnife.bind(this, mView);
        initVariables();
        setImage();
        return mView;
    }

    private void initVariables() {
        initMatrix = new Matrix();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //yeni
        PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(imgFeedItem);
        photoViewAttacher.update();
    }

    private void setImage() {

        Glide.with(this)
                .load(media.getUrl())
                .apply(RequestOptions.fitCenterTransform())
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
