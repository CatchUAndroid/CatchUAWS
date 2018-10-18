package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.uren.catchu.ApiGatewayFunctions.FriendRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ListItemClickListener;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderFollowings;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.List;

import catchu.model.FollowInfoResultArrayItem;
import catchu.model.FriendRequestList;
import catchu.model.RelationProperties;
import catchu.model.User;
import catchu.model.UserListResponse;

import static com.uren.catchu.Constants.StringConstants.FRIEND_CREATE_FOLLOW_DIRECTLY;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_FOLLOW;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_PENDING_FOLLOW_REQUEST;
import static com.uren.catchu.Constants.StringConstants.FRIEND_FOLLOW_REQUEST;
import static com.uren.catchu.Constants.StringConstants.PROVIDER_FOLLOW_STATUS_FOLLOWING;
import static com.uren.catchu.Constants.StringConstants.PROVIDER_FOLLOW_STATUS_NONE;
import static com.uren.catchu.Constants.StringConstants.PROVIDER_FOLLOW_STATUS_PENDING;

public class FacebookFriendsAdapter extends RecyclerView.Adapter<FacebookFriendsAdapter.MyViewHolder> implements Filterable {


    View view;
    LayoutInflater layoutInflater;
    Context context;
    Activity activity;
    UserListResponse userListResponse;
    UserListResponse orgUserListResponse;
    ListItemClickListener listItemClickListener;
    GradientDrawable imageShape;
    GradientDrawable buttonShape;

    public FacebookFriendsAdapter(Context context, UserListResponse userListResponse, ListItemClickListener listItemClickListener) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.userListResponse = userListResponse;
        this.orgUserListResponse = userListResponse;
        this.listItemClickListener = listItemClickListener;
        activity = (Activity) context;
        imageShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                0, GradientDrawable.OVAL, 50, 0);
    }

    @NonNull
    @Override
    public FacebookFriendsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view = layoutInflater.inflate(R.layout.person_vert_list_item, viewGroup, false);
        final FacebookFriendsAdapter.MyViewHolder holder = new FacebookFriendsAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FacebookFriendsAdapter.MyViewHolder myViewHolder, int position) {
        User user = userListResponse.getItems().get(position);
        myViewHolder.setData(user, position);
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView usernameTextView;
        TextView shortenTextView;
        ImageView profilePicImgView;
        CardView personRootCardView;
        User user;
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
                    listItemClickListener.onClick(v, followItem, position);
                }
            });
        }

        public FollowInfoResultArrayItem getFollowProperties() {
            FollowInfoResultArrayItem followItem = new FollowInfoResultArrayItem();
            followItem.setBirthday(user.getName());
            followItem.setEmail(user.getUsername());
            followItem.setProfilePhotoUrl(user.getProfilePhotoUrl());
            followItem.setUserid(user.getUserid());
            followItem.setIsPrivateAccount(user.getIsPrivateAccount());
            followItem.setIsPendingRequest(user.getFollowStatus().equals(PROVIDER_FOLLOW_STATUS_PENDING));
            followItem.setIsFollow(user.getFollowStatus().equals(PROVIDER_FOLLOW_STATUS_FOLLOWING));
            return followItem;
        }

        public void checkFriendRelation() {

            if (user.getFollowStatus() != null) {
                if (user.getFollowStatus().equals(PROVIDER_FOLLOW_STATUS_FOLLOWING))
                    processFriendRequest(FRIEND_DELETE_FOLLOW);
                else if (user.getFollowStatus().equals(PROVIDER_FOLLOW_STATUS_PENDING))
                    processFriendRequest(FRIEND_DELETE_PENDING_FOLLOW_REQUEST);
                else {
                    if (user.getIsPrivateAccount() != null && user.getIsPrivateAccount())
                        processFriendRequest(FRIEND_FOLLOW_REQUEST);
                    else
                        processFriendRequest(FRIEND_CREATE_FOLLOW_DIRECTLY);
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

                    if (relationProperties.getFriendRelation())
                        user.setFollowStatus(PROVIDER_FOLLOW_STATUS_FOLLOWING);
                    else if (relationProperties.getPendingFriendRequest())
                        user.setFollowStatus(PROVIDER_FOLLOW_STATUS_PENDING);
                    else
                        user.setFollowStatus(PROVIDER_FOLLOW_STATUS_NONE);

                    userListResponse.getItems().remove(position);
                    userListResponse.getItems().add(position, user);

                    UserDataUtil.updateFollowButton(context, relationProperties.getFriendRelation(), relationProperties.getPendingFriendRequest(), statuDisplayBtn, true);
                    AccountHolderInfo.getInstance().updateAccountHolderFollowCnt(requestType);
                    updateFollowingList(requestType);
                    statuDisplayBtn.setEnabled(true);
                }

                @Override
                public void onFailure(Exception e) {
                    statuDisplayBtn.setEnabled(true);
                    DialogBoxUtil.showErrorDialog(context, context.getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {
                        }
                    });
                }

                @Override
                public void onTaskContinue() {

                }
            }, requestType, AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid(), requestedUserid, token);

            friendRequestProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        public void updateFollowingList(final String requestType) {

            AccountHolderFollowings.getInstance(new CompleteCallback() {
                @Override
                public void onComplete(Object object) {
                    FollowInfoResultArrayItem followInfoResultArrayItem = new FollowInfoResultArrayItem();
                    followInfoResultArrayItem.setName(user.getName());
                    followInfoResultArrayItem.setProfilePhotoUrl(user.getProfilePhotoUrl());
                    followInfoResultArrayItem.setUserid(user.getUserid());
                    followInfoResultArrayItem.setUsername(user.getUsername());
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

        public void setData(User user, int position) {
            this.position = position;
            this.user = user;
            this.requestedUserid = user.getUserid();
            setUserName();
            UserDataUtil.setName(user.getName(), nameTextView);
            UserDataUtil.setProfilePicture(context, user.getProfilePhotoUrl(), user.getName(), shortenTextView, profilePicImgView);

            if (user.getUserid().equals(AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid()))
                statuDisplayBtn.setVisibility(View.GONE);
            else {
                UserDataUtil.updateFollowButton(context, user.getFollowStatus().equals(PROVIDER_FOLLOW_STATUS_FOLLOWING),
                        user.getFollowStatus().equals(PROVIDER_FOLLOW_STATUS_PENDING), statuDisplayBtn, false);
                statuDisplayBtn.setVisibility(View.VISIBLE);
            }
        }

        public void setUserName() {
            if (user.getUsername() != null && !user.getUsername().trim().isEmpty())
                this.usernameTextView.setText(user.getUsername());
        }
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String searchString = charSequence.toString();

                if (searchString.trim().isEmpty())
                    userListResponse = orgUserListResponse;
                else {
                    UserListResponse tempUserListResponse = new UserListResponse();
                    List<User> userList = new ArrayList<>();
                    tempUserListResponse.setItems(userList);

                    for (User user : orgUserListResponse.getItems()) {
                        if (user.getName().toLowerCase().contains(searchString.toLowerCase()))
                            tempUserListResponse.getItems().add(user);
                    }
                    userListResponse = tempUserListResponse;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = userListResponse;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                userListResponse = (UserListResponse) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void updateAdapter(String searchText) {
        getFilter().filter(searchText);
    }

    @Override
    public int getItemCount() {
        return userListResponse.getItems().size();
    }

    public void updateAdapterWithPosition(int position) {
        notifyItemChanged(position);
    }
}