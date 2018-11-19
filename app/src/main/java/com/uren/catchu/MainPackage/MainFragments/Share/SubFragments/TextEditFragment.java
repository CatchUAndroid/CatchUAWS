package com.uren.catchu.MainPackage.MainFragments.Share.SubFragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.GeneralUtils.ViewPagerUtils;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.R;
import com.uren.catchu.MainPackage.MainFragments.Share.Adapters.ColorPaletteAdapter;
import com.uren.catchu.MainPackage.MainFragments.Share.Interfaces.TextCompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Share.Interfaces.ColorSelectCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class TextEditFragment extends BaseFragment {

    View mView;

    @BindView(R.id.selectedImageView)
    ImageView selectedImageView;
    @BindView(R.id.finishButton)
    Button finishButton;
    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.brushImgv)
    ImageView brushImgv;
    @BindView(R.id.colorViewPager)
    ViewPager colorViewPager;
    @BindView(R.id.layoutDots)
    LinearLayout dotsLayout;

    TextView textView;

    ColorPaletteAdapter colorPaletteAdapter;
    TextCompleteCallback textCompleteCallback;
    PhotoSelectUtil photoSelectUtil;

    public TextEditFragment(View view, PhotoSelectUtil photoSelectUtil, TextCompleteCallback textCompleteCallback) {
        this.textView = (TextView) view;
        this.textCompleteCallback = textCompleteCallback;
        this.photoSelectUtil = photoSelectUtil;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_photo_text_edit, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setSelectedPhoto();
        colorPalettePrepare();
        focusEditText();
        addListeners();
        setShapes();
    }

    private void setShapes() {
        finishButton.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.transparentBlack, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 15, 2));
    }

    public void setSelectedPhoto(){
        if(photoSelectUtil != null){
            if(photoSelectUtil.getScreeanShotBitmap() != null)
                Glide.with(getContext())
                        .load(photoSelectUtil.getScreeanShotBitmap())
                        .into(selectedImageView);
            else if(photoSelectUtil.getMediaUri() != null)
                Glide.with(getContext())
                        .load(photoSelectUtil.getMediaUri())
                        .into(selectedImageView);
        }
    }

    private void addListeners() {
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textCompleteCallback.textCompleted(editText);
                getActivity().onBackPressed();
            }
        });
    }

    public void colorPalettePrepare() {
        colorPaletteAdapter = new ColorPaletteAdapter(getActivity(), R.drawable.img_border, new ColorSelectCallback() {
            @Override
            public void onClick(int colorCode) {
                brushImgv.setColorFilter(ContextCompat.getColor(getActivity(), colorCode), android.graphics.PorterDuff.Mode.SRC_IN);
                editText.setTextColor(getActivity().getResources().getColor(colorCode, null));
            }
        });
        colorViewPager.setAdapter(colorPaletteAdapter);
        ViewPagerUtils.setSliderDotsPanelWithTextView(colorPaletteAdapter.getCount(), R.color.White,
                R.color.Silver, getActivity(), colorViewPager, dotsLayout);
    }

    public void focusEditText() {
        setEditTextParams();
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    private void setEditTextParams() {
        editText.setText(textView.getText().toString());
        editText.setTextColor(textView.getCurrentTextColor());
    }
}
