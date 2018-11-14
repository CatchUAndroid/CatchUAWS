package com.uren.catchu.MainPackage.MainFragments.Profile.SettingsManagement;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.YesNoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.DynamicLinkUtil.DynamicLinkUtil;
import com.uren.catchu.GeneralUtils.ProgressDialogUtil.ProgressDialogUtil;
import com.uren.catchu.Interfaces.OnLoadedListener;
import com.uren.catchu.Interfaces.ServiceCompleteCallback;
import com.uren.catchu.MainActivity;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.Operations.SettingOperation;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ChangePasswordFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople.ContactFriendsFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople.FacebookFriendsFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.Utils.UpdateUserProfileProcess;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.UgurDeneme.FirebaseMLActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.DYNAMIC_LINK_DOMAIN;


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

    // TODO: 5.11.2018 - silinecek
    @BindView(R.id.mlDeneme)
    LinearLayout mlDeneme;

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

        mlDeneme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), FirebaseMLActivity.class));
            }
        });


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
