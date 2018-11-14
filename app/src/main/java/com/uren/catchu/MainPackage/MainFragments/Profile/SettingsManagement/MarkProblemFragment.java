package com.uren.catchu.MainPackage.MainFragments.Profile.SettingsManagement;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.R;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class MarkProblemFragment extends BaseFragment {

    View mView;

    @BindView(R.id.selectedImgv)
    ImageView selectedImgv;
    @BindView(R.id.commonToolbarTickImgv)
    ImageView commonToolbarTickImgv;
    @BindView(R.id.commonToolbarbackImgv)
    ImageView commonToolbarbackImgv;
    @BindView(R.id.toolbarTitleTv)
    TextView toolbarTitleTv;

    PhotoSelectUtil photoSelectUtil;

    public MarkProblemFragment(PhotoSelectUtil photoSelectUtil) {
        this.photoSelectUtil = photoSelectUtil;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_mark_problem, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            addListeners();
            setPhoto();
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initVariables() {
        toolbarTitleTv.setText(getResources().getString(R.string.CIRCLE_THE_PROBLEM));
        commonToolbarTickImgv.setVisibility(View.VISIBLE);
    }

    public void setPhoto(){
        if(photoSelectUtil != null && photoSelectUtil.getBitmap() != null){
            Glide.with(getContext())
                    .load(photoSelectUtil.getBitmap())
                    .into(selectedImgv);
        }
    }

    public void addListeners() {
        commonToolbarbackImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }
}
