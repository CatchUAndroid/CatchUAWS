package com.uren.catchu.MainPackage.MainFragments.Profile.OtherProfile.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.CustomDialogBox;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.CustomDialogListener;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.YesNoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.GridViewUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.LoginPackage.Models.LoginUser;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.FollowClickCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.ShowSelectedPhotoFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.OtherProfile.JavaClasses.OtherProfilePostList;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.UserInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Activities.MessageWithPersonActivity;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.FollowerFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.FollowingFragment;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu._Libraries.LayoutManager.CustomGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import catchu.model.Post;
import catchu.model.User;
import catchu.model.UserProfile;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.FCM_CODE_CHATTED_USER;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_FOLLOWING;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_NONE;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_PENDING;
import static com.uren.catchu.Constants.StringConstants.FRIEND_CREATE_FOLLOW_DIRECTLY;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_FOLLOW;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_PENDING_FOLLOW_REQUEST;
import static com.uren.catchu.Constants.StringConstants.FRIEND_FOLLOW_REQUEST;

public class OtherProfileAdapter extends RecyclerView.Adapter {

    public static final int VIEW_HEADER = 0;
    public static final int VIEW_ITEM = 1;
    public static final int VIEW_PROG = 2;
    public static final int VIEW_LAST_ITEM = 3;

    private Activity mActivity;
    private Context mContext;
    private List<Object> objectList;
    private List<Post> postList;
    private List<Post> addedPostList;
    private BaseFragment.FragmentNavigation fragmentNavigation;
    private int pageCnt;
    private FollowClickCallback followClickCallback;
    private User selectedUser;

    private static final int OPERATION_TYPE_NONE = -1;
    private static final int OPERATION_TYPE_LOAD_MORE = 0;
    private static final int OPERATION_TYPE_UPDATE_POST = 1;
    private static final int OPERATION_TYPE_PAGE_COUNT = 2;
    private int operationType = OPERATION_TYPE_NONE;

    public OtherProfileAdapter(Activity activity, Context context, BaseFragment.FragmentNavigation fragmentNavigation, int pageCnt) {
        this.mActivity = activity;
        this.mContext = context;
        this.fragmentNavigation = fragmentNavigation;
        this.pageCnt = pageCnt;
        this.objectList = new ArrayList<Object>();
        this.postList = new ArrayList<Post>();
        this.addedPostList = new ArrayList<Post>();
    }

    @Override
    public int getItemViewType(int position) {

        if (isPositionHeader(position)) {
            return VIEW_HEADER;
        } else if (objectList.get(position) instanceof Post) {
            return VIEW_ITEM;
        } else if (objectList.get(position) instanceof ProgressBar) {
            return VIEW_PROG;
        } else if (objectList.get(position) instanceof String) {
            return VIEW_LAST_ITEM;
        } else {
            return VIEW_PROG;
        }
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;

        if (viewType == VIEW_HEADER) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.otherprofile_header_view, parent, false);

            viewHolder = new ProfileHeaderViewHolder(itemView);
        } else if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.profile_shared_post_view, parent, false);

            viewHolder = new PostViewHolder(itemView);
        } else if (viewType == VIEW_LAST_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.recycler_row, parent, false);

            viewHolder = new LastItemViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar_item, parent, false);

            viewHolder = new ProgressViewHolder(itemView);
        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
        super.onBindViewHolder(holder, position, payloads);

        if (payloads.isEmpty()) {
            // Perform a full update
            onBindViewHolder(holder, position);
        } else {
            // Perform a partial update
            for (Object payload : payloads) {
                if (payload instanceof UserProfile) {
                    if (holder instanceof ProfileHeaderViewHolder) {
                        UserProfile userProfile = (UserProfile) payload;
                        ((ProfileHeaderViewHolder) holder).updateUserProfile(userProfile, position);
                    }
                }

                if (payload instanceof Post) {
                    if (holder instanceof PostViewHolder) {

                        if (operationType == OPERATION_TYPE_UPDATE_POST) {
                            ((PostViewHolder) holder).updatePostList(position);
                        } else if (operationType == OPERATION_TYPE_LOAD_MORE) {
                            ((PostViewHolder) holder).loadMorePost();
                        } else if (operationType == OPERATION_TYPE_PAGE_COUNT) {
                            ((PostViewHolder) holder).updatePageCount();
                        }

                        operationType = OPERATION_TYPE_NONE;
                    }
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ProfileHeaderViewHolder) {
            UserInfoListItem userInfoListItem = (UserInfoListItem) objectList.get(position);
            ((ProfileHeaderViewHolder) holder).setData(userInfoListItem, position);
        } else if (holder instanceof PostViewHolder) {
            Post post = (Post) objectList.get(position);
            ((PostViewHolder) holder).setData(post, position);
        } else if (holder instanceof LastItemViewHolder) {
            String s = (String) objectList.get(position);
            ((LastItemViewHolder) holder).setData(s, position);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public class ProfileHeaderViewHolder extends RecyclerView.ViewHolder {

        View mView;
        RelativeLayout profilePicLayout;
        ImageView imgProfile;
        ImageView imgInfo;
        TextView txtProfile;
        TextView txtName;
        Button btnFollowStatus;
        TextView txtFollowerCnt;
        TextView txtFollowingCnt;
        LinearLayout followersLayout;
        LinearLayout followingsLayout;

        UserProfile fetchedUser;
        String followStatus;
        int followingCount, followerCount;

        Button sendMessageBtn;

        public ProfileHeaderViewHolder(View view) {
            super(view);

            mView = view;
            profilePicLayout = view.findViewById(R.id.profilePicLayout);
            imgProfile = (ImageView) view.findViewById(R.id.imgProfile);
            imgInfo = (ImageView) view.findViewById(R.id.imgInfo);
            txtProfile = (TextView) view.findViewById(R.id.txtProfile);
            txtName = (TextView) view.findViewById(R.id.txtName);
            btnFollowStatus = (Button) view.findViewById(R.id.btnFollowStatus);
            txtFollowerCnt = (TextView) view.findViewById(R.id.txtFollowerCnt);
            txtFollowingCnt = (TextView) view.findViewById(R.id.txtFollowingCnt);
            sendMessageBtn = (Button) view.findViewById(R.id.sendMessageBtn);
            followersLayout = (LinearLayout) view.findViewById(R.id.followersLayout);
            followingsLayout = (LinearLayout) view.findViewById(R.id.followingsLayout);

            //txtFollowerCnt.setClickable(false);
            //txtFollowingCnt.setClickable(false);

            setListeners();
        }

        private void setListeners() {
            //Button follow status
            btnFollowStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnFollowStatusClicked();
                }
            });

            sendMessageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startMessageWithPersonFragment();
                }
            });

            //imgInfo
            imgInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogBoxUtil.showInfoDialogBox(mContext, mContext.getResources().getString(R.string.postsInfo), "", new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {
                        }
                    });
                }
            });

            profilePicLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedUser != null && selectedUser.getProfilePhotoUrl() != null &&
                            !selectedUser.getProfilePhotoUrl().isEmpty()) {
                        fragmentNavigation.pushFragment(new ShowSelectedPhotoFragment(selectedUser.getProfilePhotoUrl()));
                    }
                }
            });

            //followersLayout
            followersLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSelectedUserFollowInfoClickable()) {
                        followersLayout.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.image_click));
                        followerClicked();
                    }
                }
            });

            //followingsLayout
            followingsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSelectedUserFollowInfoClickable()) {
                        followingsLayout.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.image_click));
                        followingClicked();
                    }
                }
            });
        }

        private boolean isSelectedUserFollowInfoClickable() {
            if (selectedUser != null && selectedUser.getFollowStatus() != null &&
                    selectedUser.getFollowStatus().equals(FOLLOW_STATUS_FOLLOWING)) {
                return true;
            } else {
                if (selectedUser != null && selectedUser.getIsPrivateAccount() != null &&
                        selectedUser.getIsPrivateAccount()) {
                    return false;
                } else {
                    return true;
                }
            }
        }

        private void followerClicked() {
            if (fragmentNavigation != null) {
                String requestedUserId = selectedUser.getUserid();
                fragmentNavigation.pushFragment(new FollowerFragment(requestedUserId,
                        UserDataUtil.getNameOrUsername(selectedUser.getName(), selectedUser.getUsername())), ANIMATE_RIGHT_TO_LEFT);
            }
        }

        private void followingClicked() {
            if (fragmentNavigation != null) {
                String requestedUserId = selectedUser.getUserid();
                fragmentNavigation.pushFragment(new FollowingFragment(requestedUserId,
                        UserDataUtil.getNameOrUsername(selectedUser.getName(), selectedUser.getUsername())), ANIMATE_RIGHT_TO_LEFT);
            }
        }

        public void startMessageWithPersonFragment() {
            Intent intent = new Intent(mContext, MessageWithPersonActivity.class);
            intent.putExtra(FCM_CODE_CHATTED_USER, getChattedUserInfo());
            mContext.startActivity(intent);
        }

        public LoginUser getChattedUserInfo() {
            LoginUser user = null;
            user = new LoginUser();
            if (selectedUser.getEmail() != null && !selectedUser.getEmail().isEmpty())
                user.setEmail(selectedUser.getEmail());

            if (selectedUser.getName() != null && !selectedUser.getName().isEmpty())
                user.setName(selectedUser.getName());

            if (selectedUser.getProfilePhotoUrl() != null && !selectedUser.getProfilePhotoUrl().isEmpty())
                user.setProfilePhotoUrl(selectedUser.getProfilePhotoUrl());

            if (selectedUser.getUserid() != null && !selectedUser.getUserid().isEmpty())
                user.setUserId(selectedUser.getUserid());

            if (selectedUser.getUsername() != null && !selectedUser.getUsername().isEmpty())
                user.setUsername(selectedUser.getUsername());
            return user;
        }

        public void setData(UserInfoListItem userInfoListItem, int position) {
            selectedUser = userInfoListItem.getUser();

            //profil fotografi varsa set edilir.
            UserDataUtil.setProfilePicture(mContext, selectedUser.getProfilePhotoUrl(), selectedUser.getName(),
                    selectedUser.getUsername(), txtProfile, imgProfile);
            imgProfile.setPadding(3, 3, 3, 3);

            //Name
            if (isValid(selectedUser.getName())) {
                txtName.setText(selectedUser.getName());
            }

            //send msg button
            UserDataUtil.updateMessagingButton(mContext, selectedUser.getFollowStatus(),
                    selectedUser.getIsPrivateAccount(), sendMessageBtn);
        }

        public void updateUserProfile(UserProfile userProfile, int position) {
            fetchedUser = userProfile;

            //profil fotografi varsa set edilir.
            UserDataUtil.setProfilePicture(mContext, userProfile.getUserInfo().getProfilePhotoUrl(), userProfile.getUserInfo().getName(),
                    userProfile.getUserInfo().getUsername(), txtProfile, imgProfile);
            imgProfile.setPadding(3, 3, 3, 3);
            //Name
            if (isValid(userProfile.getUserInfo().getName())) {
                txtName.setText(userProfile.getUserInfo().getName());
            } else if (isValid(userProfile.getUserInfo().getUsername())) {
                txtName.setText(userProfile.getUserInfo().getUsername());
            }
            //Biography
            // todo NT - biography usera beslenmiyor.düzenlenecek

            //FollowStatus
            UserDataUtil.updateFollowButton2(mContext, userProfile.getRelationInfo().getFollowStatus(), btnFollowStatus, true);

            setUserFollowerAndFollowingCnt(userProfile);
        }

        private boolean isValid(String name) {
            if (name != null && !name.isEmpty()) {
                return true;
            } else {
                return false;
            }
        }

        private void setUserFollowerAndFollowingCnt(UserProfile user) {

            if (user != null && user.getRelationInfo() != null) {

                if (user.getRelationInfo().getFollowerCount() != null && !user.getRelationInfo().getFollowerCount().trim().isEmpty())
                    txtFollowerCnt.setText(user.getRelationInfo().getFollowerCount());

                if (user.getRelationInfo().getFollowingCount() != null && !user.getRelationInfo().getFollowingCount().trim().isEmpty())
                    txtFollowingCnt.setText(user.getRelationInfo().getFollowingCount());

            }
        }

        private void btnFollowStatusClicked() {

            //takip ediliyor ise
            if (fetchedUser.getRelationInfo().getFollowStatus().equals(FOLLOW_STATUS_FOLLOWING)) {
                if (fetchedUser.getUserInfo().getIsPrivateAccount() != null && fetchedUser.getUserInfo().getIsPrivateAccount()) {
                    openDialogBox();
                } else {
                    updateFollowStatus(FRIEND_DELETE_FOLLOW);
                }
            } else if (fetchedUser.getRelationInfo().getFollowStatus().equals(FOLLOW_STATUS_PENDING)) {
                //istek gonderilmis ise
                updateFollowStatus(FRIEND_DELETE_PENDING_FOLLOW_REQUEST);
            } else if (fetchedUser.getRelationInfo().getFollowStatus().equals(FOLLOW_STATUS_NONE)) {
                //takip istegi yok ise
                if (fetchedUser.getUserInfo().getIsPrivateAccount() != null && fetchedUser.getUserInfo().getIsPrivateAccount()) {
                    updateFollowStatus(FRIEND_FOLLOW_REQUEST);
                } else {
                    updateFollowStatus(FRIEND_CREATE_FOLLOW_DIRECTLY);
                }
            } else {
                //do nothing
            }
        }

        private void updateFollowStatus(final String requestType) {

            AccountHolderFollowProcess.friendFollowRequest(requestType, AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid()
                    , selectedUser.getUserid(), new CompleteCallback() {
                        @Override
                        public void onComplete(Object object) {
                            updateFollowUI(requestType);
                        }

                        @Override
                        public void onFailed(Exception e) {
                            DialogBoxUtil.showErrorDialog(mContext, mContext.getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                                @Override
                                public void okClick() {
                                }
                            });
                            btnFollowStatus.setEnabled(true);
                        }
                    });
        }

        private void updateFollowUI(String requestType) {

            switch (requestType) {
                case FRIEND_DELETE_FOLLOW:
                    followStatus = FOLLOW_STATUS_NONE;
                    updateSelectedUserProfile();               //gelinen yerdeki kişinin follow statüsü(UI)
                    updateOtherUserProfile(-1);     //other profildeki kişinin follow statüsü(UI)
                    updateCurrentUserProfile(-1);  //current userın follow count degerleri (UI)

                    break;

                case FRIEND_DELETE_PENDING_FOLLOW_REQUEST:
                    followStatus = FOLLOW_STATUS_NONE;
                    updateSelectedUserProfile();
                    fetchedUser.getRelationInfo().setFollowStatus(followStatus);
                    break;

                case FRIEND_FOLLOW_REQUEST:
                    followStatus = FOLLOW_STATUS_PENDING;
                    updateSelectedUserProfile();
                    fetchedUser.getRelationInfo().setFollowStatus(followStatus);
                    break;

                case FRIEND_CREATE_FOLLOW_DIRECTLY:
                    followStatus = FOLLOW_STATUS_FOLLOWING;
                    updateSelectedUserProfile();
                    updateOtherUserProfile(1);
                    updateCurrentUserProfile(1);
                    break;

                default:
                    break;
            }

            informFragmentFollowStatusChanged();
            UserDataUtil.updateFollowButton2(mContext, fetchedUser.getRelationInfo().getFollowStatus(), btnFollowStatus, true);
        }

        private void updateSelectedUserProfile() {
            selectedUser.setFollowStatus(followStatus);
        }

        private void updateOtherUserProfile(int updateValue) {
            followerCount = Integer.parseInt(fetchedUser.getRelationInfo().getFollowerCount());
            fetchedUser.getRelationInfo().setFollowerCount(String.valueOf(followerCount + updateValue));
            fetchedUser.getRelationInfo().setFollowStatus(followStatus);
            txtFollowerCnt.setText(fetchedUser.getRelationInfo().getFollowerCount());
        }

        private void updateCurrentUserProfile(int updateValue) {
            UserProfile user = AccountHolderInfo.getInstance().getUser();
            followingCount = Integer.parseInt(user.getRelationInfo().getFollowingCount());
            user.getRelationInfo().setFollowingCount(String.valueOf(followingCount + updateValue));
        }

        private void openDialogBox() {

            DialogBoxUtil.removeFromFollowingsDialog(mContext, selectedUser, new YesNoDialogBoxCallback() {
                @Override
                public void yesClick() {
                    updateFollowStatus(FRIEND_DELETE_FOLLOW);
                }

                @Override
                public void noClick() {

                }
            });
        }

        private void informFragmentFollowStatusChanged() {
            followClickCallback.onFollowStatusChanged(followStatus);
        }

    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        View mView;
        private Post post;
        private int position;

        //View items
        RecyclerView gridRecyclerView;
        OtherProfilePostAdapter otherProfilePostAdapter;
        CustomGridLayoutManager customGridLayoutManager;
        RelativeLayout mainExceptionLayout;
        LinearLayout noPostFoundLayout;
        ImageView imgNoPostFound;
        TextView txtNoPostFound;


        private static final int MARGING_GRID = 2;
        private static final int SPAN_COUNT = 3;
        private static final int RECYCLER_VIEW_CACHE_COUNT = 50;


        public PostViewHolder(View view) {
            super(view);

            mView = view;
            gridRecyclerView = (RecyclerView) view.findViewById(R.id.gridRecyclerView);
            mainExceptionLayout = (RelativeLayout) view.findViewById(R.id.mainExceptionLayout);
            noPostFoundLayout = (LinearLayout) view.findViewById(R.id.noPostFoundLayout);
            imgNoPostFound = (ImageView) view.findViewById(R.id.imgNoPostFound);
            txtNoPostFound = (TextView) view.findViewById(R.id.txtNoPostFound);

            setListeners();
            setLayoutManager();
            setAdapter();

            if (postList.size() > 0) {
                showNoFeedLayout(false);
            } else {
                showNoFeedLayout(true);
            }
        }

        private void setListeners() {
        }

        private void setLayoutManager() {
            customGridLayoutManager = new CustomGridLayoutManager(mContext, SPAN_COUNT);
            gridRecyclerView.setLayoutManager(customGridLayoutManager);
            gridRecyclerView.addItemDecoration(GridViewUtil.addItemDecoration(SPAN_COUNT, MARGING_GRID));

            customGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    switch (otherProfilePostAdapter.getItemViewType(position)) {
                        case OtherProfilePostAdapter.VIEW_ITEM:
                            return 1;
                        case OtherProfilePostAdapter.VIEW_PROG:
                            return SPAN_COUNT; //number of columns of the grid
                        default:
                            return -1;
                    }
                }
            });
        }

        private void setAdapter() {
            otherProfilePostAdapter = new OtherProfilePostAdapter(mActivity, mContext, fragmentNavigation, selectedUser, pageCnt);
            gridRecyclerView.setAdapter(otherProfilePostAdapter);
            gridRecyclerView.setItemViewCacheSize(RECYCLER_VIEW_CACHE_COUNT);

            otherProfilePostAdapter.addAll(postList);
            //gridRecyclerView.setNestedScrollingEnabled(false);
        }

        public void setData(Post post, int position) {
        }

        public void updatePostList(int position) {
            otherProfilePostAdapter.updatePostListItems(postList);
        }

        public void loadMorePost() {
            otherProfilePostAdapter.addAll(addedPostList);
        }

        public void updatePageCount() {
            otherProfilePostAdapter.updatePageCount(pageCnt);
        }

        private void showNoFeedLayout(boolean showNoFeedLayout) {
            if (showNoFeedLayout) {
                gridRecyclerView.setVisibility(View.GONE);
                imgNoPostFound.setVisibility(View.GONE);
                mainExceptionLayout.setVisibility(View.VISIBLE);
                noPostFoundLayout.setVisibility(View.VISIBLE);
                txtNoPostFound.setText(mContext.getResources().getString(R.string.emptyFeed2));
            } else {
                gridRecyclerView.setVisibility(View.VISIBLE);
                mainExceptionLayout.setVisibility(View.GONE);
                noPostFoundLayout.setVisibility(View.GONE);
            }
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBarLoading);
        }
    }

    public static class LastItemViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public LastItemViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.tvRv);
        }

        public void setData(String s, int position) {
            textView.setText(s);
            textView.setVisibility(View.GONE);
        }
    }

    public void updateItems() {
        /**/
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (objectList != null ? objectList.size() : 0);
    }

    public void addHeader(UserInfoListItem userInfoListItem) {
        if (userInfoListItem != null) {
            objectList.add(userInfoListItem);
            notifyItemInserted(0);
        }
    }

    public void updateHeader(UserProfile userProfile) {
        if (userProfile != null) {
            notifyItemRangeChanged(0, 1, userProfile);
            //notifyItemChanged(0, userProfile);
        }
    }

    public void addPosts(List<Post> addedPostList) {
        if (addedPostList != null) {
            Post post = new Post();
            objectList.add(post);
            postList.addAll(addedPostList);
            OtherProfilePostList.getInstance().clearPostList();
            OtherProfilePostList.getInstance().addPostList(addedPostList);
            notifyItemRangeInserted(1, 1);
        }

        addLastItem();
    }

    public void updatePosts(List<Post> addedPostList) {
        if (objectList.size() == 1) {
            addPosts(addedPostList);
            return;
        }

        this.postList.clear();
        this.postList.addAll(addedPostList);
        OtherProfilePostList.getInstance().clearPostList();
        OtherProfilePostList.getInstance().addPostList(addedPostList);

        operationType = OPERATION_TYPE_UPDATE_POST;
        Post post = new Post(); //just to recognize the 'instance of'
        notifyItemRangeChanged(1, 1, post);
    }

    public void loadMorePost(List<Post> addedPostList) {
        if (addedPostList != null) {
            this.addedPostList.clear();
            this.addedPostList.addAll(addedPostList);
            this.postList.addAll(addedPostList);
            OtherProfilePostList.getInstance().addPostList(addedPostList);

            Post post = new Post(); //just to recognize the 'instance of'
            operationType = OPERATION_TYPE_LOAD_MORE;
            notifyItemRangeChanged(1, 1, post);
        }
    }

    public void addProgressLoading() {
        if (getItemViewType(objectList.size() - 1) != VIEW_PROG) {
            ProgressBar progressBar = new ProgressBar(mContext);
            objectList.add(progressBar);
            notifyItemInserted(objectList.size() - 1);
        }
    }

    public void addLastItem() {
        String s = "SON";
        objectList.add(s);
        notifyItemRangeInserted(2, 1);
    }

    public void removeProgressLoading() {
        if (getItemViewType(objectList.size() - 1) == VIEW_PROG) {
            objectList.remove(objectList.size() - 1);
            notifyItemRemoved(objectList.size());
        }
    }

    public boolean isShowingProgressLoading() {
        if (getItemViewType(objectList.size() - 1) == VIEW_PROG)
            return true;
        else
            return false;
    }

    public void innerRecyclerPageCntChanged(int pageCnt) {
        this.pageCnt = pageCnt;
        Post post = new Post(); //just to recognize the 'instance of'
        operationType = OPERATION_TYPE_PAGE_COUNT;
        notifyItemRangeChanged(1, 1, post);
    }

    public void setFollowClickCallback(FollowClickCallback followClickCallback) {
        this.followClickCallback = followClickCallback;
    }

}


