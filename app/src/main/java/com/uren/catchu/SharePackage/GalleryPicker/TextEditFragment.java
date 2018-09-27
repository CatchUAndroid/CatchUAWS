package com.uren.catchu.SharePackage.GalleryPicker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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

import com.uren.catchu.Interfaces.OnBackClicked;
import com.uren.catchu.MainPackage.Interfaces.IOnBackPressed;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.GalleryPicker.Adapters.ColorPaletteAdapter;
import com.uren.catchu.SharePackage.Utils.ColorSelectCallback;
import com.uren.catchu.Singleton.Share.ShareItems;

import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class TextEditFragment extends Fragment{

    View mView;
    Button finishButton;
    EditText editText;
    SeekBar seekbar;
    ImageView brushImgv;
    ViewPager colorViewPager;
    LinearLayout dotsLayout;
    String text;

    ColorPaletteAdapter colorPaletteAdapter;

    @SuppressLint("ValidFragment")
    public TextEditFragment(String text){
        this.text = text;
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
        seekbar = mView.findViewById(R.id.seekbar);
        brushImgv = mView.findViewById(R.id.brushImgv);
        colorViewPager = mView.findViewById(R.id.colorViewPager);
        dotsLayout = mView.findViewById(R.id.layoutDots);
        colorPalettePrepare();
        focusEditText();
        addListeners();
    }

    private void addListeners() {
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

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (editText != null)
                    editText.setTextSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    public void colorPalettePrepare() {
        colorPaletteAdapter = new ColorPaletteAdapter(getActivity(), new ColorSelectCallback() {
            @Override
            public void onClick(int colorCode) {
                brushImgv.setColorFilter(ContextCompat.getColor(getActivity(), colorCode), android.graphics.PorterDuff.Mode.SRC_IN);
                editText.setTextColor(getActivity().getResources().getColor(colorCode, null));
            }
        });
        colorViewPager.setAdapter(colorPaletteAdapter);
        addBottomDots(0);
    }

    private void addBottomDots(int currentPage) {
        TextView[] dots;
        dots = new TextView[colorPaletteAdapter.getCount()];

        int cActive = getActivity().getResources().getColor(R.color.White, null);
        int cInactive = getActivity().getResources().getColor(R.color.Silver, null);

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

    public void focusEditText() {
        editText.setText(text);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }
}
