package com.uren.catchu.MainPackage.MainFragments.Profile.SettingsManagement;

import android.annotation.SuppressLint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.ModelViews.PaintView;
import com.uren.catchu.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;

@SuppressLint("ValidFragment")
public class MarkProblemFragment extends BaseFragment {

    View mView;

    @BindView(R.id.commonToolbarTickImgv)
    ImageView commonToolbarTickImgv;
    @BindView(R.id.commonToolbarbackImgv)
    ImageView commonToolbarbackImgv;
    @BindView(R.id.toolbarTitleTv)
    TextView toolbarTitleTv;
    @BindView(R.id.paintView)
    PaintView paintView;
    @BindView(R.id.markProblemLayout)
    RelativeLayout markProblemLayout;

    PhotoSelectUtil photoSelectUtil;
    ReturnCallback returnCallback;

    public MarkProblemFragment(PhotoSelectUtil photoSelectUtil, ReturnCallback returnCallback) {
        this.photoSelectUtil = photoSelectUtil;
        this.returnCallback = returnCallback;
    }

    @Override
    public void onStart() {
        getActivity().findViewById(R.id.tabMainLayout).setVisibility(View.GONE);
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_mark_problem, container, false);
        ButterKnife.bind(this, mView);
        initVariables();
        addListeners();
        setCanvas();
        return mView;
    }

    private void setCanvas() {
        paintView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                paintView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                paintView.init(paintView.getWidth(), paintView.getHeight(), photoSelectUtil.getBitmap());
                paintView.normal();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initVariables() {
        toolbarTitleTv.setText(getResources().getString(R.string.CIRCLE_THE_PROBLEM));
        commonToolbarTickImgv.setVisibility(View.VISIBLE);
        markProblemLayout.setBackground(ShapeUtil.getShape(0,
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 0, 2));
    }

    public void addListeners() {
        commonToolbarbackImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
                getActivity().onBackPressed();
            }
        });

        commonToolbarTickImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoSelectUtil.setBitmap(paintView.getmBitmap());
                returnCallback.onReturn(photoSelectUtil);
                ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
                getActivity().onBackPressed();
            }
        });
    }

}
