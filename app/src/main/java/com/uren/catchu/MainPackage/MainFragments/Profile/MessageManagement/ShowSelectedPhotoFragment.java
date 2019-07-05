package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement;


import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoViewAttacher;

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
        PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(photoSelectImgv);
        photoViewAttacher.update();
    }

    private void setImage() {
        Glide.with(getContext())
                .load(photoUrl)
                .apply(RequestOptions.fitCenterTransform())
                .into(photoSelectImgv);
    }
}