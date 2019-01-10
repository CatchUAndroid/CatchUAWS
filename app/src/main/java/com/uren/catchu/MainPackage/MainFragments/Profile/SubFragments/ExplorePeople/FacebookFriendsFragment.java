package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.uren.catchu.FragmentControllers.FragNavController;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ListItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.UserInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople.Adapters.FacebookFriendsAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.OtherProfile.OtherProfileFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderFacebookFriends;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

import catchu.model.User;
import catchu.model.UserListResponse;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.PROVIDER_TYPE_FACEBOOK;

@SuppressLint("ValidFragment")
public class FacebookFriendsFragment extends BaseFragment {

    View mView;

    @BindView(R.id.warningMsgTv)
    TextView warningMsgTv;
    @BindView(R.id.specialRecyclerView)
    RecyclerView personRecyclerView;
    @BindView(R.id.toolbarLayout)
    LinearLayout toolbarLayout;
    @BindView(R.id.commonToolbarbackImgv)
    ImageView commonToolbarbackImgv;
    @BindView(R.id.toolbarTitleTv)
    TextView toolbarTitleTv;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.connectFacebookLayout)
    LinearLayout connectFacebookLayout;
    @BindView(R.id.facebookLoginButton)
    LoginButton loginButton;
    @BindView(R.id.connectFacebookButton)
    Button connectFacebookButton;

    FacebookFriendsAdapter facebookFriendsAdapter;
    CallbackManager mCallbackManager;
    boolean showTollbar;

    public FacebookFriendsFragment(boolean showTollbar) {
        this.showTollbar = showTollbar;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_RIGHT_TO_LEFT;
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_facebook_friends, container, false);
            ButterKnife.bind(this, mView);
            FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
            mCallbackManager = CallbackManager.Factory.create();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initVariables();
        checkToolbarVisibility();
        addListeners();
        checkUserLoggedInWithFacebook();
    }

    private void initVariables() {
        warningMsgTv.setTypeface(warningMsgTv.getTypeface(), Typeface.BOLD);
        warningMsgTv.setTextSize(15f);
        warningMsgTv.setTextColor(getResources().getColor(R.color.DodgerBlue, null));
        warningMsgTv.setText(getContext().getResources().getString(R.string.THERE_IS_NO_FACEFRIEND_WHO_USING_CATCHU));
        connectFacebookButton.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.DodgerBlue, null),
                0, GradientDrawable.RECTANGLE, 15, 2));
    }

    public void checkUserLoggedInWithFacebook() {
        if (AccountHolderInfo.getInstance().getUser().getUserInfo().getProvider() != null &&
                AccountHolderInfo.getInstance().getUser().getUserInfo().getProvider().getProviderType().equals(PROVIDER_TYPE_FACEBOOK)) {
            getFacebookFriends();
        } else {
            getUserAccessToken();
        }
    }

    private void checkToolbarVisibility() {
        if (showTollbar) {
            toolbarLayout.setVisibility(View.VISIBLE);
            toolbarTitleTv.setText(getActivity().getResources().getString(R.string.FACEBOOK_FRIENDS));
        }
    }

    private void addListeners() {
        commonToolbarbackImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        connectFacebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectFacebook();
            }
        });
    }

    public void getFacebookFriends() {
        progressBar.setVisibility(View.VISIBLE);
        AccountHolderFacebookFriends.getInstance(new CompleteCallback() {
            @Override
            public void onComplete(Object object) {
                UserListResponse userListResponse = (UserListResponse) object;

                if (userListResponse != null && userListResponse.getItems() != null &&
                        userListResponse.getItems().size() > 0 && getContext() != null) {
                    setMessageWarning(userListResponse);

                    facebookFriendsAdapter = new FacebookFriendsAdapter(getContext(), userListResponse, new ListItemClickListener() {
                        @Override
                        public void onClick(View view, User user, int clickedPosition) {
                            displayUserProfile(user, clickedPosition);
                        }
                    });
                    personRecyclerView.setAdapter(facebookFriendsAdapter);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    personRecyclerView.setLayoutManager(linearLayoutManager);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailed(Exception e) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setMessageWarning(UserListResponse userListResponse) {
        if(userListResponse != null && userListResponse.getItems() != null &&
                userListResponse.getItems().size() > 0)
            warningMsgTv.setVisibility(View.GONE);
        else
            warningMsgTv.setVisibility(View.VISIBLE);
    }

    private void displayUserProfile(User user, int clickedPosition) {

        if (!user.getUserid().equals(AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid())) {
            if (mFragmentNavigation != null && facebookFriendsAdapter != null) {
                UserInfoListItem userInfoListItem = new UserInfoListItem(user);
                userInfoListItem.setAdapter(facebookFriendsAdapter);
                userInfoListItem.setClickedPosition(clickedPosition);
                mFragmentNavigation.pushFragment(new OtherProfileFragment(userInfoListItem), ANIMATE_RIGHT_TO_LEFT);
            }
        } else {
            NextActivity.switchAndUpdateTabSelection(FragNavController.TAB3);
        }
    }

    public void updateAdapter(String searchText) {
        if (searchText != null && facebookFriendsAdapter != null)
            facebookFriendsAdapter.updateAdapter(searchText);
    }

    public void getUserAccessToken() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            if (accessToken.isExpired()) {
                connectFacebookLayout.setVisibility(View.VISIBLE);
            } else {
                getFacebookFriends();
            }
        } else{
            connectFacebookLayout.setVisibility(View.VISIBLE);
        }
    }

    public void connectFacebook() {
        loginButton.setFragment(this);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile",
                "email",
                "user_birthday",
                "user_friends"));

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                connectFacebookLayout.setVisibility(View.GONE);
                getFacebookFriends();
            }

            @Override
            public void onCancel() {
                System.out.println("facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                System.out.println("facebook:onError:" + error.toString());
            }
        });

        loginButton.performClick();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
