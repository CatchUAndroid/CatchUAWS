package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.mobile.auth.core.IdentityManager;
import com.uren.catchu.MainActivity;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.uren.catchu.Constants.StringConstants.AnimateLeftToRight;
import static com.uren.catchu.Constants.StringConstants.AnimateRightToLeft;


public class SettingsFragment extends Fragment
        implements View.OnClickListener {

    View mView;

    @BindView(R.id.btnSignOut)
    Button btnSignOut;

    @BindView(R.id.txtCancel)
    TextView txtCancel;


    public SettingsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.profile_subfragment_settings, container, false);
        ButterKnife.bind(this, mView);

        init();

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void init() {

        btnSignOut.setOnClickListener(this);
        txtCancel.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {

        if(v == btnSignOut){
            signOutClicked();
        }

        if(v==txtCancel){

            settingsCancelClicked();
        }

    }

    private void signOutClicked() {

        //AWS sign-out
        IdentityManager.getDefaultIdentityManager().signOut();

        getActivity().finish();
        startActivity(new Intent(getActivity(), MainActivity.class));

    }

    private void settingsCancelClicked() {

        ((NextActivity) getActivity()).ANIMATION_TAG = AnimateRightToLeft;
        getActivity().onBackPressed();


    }


}
