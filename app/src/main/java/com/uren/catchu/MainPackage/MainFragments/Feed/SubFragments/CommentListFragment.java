package com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.PostCommentListProcess;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.Adapters.CommentListAdapter;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.PersonListItemClickListener;

import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostHelper;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.Utils;

import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.UserInfoListItem;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu._Libraries.SendCommentButton.SendCommentButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.Comment;
import catchu.model.CommentListResponse;
import catchu.model.User;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.AWS_EMPTY;


public class CommentListFragment extends BaseFragment
        implements View.OnClickListener, SendCommentButton.OnSendClickListener, PersonListItemClickListener {

    View mView;
    String postId;
    int position;
    CommentListAdapter commentListAdapter;
    LinearLayoutManager mLayoutManager;

    @BindView(R.id.toolbarLayout)
    Toolbar toolbarLayout;

    @BindView(R.id.toolbarTitle)
    TextView txtToolbarTitle;

    @BindView(R.id.contentRoot)
    LinearLayout contentRoot;
    @BindView(R.id.llAddComment)
    LinearLayout llAddComment;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.imgBack)
    ClickableImageView imgBack;

    @BindView(R.id.commentList_recyclerView)
    RecyclerView commentList_recyclerView;

    @BindView(R.id.edtAddComment)
    EditText edtAddComment;

    @BindView(R.id.btnSendComment)
    SendCommentButton btnSendComment;

    private int drawingStartLocation = 0;

    public static CommentListFragment newInstance(String postId, int position) {
        Bundle args = new Bundle();
        args.putString("postId", postId);
        args.putInt("position", position);
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

            if (savedInstanceState == null) {
                contentRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        contentRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                        startIntroAnimation();
                        return true;
                    }
                });
            }

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
            position = (Integer) args.getInt("position");
        }
    }

    private void init() {
        txtToolbarTitle.setText(R.string.comments);
        imgBack.setOnClickListener(this);
        btnSendComment.setOnSendClickListener(this);

        setLayoutManager();
        setAdapter();
    }

    private void setLayoutManager() {
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        commentList_recyclerView.setLayoutManager(mLayoutManager);
    }

    private void setAdapter() {
        commentListAdapter = new CommentListAdapter(getContext(), postId);
        commentList_recyclerView.setAdapter(commentListAdapter);
    }

    private void startIntroAnimation() {
        ViewCompat.setElevation(toolbarLayout, 0);
        contentRoot.setScaleY(0.1f);
        contentRoot.setPivotY(drawingStartLocation);
        llAddComment.setTranslationY(200);
    }

    @Override
    public void onClick(View v) {

        if (v == imgBack) {

            ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
            getActivity().onBackPressed();

        }

    }

    private void getCommentList() {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetCommentList(token);
            }

            @Override
            public void onTokenFail(String message) {
            }
        });

    }

    private void startGetCommentList(String token) {

        String userId = AccountHolderInfo.getUserID();
        String postID = postId;
        String commentId = AWS_EMPTY;

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

        commentListAdapter.setPersonListItemClickListener(this);
        commentListAdapter.addAll(commentList.getItems());


        contentRoot.animate()
                .scaleY(1)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewCompat.setElevation(toolbarLayout, Utils.dpToPx(8));
                        animateContent();

                    }
                })
                .start();


    }

    private void animateContent() {
        commentListAdapter.updateItems();
        llAddComment.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .start();
    }

    private void startPersonInfoProcess(User user, int clickedPosition) {
        UserInfoListItem userInfoListItem = new UserInfoListItem(user);
        PostHelper.ProfileClicked.startProcess(getContext(), mFragmentNavigation, userInfoListItem);
    }


    private Comment createCommentBody() {

        User user = new User();
        user.setUserid(AccountHolderInfo.getUserID());
        user.setName(AccountHolderInfo.getInstance().getUser().getUserInfo().getName());
        user.setUsername(AccountHolderInfo.getInstance().getUser().getUserInfo().getUsername());
        user.setProfilePhotoUrl(AccountHolderInfo.getInstance().getUser().getUserInfo().getProfilePhotoUrl());

        Comment comment = new Comment();
        comment.setMessage(edtAddComment.getText().toString());
        comment.setLikeCount(0);
        comment.setIsLiked(false);
        comment.setCreateAt("Just now");
        comment.setUser(user);
        return comment;
    }


    private boolean validateComment() {
        if (TextUtils.isEmpty(edtAddComment.getText())) {
            btnSendComment.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_error));
            return false;
        }

        return true;
    }

    @Override
    public void onSendClickListener(View v) {
        if (validateComment()) {
            Comment comment = createCommentBody();
            PostHelper.AddComment.startProcess(getContext(), postId, comment, position);
            commentListAdapter.add(comment);
            commentListAdapter.setAnimationsLocked(false);
            commentListAdapter.setDelayEnterAnimation(false);

            commentList_recyclerView.smoothScrollToPosition(commentListAdapter.getItemCount());
            edtAddComment.setText(null);
            btnSendComment.setCurrentState(SendCommentButton.STATE_DONE);
        }
    }

    @Override
    public void onPersonListItemClicked(View view, User user, int clickedPosition) {
        startPersonInfoProcess(user, clickedPosition);
    }
}
