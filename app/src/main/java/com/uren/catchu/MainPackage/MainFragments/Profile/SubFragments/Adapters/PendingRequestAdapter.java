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
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
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
        try {
            this.context = context;
            this.friendRequestList = friendRequestList;
            this.listItemClickListener = listItemClickListener;
            this.returnCallback = returnCallback;
            imageShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                    0, GradientDrawable.OVAL, 50, 0);
        } catch (Exception e) {
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
                    .inflate(R.layout.pending_request_list_item, parent, false);
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
        Button btnApprove;
        Button btnReject;
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

        public void rejectPendingRequest() {
            try {
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
                UserDataUtil.updatePendingApproveButton(context, btnApprove);
                UserDataUtil.updatePendingRejectButton(context, btnReject);
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