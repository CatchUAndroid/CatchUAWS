package com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.uren.catchu.Adapters.LocationTrackerAdapter;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.PostCommentListProcess;

import com.uren.catchu.ApiGatewayFunctions.PostListResponseProcess;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;

import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;

import com.uren.catchu.MainPackage.MainFragments.Feed.Adapters.SinglePostAdapter;

import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.CommentAllowedCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.PersonListItemClickListener;

import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.PostDeletedCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostHelper;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.SinglePostItemAnimator;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.SingletonSinglePost;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.UserInfoListItem;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.MainPackage.MainFragments.Share.Interfaces.LocationCallback;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu._Libraries.SendCommentButton.SendCommentButton;
import com.uren.catchu._Libraries.VideoPlay.CustomRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import catchu.model.Comment;
import catchu.model.CommentListResponse;

import catchu.model.Post;
import catchu.model.PostListResponse;
import catchu.model.User;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

import static com.uren.catchu.Constants.NumericConstants.FILTERED_FEED_RADIUS;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.AWS_EMPTY;
import static com.uren.catchu.Constants.StringConstants.CREATE_AT_NOW;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_ALL_FOLLOWERS;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_CUSTOM;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_EVERYONE;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_GROUP;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_SELF;


public class SinglePostFragment extends BaseFragment
        implements View.OnClickListener,
        SendCommentButton.OnSendClickListener,
        PersonListItemClickListener,
        CommentAllowedCallback,
        PostDeletedCallback{

    private View mView;
    private String toolbarTitle;
    private Post post;
    private String postId;
    private int position;
    private int numberOfCallback;
    private SinglePostAdapter singlePostAdapter;
    private LinearLayoutManager mLayoutManager;

    @BindView(R.id.rootLayout)
    RelativeLayout rootLayout;

    @BindView(R.id.refresh_layout)
    RecyclerRefreshLayout refresh_layout;

    @BindView(R.id.rv_single_post)
    CustomRecyclerView recyclerView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.smileyImgv)
    ImageView smileyImgv;
    @BindView(R.id.edittextRelLayout)
    RelativeLayout edittextRelLayout;

    //toolbar
    @BindView(R.id.commonToolbarbackImgv)
    ClickableImageView commonToolbarbackImgv;
    @BindView(R.id.toolbarTitleTv)
    TextView toolbarTitleTv;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    //Content
    @BindView(R.id.contentRoot)
    LinearLayout contentRoot;
    @BindView(R.id.llAddComment)
    LinearLayout llAddComment;
    @BindView(R.id.edtAddComment)
    EmojiconEditText edtAddComment;
    @BindView(R.id.btnSendComment)
    SendCommentButton btnSendComment;

    //Location
    private LocationTrackerAdapter locationTrackObj;
    private PermissionModule permissionModule;
    private String longitude;
    private String latitude;
    private String radius;

    private boolean pulledToRefreshPost = false;
    private boolean pulledToRefreshComment = false;
    private List<Post> postList = new ArrayList<Post>();
    private List<Comment> commentList = new ArrayList<Comment>();

    public static SinglePostFragment newInstance(String toolbarTitle, String postId, int position, int numberOfCallback) {
        Bundle args = new Bundle();
        args.putString("toolbarTitle", toolbarTitle);
        args.putString("postId", postId);
        args.putInt("position", position);
        args.putInt("numberOfCallback", numberOfCallback);
        SinglePostFragment fragment = new SinglePostFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        getActivity().findViewById(R.id.tabMainLayout).setVisibility(View.GONE);
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.single_post_fragment, container, false);
            ButterKnife.bind(this, mView);

            //input for the fragment
            post = SingletonSinglePost.getInstance().getPost();
            getItemsFromBundle();

            //setting content
            init();
            setContent();

        }

        return mView;
    }

    private void getItemsFromBundle() {
        Bundle args = getArguments();
        if (args != null) {
            toolbarTitle = (String) args.getString("toolbarTitle");
            postId = (String) args.getString("postId");
            position = (Integer) args.getInt("position");
            numberOfCallback = (Integer) args.getInt("numberOfCallback");
        }
    }

    private void init() {

        if (validOperation()) {
            setVariables();
            setLayoutManager();
            setAdapter();
            setPullToRefresh();
        }

    }

    private void setContent() {

        //sadece postId ile gelindiğinde
        if (post == null && postId != null) {
            checkLocationAndRetrievePosts();
        } else {
            //post ile gelindiğinde
            if (post != null) {
                fillContent(post);
            }
        }
    }

    private void checkLocationAndRetrievePosts() {
        permissionModule = new PermissionModule(getContext());
        initLocationTracker();
        checkCanGetLocation();
    }

    private void fillContent(Post post) {
        setListeners();
        setPostDetail(post);
        getCommentList();
    }

    private void initLocationTracker() {
        locationTrackObj = new LocationTrackerAdapter(getContext(), new LocationCallback() {
            @Override
            public void onLocationChanged(Location location) {
            }
        });
    }

    private void checkCanGetLocation() {

        if (!locationTrackObj.canGetLocation())
            //gps ve network provider olup olmadığı kontrol edilir
            DialogBoxUtil.showSettingsAlert(getActivity());
        else {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (permissionModule.checkAccessFineLocationPermission()) {
                    getPost();
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PermissionModule.PERMISSION_ACCESS_FINE_LOCATION);
                }
            } else {
                getPost();
            }
        }
    }

    private boolean validOperation() {
        if (post == null && postId == null) {
            return false;
        } else {
            return true;
        }
    }

    private void setVariables() {

        //toolbar
        toolbar.setBackground(getResources().getDrawable(R.color.white, null));
        toolbarTitleTv.setText(getContext().getResources().getString(R.string.detail));
        toolbarTitleTv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary)) ;

        //imgBack
        commonToolbarbackImgv.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
        commonToolbarbackImgv.setOnClickListener(this);

        //Comment Allowed
        llAddComment.setVisibility(View.GONE);

        //Emojicon
        EmojIconActions emojIcon = new EmojIconActions(getContext(), rootLayout, edtAddComment, smileyImgv);
        emojIcon.ShowEmojIcon();

        smileyImgv.setColorFilter(this.getResources().getColor(R.color.Gray, null), PorterDuff.Mode.SRC_IN);
        edittextRelLayout.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.Gray, null), GradientDrawable.RECTANGLE, 50, 2));

    }

    private void setListeners() {

        btnSendComment.setOnSendClickListener(this);

        //Comment Allowed
        if (!post.getIsCommentAllowed()) {
            llAddComment.setVisibility(View.GONE);
        } else {
            llAddComment.setVisibility(View.VISIBLE);
        }

    }

    private void setLayoutManager() {
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new SinglePostItemAnimator());
    }

    private void setAdapter() {
        singlePostAdapter = new SinglePostAdapter(getActivity(), getContext(), mFragmentNavigation, position, numberOfCallback);
        singlePostAdapter.setPersonListItemClickListener(this);
        singlePostAdapter.setCommentAllowedCallback(this);
        singlePostAdapter.setPostDeletedCallback(this);
        recyclerView.setAdapter(singlePostAdapter);
    }

    private void setPullToRefresh() {
        refresh_layout.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setPaginationValues();
                checkLocationAndRetrievePosts();
            }
        });
    }

    private void setPaginationValues() {
        pulledToRefreshPost = true;
        pulledToRefreshComment = true;
        float radiusInKm = (float) ((double) FILTERED_FEED_RADIUS / (double) 1000);
        radius = String.valueOf(radiusInKm);
    }


    @Override
    public void onClick(View v) {

        if (v == commonToolbarbackImgv) {
            SingletonSinglePost.getInstance().setPost(null);
            ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
            getActivity().onBackPressed();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermissionModule.PERMISSION_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(getContext(), " ACCESS_FINE_LOCATION - Permission granted", Toast.LENGTH_SHORT).show();
                    getPost();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    CommonUtils.showToastShort(getContext(), getContext().getResources().getString(R.string.needLocationPermission));
                    refresh_layout.setRefreshing(false);
                }

            }

            // other 'case' lines to check for other
            // permissions this app might request

        }

    }

    private void getPost() {

        //get post detail...
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(final String token) {
                Location location = locationTrackObj.getLocation();
                if (location != null) {
                    startGetPost(token);
                } else {
                    //showPulsatorLayout(false);
                    //showNoFeedLayout(true, R.string.locationError );
                    refresh_layout.setRefreshing(false);
                }
            }
        });

    }

    private void startGetPost(String token) {

        setLocationInfo();

        String sUserId = AccountHolderInfo.getUserID();
        String sPostId = postId;
        String sCatchType = "";
        String sLongitude = longitude;
        String sLatitude = latitude;
        String sRadius = radius;
        String sPerpage = String.valueOf(1);
        String sPage = String.valueOf(1);

        PostListResponseProcess postListResponseProcess = new PostListResponseProcess(getContext(), new OnEventListener<PostListResponse>() {
            @Override
            public void onSuccess(final PostListResponse postListResponse) {
                if (postListResponse == null) {
                    CommonUtils.LOG_OK_BUT_NULL("PostListResponseProcess");
                } else {
                    CommonUtils.LOG_OK("PostListResponseProcess");
                    if (postListResponse.getItems().size() == 0) {
                        //no such data - post bulunamadi
                        CommonUtils.showToastShort(getContext(), getString(R.string.notFound));
                    } else {
                        post = postListResponse.getItems().get(0);
                        fillContent(postListResponse.getItems().get(0));
                    }
                }

                progressBar.setVisibility(View.GONE);
                refresh_layout.setRefreshing(false);

            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.LOG_FAIL("PostListResponseProcess", e.toString());
                progressBar.setVisibility(View.GONE);
                refresh_layout.setRefreshing(false);

            }

            @Override
            public void onTaskContinue() {

                if (!pulledToRefreshPost) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        }, sUserId, sPostId, sCatchType, sLongitude, sLatitude, sRadius, sPerpage, sPage, token);

        postListResponseProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void setLocationInfo() {
        longitude = String.valueOf(locationTrackObj.getLocation().getLongitude());
        latitude = String.valueOf(locationTrackObj.getLocation().getLatitude());
    }

    private void setPostDetail(Post post) {

        postList.clear();
        postList.add(post);

        if (pulledToRefreshPost) {
            singlePostAdapter.updatePostListItems(postList);
            pulledToRefreshPost = false;
        } else {
            singlePostAdapter.addAll(postList, null);
        }

    }

    private void getCommentList() {

        if (post.getIsCommentAllowed()) {
            AccountHolderInfo.getToken(new TokenCallback() {
                @Override
                public void onTokenTaken(String token) {
                    startGetCommentList(token);
                }
            });
        } else {
            commentDisabled();
        }

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
                    setRecyclerViewComments(commentListResponse);
                }
            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.LOG_FAIL("PostCommentListProcess", e.toString());
                singlePostAdapter.removeProgressLoading();
            }

            @Override
            public void onTaskContinue() {
                //progressBar.setVisibility(View.VISIBLE);
                if (!pulledToRefreshComment) {
                    singlePostAdapter.addProgressLoading();
                }

            }
        }, userId, postID, commentId, token);

        postCommentListProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void setRecyclerViewComments(CommentListResponse commentListResponse) {

        commentList.clear();
        commentList.addAll(commentListResponse.getItems());
        //commentList = reverseList(commentListResponse.getItems());

        if (pulledToRefreshComment) {
            singlePostAdapter.updateCommentListItems(commentList);
            pulledToRefreshComment = false;
        } else {
            singlePostAdapter.removeProgressLoading(); // pulled to refresh değilse progress eklenip/kaldırılıyor
            singlePostAdapter.addAll(null, commentList);
        }
    }

    private List<Comment> reverseList(List<Comment> items) {

        List<Comment> reverseList = new ArrayList<Comment>();
        for (int i = items.size(); i > 0; i--) {
            reverseList.add(items.get(i - 1));
        }
        return reverseList;

    }

    @Override
    public void onSendClickListener(View v) {
        if (validateComment()) {
            Comment comment = createCommentBody();
            post.setCommentCount(post.getCommentCount() + 1);
            singlePostAdapter.addComment(comment);
            singlePostAdapter.updateCommentCount(post.getCommentCount());

            recyclerView.smoothScrollToPosition(singlePostAdapter.getItemCount());
            edtAddComment.setText(null);
            btnSendComment.setCurrentState(SendCommentButton.STATE_DONE);

            PostHelper.AddComment.startProcess(getContext(), postId, comment, position);
            PostHelper.SinglePostClicked.postCommentCountChanged(position, post.getCommentCount(), numberOfCallback);
        }
    }

    private boolean validateComment() {
        if (TextUtils.isEmpty(edtAddComment.getText())) {
            btnSendComment.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_error));
            return false;
        }

        return true;
    }

    @Override
    public void onPersonListItemClicked(View view, User user, int clickedPosition) {
        startPersonInfoProcess(user, clickedPosition);
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
        comment.setCreateAt(CREATE_AT_NOW);
        comment.setUser(user);
        return comment;
    }


    @Override
    public void onCommentAllowedStatusChanged(boolean isCommentAllowed) {

        post.setIsCommentAllowed(isCommentAllowed);

        if (isCommentAllowed) {
            getCommentList();
            llAddComment.setVisibility(View.VISIBLE);
        } else {
            commentDisabled();
        }

    }

    private void commentDisabled() {
        pulledToRefreshComment = true;
        CommentListResponse commentListResponse = new CommentListResponse();
        List<Comment> items = new ArrayList<Comment>();
        commentListResponse.setItems(items);
        singlePostAdapter.removeAllComments();

        setRecyclerViewComments(commentListResponse);
        llAddComment.setVisibility(View.GONE);
    }


    @Override
    public void onPostDeleted() {
        commonToolbarbackImgv.callOnClick();
    }
}
