package com.uren.catchu.SharePackage.TextPicker;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.BitmapConversion;
import com.uren.catchu.MainPackage.Interfaces.IOnBackPressed;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.GalleryPicker.Adapters.ColorPaletteAdapter;
import com.uren.catchu.SharePackage.TextPicker.Adapters.PostitAdapter;
import com.uren.catchu.SharePackage.TextPicker.Interfaces.PostitSelectCallback;
import com.uren.catchu.SharePackage.Utils.ColorSelectCallback;
import com.uren.catchu.Singleton.Share.ShareItems;

import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class TextPickerFrag extends Fragment {

    RelativeLayout textRelativeLay;
    EditText textEditText;
    ViewPager colorViewPager;
    ImageView brushImgv;
    LinearLayout dotsLayout;
    LinearLayout colorPaletteLayout;
    LinearLayout closePalletteBtnLayout;
    LinearLayout postitLayout;
    Button closePalletteButton;
    private View mView;
    ColorPaletteAdapter colorPaletteAdapter;
    PostitAdapter postitAdapter;

    ImageView trashImgv;
    ImageView postitImgv;
    ImageView backColorSelectImgv;
    ImageView boldImgv;
    ImageView italicImgv;

    boolean postitImgvClicked = false;
    boolean backColorClicked = false;
    boolean boldImgvClicked = false;
    boolean italicImgvClicked = false;

    private static final String bold = "bold";
    private static final String italic = "italic";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.text_picker, container, false);
            ButterKnife.bind(this, mView);
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initUI();
        addListeners();
        focusEditText();
    }

    public void initUI() {
        textRelativeLay = mView.findViewById(R.id.textRelativeLay);
        textEditText = mView.findViewById(R.id.textEditText);
        colorViewPager = mView.findViewById(R.id.colorViewPager);
        dotsLayout = mView.findViewById(R.id.layoutDots);
        colorPaletteLayout = mView.findViewById(R.id.colorPaletteLayout);
        brushImgv = mView.findViewById(R.id.brushImgv);
        closePalletteBtnLayout = mView.findViewById(R.id.closePalletteBtnLayout);
        closePalletteButton = mView.findViewById(R.id.closePalletteButton);
        colorPaletteLayout.setVisibility(View.GONE);
        postitImgv = mView.findViewById(R.id.postitImgv);
        backColorSelectImgv = mView.findViewById(R.id.backColorSelectImgv);
        boldImgv = mView.findViewById(R.id.boldImgv);
        italicImgv = mView.findViewById(R.id.italicImgv);
        trashImgv = mView.findViewById(R.id.trashImgv);
        postitLayout = mView.findViewById(R.id.postitLayout);
        brushImgv.setColorFilter(ContextCompat.getColor(getActivity(), R.color.Black), android.graphics.PorterDuff.Mode.SRC_IN);
        textEditText.clearFocus();
        colorPalettePrepare();
        postitPrepare();
    }

    private void addListeners() {
        textEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ShareItems.getInstance().getPost().setMessage(s.toString());
                Bitmap editTextBitmap;

                if (!s.toString().isEmpty()) {
                    editTextBitmap = BitmapConversion.getScreenShot(textEditText);
                } else
                    editTextBitmap = null;

                ShareItems.getInstance().setTextBitmap(editTextBitmap);
            }
        });

        textEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focusEditText();
            }
        });

        colorViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        closePalletteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorPaletteLayout.setVisibility(View.GONE);
                closePalletteBtnLayout.setVisibility(View.GONE);
                hideKeyBoard();
            }
        });

        postitImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postitLayout.setVisibility(View.VISIBLE);
            }
        });

        backColorSelectImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!backColorClicked) {
                    backColorClicked = true;
                    backColorSelectImgv.setColorFilter(ContextCompat.getColor(getActivity(), R.color.DodgerBlue), android.graphics.PorterDuff.Mode.SRC_IN);
                    setColorPaletteVisibility(View.VISIBLE);
                } else {
                    backColorClicked = false;
                    backColorSelectImgv.setColorFilter(ContextCompat.getColor(getActivity(), R.color.White), android.graphics.PorterDuff.Mode.SRC_IN);
                    textEditText.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.transparent));
                    colorPaletteLayout.setVisibility(View.GONE);
                    closePalletteBtnLayout.setVisibility(View.GONE);
                }
            }
        });

        boldImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!boldImgvClicked) {
                    boldImgvClicked = true;
                    boldImgv.setColorFilter(ContextCompat.getColor(getActivity(), R.color.DodgerBlue), android.graphics.PorterDuff.Mode.SRC_IN);

                    if (italicImgvClicked)
                        textEditText.setTypeface(null, Typeface.BOLD | Typeface.ITALIC);
                    else
                        textEditText.setTypeface(null, Typeface.BOLD);
                } else {
                    boldImgvClicked = false;
                    boldImgv.setColorFilter(ContextCompat.getColor(getActivity(), R.color.White), android.graphics.PorterDuff.Mode.SRC_IN);

                    if (italicImgvClicked)
                        textEditText.setTypeface(null, Typeface.ITALIC);
                    else
                        textEditText.setTypeface(null, Typeface.NORMAL);
                }

            }
        });

        italicImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!italicImgvClicked) {
                    italicImgvClicked = true;
                    italicImgv.setColorFilter(ContextCompat.getColor(getActivity(), R.color.DodgerBlue), android.graphics.PorterDuff.Mode.SRC_IN);

                    if (boldImgvClicked)
                        textEditText.setTypeface(null, Typeface.BOLD | Typeface.ITALIC);
                    else
                        textEditText.setTypeface(null, Typeface.ITALIC);
                } else {
                    italicImgvClicked = false;
                    italicImgv.setColorFilter(ContextCompat.getColor(getActivity(), R.color.White), android.graphics.PorterDuff.Mode.SRC_IN);

                    if (boldImgvClicked)
                        textEditText.setTypeface(null, Typeface.BOLD);
                    else
                        textEditText.setTypeface(null, Typeface.NORMAL);
                }
            }
        });

        trashImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textEditText.setText("");
            }
        });
    }

    public void focusEditText() {
        setColorPaletteVisibility(View.VISIBLE);
        textEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(textEditText, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public void colorPalettePrepare() {
        colorPaletteAdapter = new ColorPaletteAdapter(getActivity(), R.drawable.img_border, new ColorSelectCallback() {
            @Override
            public void onClick(int colorCode) {
                if (backColorClicked) {
                    textEditText.setBackgroundColor(ContextCompat.getColor(getActivity(), colorCode));
                } else {
                    textEditText.setTextColor(getActivity().getResources().getColor(colorCode, null));
                    brushImgv.setColorFilter(ContextCompat.getColor(getActivity(), colorCode), android.graphics.PorterDuff.Mode.SRC_IN);
                }
            }
        });
        colorViewPager.setAdapter(colorPaletteAdapter);
        addBottomDots(0);
    }

    public void postitPrepare(){
        postitAdapter = new PostitAdapter(getActivity(), new PostitSelectCallback() {
            @Override
            public void onSelect() {

            }
        });
    }

    public void setColorPaletteVisibility(int visibleType){
        colorPaletteLayout.setVisibility(visibleType);
        closePalletteBtnLayout.setVisibility(visibleType);
    }

    private void addBottomDots(int currentPage) {
        TextView[] dots;
        dots = new TextView[colorPaletteAdapter.getCount()];

        int cActive = getActivity().getResources().getColor(R.color.Black, null);
        int cInactive = getActivity().getResources().getColor(R.color.White, null);

        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(getActivity());
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(cInactive);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(cActive);
    }

    public void hideKeyBoard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}