package com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.Media;

import static com.uren.catchu.MainPackage.MainFragments.BaseFragment.ARGS_INSTANCE;

public class ImageFragment extends Fragment {

    View mView;

    @BindView(R.id.imgFeedItem)
    ImageView imgFeedItem;

    public ImageFragment () {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        CommonUtils.LOG_NEREDEYIZ("ImageFragment");

        mView = inflater.inflate(R.layout.viewpager_image, container, false);
        ButterKnife.bind(this, mView);



        return mView;
    }




}
