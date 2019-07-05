package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ListItemClickListener;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import catchu.model.FriendRequestList;
import catchu.model.User;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.FRIEND_ACCEPT_REQUEST;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_PENDING_FOLLOW_REQUEST;

public class PendingRequestAdapter extends RecyclerView.Adapter<PendingRequestAdapter.PendingRequestHolder> {

    private Context context;
    GradientDrawable imageShape;
    private ListItemClickListener listItemClickListener;
    ReturnCallback returnCallback;
    FriendRequestList friendRequestList;

    public PendingRequestAdapter(Context context, FriendRequestList friendRequestList, ListItemClickListener listItemClickListener,
                                 ReturnCallback returnCallback) {
        this.context = context;
        this.friendRequestList = friendRequestList;
        this.listItemClickListener = listItemClickListener;
        this.returnCallback = returnCallback;
        imageShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                0, GradientDrawable.OVAL, 50, 0);
    }

    @Override
    public PendingRequestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_request_list_item, parent, false);
        return new PendingRequestHolder(itemView);
    }

    public class PendingRequestHolder extends RecyclerView.ViewHolder {

        TextView profileName;
        TextView profileUserName;
        TextView shortUserNameTv;
        ImageView profileImage;
        Button btnApprove;
        Button btnReject;
        CardView cardView;
        UserProfileProperties userProfileProperties;
        int position;

        public PendingRequestHolder(View view) {
            super(view);

            profileName = view.findViewById(R.id.profile_name);
            profileUserName = view.findViewById(R.id.profile_user_name);
            shortUserNameTv = view.findViewById(R.id.shortUserNameTv);
            profileImage = view.findViewById(R.id.profile_image);
            btnApprove = view.findViewById(R.id.btnApprove);
            btnReject = view.findViewById(R.id.btnReject);
            cardView = view.findViewById(R.id.card_view);
            profileImage.setBackground(imageShape);

            btnApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnApprove.setEnabled(false);
                    btnApprove.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_click));
                    managePendingRequest();
                }
            });

            btnReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnReject.setEnabled(false);
                    btnReject.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_click));
                    rejectPendingRequest();
                }
            });

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    User user = new User();
                    user.setUserid(userProfileProperties.getUserid());
                    user.setProfilePhotoUrl(userProfileProperties.getProfilePhotoUrl());
                    user.setUsername(userProfileProperties.getUsername());

                    listItemClickListener.onClick(v, user, position);
                }
            });
        }

        public void managePendingRequest() {

            AccountHolderFollowProcess.acceptFriendRequest(userProfileProperties.getUserid(), new CompleteCallback() {
                @Override
                public void onComplete(Object object) {
                    AccountHolderInfo.getInstance().updateAccountHolderFollowCnt(FRIEND_ACCEPT_REQUEST);
                    friendRequestList.getResultArray().remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                    returnCallback.onReturn(null);
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

        public void rejectPendingRequest() {
            AccountHolderFollowProcess.friendFollowRequest(FRIEND_DELETE_PENDING_FOLLOW_REQUEST,
                    userProfileProperties.getUserid(), AccountHolderInfo.getUserID(),
                    new CompleteCallback() {
                        @Override
                        public void onComplete(Object object) {
                            friendRequestList.getResultArray().remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, getItemCount());
                            returnCallback.onReturn(null);
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

        public void setData(UserProfileProperties userProfileProperties, int position) {
            this.userProfileProperties = userProfileProperties;
            this.position = position;
            UserDataUtil.setName(userProfileProperties.getName(), profileName);
            UserDataUtil.setUsername(userProfileProperties.getUsername(), profileUserName);
            UserDataUtil.setProfilePicture(context, userProfileProperties.getProfilePhotoUrl(),
                    userProfileProperties.getName(),
                    userProfileProperties.getUsername(), shortUserNameTv, profileImage, false);
            UserDataUtil.updatePendingApproveButton(context, btnApprove);
            UserDataUtil.updatePendingRejectButton(context, btnReject);
        }
    }

    @Override
    public void onBindViewHolder(final PendingRequestHolder holder, final int position) {
        UserProfileProperties userProfileProperties = friendRequestList.getResultArray().get(position);
        holder.setData(userProfileProperties, position);
    }

    @Override
    public int getItemCount() {
        int size = 0;
        size = friendRequestList.getResultArray().size();
        return size;
    }
}