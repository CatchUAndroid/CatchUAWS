package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.YesNoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.PostFeaturesCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostDiffCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostHelper;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.SingletonSinglePost;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ListItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.UserInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.Adapters.UserPostGridViewAdapter;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import catchu.model.FollowInfoListResponse;
import catchu.model.Post;
import catchu.model.PostListResponse;
import catchu.model.User;
import catchu.model.UserProfile;

import static com.uren.catchu.Constants.StringConstants.CHAR_AMPERSAND;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_FOLLOWING;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_NONE;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_PENDING;
import static com.uren.catchu.Constants.StringConstants.FRIEND_CREATE_FOLLOW_DIRECTLY;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_FOLLOW;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_PENDING_FOLLOW_REQUEST;
import static com.uren.catchu.Constants.StringConstants.FRIEND_FOLLOW_REQUEST;
import static com.uren.catchu.Constants.StringConstants.IMAGE_TYPE;
import static com.uren.catchu.Constants.StringConstants.VIDEO_TYPE;

public class DenemeAdapter extends RecyclerView.Adapter {

    public static final int VIEW_HEADER = 0;
    public static final int VIEW_ITEM = 1;
    public static final int VIEW_PROG = 2;

    public static String PARTIAL_DATA_LOADING = "PARTIAL_DATA_LOADING";

    private Activity mActivity;
    public Context mContext;
    private List<Object> objectList;
    private BaseFragment.FragmentNavigation fragmentNavigation;

    public DenemeAdapter(Activity activity, Context context, BaseFragment.FragmentNavigation fragmentNavigation) {
        this.mActivity = activity;
        this.mContext = context;
        this.fragmentNavigation = fragmentNavigation;
        this.objectList = new ArrayList<Object>();
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

            viewHolder = new DenemeAdapter.ProfileHeaderViewHolder(itemView);
        } else if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_media_grid_item, parent, false);

            viewHolder = new DenemeAdapter.MyViewHolder(itemView);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar_item, parent, false);

            viewHolder = new DenemeAdapter.ProgressViewHolder(v);
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

            }
        }

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof DenemeAdapter.ProfileHeaderViewHolder) {
            UserInfoListItem userInfoListItem = (UserInfoListItem) objectList.get(position);
            ((DenemeAdapter.ProfileHeaderViewHolder) holder).setData(userInfoListItem, position);
        } else if (holder instanceof DenemeAdapter.MyViewHolder) {
            Post post = (Post) objectList.get(position);
            ((DenemeAdapter.MyViewHolder) holder).setData(post, position);
        } else {
            ((DenemeAdapter.ProgressViewHolder) holder).progressBar.setIndeterminate(true);
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

        ProgressBar progressBar;

        User selectedUser;

        String followStatus;
        private int followingCount, followerCount;


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

            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        }

        public void setData(UserInfoListItem userInfoListItem, int position) {

            selectedUser = userInfoListItem.getUser();

            txtFollowerCnt.setClickable(false);
            txtFollowingCnt.setClickable(false);

            //username
            /*
            if (isValid(selectedUser.getUsername())) {
                toolbarTitleTv.setText(selectedUser.getUsername());
            }
            */

            //profil fotografi varsa set edilir.
            UserDataUtil.setProfilePicture(mContext, selectedUser.getProfilePhotoUrl(), selectedUser.getName(),
                    selectedUser.getUsername(), txtProfile, imgProfile);
            imgProfile.setPadding(3, 3, 3, 3);

            //Name
            if (isValid(selectedUser.getName())) {
                txtName.setText(selectedUser.getName());
            }

        }

        public void updateUserProfile(UserProfile userProfile, int position) {

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
/*
            //takip ediliyor ise
            if (otherProfile.getRelationInfo().getFollowStatus().equals(FOLLOW_STATUS_FOLLOWING)) {
                if (otherProfile.getUserInfo().getIsPrivateAccount() != null && otherProfile.getUserInfo().getIsPrivateAccount()) {
                    openDialogBox();
                } else {
                    updateFollowStatus(FRIEND_DELETE_FOLLOW);
                }
            } else if (otherProfile.getRelationInfo().getFollowStatus().equals(FOLLOW_STATUS_PENDING)) {
                //istek gonderilmis ise
                updateFollowStatus(FRIEND_DELETE_PENDING_FOLLOW_REQUEST);
            } else if (otherProfile.getRelationInfo().getFollowStatus().equals(FOLLOW_STATUS_NONE)) {
                //takip istegi yok ise
                if (otherProfile.getUserInfo().getIsPrivateAccount() != null && otherProfile.getUserInfo().getIsPrivateAccount()) {
                    updateFollowStatus(FRIEND_FOLLOW_REQUEST);
                } else {
                    updateFollowStatus(FRIEND_CREATE_FOLLOW_DIRECTLY);
                }
            } else {
                //do nothing
            }
*/
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

/*
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
                    otherProfile.getRelationInfo().setFollowStatus(followStatus);
                    break;

                case FRIEND_FOLLOW_REQUEST:
                    followStatus = FOLLOW_STATUS_PENDING;
                    updateSelectedUserProfile();
                    otherProfile.getRelationInfo().setFollowStatus(followStatus);
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

            //updateAdapters();
            UserDataUtil.updateFollowButton2(mContext, otherProfile.getRelationInfo().getFollowStatus(), btnFollowStatus, true);
  */
        }

        private void updateSelectedUserProfile() {
            selectedUser.setFollowStatus(followStatus);
        }

        private void updateOtherUserProfile(int updateValue) {
            /*
            followerCount = Integer.parseInt(otherProfile.getRelationInfo().getFollowerCount());
            otherProfile.getRelationInfo().setFollowerCount(String.valueOf(followerCount + updateValue));
            otherProfile.getRelationInfo().setFollowStatus(followStatus);
            txtFollowerCnt.setText(otherProfile.getRelationInfo().getFollowerCount());
            */
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


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        View mView;
        private Post post;
        private int position;

        //View items
        ImageView imgPost;
        ProgressBar progressLoading;
        RelativeLayout rlContent;
        LinearLayout llProgress, llError;
        LinearLayout llPostImage;
        LinearLayout llExplanation;
        ClickableImageView imgRetry;
        TextView txtExplanation;
        ImageView imgVideoIcon, imgGridMore;

        public MyViewHolder(View view) {
            super(view);

            mView = view;
            imgPost = (ImageView) view.findViewById(R.id.imgPost);
            progressLoading = (ProgressBar) view.findViewById(R.id.progressLoading);
            llProgress = (LinearLayout) view.findViewById(R.id.llProgress);
            llError = (LinearLayout) view.findViewById(R.id.llError);
            llExplanation = (LinearLayout) view.findViewById(R.id.llExplanation);
            llPostImage = (LinearLayout) view.findViewById(R.id.llPostImage);
            imgRetry = (ClickableImageView) view.findViewById(R.id.imgRetry);
            txtExplanation = (TextView) view.findViewById(R.id.txtExplanation);
            rlContent = (RelativeLayout) view.findViewById(R.id.rlContent);
            imgVideoIcon = (ImageView) view.findViewById(R.id.imgVideoCamera);
            imgGridMore = (ImageView) view.findViewById(R.id.imgGridMore);

            setListeners();

        }

        private void setListeners() {
            rlContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setSinglePostFragmentItems();

                    Post post = (Post) objectList.get(position);

                    Log.i("clickedPostId ", post.getPostid());
                    CommonUtils.showCustomToast(mContext, post.getPostid());
                }
            });

            imgRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setData(post, position);
                }
            });

        }

        private void setSinglePostFragmentItems() {

            SingletonSinglePost.getInstance().setPost(post);
            String toolbarTitle = post.getUser().getUsername();

            PostHelper.SinglePostClicked singlePostClickedInstance = PostHelper.SinglePostClicked.getInstance();
            singlePostClickedInstance.setSinglePostItems(mContext, fragmentNavigation, toolbarTitle, post.getPostid(), position);
            singlePostClickedInstance.setPostFeaturesCallback(new PostFeaturesCallback() {
                @Override
                public void onPostLikeClicked(boolean isPostLiked, int newLikeCount, int position) {
                    Post post = (Post) objectList.get(position);
                    post.setLikeCount(newLikeCount);
                    post.setIsLiked(isPostLiked);
                    notifyItemChanged(position);
                }

                @Override
                public void onCommentAdd(int position, int newCommentCount) {
                    Post post = (Post) objectList.get(position);
                    post.setCommentCount(newCommentCount);
                    notifyItemChanged(position);
                }

                @Override
                public void onCommentAllowedStatusChanged(int position, boolean commentAllowed) {
                    Post post = (Post) objectList.get(position);
                    post.setIsCommentAllowed(commentAllowed);
                    notifyItemChanged(position);
                }

                @Override
                public void onPostDeleted(int position) {
                    Post deletedPost = (Post) objectList.get(position);
                    objectList.remove(position);
                    notifyItemRemoved(position);
                    notifyDataSetChanged();
                    PostHelper.DeletePost.startProcess(mContext, AccountHolderInfo.getUserID(), deletedPost.getPostid());
                }
            });

            singlePostClickedInstance.startSinglePostProcess();

        }

        public void setData(Post post, int position) {

            this.post = post;
            this.position = position;
            boolean sourceFound = false;
            String loadUrl = "empty";

            llProgress.setVisibility(View.VISIBLE);
            progressLoading.setVisibility(View.VISIBLE);

            llPostImage.setVisibility(View.GONE);
            llError.setVisibility(View.GONE);
            llExplanation.setVisibility(View.GONE);
            imgVideoIcon.setVisibility(View.GONE);
            imgGridMore.setVisibility(View.GONE);

            if (post.getAttachments().size() > 1) {
                imgGridMore.setVisibility(View.VISIBLE);
            }

            //varsa video ThumbNail
            if (!sourceFound) {
                for (int i = 0; i < post.getAttachments().size(); i++) {
                    if (post.getAttachments().get(i).getType().equals(VIDEO_TYPE)) {
                        loadUrl = post.getAttachments().get(i).getThumbnail();
                        sourceFound = true;
                        imgVideoIcon.setVisibility(View.VISIBLE);
                    }
                }
            }
            //varsa image
            if (!sourceFound) {
                for (int i = 0; i < post.getAttachments().size(); i++) {
                    if (post.getAttachments().get(i).getType().equals(IMAGE_TYPE)) {
                        loadUrl = post.getAttachments().get(i).getUrl();
                        sourceFound = true;
                    }
                }
            }
            //varsa post açıklaması
            if (!sourceFound) {
                setGridBackgroundColor();
                if (post.getMessage() != null && !post.getMessage().isEmpty()) {
                    llExplanation.setVisibility(View.VISIBLE);
                    llProgress.setVisibility(View.GONE);
                    progressLoading.setVisibility(View.GONE);
                    llPostImage.setVisibility(View.GONE);
                    llError.setVisibility(View.GONE);

                    txtExplanation.setText(post.getMessage());
                }
            } else {
                llProgress.setVisibility(View.VISIBLE);
                progressLoading.setVisibility(View.VISIBLE);
                llPostImage.setVisibility(View.VISIBLE);
                llExplanation.setVisibility(View.GONE);
                llError.setVisibility(View.GONE);

                Glide.with(mContext)
                        .load(loadUrl)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                llError.setVisibility(View.VISIBLE);
                                llProgress.setVisibility(View.GONE);
                                progressLoading.setVisibility(View.GONE);
                                ;
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                llPostImage.setVisibility(View.VISIBLE);
                                progressLoading.setVisibility(View.GONE);
                                llProgress.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .apply(RequestOptions.centerCropTransform())
                        .into(imgPost);
            }


        }

        private void setGridBackgroundColor() {
            int colorCode = CommonUtils.getRandomColor(mContext);
            llExplanation.setBackgroundColor(mContext.getResources().getColor(colorCode, null));
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
            objectList.addAll(addedPostList);
            notifyItemRangeInserted(1, addedPostList.size());
        }
    }

    public void updatePosts(List<Post> addedPostList) {

    }

    public void addProgressLoading() {
        ProgressBar progressBar = new ProgressBar(mContext);
        objectList.add(progressBar);
        notifyItemInserted(objectList.size() - 1);
    }

    public void removeProgressLoading() {
        objectList.remove(objectList.size() - 1);
        notifyItemRemoved(objectList.size());
    }

    public boolean isShowingProgressLoading() {
        if (getItemViewType(objectList.size() - 1) == VIEW_PROG)
            return true;
        else
            return false;
    }


}


