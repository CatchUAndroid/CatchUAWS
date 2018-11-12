package com.uren.catchu.MainPackage.MainFragments.Profile.UserShareManagement;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.GeneralUtils.ApiModelsProcess.UserGroupsProcess;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.Interfaces.RecyclerViewAdapterCallback;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.ViewGroupDetailFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.UserShareManagement.Adapters.UserGroupsPostAdapter;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.BucketUpload;
import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;

@SuppressLint("ValidFragment")
public class UserGroupsPostFragment extends BaseFragment {

    View mView;

    @BindView(R.id.groupsPostRecyclerView)
    RecyclerView groupsPostRecyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.warningMsgTv)
    TextView warningMsgTv;

    String userid;
    UserGroupsPostAdapter userGroupsPostAdapter;

    private static final int MARGING_GRID = 2;
    private static final int SPAN_COUNT = 3;

    public UserGroupsPostFragment(String userid) {
        this.userid = userid;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_user_groups_post_layout, container, false);
            ButterKnife.bind(this, mView);
            addListeners();
            setInitVariables();
            getUserGroups();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    public void addListeners() {

    }

    public void setInitVariables() {
        groupsPostRecyclerView.addItemDecoration(addItemDecoration());
        //groupsPostRecyclerView.setNestedScrollingEnabled(false);
    }

    public void getUserGroups() {
        progressBar.setVisibility(View.VISIBLE);
        UserGroupsProcess.getGroups(userid, new CompleteCallback() {
            @Override
            public void onComplete(Object object) {

                if(getContext() != null) {
                    userGroupsPostAdapter = new UserGroupsPostAdapter(getContext(), (GroupRequestResult) object, new ItemClickListener() {
                        @Override
                        public void onClick(Object object, int clickedItem) {
                            GroupRequestResultResultArrayItem selectedGroup = (GroupRequestResultResultArrayItem) object;


                        }
                    });
                    groupsPostRecyclerView.setAdapter(userGroupsPostAdapter);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), SPAN_COUNT);
                    groupsPostRecyclerView.setLayoutManager(gridLayoutManager);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailed(Exception e) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private RecyclerView.ItemDecoration addItemDecoration() {
        return new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = MARGING_GRID;
                outRect.right = MARGING_GRID;
                outRect.bottom = MARGING_GRID;
                if (parent.getChildLayoutPosition(view) >= 0 && parent.getChildLayoutPosition(view) <= 3) {
                    outRect.top = MARGING_GRID;
                }
            }
        };
    }
}
