package com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SimpleItemAnimator;
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
import android.widget.TextView;
import android.widget.Toast;

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

import com.uren.catchu.MainPackage.MainFragments.BaseFragment;

import com.uren.catchu.MainPackage.MainFragments.Feed.Adapters.SinglePostAdapter;

import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.PersonListItemClickListener;

import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.FeedItemAnimator;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostHelper;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.SinglePost;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.SinglePostItemAnimator;
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

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.AWS_EMPTY;
import static com.uren.catchu.Constants.StringConstants.CREATE_AT_NOW;


public class SinglePostFragment extends BaseFragment
        implements View.OnClickListener, SendCommentButton.OnSendClickListener, PersonListItemClickListener {

    View mView;
    String toolbarTitle;
    Post post;
    String postId;
    int position;
    int numberOfCallback;
    SinglePostAdapter singlePostAdapter;
    LinearLayoutManager mLayoutManager;

    //toolbar
    ImageView imgProfilePic;
    TextView txtProfilePic;
    TextView txtUserName;
    TextView txtCreateAt;
    ImageView imgLike;
    boolean isPostLiked = false;
    int likeCount = 0;

    @BindView(R.id.refresh_layout)
    RecyclerRefreshLayout refresh_layout;

    @BindView(R.id.rv_single_post)
    CustomRecyclerView recyclerView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.imgBack)
    ClickableImageView imgBack;

    @BindView(R.id.edtAddComment)
    EditText edtAddComment;

    @BindView(R.id.btnSendComment)
    SendCommentButton btnSendComment;

    @BindView(R.id.contentRoot)
    LinearLayout contentRoot;
    @BindView(R.id.llAddComment)
    LinearLayout llAddComment;
    @BindView(R.id.toolbarLayout)
    Toolbar toolbarLayout;
    @BindView(R.id.llProfilePic)
    LinearLayout llProfilePic;
    @BindView(R.id.llUserName)
    LinearLayout llUserName;

    //Location
    private LocationTrackerAdapter locationTrackObj;
    PermissionModule permissionModule;
    String longitude;
    String latitude;
    String radius;

    private boolean pulledToRefresh = false;
    private List<Post> postList = new ArrayList<Post>();
    private int drawingStartLocation = 0;

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
        NextActivity.bottomTabLayout.setVisibility(View.GONE);
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.single_post_fragment, container, false);
            ButterKnife.bind(this, mView);

            //input for the fragment
            post = SinglePost.getInstance().getPost();
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

        Log.i("nrlh_postId", post.getPostid());

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
        setPostDetailOnToolbar();
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

    private void setPostDetailOnToolbar() {

        //profile picture
        UserDataUtil.setProfilePicture2(getContext(), post.getUser().getProfilePhotoUrl(),
                post.getUser().getName(), post.getUser().getUsername(), txtProfilePic, imgProfilePic);

        //Username
        if (post.getUser().getUsername() != null && !post.getUser().getUsername().isEmpty()) {
            this.txtUserName.setText(post.getUser().getUsername());
        }
        //likeCount
        likeCount = post.getLikeCount();
        //isLiked
        isPostLiked = post.getIsLiked();
        //Like
        if (post.getIsLiked()) {
            setLikeIconUI(R.color.likeButtonColor, R.mipmap.icon_like_filled, false);
        } else {
            setLikeIconUI(R.color.black, R.mipmap.icon_like, false);
        }
        //create At
        if (post.getCreateAt() != null) {
            String text = CommonUtils.timeAgo(getContext(), post.getCreateAt());
            txtCreateAt.setText(text);
        }


    }

    private void setVariables() {

        if (toolbarTitle != null && !toolbarTitle.isEmpty()) {
            //txtToolbarTitle.setText(toolbarTitle);
        }

        imgProfilePic = (ImageView) mView.findViewById(R.id.imgProfilePic);
        txtProfilePic = (TextView) mView.findViewById(R.id.txtProfilePic);
        txtUserName = (TextView) mView.findViewById(R.id.txtUserName);
        txtCreateAt = (TextView) mView.findViewById(R.id.txtCreateAt);
        imgLike = (ImageView) mView.findViewById(R.id.imgLike);

        //imgBack
        imgBack.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
        imgBack.setOnClickListener(this);

    }

    private void setListeners() {

        btnSendComment.setOnSendClickListener(this);

        //Profile layout
        llProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPersonInfoProcess(post.getUser(), 0);
            }
        });
        llUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPersonInfoProcess(post.getUser(), 0);
            }
        });

        //imgLike
        imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imgLike.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));

                if (isPostLiked) {
                    isPostLiked = false;
                    post.setIsLiked(false);
                    post.setLikeCount(post.getLikeCount() - 1);
                    setLikeIconUI(R.color.black, R.mipmap.icon_like, true);
                } else {
                    isPostLiked = true;
                    post.setIsLiked(true);
                    post.setLikeCount(post.getLikeCount() + 1);
                    setLikeIconUI(R.color.likeButtonColor, R.mipmap.icon_like_filled, true);
                }
                PostHelper.LikeClicked.startProcess(getContext(), post.getPostid(), null, isPostLiked);
                PostHelper.SinglePostClicked.postLikeStatusChanged(isPostLiked, post.getLikeCount(), position, numberOfCallback);

                //singlePostAdapter.updateLikeCount(post.getLikeCount());
                SinglePostAdapter.PostViewHolder viewHolderForLayoutPosition = (SinglePostAdapter.PostViewHolder) recyclerView.findViewHolderForLayoutPosition(0);
                viewHolderForLayoutPosition.setPartialData(post, 0);

            }
        });

    }

    private void setLikeIconUI(int color, int icon, boolean isClientOperation) {
        imgLike.setColorFilter(ContextCompat.getColor(getContext(), color), android.graphics.PorterDuff.Mode.SRC_IN);
        imgLike.setImageResource(icon);

        if (isClientOperation) {
            if (isPostLiked) {
                likeCount++;
                post.setLikeCount(likeCount);
            } else {
                likeCount--;
                post.setLikeCount(likeCount);
            }
        }

        //txtLikeCount.setText(String.valueOf(likeCount));

    }

    private void setLayoutManager() {
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void setAdapter() {
        singlePostAdapter = new SinglePostAdapter(getActivity(), getContext(), mFragmentNavigation);
        singlePostAdapter.setPersonListItemClickListener(this);
        recyclerView.setAdapter(singlePostAdapter);

    }

    private void setPullToRefresh() {
        refresh_layout.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setPaginationValues();
                refresh_layout.setRefreshing(false);
            }
        });
    }

    private void setPaginationValues() {
        pulledToRefresh = true;
    }


    @Override
    public void onClick(View v) {

        if (v == imgBack) {
            SinglePost.getInstance().setPost(null);
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
                    Toast.makeText(getApplicationContext(), " ACCESS_FINE_LOCATION - Permission granted", Toast.LENGTH_SHORT).show();
                    getPost();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    CommonUtils.showToast(getContext(), getContext().getResources().getString(R.string.needLocationPermission));
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
                        //no such data
                    }
                    fillContent(postListResponse.getItems().get(0));
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

                if (!pulledToRefresh) {
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

        if (pulledToRefresh) {
            //feedAdapter.updatePostListItems(postListResponse.getItems());
            pulledToRefresh = false;
        } else {
            singlePostAdapter.addAll(postList, null);
        }


    }

    private void animateContent() {
        singlePostAdapter.updateItems();
        llAddComment.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .start();
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
        String commentId = AWS_EMPTY;

        PostCommentListProcess postCommentListProcess = new PostCommentListProcess(getContext(), new OnEventListener<CommentListResponse>() {

            @Override
            public void onSuccess(CommentListResponse commentListResponse) {
                if (commentListResponse == null) {
                    CommonUtils.LOG_OK_BUT_NULL("PostCommentListProcess");
                } else {
                    CommonUtils.LOG_OK("PostCommentListProcess");
                    setComments(commentListResponse);
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
                singlePostAdapter.addProgressLoading();
            }
        }, userId, postID, commentId, token);

        postCommentListProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void setComments(CommentListResponse commentListResponse) {
        singlePostAdapter.removeProgressLoading();
        singlePostAdapter.addAll(null, commentListResponse.getItems());
    }

    @Override
    public void onSendClickListener(View v) {
        if (validateComment()) {
            Comment comment = createCommentBody();
            post.setCommentCount(post.getCommentCount() + 1);
            singlePostAdapter.addComment(comment);

            //singlePostAdapter.updateLikeCount(post.getLikeCount());
            SinglePostAdapter.PostViewHolder viewHolderForLayoutPosition = (SinglePostAdapter.PostViewHolder) recyclerView.findViewHolderForLayoutPosition(0);
            viewHolderForLayoutPosition.setPartialData(post, 0);


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


}
