package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.YesNoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.GridViewUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.FollowClickCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.RecyclerScrollListener;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.UserInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.MessageWithPersonFragment;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu._Libraries.LayoutManager.CustomGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import catchu.model.Post;
import catchu.model.User;
import catchu.model.UserProfile;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
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

    private Activity mActivity;
    private Context mContext;
    private List<Object> objectList;
    private List<Post> postList;
    private List<Post> addedPostList;
    private BaseFragment.FragmentNavigation fragmentNavigation;
    private FollowClickCallback followClickCallback;
    private RecyclerScrollListener recyclerScrollListener;

    private static final int OPERATION_TYPE_NONE = -1;
    private static final int OPERATION_TYPE_LOAD_MORE = 0;
    private static final int OPERATION_TYPE_UPDATE_POST = 1;
    private int operationType = OPERATION_TYPE_NONE;

    public OtherProfileAdapter(Activity activity, Context context, BaseFragment.FragmentNavigation fragmentNavigation) {
        this.mActivity = activity;
        this.mContext = context;
        this.fragmentNavigation = fragmentNavigation;
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
        } else {
            return VIEW_PROG;
        }
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;

        if (viewType == VIEW_HEADER) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.profile_header_view, parent, false);

            viewHolder = new OtherProfileAdapter.ProfileHeaderViewHolder(itemView);
        } else if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.profile_shared_post_view, parent, false);

            viewHolder = new OtherProfileAdapter.PostViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar_item, parent, false);

            viewHolder = new OtherProfileAdapter.ProgressViewHolder(itemView);
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

                        if(operationType == OPERATION_TYPE_UPDATE_POST){
                            ((PostViewHolder) holder).updatePostList(position);
                        }else if(operationType == OPERATION_TYPE_LOAD_MORE){
                            ((PostViewHolder) holder).loadMorePost();
                        }

                        operationType = OPERATION_TYPE_NONE;

                    }
                }

            }
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof OtherProfileAdapter.ProfileHeaderViewHolder) {
            UserInfoListItem userInfoListItem = (UserInfoListItem) objectList.get(position);
            ((OtherProfileAdapter.ProfileHeaderViewHolder) holder).setData(userInfoListItem, position);
        } else if (holder instanceof OtherProfileAdapter.PostViewHolder) {
            Post post = (Post) objectList.get(position);
            ((OtherProfileAdapter.PostViewHolder) holder).setData(post, position);
        } else {
            ((OtherProfileAdapter.ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

    }

    public class ProfileHeaderViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageView imgProfile;
        TextView txtProfile;
        TextView txtName;
        TextView txtBio;
        Button btnFollowStatus;
        TextView txtFollowerCnt;
        TextView txtFollowingCnt;

        User selectedUser;
        UserProfile fetchedUser;
        String followStatus;
        int followingCount, followerCount;

        Button sendMessageBtn;

        public ProfileHeaderViewHolder(View view) {
            super(view);

            mView = view;
            imgProfile = (ImageView) view.findViewById(R.id.imgProfile);
            txtProfile = (TextView) view.findViewById(R.id.txtProfile);
            txtName = (TextView) view.findViewById(R.id.txtName);
            txtBio = (TextView) view.findViewById(R.id.txtBio);
            btnFollowStatus = (Button) view.findViewById(R.id.btnFollowStatus);
            txtFollowerCnt = (TextView) view.findViewById(R.id.txtFollowerCnt);
            txtFollowingCnt = (TextView) view.findViewById(R.id.txtFollowingCnt);
            sendMessageBtn = (Button) view.findViewById(R.id.sendMessageBtn);

            txtFollowerCnt.setClickable(false);
            txtFollowingCnt.setClickable(false);

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
                    fragmentNavigation.pushFragment(new MessageWithPersonFragment(selectedUser), ANIMATE_LEFT_TO_RIGHT);
                }
            });
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
            sendMessageBtn.setBackground(ShapeUtil.getShape(mContext.getResources().getColor(R.color.White, null),
                    mContext.getResources().getColor(R.color.Gray, null), GradientDrawable.RECTANGLE, 15, 2));

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
            YesNoDialogBoxCallback yesNoDialogBoxCallback = new YesNoDialogBoxCallback() {
                @Override
                public void yesClick() {
                    updateFollowStatus(FRIEND_DELETE_FOLLOW);
                }
                @Override
                public void noClick() {
                }
            };
            DialogBoxUtil.showYesNoDialog(mContext, "", mContext.getString(R.string.cancel_following), yesNoDialogBoxCallback);
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

        private static final int MARGING_GRID = 2;
        private static final int SPAN_COUNT = 3;
        private static final int RECYCLER_VIEW_CACHE_COUNT = 50;

        private boolean loading = true;
        private boolean isFirstFetch = false;
        private int pastVisibleItems, visibleItemCount, totalItemCount;

        public PostViewHolder(View view) {
            super(view);

            mView = view;
            gridRecyclerView = (RecyclerView) view.findViewById(R.id.gridRecyclerView);

            setListeners();
            setLayoutManager();
            setAdapter();
            //setRecyclerViewScroll();

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
            otherProfilePostAdapter = new OtherProfilePostAdapter(mActivity, mContext, fragmentNavigation);
            gridRecyclerView.setAdapter(otherProfilePostAdapter);
            gridRecyclerView.setItemViewCacheSize(RECYCLER_VIEW_CACHE_COUNT);

            otherProfilePostAdapter.addAll(postList);

        }

        private void setRecyclerViewScroll() {

            gridRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if (!recyclerView.canScrollVertically(1) ) {
                        CommonUtils.showCustomToast(mContext,  "end");
                    }


                    /*
                    if (dy > 0) //check for scroll down
                    {
                        visibleItemCount = customGridLayoutManager.getChildCount();
                        totalItemCount = customGridLayoutManager.getItemCount();
                        pastVisibleItems = customGridLayoutManager.findFirstVisibleItemPosition();

                        if (loading) {

                            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                loading = false;
                                //Do pagination.. i.e. fetch new data
                                recyclerScrollListener.onLoadMore();


                            }
                        }
                    }
                    */
                }

            });

        }


        public void setData(Post post, int position) {
        }

        public void updatePostList(int position) {
            otherProfilePostAdapter.updatePostListItems(postList);
        }

        public void loadMorePost(){
            otherProfilePostAdapter.addAll(addedPostList);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBarLoading);
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
            notifyItemChanged(0, userProfile);
        }
    }

    public void addPosts(List<Post> addedPostList) {
        if (addedPostList != null) {
            Post post = new Post();
            objectList.add(post);
            postList.addAll(addedPostList);
            notifyItemRangeInserted(1, 1);
        }
    }

    public void updatePosts(List<Post> addedPostList) {
        this.postList.clear();
        this.postList.addAll(addedPostList);
        operationType = OPERATION_TYPE_UPDATE_POST;
        Post post = new Post(); //just to recognize the 'instance of'
        notifyItemRangeChanged(1, 1, post);
    }

    public void loadMorePost(List<Post> addedPostList) {
        if (addedPostList != null) {
            this.addedPostList.clear();
            this.addedPostList.addAll(addedPostList);
            this.postList.addAll(addedPostList);
            Post post = new Post(); //just to recognize the 'instance of'
            operationType = OPERATION_TYPE_LOAD_MORE;
            notifyItemRangeChanged(1, 1, post);
        }
    }

    public void addProgressLoading() {
        if(getItemViewType(objectList.size() - 1) != VIEW_PROG){
            ProgressBar progressBar = new ProgressBar(mContext);
            objectList.add(progressBar);
            notifyItemInserted(objectList.size() - 1);
        }
    }

    public void removeProgressLoading() {
        if(getItemViewType(objectList.size() - 1) == VIEW_PROG){
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


    public void setFollowClickCallback(FollowClickCallback followClickCallback) {
        this.followClickCallback = followClickCallback;
    }

    public void setInnerRecyclerScrollListener(RecyclerScrollListener recyclerScrollListener) {
        this.recyclerScrollListener = recyclerScrollListener;
    }

}


