package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uren.catchu.FragmentControllers.FragNavController;
import com.uren.catchu.GeneralUtils.DataModelUtil.MessageDataUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.OnLoadedListener;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ListItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.UserInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople.Adapters.FacebookFriendsAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.OtherProfileFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderFacebookFriends;
import com.uren.catchu.Singleton.AccountHolderInfo;

import butterknife.ButterKnife;

import catchu.model.User;
import catchu.model.UserListResponse;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.PROVIDER_TYPE_FACEBOOK;

@SuppressLint("ValidFragment")
public class FacebookFriendsFragment extends BaseFragment {

    View mView;
    TextView warningMsgTv;
    FacebookFriendsAdapter facebookFriendsAdapter;
    RecyclerView personRecyclerView;
    OnLoadedListener onLoadedListener;
    boolean showTollbar;
    LinearLayout toolbarLayout;
    ImageView commonToolbarbackImgv;
    TextView toolbarTitleTv;
    ProgressBar progressBar;

    public FacebookFriendsFragment(OnLoadedListener onLoadedListener, boolean showTollbar) {
        this.onLoadedListener = onLoadedListener;
        this.showTollbar = showTollbar;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_special_select, container, false);
            ButterKnife.bind(this, mView);
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

    public void checkUserLoggedInWithFacebook(){
        if(AccountHolderInfo.getInstance().getUser().getUserInfo().getProvider() != null &&
                AccountHolderInfo.getInstance().getUser().getUserInfo().getProvider().getProviderType().equals(PROVIDER_TYPE_FACEBOOK)){
            getFacebookFriends();
        }else {
            MessageDataUtil.setWarningMessageVisibility(null, warningMsgTv,
                    getActivity().getResources().getString(R.string.CONNECT_FACEBOOK));
            onLoadedListener.onLoaded();
        }
    }

    public void initVariables() {
        personRecyclerView = mView.findViewById(R.id.specialRecyclerView);
        warningMsgTv = mView.findViewById(R.id.warningMsgTv);
        toolbarLayout = mView.findViewById(R.id.toolbarLayout);
        commonToolbarbackImgv = mView.findViewById(R.id.commonToolbarbackImgv);
        toolbarTitleTv = mView.findViewById(R.id.toolbarTitleTv);
        progressBar = mView.findViewById(R.id.progressBar);
    }

    private void checkToolbarVisibility() {
        if(showTollbar) {
            toolbarLayout.setVisibility(View.VISIBLE);
            toolbarTitleTv.setText(getActivity().getResources().getString(R.string.FACEBOOK_FRIENDS));
        }
    }

    private void addListeners() {
        commonToolbarbackImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_RIGHT_TO_LEFT;
                getActivity().onBackPressed();
            }
        });
    }

    public void getFacebookFriends() {
        progressBar.setVisibility(View.VISIBLE);
        AccountHolderFacebookFriends.getInstance(new CompleteCallback() {
            @Override
            public void onComplete(Object object) {
                UserListResponse userListResponse = (UserListResponse) object;

                MessageDataUtil.setWarningMessageVisibility(userListResponse, warningMsgTv,
                        getActivity().getResources().getString(R.string.THERE_IS_NO_FACEFRIEND_WHO_USING_CATCHU));

                if (userListResponse != null && userListResponse.getItems() != null &&
                        userListResponse.getItems().size() > 0 && getContext() != null) {
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
                onLoadedListener.onLoaded();
            }

            @Override
            public void onFailed(Exception e) {
                progressBar.setVisibility(View.GONE);
                onLoadedListener.onError(e.getMessage());
            }
        });
    }

    private void displayUserProfile(User user, int clickedPosition) {

        if (!user.getUserid().equals(AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid())) {
            if (mFragmentNavigation != null && facebookFriendsAdapter != null) {
                UserInfoListItem userInfoListItem = new UserInfoListItem(user);
                userInfoListItem.setAdapter(facebookFriendsAdapter);
                userInfoListItem.setClickedPosition(clickedPosition);
                mFragmentNavigation.pushFragment(OtherProfileFragment.newInstance(userInfoListItem), ANIMATE_RIGHT_TO_LEFT);
            }
        } else {
            NextActivity.switchAndUpdateTabSelection(FragNavController.TAB3);
        }
    }

    public void updateAdapter(String searchText) {
        if (searchText != null && facebookFriendsAdapter != null)
            facebookFriendsAdapter.updateAdapter(searchText);
    }
}
