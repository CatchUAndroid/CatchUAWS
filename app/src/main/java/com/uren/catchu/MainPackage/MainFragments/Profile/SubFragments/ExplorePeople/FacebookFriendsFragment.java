package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.uren.catchu.Adapters.UserDetailAdapter;
import com.uren.catchu.ApiGatewayFunctions.ProviderListRequestProcess;
import com.uren.catchu.FragmentControllers.FragNavController;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ListItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.FollowInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople.Adapters.FacebookFriendsAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.OtherProfileFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderFacebookFriends;
import com.uren.catchu.Singleton.AccountHolderInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import catchu.model.FollowInfoResultArrayItem;
import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;
import catchu.model.UserListResponse;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;

public class FacebookFriendsFragment extends BaseFragment{

    View mView;
    FacebookFriendsAdapter facebookFriendsAdapter;
    RecyclerView personRecyclerView;

    public FacebookFriendsFragment() { }

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
        addListeners();
        getFacebookFriends();
    }

    public void initVariables(){
        personRecyclerView = (RecyclerView) mView.findViewById(R.id.specialRecyclerView);

    }

    private void addListeners() {

    }

    public void getFacebookFriends() {
        AccountHolderFacebookFriends.getInstance(new CompleteCallback() {
            @Override
            public void onComplete(Object object) {
                UserListResponse userListResponse = (UserListResponse) object;
                facebookFriendsAdapter = new FacebookFriendsAdapter(getActivity(), userListResponse, new ListItemClickListener() {
                    @Override
                    public void onClick(View view, FollowInfoResultArrayItem rowItem, int clickedPosition) {
                        displayUserProfile(rowItem, clickedPosition);
                    }
                });
                personRecyclerView.setAdapter(facebookFriendsAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                personRecyclerView.setLayoutManager(linearLayoutManager);
            }

            @Override
            public void onFailed(Exception e) {
                DialogBoxUtil.showErrorDialog(getActivity(), getActivity().getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                    @Override
                    public void okClick() {
                    }
                });
            }
        });
    }

    private void displayUserProfile(FollowInfoResultArrayItem rowItem, int clickedPosition) {

        if (!rowItem.getUserid().equals(AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid())) {
            if (mFragmentNavigation != null) {
                FollowInfoListItem followInfoListItem = new FollowInfoListItem(rowItem);
                followInfoListItem.setAdapter(facebookFriendsAdapter);
                followInfoListItem.setClickedPosition(clickedPosition);
                mFragmentNavigation.pushFragment(OtherProfileFragment.newInstance(followInfoListItem), ANIMATE_RIGHT_TO_LEFT);
            }
        } else {
            NextActivity.switchAndUpdateTabSelection(FragNavController.TAB5);
        }
    }

    public void updateAdapter(String searchText) {
        facebookFriendsAdapter.updateAdapter(searchText);
    }
}
