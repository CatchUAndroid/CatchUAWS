package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.uren.catchu.Constants.StringConstants.AnimateLeftToRight;
import static com.uren.catchu.Constants.StringConstants.AnimateRightToLeft;

public class UserEditFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;

    @BindView(R.id.toolbarLayout)
    Toolbar mToolBar;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;

    @BindView(R.id.imgCancel)
    ClickableImageView imgCancel;
    @BindView(R.id.imgConfirm)
    ClickableImageView imgConfirm;


    public UserEditFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.profile_subfragment_user_edit, container, false);
        ButterKnife.bind(this, mView);

        setUpToolbar();

        return mView;
    }

    private void setUpToolbar() {

        imgCancel.setOnClickListener(this);
        imgConfirm.setOnClickListener(this);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //setUpRecyclerView();


        Button btn = (Button) view.findViewById(R.id.btnBack);
        Button btnbtnNextFrag = (Button) view.findViewById(R.id.btnNextFrag);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnbtnNextFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mFragmentNavigation != null) {

                    mFragmentNavigation.pushFragment(new UserEditFragment(), AnimateRightToLeft);

                    //mFragmentNavigation.pushFragment(new UserEditFragment());

                }
            }
        });

    }

    @Override
    public void onClick(View v) {

        if (v == imgCancel) {
            editProfileCancelClicked();
        }

        if (v == imgConfirm) {
            editProfileConfirmClicked();
        }


    }

    private void editProfileCancelClicked() {

        ((NextActivity) getActivity()).ANIMATION_TAG = AnimateLeftToRight;
        getActivity().onBackPressed();

    }

    private void editProfileConfirmClicked() {

    }
}