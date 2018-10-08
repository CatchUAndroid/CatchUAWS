package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.ApiGatewayFunctions.FriendRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.YesNoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.SettingsFragment;
import com.uren.catchu.MainPackage.MainFragments.SearchTab.SearchFragment;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.UserFriends;

import java.util.List;

import catchu.model.FollowInfoResultArrayItem;
import catchu.model.FriendRequestList;
import catchu.model.UserProfile;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.AnimateLeftToRight;
import static com.uren.catchu.Constants.StringConstants.FRIEND_CREATE_FOLLOW_DIRECTLY;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_FOLLOW;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_PENDING_FOLLOW_REQUEST;
import static com.uren.catchu.Constants.StringConstants.FRIEND_FOLLOW_REQUEST;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.MyViewHolder> {

    private Context context;
    private List<FollowInfoResultArrayItem> followList;
    private RowItemClickListener rowItemClickListener;
    GradientDrawable imageShape;
    GradientDrawable buttonShape;

    public FollowAdapter(Context context, List<FollowInfoResultArrayItem> followList, RowItemClickListener rowItemClickListener) {
        this.context = context;
        this.followList = followList;
        this.rowItemClickListener = rowItemClickListener;
        imageShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                0, GradientDrawable.OVAL, 50, 0);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.follow_vert_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView profileName;
        TextView shortUserNameTv;
        ImageView profileImage;
        Button btnFollowStatus;
        CardView cardView;
        FollowInfoResultArrayItem followListItem;
        int position;

        public MyViewHolder(View view) {
            super(view);

            profileName = (TextView) view.findViewById(R.id.profile_name);
            shortUserNameTv = view.findViewById(R.id.shortUserNameTv);
            profileImage = (ImageView) view.findViewById(R.id.profile_image);
            btnFollowStatus = (Button) view.findViewById(R.id.btnFollowStatus);
            cardView = (CardView) view.findViewById(R.id.card_view);
            profileImage.setBackground(imageShape);

            btnFollowStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnFollowStatus.setEnabled(false);
                    btnFollowStatus.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_click));
                    manageFollowStatus();
                }
            });

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    rowItemClickListener.onClick(v, followListItem, position);
                }
            });
        }

        public void manageFollowStatus() {
            if (followListItem.getIsFollow() != null && followListItem.getIsFollow()) {

                if (followListItem.getIsPrivateAccount() != null && followListItem.getIsPrivateAccount()) {
                    openDialogBox();
                } else {
                    updateFollowStatus(FRIEND_DELETE_FOLLOW);
                }

            } else if (followListItem.getIsFollow() != null && followListItem.getIsPendingRequest()) {

                updateFollowStatus(FRIEND_DELETE_PENDING_FOLLOW_REQUEST);
            } else {
                if (followListItem.getIsPrivateAccount() != null && followListItem.getIsPrivateAccount()) {
                    updateFollowStatus(FRIEND_FOLLOW_REQUEST);
                } else {
                    updateFollowStatus(FRIEND_CREATE_FOLLOW_DIRECTLY);
                }
            }
        }

        public void setData(FollowInfoResultArrayItem rowItem, int position) {

            this.profileName.setText(rowItem.getName());
            this.followListItem = rowItem;
            this.position = position;
            UserDataUtil.setProfilePicture(context, followListItem.getProfilePhotoUrl(),
                    followListItem.getName(), shortUserNameTv, profileImage);
            UserDataUtil.updateFollowButton(context, followListItem.getIsFollow(), followListItem.getIsPendingRequest(), btnFollowStatus);
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

            DialogBoxUtil.showYesNoDialog(context, "", context.getString(R.string.cancel_following), yesNoDialogBoxCallback);
        }

        private void updateFollowStatus(final String requestType) {

            AccountHolderInfo.getToken(new TokenCallback() {
                @Override
                public void onTokenTaken(String token) {
                    startUpdateFollowStatus(requestType, token);
                }
            });
        }

        private void startUpdateFollowStatus(final String requestType, String token) {

            FriendRequestProcess friendRequestProcess = new FriendRequestProcess(new OnEventListener<FriendRequestList>() {
                @Override
                public void onSuccess(FriendRequestList object) {
                    updateFollowUI(requestType);
                    updateUserFriends(requestType);
                    btnFollowStatus.setEnabled(true);
                }

                @Override
                public void onFailure(Exception e) {
                    CommonUtils.showToastLong(context, context.getResources().getString(R.string.error) + e.toString());
                    btnFollowStatus.setEnabled(true);
                }

                @Override
                public void onTaskContinue() {

                }
            }, requestType
                    , AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid()
                    , followListItem.getUserid()
                    , token);

            friendRequestProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        private void updateFollowUI(String updateType) {
            AccountHolderInfo.updateAccountHolderFollowCnt(updateType);
            updateFollowTypeAfterOperation(updateType);
            notifyItemChanged(position, followListItem.getIsPendingRequest());
            notifyItemChanged(position, followListItem.getIsFollow());
        }

        public void updateFollowTypeAfterOperation(String updateType){
            switch (updateType) {
                case FRIEND_DELETE_FOLLOW:
                    followListItem.setIsFollow(false);
                    followListItem.setIsPendingRequest(false);
                    break;

                case FRIEND_DELETE_PENDING_FOLLOW_REQUEST:
                    followListItem.setIsFollow(false);
                    followListItem.setIsPendingRequest(false);
                    break;

                case FRIEND_FOLLOW_REQUEST:
                    followListItem.setIsPendingRequest(true);
                    break;

                case FRIEND_CREATE_FOLLOW_DIRECTLY:
                    followListItem.setIsFollow(true);
                    break;

                default:
                    break;
            }
        }

        public void updateUserFriends(String requestType) {
            UserProfileProperties userProfileProperties = new UserProfileProperties();
            userProfileProperties.setName(followListItem.getName());
            userProfileProperties.setProfilePhotoUrl(followListItem.getProfilePhotoUrl());
            userProfileProperties.setUserid(followListItem.getUserid());
            userProfileProperties.setUsername(followListItem.getUsername());
            UserFriends.updateFriendListByFollowType(requestType, userProfileProperties);
        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        FollowInfoResultArrayItem followInfoResultArrayItem = followList.get(position);
        holder.setData(followInfoResultArrayItem, position);
    }

    @Override
    public int getItemCount() {
        return followList.size();
    }

    public void updateAdapterWithPosition(int position) {

        notifyItemChanged(position);
    }

    public interface RowItemClickListener {

        void onClick(View view, FollowInfoResultArrayItem rowItem, int clickedPosition);
    }
}


