package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.accounts.Account;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.amazonaws.mobile.auth.core.IdentityManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.appinvite.FirebaseAppInvite;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.iid.FirebaseInstanceId;
import com.twitter.sdk.android.core.TwitterCore;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DynamicLinkUtil.DynamicLinkUtil;
import com.uren.catchu.GeneralUtils.ProgressDialogUtil.ProgressDialogUtil;
import com.uren.catchu.Interfaces.OnLoadedListener;
import com.uren.catchu.MainActivity;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople.ContactFriendsFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople.FacebookFriendsFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.DYNAMIC_LINK_DOMAIN;


public class SettingsFragment extends BaseFragment{

    View mView;
    ProgressDialogUtil progressDialogUtil;
    Fragment fragment;

    ImageView backImgv;
    TextView toolbarTitleTv;
    LinearLayout logoutLayout;
    LinearLayout addFromFacebookLayout;
    LinearLayout addFromContactLayout;
    LinearLayout inviteForInstallLayout;
    LinearLayout changePasswordLayout;
    //ToggleButton privateToogleButton;
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
        backImgv = mView.findViewById(R.id.backImgv);
        toolbarTitleTv = mView.findViewById(R.id.toolbarTitleTv);
        logoutLayout = mView.findViewById(R.id.logoutLayout);
        addFromFacebookLayout = mView.findViewById(R.id.addFromFacebookLayout);
        addFromContactLayout = mView.findViewById(R.id.addFromContactLayout);
        inviteForInstallLayout = mView.findViewById(R.id.inviteForInstallLayout);
        changePasswordLayout = mView.findViewById(R.id.changePasswordLayout);
        privateAccSwitch = mView.findViewById(R.id.privateAccSwitch);
        //privateToogleButton = (ToggleButton) mView.findViewById(R.id.privateAccSwitch);
        progressDialogUtil = new ProgressDialogUtil(getActivity(), null, false);
        fragment = this;
    }

    public void setDefaultUIValues(){
        toolbarTitleTv.setText(getActivity().getResources().getString(R.string.settings));

        if(AccountHolderInfo.getInstance().getUser() != null &&
                AccountHolderInfo.getInstance().getUser().getUserInfo().getIsPrivateAccount() != null &&
                AccountHolderInfo.getInstance().getUser().getUserInfo().getIsPrivateAccount().booleanValue())
            privateAccSwitch.setChecked(true);
        else
            privateAccSwitch.setChecked(false);
    }

    public void addListeners(){
        backImgv.setOnClickListener(new View.OnClickListener() {
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

        privateAccSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUserPrivateSpec();
            }
        });
    }

    public void startFacebookFriendsFragment(){
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

    public void startContactFriendsFragment(){
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

    public void startChangePasswordFragment(){
        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new ChangePasswordFragment(), ANIMATE_LEFT_TO_RIGHT);
        }
    }

    public void changeUserPrivateSpec(){
        if(AccountHolderInfo.getInstance().getUser().getUserInfo().getIsPrivateAccount().booleanValue()){

        }
    }

    private void signOutClicked() {

        UserDataUtil.userSignOut();
        getActivity().finish();
        startActivity(new Intent(getActivity(), MainActivity.class));
    }
}
