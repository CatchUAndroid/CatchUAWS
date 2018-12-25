package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.YesNoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.GeneralUtils.GridViewUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.LoginPackage.AppIntroductionActivity;
import com.uren.catchu.LoginPackage.LoginActivity;
import com.uren.catchu.LoginPackage.Models.LoginUser;
import com.uren.catchu.LoginPackage.RegisterActivity;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.FollowClickCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.RecyclerScrollListener;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.UserInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses.MessageWithPersonActivity;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.MessageWithPersonFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.ChattedUser;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.OtherProfileFragment;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu._Libraries.LayoutManager.CustomGridLayoutManager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import catchu.model.Post;
import catchu.model.User;
import catchu.model.UserProfile;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
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

        try {
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
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return VIEW_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;

        try {
            if (viewType == VIEW_HEADER) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.profile_header_view, parent, false);

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
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
        super.onBindViewHolder(holder, position, payloads);

        try {
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
                            }

                            operationType = OPERATION_TYPE_NONE;
                        }
                    }
                }
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        try {
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
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }

    }

    public class ProfileHeaderViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageView imgProfile;
        ImageView imgInfo;
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

            try {
                mView = view;
                imgProfile = (ImageView) view.findViewById(R.id.imgProfile);
                imgInfo = (ImageView) view.findViewById(R.id.imgInfo);
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
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        private void setListeners() {
            try {
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
                        //fragmentNavigation.pushFragment(new MessageWithPersonFragment(selectedUser), ANIMATE_LEFT_TO_RIGHT);
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
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        public void startMessageWithPersonFragment(){
            try {
                Intent intent = new Intent(mContext, MessageWithPersonActivity.class);
                intent.putExtra(FCM_CODE_CHATTED_USER, getChattedUserInfo());
                mContext.startActivity(intent);
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        public LoginUser getChattedUserInfo() {
            LoginUser user = null;
            try {
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
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
            return user;
        }

        public void setData(UserInfoListItem userInfoListItem, int position) {

            try {
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
            } catch (Resources.NotFoundException e) {
                ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }

        }

        public void updateUserProfile(UserProfile userProfile, int position) {

            try {
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
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }

        }

        private boolean isValid(String name) {
            if (name != null && !name.isEmpty()) {
                return true;
            } else {
                return false;
            }
        }

        private void setUserFollowerAndFollowingCnt(UserProfile user) {

            try {
                if (user != null && user.getRelationInfo() != null) {

                    if (user.getRelationInfo().getFollowerCount() != null && !user.getRelationInfo().getFollowerCount().trim().isEmpty())
                        txtFollowerCnt.setText(user.getRelationInfo().getFollowerCount());

                    if (user.getRelationInfo().getFollowingCount() != null && !user.getRelationInfo().getFollowingCount().trim().isEmpty())
                        txtFollowingCnt.setText(user.getRelationInfo().getFollowingCount());

                }
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        private void btnFollowStatusClicked() {

            try {
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
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }

        }

        private void updateFollowStatus(final String requestType) {

            try {
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
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        private void updateFollowUI(String requestType) {

            try {
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
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }

        }

        private void updateSelectedUserProfile() {
            selectedUser.setFollowStatus(followStatus);
        }

        private void updateOtherUserProfile(int updateValue) {
            try {
                followerCount = Integer.parseInt(fetchedUser.getRelationInfo().getFollowerCount());
                fetchedUser.getRelationInfo().setFollowerCount(String.valueOf(followerCount + updateValue));
                fetchedUser.getRelationInfo().setFollowStatus(followStatus);
                txtFollowerCnt.setText(fetchedUser.getRelationInfo().getFollowerCount());
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        private void updateCurrentUserProfile(int updateValue) {
            try {
                UserProfile user = AccountHolderInfo.getInstance().getUser();
                followingCount = Integer.parseInt(user.getRelationInfo().getFollowingCount());
                user.getRelationInfo().setFollowingCount(String.valueOf(followingCount + updateValue));
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
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
        RelativeLayout rl_no_feed;
        ImageView imgSad;
        TextView txtNoFeedExplanation;

        private static final int MARGING_GRID = 2;
        private static final int SPAN_COUNT = 3;
        private static final int RECYCLER_VIEW_CACHE_COUNT = 50;


        public PostViewHolder(View view) {
            super(view);

            try {
                mView = view;
                gridRecyclerView = (RecyclerView) view.findViewById(R.id.gridRecyclerView);
                rl_no_feed = (RelativeLayout) view.findViewById(R.id.rl_no_feed);
                imgSad = (ImageView) view.findViewById(R.id.imgSad);
                txtNoFeedExplanation = (TextView) view.findViewById(R.id.txtNoFeedExplanation);

                setListeners();
                setLayoutManager();
                setAdapter();

                if (postList.size() > 0) {
                    showNoFeedLayout(false);
                } else {
                    showNoFeedLayout(true);
                }
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }

        }

        private void setListeners() {
        }

        private void setLayoutManager() {
            try {
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
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }

        }

        private void setAdapter() {

            try {
                otherProfilePostAdapter = new OtherProfilePostAdapter(mActivity, mContext, fragmentNavigation);
                gridRecyclerView.setAdapter(otherProfilePostAdapter);
                gridRecyclerView.setItemViewCacheSize(RECYCLER_VIEW_CACHE_COUNT);

                otherProfilePostAdapter.addAll(postList);
                //gridRecyclerView.setNestedScrollingEnabled(false);
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }

        }

        public void setData(Post post, int position) {
        }

        public void updatePostList(int position) {
            otherProfilePostAdapter.updatePostListItems(postList);
        }

        public void loadMorePost() {
            otherProfilePostAdapter.addAll(addedPostList);
        }

        private void showNoFeedLayout(boolean showNoFeedLayout) {
            try {
                if (showNoFeedLayout) {
                    gridRecyclerView.setVisibility(View.GONE);
                    imgSad.setVisibility(View.GONE);
                    rl_no_feed.setVisibility(View.VISIBLE);
                    txtNoFeedExplanation.setText(mContext.getResources().getString(R.string.emptyFeed2));
                } else {
                    gridRecyclerView.setVisibility(View.VISIBLE);
                    rl_no_feed.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
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
        try {
            if (userInfoListItem != null) {
                objectList.add(userInfoListItem);
                notifyItemInserted(0);
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void updateHeader(UserProfile userProfile) {
        try {
            if (userProfile != null) {
                notifyItemRangeChanged(0, 1, userProfile);
                //notifyItemChanged(0, userProfile);
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void addPosts(List<Post> addedPostList) {
        try {
            if (addedPostList != null) {
                Post post = new Post();
                objectList.add(post);
                postList.addAll(addedPostList);
                notifyItemRangeInserted(1, 1);
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void updatePosts(List<Post> addedPostList) {
        try {
            this.postList.clear();
            this.postList.addAll(addedPostList);
            operationType = OPERATION_TYPE_UPDATE_POST;
            Post post = new Post(); //just to recognize the 'instance of'
            notifyItemRangeChanged(1, 1, post);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void loadMorePost(List<Post> addedPostList) {
        try {
            if (addedPostList != null) {
                this.addedPostList.clear();
                this.addedPostList.addAll(addedPostList);
                this.postList.addAll(addedPostList);
                Post post = new Post(); //just to recognize the 'instance of'
                operationType = OPERATION_TYPE_LOAD_MORE;
                notifyItemRangeChanged(1, 1, post);
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void addProgressLoading() {
        try {
            if (getItemViewType(objectList.size() - 1) != VIEW_PROG) {
                ProgressBar progressBar = new ProgressBar(mContext);
                objectList.add(progressBar);
                notifyItemInserted(objectList.size() - 1);
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void addLastItem() {
        try {
            String s = "SON";
            objectList.add(s);
            notifyItemRangeInserted(2, 1);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void removeProgressLoading() {
        try {
            if (getItemViewType(objectList.size() - 1) == VIEW_PROG) {
                objectList.remove(objectList.size() - 1);
                notifyItemRemoved(objectList.size());
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
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


