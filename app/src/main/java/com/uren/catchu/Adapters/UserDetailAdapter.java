package com.uren.catchu.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.ApiGatewayFunctions.FriendRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ImageCache.ImageLoader;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.RowItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.FollowInfoRowItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters.FollowAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.OtherProfileFragment;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.ShareDetailActivity;
import com.uren.catchu.Singleton.AccountHolderFollowings;
import com.uren.catchu.Singleton.AccountHolderInfo;

import catchu.model.FollowInfo;
import catchu.model.FollowInfoResultArrayItem;
import catchu.model.FriendRequestList;
import catchu.model.RelationProperties;
import catchu.model.SearchResult;
import catchu.model.SearchResultResultArrayItem;
import catchu.model.UserProfileProperties;
import catchu.model.UserProfileRelationCountInfo;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.FRIEND_CREATE_FOLLOW_DIRECTLY;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_FOLLOW;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_PENDING_FOLLOW_REQUEST;
import static com.uren.catchu.Constants.StringConstants.FRIEND_FOLLOW_REQUEST;
import static com.uren.catchu.Constants.StringConstants.displayRounded;
import static com.uren.catchu.Constants.StringConstants.friendsCacheDirectory;

public class UserDetailAdapter extends RecyclerView.Adapter<UserDetailAdapter.MyViewHolder> {

    View view;
    LayoutInflater layoutInflater;
    String userid;
    String searchText;
    Context context;
    Activity activity;
    SearchResult searchResult;
    RowItemClickListener rowItemClickListener;
    GradientDrawable imageShape;
    GradientDrawable buttonShape;

    public UserDetailAdapter(Context context, String searchText, SearchResult searchResult, String userid, RowItemClickListener rowItemClickListener) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.searchText = searchText;
        this.searchResult = searchResult;
        this.userid = userid;
        this.rowItemClickListener = rowItemClickListener;
        activity = (Activity) context;
        imageShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                0, GradientDrawable.OVAL, 50, 0);
    }

    @NonNull
    @Override
    public UserDetailAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view = layoutInflater.inflate(R.layout.person_vert_list_item, viewGroup, false);
        final UserDetailAdapter.MyViewHolder holder = new UserDetailAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserDetailAdapter.MyViewHolder myViewHolder, int position) {
        SearchResultResultArrayItem selectedFriend = searchResult.getResultArray().get(position);
        myViewHolder.setData(selectedFriend, position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView usernameTextView;
        TextView shortenTextView;
        ImageView profilePicImgView;
        CardView personRootCardView;
        SearchResultResultArrayItem selectedFriend;
        Button statuDisplayBtn;
        String requestedUserid;

        int position = 0;

        public MyViewHolder(final View itemView) {
            super(itemView);

            profilePicImgView = view.findViewById(R.id.profilePicImgView);
            usernameTextView = view.findViewById(R.id.usernameTextView);
            nameTextView = view.findViewById(R.id.nameTextView);
            statuDisplayBtn = view.findViewById(R.id.statuDisplayBtn);
            shortenTextView = view.findViewById(R.id.shortenTextView);
            personRootCardView = view.findViewById(R.id.personRootCardView);
            profilePicImgView.setBackground(imageShape);
            statuDisplayBtn.setBackground(buttonShape);

            statuDisplayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    statuDisplayBtn.setEnabled(false);
                    statuDisplayBtn.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_click));
                    checkFriendRelation();
                }
            });

            personRootCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FollowInfoResultArrayItem followItem = getFollowProperties();
                    rowItemClickListener.onClick(v, followItem, position);
                }
            });
        }

        public FollowInfoResultArrayItem getFollowProperties() {
            FollowInfoResultArrayItem followItem = new FollowInfoResultArrayItem();
            followItem.setBirthday(selectedFriend.getName());
            followItem.setEmail(selectedFriend.getUsername());
            followItem.setProfilePhotoUrl(selectedFriend.getProfilePhotoUrl());
            followItem.setUserid(selectedFriend.getUserid());
            followItem.setIsPendingRequest(selectedFriend.getPendingFriendRequest());
            followItem.setIsPrivateAccount(selectedFriend.getIsPrivateAccount());

            if (selectedFriend.getFriendRelation() != null && selectedFriend.getFriendRelation())
                followItem.setIsFollow(true);
            else
                followItem.setIsFollow(false);

            return followItem;
        }

        public void checkFriendRelation() {

            if (selectedFriend.getFriendRelation() != null && selectedFriend.getFriendRelation())
                processFriendRequest(FRIEND_DELETE_FOLLOW);
            else {
                if (selectedFriend.getPendingFriendRequest() != null && selectedFriend.getPendingFriendRequest())
                    processFriendRequest(FRIEND_DELETE_PENDING_FOLLOW_REQUEST);
                else {
                    if (selectedFriend.getIsPrivateAccount() != null) {
                        if (selectedFriend.getIsPrivateAccount()) {
                            processFriendRequest(FRIEND_FOLLOW_REQUEST);
                        } else {
                            processFriendRequest(FRIEND_CREATE_FOLLOW_DIRECTLY);
                        }
                    }
                }
            }
        }

        public void processFriendRequest(final String requestType) {
            AccountHolderInfo.getToken(new TokenCallback() {
                @Override
                public void onTokenTaken(String token) {
                    startFriendRequest(requestType, token);
                }
            });
        }

        private void startFriendRequest(final String requestType, String token) {
            final FriendRequestProcess friendRequestProcess = new FriendRequestProcess(new OnEventListener<FriendRequestList>() {

                @Override
                public void onSuccess(FriendRequestList object) {
                    RelationProperties relationProperties = object.getUpdatedUserRelationInfo();
                    selectedFriend.setFriendRelation(relationProperties.getFriendRelation());
                    selectedFriend.setPendingFriendRequest(relationProperties.getPendingFriendRequest());
                    searchResult.getResultArray().remove(position);
                    searchResult.getResultArray().add(position, selectedFriend);
                    UserDataUtil.updateFollowButton(context, selectedFriend.getFriendRelation(), selectedFriend.getPendingFriendRequest(), statuDisplayBtn);
                    AccountHolderInfo.updateAccountHolderFollowCnt(requestType);
                    updateFollowingList(requestType);
                    statuDisplayBtn.setEnabled(true);
                }

                @Override
                public void onFailure(Exception e) {
                    CommonUtils.showToastLong(context, context.getResources().getString(R.string.error) + e.toString());
                    statuDisplayBtn.setEnabled(true);
                }

                @Override
                public void onTaskContinue() {

                }
            }, requestType, userid, requestedUserid, token);

            friendRequestProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        public void updateFollowingList(final String requestType) {

            AccountHolderFollowings.getInstance(new CompleteCallback() {
                @Override
                public void onComplete(Object object) {
                    FollowInfoResultArrayItem followInfoResultArrayItem = new FollowInfoResultArrayItem();
                    followInfoResultArrayItem.setName(selectedFriend.getName());
                    followInfoResultArrayItem.setProfilePhotoUrl(selectedFriend.getProfilePhotoUrl());
                    followInfoResultArrayItem.setUserid(selectedFriend.getUserid());
                    followInfoResultArrayItem.setUsername(selectedFriend.getUsername());
                    AccountHolderFollowings.updateFriendListByFollowType(requestType, followInfoResultArrayItem);
                }

                @Override
                public void onFailed(Exception e) {
                    DialogBoxUtil.showErrorDialog(context, context.getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {

                        }
                    });
                }
            });
        }

        public void setData(SearchResultResultArrayItem selectedFriend, int position) {
            this.position = position;
            this.selectedFriend = selectedFriend;
            this.requestedUserid = selectedFriend.getUserid();
            setUserName();
            setName();
            UserDataUtil.setProfilePicture(context, selectedFriend.getProfilePhotoUrl(),
                    selectedFriend.getName(), shortenTextView, profilePicImgView);
            UserDataUtil.updateFollowButton(context, selectedFriend.getFriendRelation(), selectedFriend.getPendingFriendRequest(), statuDisplayBtn);
        }

        public void setUserName() {
            if (selectedFriend.getUsername() != null && !selectedFriend.getUsername().trim().isEmpty())
                this.usernameTextView.setText(selectedFriend.getUsername());
        }

        public void setName() {
            if (selectedFriend.getName() != null && !selectedFriend.getName().trim().isEmpty()) {
                if (selectedFriend.getName().length() > 30)
                    this.nameTextView.setText(selectedFriend.getName().trim().substring(0, 30) + "...");
                else
                    this.nameTextView.setText(selectedFriend.getName());
            }
        }
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return searchResult.getResultArray().size();
    }

    public void updateAdapterWithPosition(int position) {
        notifyItemChanged(position);
    }
}
