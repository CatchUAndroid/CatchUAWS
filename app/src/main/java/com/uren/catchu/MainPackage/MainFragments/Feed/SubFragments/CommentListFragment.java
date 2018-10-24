package com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.PostCommentListProcess;
import com.uren.catchu.ApiGatewayFunctions.PostLikeListProcess;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.Adapters.CommentListAdapter;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.PersonListItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostHelper;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.FollowInfoListItem;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.Comment;
import catchu.model.CommentListResponse;
import catchu.model.FollowInfoResultArrayItem;
import catchu.model.User;
import catchu.model.UserListResponse;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_DOWN_TO_UP;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;


public class CommentListFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;
    String postId;
    CommentListAdapter commentListAdapter;

    @BindView(R.id.toolbarTitle)
    TextView txtToolbarTitle;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.imgBack)
    ClickableImageView imgBack;

    @BindView(R.id.commentList_recyclerView)
    RecyclerView commentList_recyclerView;

    @BindView(R.id.edtAddComment)
    EditText edtAddComment;

    @BindView(R.id.btnSendComment)
    Button btnSendComment;

    public static CommentListFragment newInstance(String postId) {
        Bundle args = new Bundle();
        args.putString("postId", postId);
        CommentListFragment fragment = new CommentListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.comment_list_fragment, container, false);
            ButterKnife.bind(this, mView);

            getItemsFromBundle();
            init();
            getCommentList();
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
            postId = (String) args.getString("postId");
        }
    }

    private void init() {

        txtToolbarTitle.setText(R.string.comments);
        imgBack.setOnClickListener(this);
        btnSendComment.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        if (v == imgBack) {

            ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
            getActivity().onBackPressed();

        }

        if(v == btnSendComment){
            sendCommentClicked();
        }

    }

    private void getCommentList() {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetCommentList(token);
            }
        });

    }

    private void startGetCommentList(String token) {

        String userId = AccountHolderInfo.getUserID();
        String postID = postId;
        String commentId = null;

        PostCommentListProcess postCommentListProcess = new PostCommentListProcess(getContext(), new OnEventListener<CommentListResponse>() {

            @Override
            public void onSuccess(CommentListResponse commentListResponse) {
                if (commentListResponse == null) {
                    CommonUtils.LOG_OK_BUT_NULL("PostCommentListProcess");
                } else {
                    CommonUtils.LOG_OK("PostCommentListProcess");
                    setUpRecyclerView(commentListResponse);
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.LOG_FAIL("PostCommentListProcess", e.toString());
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onTaskContinue() {
                progressBar.setVisibility(View.VISIBLE);
            }
        }, userId, postID, commentId, token);

        postCommentListProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void setUpRecyclerView(CommentListResponse commentList) {

        commentListAdapter = new CommentListAdapter(getActivity(), commentList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        commentList_recyclerView.setLayoutManager(mLayoutManager);
        commentList_recyclerView.setAdapter(commentListAdapter);

        commentListAdapter.setPersonListItemClickListener(new PersonListItemClickListener() {
            @Override
            public void onClick(View view, User user, int clickedPosition) {
                startPersonInfoProcess(user,clickedPosition);
            }
        });

    }

    private void startPersonInfoProcess(User user, int clickedPosition) {

        FollowInfoResultArrayItem rowItem = new FollowInfoResultArrayItem();
        rowItem.setUserid(user.getUserid());
        rowItem.setProfilePhotoUrl(user.getProfilePhotoUrl());
        rowItem.setName(user.getName());

        FollowInfoListItem followInfoListItem = new FollowInfoListItem(rowItem);
        followInfoListItem.setAdapter(commentListAdapter);
        followInfoListItem.setClickedPosition(clickedPosition);

        PostHelper.ProfileClicked.startProcess(getContext(), mFragmentNavigation, followInfoListItem);

    }

    private void sendCommentClicked() {
        Comment comment = createCommentBody();
        PostHelper.AddComment.startProcess(getContext(), postId, comment);
        commentListAdapter.add(comment);
    }

    private Comment createCommentBody() {

        User user = new User();
        user.setUserid(AccountHolderInfo.getUserID());
        user.setName(AccountHolderInfo.getInstance().getUser().getUserInfo().getName());
        user.setUsername(AccountHolderInfo.getInstance().getUser().getUserInfo().getUsername());
        user.setProfilePhotoUrl(AccountHolderInfo.getInstance().getUser().getUserInfo().getProfilePhotoUrl());

        Comment comment = new Comment() ;
        comment.setMessage("message");
        comment.setUser(user);
        return comment;
    }

}
