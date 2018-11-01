package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ProgressDialogUtil.ProgressDialogUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ListItemClickListener;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import catchu.model.FollowInfo;
import catchu.model.FollowInfoResultArrayItem;
import catchu.model.FriendRequestList;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.FRIEND_ACCEPT_REQUEST;

public class PendingRequestAdapter extends RecyclerView.Adapter<PendingRequestAdapter.MyViewHolder> {

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
        UserProfileProperties userProfileProperties;
        int position;

        public MyViewHolder(View view) {
            super(view);

            profileName = view.findViewById(R.id.profile_name);
            shortUserNameTv = view.findViewById(R.id.shortUserNameTv);
            profileImage = view.findViewById(R.id.profile_image);
            btnFollowStatus = view.findViewById(R.id.btnFollowStatus);
            cardView = view.findViewById(R.id.card_view);
            profileImage.setBackground(imageShape);

            btnFollowStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnFollowStatus.setEnabled(false);
                    btnFollowStatus.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_click));
                    managePendingRequest();
                }
            });

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    final ProgressDialogUtil progressDialogUtil = new ProgressDialogUtil(context,
                            context.getResources().getString(R.string.loading), false);
                    progressDialogUtil.dialogShow();

                    AccountHolderFollowProcess.getFollowings(new CompleteCallback() {
                        @Override
                        public void onComplete(Object object) {
                            FollowInfoResultArrayItem followItem = new FollowInfoResultArrayItem();
                            followItem.setBirthday(userProfileProperties.getName());
                            followItem.setEmail(userProfileProperties.getUsername());
                            followItem.setProfilePhotoUrl(userProfileProperties.getProfilePhotoUrl());
                            followItem.setUserid(userProfileProperties.getUserid());
                            followItem.setIsPrivateAccount(userProfileProperties.getIsPrivateAccount());
                            followItem.setIsFollow(false);

                            if(object != null){
                                FollowInfo followInfo = (FollowInfo) object;
                                for(FollowInfoResultArrayItem item : followInfo.getResultArray()){
                                    if(item != null && item.getUserid() != null && !item.getUserid().isEmpty()){
                                        if(item.getUserid().equals(userProfileProperties.getUserid())){
                                            followItem.setIsFollow(true);
                                            break;
                                        }
                                    }
                                }
                            }
                            progressDialogUtil.dialogDismiss();
                            listItemClickListener.onClick(v, followItem, position);
                        }

                        @Override
                        public void onFailed(Exception e) {
                            progressDialogUtil.dialogDismiss();
                            DialogBoxUtil.showErrorDialog(context, context.getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                                @Override
                                public void okClick() {

                                }
                            });
                        }
                    });
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

        public void setData(UserProfileProperties userProfileProperties, int position) {
            this.profileName.setText(userProfileProperties.getName());
            this.userProfileProperties = userProfileProperties;
            this.position = position;
            UserDataUtil.setProfilePicture(context, userProfileProperties.getProfilePhotoUrl(),
                    userProfileProperties.getName(), shortUserNameTv, profileImage);
            UserDataUtil.updatePendingButton(context, btnFollowStatus);
        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        UserProfileProperties userProfileProperties = friendRequestList.getResultArray().get(position);
        holder.setData(userProfileProperties, position);
    }

    @Override
    public int getItemCount() {
        if (friendRequestList != null && friendRequestList.getResultArray() != null)
            return friendRequestList.getResultArray().size();
        else
            return 0;
    }

    public void updateAdapterWithPosition(int position) {

        notifyItemChanged(position);
    }

}