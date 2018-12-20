package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters;


import android.content.Context;
import android.content.res.Resources;
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
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.GeneralUtils.ProgressDialogUtil.ProgressDialogUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ListItemClickListener;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import catchu.model.FollowInfoListResponse;
import catchu.model.FriendRequestList;
import catchu.model.User;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.CHAR_AMPERSAND;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_FOLLOWING;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_NONE;
import static com.uren.catchu.Constants.StringConstants.FRIEND_ACCEPT_REQUEST;

public class PendingRequestAdapter extends RecyclerView.Adapter<PendingRequestAdapter.PendingRequestHolder> {

    private Context context;
    GradientDrawable imageShape;
    private ListItemClickListener listItemClickListener;
    ReturnCallback returnCallback;
    FriendRequestList friendRequestList;

    public PendingRequestAdapter(Context context, FriendRequestList friendRequestList, ListItemClickListener listItemClickListener,
                                 ReturnCallback returnCallback) {
        try {
            this.context = context;
            this.friendRequestList = friendRequestList;
            this.listItemClickListener = listItemClickListener;
            this.returnCallback = returnCallback;
            imageShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                    0, GradientDrawable.OVAL, 50, 0);
        } catch (Resources.NotFoundException e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public PendingRequestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        try {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.follow_vert_list_item, parent, false);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return new PendingRequestHolder(itemView);
    }

    public class PendingRequestHolder extends RecyclerView.ViewHolder {

        TextView profileName;
        TextView profileUserName;
        TextView shortUserNameTv;
        ImageView profileImage;
        Button btnFollowStatus;
        CardView cardView;
        UserProfileProperties userProfileProperties;
        int position;

        public PendingRequestHolder(View view) {
            super(view);

            try {
                profileName = view.findViewById(R.id.profile_name);
                profileUserName = view.findViewById(R.id.profile_user_name);
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
                                User user = new User();
                                user.setEmail(userProfileProperties.getUsername());
                                user.setProfilePhotoUrl(userProfileProperties.getProfilePhotoUrl());
                                user.setUserid(userProfileProperties.getUserid());
                                user.setIsPrivateAccount(userProfileProperties.getIsPrivateAccount());
                                user.setFollowStatus(FOLLOW_STATUS_NONE);

                                if (object != null) {
                                    FollowInfoListResponse followInfo = (FollowInfoListResponse) object;
                                    for (User item : followInfo.getItems()) {
                                        if (item != null && item.getUserid() != null && !item.getUserid().isEmpty()) {
                                            if (item.getUserid().equals(userProfileProperties.getUserid())) {
                                                user.setFollowStatus(FOLLOW_STATUS_FOLLOWING);
                                                break;
                                            }
                                        }
                                    }
                                }
                                progressDialogUtil.dialogDismiss();
                                listItemClickListener.onClick(v, user, position);
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
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        public void managePendingRequest() {

            try {
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
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        public void setData(UserProfileProperties userProfileProperties, int position) {
            try {
                this.userProfileProperties = userProfileProperties;
                this.position = position;
                UserDataUtil.setName(userProfileProperties.getName(), profileName);
                UserDataUtil.setUsername(userProfileProperties.getUsername(), profileUserName);
                UserDataUtil.setProfilePicture(context, userProfileProperties.getProfilePhotoUrl(),
                        userProfileProperties.getName(),
                        userProfileProperties.getUsername(), shortUserNameTv, profileImage);
                UserDataUtil.updatePendingButton(context, btnFollowStatus);
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBindViewHolder(final PendingRequestHolder holder, final int position) {
        try {
            UserProfileProperties userProfileProperties = friendRequestList.getResultArray().get(position);
            holder.setData(userProfileProperties, position);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        int size = 0;
        try {
            size = friendRequestList.getResultArray().size();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return size;
    }
}