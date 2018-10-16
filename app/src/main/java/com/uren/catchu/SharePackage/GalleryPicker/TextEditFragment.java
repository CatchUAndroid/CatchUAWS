package com.uren.catchu.SharePackage.GalleryPicker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.ViewPagerUtils;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.GalleryPicker.Adapters.ColorPaletteAdapter;
import com.uren.catchu.SharePackage.GalleryPicker.Interfaces.TextCompleteCallback;
import com.uren.catchu.SharePackage.Utils.ColorSelectCallback;

import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class TextEditFragment extends Fragment{

    View mView;
    Button finishButton;
    EditText editText;
    ImageView brushImgv;
    ViewPager colorViewPager;
    LinearLayout dotsLayout;
    TextView textView;

    ColorPaletteAdapter colorPaletteAdapter;
    TextCompleteCallback textCompleteCallback;

    @SuppressLint("ValidFragment")
    public TextEditFragment(View view, TextCompleteCallback textCompleteCallback){
        this.textView = (TextView) view;
        this.textCompleteCallback = textCompleteCallback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.text_edit_frag_layout, container, false);
            ButterKnife.bind(this, mView);
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        finishButton = mView.findViewById(R.id.finishButton);
        editText = mView.findViewById(R.id.editText);
        brushImgv = mView.findViewById(R.id.brushImgv);
        colorViewPager = mView.findViewById(R.id.colorViewPager);
        dotsLayout = mView.findViewById(R.id.layoutDots);
        colorPalettePrepare();
        focusEditText();
        addListeners();
    }

    private void addListeners() {
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Info", "editText.getTextSize22():" + editText.getTextSize());
                textCompleteCallback.textCompleted(editText);
                getActivity().onBackPressed();
            }
        });
    }

    public void colorPalettePrepare() {
        colorPaletteAdapter = new ColorPaletteAdapter(getActivity(), R.drawable.img_border,  new ColorSelectCallback() {
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
