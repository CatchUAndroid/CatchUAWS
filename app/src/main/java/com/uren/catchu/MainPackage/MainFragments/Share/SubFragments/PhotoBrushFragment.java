package com.uren.catchu.MainPackage.MainFragments.Share.SubFragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.GeneralUtils.ViewPagerUtils;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Share.Adapters.ColorPaletteAdapter;
import com.uren.catchu.MainPackage.MainFragments.Share.Interfaces.BrushCompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Share.Interfaces.ColorSelectCallback;
import com.uren.catchu.ModelViews.PaintView;
import com.uren.catchu.R;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class PhotoBrushFragment extends BaseFragment {

    View mView;

    @BindView(R.id.photoRelLayout)
    RelativeLayout photoRelLayout;
    @BindView(R.id.paintView)
    PaintView paintView;
    @BindView(R.id.pencilImgv)
    ImageView pencilImgv;
    @BindView(R.id.blurImgv)
    ImageView blurImgv;
    @BindView(R.id.trashImgv)
    ImageView trashImgv;
    @BindView(R.id.finishButton)
    Button finishButton;
    @BindView(R.id.seekbar)
    SeekBar seekbar;
    @BindView(R.id.brushImgv)
    ImageView brushImgv;
    @BindView(R.id.colorViewPager)
    ViewPager colorViewPager;
    @BindView(R.id.layoutDots)
    LinearLayout layoutDots;
    @BindView(R.id.seekbarLayout)
    FrameLayout seekbarLayout;

    ColorPaletteAdapter colorPaletteAdapter;
    BrushCompleteCallback brushCompleteCallback;
    PhotoSelectUtil photoSelectUtil;
    Bitmap comingBitmap;
    int bitmapHeigth;
    int bitmapWidth;

    boolean isBlurSelected;
    boolean isPencilSelected;

    public PhotoBrushFragment(PhotoSelectUtil photoSelectUtil, BrushCompleteCallback brushCompleteCallback) {
        this.brushCompleteCallback = brushCompleteCallback;
        this.photoSelectUtil = photoSelectUtil;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_photo_brush, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setCanvas();
        colorPalettePrepare();
        addListeners();
        setShapes();
    }

    private void setShapes() {
        finishButton.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.Black, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 15, 2));

        pencilImgv.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.transparentBlack, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 2));
        blurImgv.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.transparentBlack, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 2));
        trashImgv.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.transparentBlack, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 2));
        seekbarLayout.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.transparentBlack, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 2));
    }

    private void setCanvas() {
        paintView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                paintView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                if (photoSelectUtil.getScreeanShotBitmap() != null)
                    comingBitmap = photoSelectUtil.getScreeanShotBitmap();
                else if (photoSelectUtil.getBitmap() != null)
                    comingBitmap = photoSelectUtil.getBitmap();

                if (comingBitmap != null) {
                    bitmapHeigth = comingBitmap.getHeight();
                    bitmapWidth = comingBitmap.getWidth();
                    paintView.init(bitmapWidth, bitmapHeigth, comingBitmap);
                    pencilImgv.setColorFilter(paintView.getCurrentColor(), android.graphics.PorterDuff.Mode.SRC_IN);
                    isPencilSelected = true;
                }

                paintView.normal();
            }
        });
    }

    private void addListeners() {
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoSelectUtil.setScreeanShotBitmap(paintView.getmBitmap());
                brushCompleteCallback.OnBrushCompleted(photoSelectUtil);
                getActivity().onBackPressed();
            }
        });

        blurImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPencilSelected = false;
                isBlurSelected = true;
                pencilImgv.setColorFilter(getContext().getResources().getColor(R.color.White, null), PorterDuff.Mode.SRC_IN);
                blurImgv.setColorFilter(paintView.getCurrentColor(), android.graphics.PorterDuff.Mode.SRC_IN);
                paintView.blur();
            }
        });

        pencilImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPencilSelected = true;
                isBlurSelected = false;
                pencilImgv.setColorFilter(paintView.getCurrentColor(), android.graphics.PorterDuff.Mode.SRC_IN);
                blurImgv.setColorFilter(getContext().getResources().getColor(R.color.White, null), PorterDuff.Mode.SRC_IN);
                paintView.normal();
            }
        });

        trashImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.setmBitmap(bitmapWidth, bitmapHeigth, comingBitmap);
                paintView.clear();
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                paintView.setBrushSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void colorPalettePrepare() {
        colorPaletteAdapter = new ColorPaletteAdapter(getActivity(), new ColorSelectCallback() {
            @Override
            public void onClick(int colorCode) {
                brushImgv.setColorFilter(ContextCompat.getColor(getActivity(), colorCode), android.graphics.PorterDuff.Mode.SRC_IN);
                paintView.setCurrentColor(getContext().getResources().getColor(colorCode, null));

                if(isBlurSelected)
                    blurImgv.setColorFilter(paintView.getCurrentColor(), android.graphics.PorterDuff.Mode.SRC_IN);
                else if(isPencilSelected)
                    pencilImgv.setColorFilter(paintView.getCurrentColor(), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        });
        colorViewPager.setAdapter(colorPaletteAdapter);
        ViewPagerUtils.setSliderDotsPanelWithTextView(colorPaletteAdapter.getCount(), R.color.White,
                R.color.Silver, getActivity(), colorViewPager, layoutDots);
    }


}
