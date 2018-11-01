package com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.PostLikeListProcess;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.Adapters.PersonListAdapter;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.PersonListItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostHelper;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ListItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.FollowInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.OtherProfileFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.FollowInfo;
import catchu.model.FollowInfoResultArrayItem;
import catchu.model.User;
import catchu.model.UserListResponse;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_DOWN_TO_UP;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_UP_TO_DOWN;
import static com.uren.catchu.Constants.StringConstants.AWS_EMPTY;
import static com.uren.catchu.Constants.StringConstants.COMING_FOR_LIKE_LIST;


public class PersonListFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;
    PersonListAdapter personListAdapter;
    String toolbarTitle;
    String postId;
    String comingFor;
    String page;
    String perPage;

    @BindView(R.id.toolbarTitle)
    TextView txtToolbarTitle;

    @BindView(R.id.personList_recyclerView)
    RecyclerView personList_recyclerView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.imgBack)
    ClickableImageView imgBack;

    public static PersonListFragment newInstance(String toolbarTitle, String postId, String comingFor) {
        Bundle args = new Bundle();
        args.putString("toolbarTitle", toolbarTitle);
        args.putString("postId", postId);
        args.putString("comingFor", comingFor);
        PersonListFragment fragment = new PersonListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.person_list_fragment, container, false);
            ButterKnife.bind(this, mView);

            getItemsFromBundle();
            init();
            getPersonList();
        }


        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void getItemsFromBundle() {
        Bundle args = getArguments();
        if (args != null) {
            toolbarTitle = (String) args.getString("toolbarTitle");
            postId = (String) args.getString("postId");
            comingFor = (String) args.getString("comingFor");
        }
    }

    private void init() {

        if (toolbarTitle != null && !toolbarTitle.isEmpty()) {
            txtToolbarTitle.setText(toolbarTitle);
        }

        imgBack.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        if (v == imgBack) {

            ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
            getActivity().onBackPressed();

        }


    }

    private void getPersonList() {

        AccountHolderInfo.getToken(new TokenCallback() {

            @Override
            public void onTokenTaken(String token) {

                if(comingFor!= null && !comingFor.isEmpty()){

                    if(comingFor.equals(COMING_FOR_LIKE_LIST)){
                        startGetPersonList(token);
                    }else{
                        // lazım oldukca doldurulacak..
                    }

                }

            }

        });

    }

    private void startGetPersonList(String token) {

        String userId = AccountHolderInfo.getUserID();
        String postID = postId;
        String perPage = "100";
        String page = "1";
        String commentId = AWS_EMPTY;


        PostLikeListProcess postLikeListProcess = new PostLikeListProcess(getContext(), new OnEventListener<UserListResponse>() {
            @Override
            public void onSuccess(UserListResponse userListResponse) {

                if (userListResponse == null) {
                    CommonUtils.LOG_OK_BUT_NULL("PostLikeListProcess");
                } else {
                    CommonUtils.LOG_OK("PostLikeListProcess");
                    setUpRecyclerView(userListResponse);
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.LOG_FAIL("PostLikeListProcess", e.toString());
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onTaskContinue() {
                progressBar.setVisibility(View.VISIBLE);
            }
        }, userId, postID, perPage, page, commentId, token);

        postLikeListProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void setUpRecyclerView(UserListResponse personList) {

        PersonListItemClickListener personListItemClickListener = new PersonListItemClickListener() {
            @Override
            public void onClick(View view, User user, int clickedPosition) {
                CommonUtils.showToast(getContext(), "Clicked : " + user.getName());
                startPersonInfoProcess(user,clickedPosition);
            }
        };



        personListAdapter = new PersonListAdapter(getContext(), personList, personListItemClickListener);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        personList_recyclerView.setLayoutManager(mLayoutManager);
        personList_recyclerView.setAdapter(personListAdapter);

    }

    private void startPersonInfoProcess(User user, int clickedPosition) {

        FollowInfoResultArrayItem rowItem = new FollowInfoResultArrayItem();
        rowItem.setUserid(user.getUserid());
        rowItem.setProfilePhotoUrl(user.getProfilePhotoUrl());
        rowItem.setName(user.getName());

        FollowInfoListItem followInfoListItem = new FollowInfoListItem(rowItem);
        followInfoListItem.setAdapter(personListAdapter);
        followInfoListItem.setClickedPosition(clickedPosition);

        PostHelper.ProfileClicked.startProcess(getContext(), mFragmentNavigation, followInfoListItem);

    }


}
