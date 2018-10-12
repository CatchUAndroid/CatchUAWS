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
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ListItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.FollowInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.OtherProfileFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.FollowInfo;
import catchu.model.FollowInfoResultArrayItem;
import catchu.model.UserListResponse;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_DOWN_TO_UP;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_UP_TO_DOWN;


public class PersonListFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;
    FollowInfo followInfo;
    PersonListAdapter personListAdapter;

    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;

    @BindView(R.id.personList_recyclerView)
    RecyclerView personList_recyclerView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.imgBack)
    ClickableImageView imgBack;

    public static PersonListFragment newInstance(String toolbarTitle) {
        Bundle args = new Bundle();
        args.putString(ARGS_INSTANCE, toolbarTitle);
        PersonListFragment fragment = new PersonListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(mView == null){
            mView = inflater.inflate(R.layout.person_list_fragment, container, false);
            ButterKnife.bind(this, mView);



            setToolbarTitle();
            init();
            getPersonList();
        }


        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void setToolbarTitle() {
        Bundle args = getArguments();
        if (args != null) {
            toolbarTitle.setText((String) args.getString(ARGS_INSTANCE));
        }else{
            toolbarTitle.setText("List");
        }
    }

    private void init() {



        imgBack.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        if (v == imgBack) {

            ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_DOWN_TO_UP;
            getActivity().onBackPressed();

        }


    }

    private void getPersonList() {

        AccountHolderInfo.getToken(new TokenCallback() {

            @Override
            public void onTokenTaken(String token) {
                startGetPersonList(token);
            }

        });

    }

    private void startGetPersonList(String token) {

        String userId = AccountHolderInfo.getUserID();
        String postId;
        String perPage="10";
        String page="1";
        String commentId=null;

/*
        PostLikeListProcess postLikeListProcess = new PostLikeListProcess(getContext(), new OnEventListener<UserListResponse>() {
            @Override
            public void onSuccess(UserListResponse userListResponse) {

                if (userListResponse == null) {
                    CommonUtils.LOG_OK_BUT_NULL("PostLikeListProcess");
                } else {
                    CommonUtils.LOG_OK("PostLikeListProcess");
                    setUpRecyclerView(userListResponse);
                }
            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.LOG_FAIL("PostLikeListProcess", e.toString());
            }

            @Override
            public void onTaskContinue() {
            }
        }, userId, postId, perPage, page, commentId, token);

        postLikeListProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

*/
    }

    private void setUpRecyclerView(UserListResponse followInfo) {

        ListItemClickListener listItemClickListener = new ListItemClickListener() {
            @Override
            public void onClick(View view, FollowInfoResultArrayItem rowItem, int clickedPosition) {
                CommonUtils.showToast(getContext(), "Clicked : " + rowItem.getName());
                startPersonInfoProcess(rowItem, clickedPosition);
            }
        };

        //personListAdapter = new PersonListAdapter(getActivity(), followInfo.getResultArray(), listItemClickListener);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        personList_recyclerView.setLayoutManager(mLayoutManager);
        personList_recyclerView.setAdapter(personListAdapter);

    }

    private void startPersonInfoProcess(FollowInfoResultArrayItem rowItem, int clickedPosition) {

        if (mFragmentNavigation != null) {
            FollowInfoListItem followInfoListItem = new FollowInfoListItem(rowItem);
            mFragmentNavigation.pushFragment(OtherProfileFragment.newInstance(followInfoListItem), ANIMATE_RIGHT_TO_LEFT);
        }




    }


}
