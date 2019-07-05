package com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.PostLikeListProcess;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.Adapters.PersonListAdapter;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.PersonListItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostHelper;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.UserInfoListItem;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.User;
import catchu.model.UserListResponse;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.AWS_EMPTY;
import static com.uren.catchu.Constants.StringConstants.COMING_FOR_LIKE_LIST;


public class PersonListFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;
    PersonListAdapter personListAdapter;
    String toolbarTitle;
    String postId;
    String comingFor;
    String page = "1";
    String perPage = "5000";

    @BindView(R.id.personList_recyclerView)
    RecyclerView personList_recyclerView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.commonToolbarbackImgv)
    ClickableImageView commonToolbarbackImgv;

    @BindView(R.id.toolbarTitleTv)
    TextView toolbarTitleTv;

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
        ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
    }

    private void getItemsFromBundle() {
        Bundle args = getArguments();
        if (args != null) {
            toolbarTitle = args.getString("toolbarTitle");
            postId = args.getString("postId");
            comingFor = args.getString("comingFor");
        }
    }

    private void init() {

        if (toolbarTitle != null && !toolbarTitle.isEmpty()) {
            toolbarTitleTv.setText(toolbarTitle);
        }

        commonToolbarbackImgv.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        if (v == commonToolbarbackImgv) {
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

            @Override
            public void onTokenFail(String message) {
            }

        });

    }

    private void startGetPersonList(String token) {

        String userId = AccountHolderInfo.getUserID();
        String postID = postId;
        String perPage = this.perPage;
        String page = this.page;
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
            public void onPersonListItemClicked(View view, User user, int clickedPosition) {
                startPersonInfoProcess(user,clickedPosition);
            }
        };

        personListAdapter = new PersonListAdapter(getContext(), personList, personListItemClickListener);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        personList_recyclerView.setLayoutManager(mLayoutManager);
        personList_recyclerView.setAdapter(personListAdapter);

    }

    private void startPersonInfoProcess(User user, int clickedPosition) {

        UserInfoListItem userInfoListItem = new UserInfoListItem(user);
        userInfoListItem.setAdapter(personListAdapter);
        userInfoListItem.setClickedPosition(clickedPosition);

        PostHelper.ProfileClicked.startProcess(getContext(), mFragmentNavigation, userInfoListItem);

    }


}
