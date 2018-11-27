package com.uren.catchu.UgurDeneme;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Share.Adapters.ColorPaletteAdapter;
import com.uren.catchu.MainPackage.MainFragments.Share.Interfaces.BrushCompleteCallback;
import com.uren.catchu.ModelViews.PaintView;
import com.uren.catchu.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentXXX extends BaseFragment{

    View mView;

    public FragmentXXX() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_share_advance_settings, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }
}
