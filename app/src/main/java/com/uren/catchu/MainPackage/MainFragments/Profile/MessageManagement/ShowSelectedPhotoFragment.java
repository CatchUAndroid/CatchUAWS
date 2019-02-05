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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.Media;
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
        try {
            mView = inflater.inflate(R.layout.fragment_show_selected_photo, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            setImage();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return mView;
    }

    private void initVariables() {
        initMatrix = new Matrix();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        try {
            PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(photoSelectImgv);
            photoViewAttacher.update();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void setImage() {

        try {
            Glide.with(getContext())
                    .load(photoUrl)
                    .apply(RequestOptions.fitCenterTransform())
                    .into(photoSelectImgv);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }
}