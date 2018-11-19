package com.uren.catchu.MainPackage.MainFragments.Profile.SettingsManagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.DynamicLinkUtil.DynamicLinkUtil;
import com.uren.catchu.GeneralUtils.ProgressDialogUtil.ProgressDialogUtil;
import com.uren.catchu.Interfaces.OnLoadedListener;
import com.uren.catchu.MainActivity;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.Operations.SettingOperation;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ChangePasswordFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople.ContactFriendsFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople.FacebookFriendsFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;


public class SettingsFragment extends BaseFragment {

    View mView;
    ProgressDialogUtil progressDialogUtil;
    Fragment fragment;

    @BindView(R.id.commonToolbarbackImgv)
    ImageView commonToolbarbackImgv;
    @BindView(R.id.toolbarTitleTv)
    TextView toolbarTitleTv;
    @BindView(R.id.logoutLayout)
    LinearLayout logoutLayout;
    @BindView(R.id.addFromFacebookLayout)
    LinearLayout addFromFacebookLayout;
    @BindView(R.id.addFromContactLayout)
    LinearLayout addFromContactLayout;
    @BindView(R.id.inviteForInstallLayout)
    LinearLayout inviteForInstallLayout;
    @BindView(R.id.changePasswordLayout)
    LinearLayout changePasswordLayout;
    @BindView(R.id.problemInformLayout)
    LinearLayout problemInformLayout;
    @BindView(R.id.privateAccSwitch)
    Switch privateAccSwitch;

    public SettingsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.profile_subfragment_settings, container, false);
        ButterKnife.bind(this, mView);

        init();
        setDefaultUIValues();
        addListeners();
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void init() {
        progressDialogUtil = new ProgressDialogUtil(getActivity(), null, false);
        fragment = this;
    }

    public void setDefaultUIValues() {
        toolbarTitleTv.setText(getActivity().getResources().getString(R.string.settings));

        if (AccountHolderInfo.getInstance().getUser() != null &&
                AccountHolderInfo.getInstance().getUser().getUserInfo().getIsPrivateAccount() != null &&
                AccountHolderInfo.getInstance().getUser().getUserInfo().getIsPrivateAccount().booleanValue())
            privateAccSwitch.setChecked(true);
        else
            privateAccSwitch.setChecked(false);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void addListeners() {

        commonToolbarbackImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
                getActivity().onBackPressed();
            }
        });

        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutClicked();
            }
        });

        addFromFacebookLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFacebookFriendsFragment();
            }
        });

        addFromContactLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startContactFriendsFragment();
            }
        });

        inviteForInstallLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DynamicLinkUtil.shareShortDynamicLink(getContext(), fragment);
            }
        });

        changePasswordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChangePasswordFragment();
            }
        });

        problemInformLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNotifyProblemFragment();
            }
        });

        privateAccSwitch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        SettingOperation.changeUserPrivacy(getContext(), privateAccSwitch);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    public void startFacebookFriendsFragment() {
        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new FacebookFriendsFragment(new OnLoadedListener() {
                @Override
                public void onLoaded() {

                }

                @Override
                public void onError(String message) {

                }
            }, true), ANIMATE_LEFT_TO_RIGHT);
        }
    }

    public void startContactFriendsFragment() {
        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new ContactFriendsFragment(new OnLoadedListener() {
                @Override
                public void onLoaded() {

                }

                @Override
                public void onError(String message) {

                }
            }, true), ANIMATE_LEFT_TO_RIGHT);
        }
    }

    public void startChangePasswordFragment() {
        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new ChangePasswordFragment(), ANIMATE_LEFT_TO_RIGHT);
        }
    }

    public void startNotifyProblemFragment(){
        if (mFragmentNavigation != null) {
            NextActivity.screenShotMainLayout.setVisibility(View.GONE);
            NextActivity.notifyProblemFragment = null;
            mFragmentNavigation.pushFragment(new NotifyProblemFragment(), ANIMATE_LEFT_TO_RIGHT);
        }
    }

    private void signOutClicked() {

        SettingOperation.userSignOut();
        getActivity().finish();
        startActivity(new Intent(getActivity(), MainActivity.class));
    }
}
