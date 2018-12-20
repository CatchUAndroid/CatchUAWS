package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement;


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
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.SingletonPostItem;
import com.uren.catchu.MainPackage.MainFragments.Feed.Utils.ImageZoomListener;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.Media;

@SuppressLint("ValidFragment")
public class ShowSelectedPhotoFragment extends BaseFragment {


    View mView;

    @BindView(R.id.photoSelectImgv)
    ImageView photoSelectImgv;

    Matrix initMatrix;
    String photoUrl;

    public ShowSelectedPhotoFragment(String photoUrl) {
        this.photoUrl = photoUrl;
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
        mView = inflater.inflate(R.layout.fragment_show_selected_photo, container, false);
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
        photoSelectImgv.getImageMatrix();
        photoSelectImgv.setOnTouchListener(new ImageZoomListener(initMatrix));
    }

    private void setImage() {

        Glide.with(getContext())
                .load(photoUrl)
                .apply(RequestOptions.fitCenterTransform())
                .into(photoSelectImgv);
    }
}