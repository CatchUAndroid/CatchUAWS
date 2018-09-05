package com.uren.catchu.SharePackage.TextPicker;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.uren.catchu.R;

import butterknife.ButterKnife;

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

        textRelativeLay = mView.findViewById(R.id.textRelativeLay);
        textEditText = mView.findViewById(R.id.textEditText);
    }
}