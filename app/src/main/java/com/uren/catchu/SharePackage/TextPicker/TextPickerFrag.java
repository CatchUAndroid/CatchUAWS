package com.uren.catchu.SharePackage.TextPicker;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.uren.catchu.GeneralUtils.BitmapConversion;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.ShareItems;

import butterknife.ButterKnife;

import static com.uren.catchu.GeneralUtils.BitmapConversion.getScreenShot;

@SuppressLint("ValidFragment")
public class TextPickerFrag extends Fragment {

    RelativeLayout textRelativeLay;
    EditText textEditText;
    private View mView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.text_picker, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initUI();
        addListeners();
    }

    public void initUI(){
        textRelativeLay = mView.findViewById(R.id.textRelativeLay);
        textEditText = mView.findViewById(R.id.textEditText);
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
                ShareItems.getInstance().getShare().setText(s.toString());

                Bitmap editTextBitmap;

                if(!s.toString().isEmpty()){
                    editTextBitmap = BitmapConversion.getScreenShot(textEditText);
                    editTextBitmap = BitmapConversion.getRoundedShape(editTextBitmap, 600, 600, null);
                }else
                    editTextBitmap = null;

                ShareItems.getInstance().setTextBitmap(editTextBitmap);
            }
        });

    }
}