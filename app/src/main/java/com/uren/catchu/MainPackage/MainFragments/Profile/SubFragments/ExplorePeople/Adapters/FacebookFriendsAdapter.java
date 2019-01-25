package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
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

import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ListItemClickListener;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.List;

import catchu.model.FriendRequestList;
import catchu.model.RelationProperties;
import catchu.model.User;
import catchu.model.UserListResponse;

import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_FOLLOWING;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_NONE;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_PENDING;
import static com.uren.catchu.Constants.StringConstants.FRIEND_CREATE_FOLLOW_DIRECTLY;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_FOLLOW;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_PENDING_FOLLOW_REQUEST;
import static com.uren.catchu.Constants.StringConstants.FRIEND_FOLLOW_REQUEST;

public class FacebookFriendsAdapter extends RecyclerView.Adapter<FacebookFriendsAdapter.FacebookFriendsHolder> implements Filterable {


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
        try {
            layoutInflater = LayoutInflater.from(context);
            this.context = context;
            this.userListResponse = userListResponse;
            this.orgUserListResponse = userListResponse;
            this.listItemClickListener = listItemClickListener;
            activity = (Activity) context;
            imageShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                    0, GradientDrawable.OVAL, 50, 0);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public FacebookFriendsAdapter.FacebookFriendsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        FacebookFriendsHolder holder = null;
        try {
            view = layoutInflater.inflate(R.layout.person_vert_list_item, viewGroup, false);
            holder = new FacebookFriendsHolder(view);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FacebookFriendsAdapter.FacebookFriendsHolder myViewHolder, int position) {
        try {
            User user = userListResponse.getItems().get(position);
            myViewHolder.setData(user, position);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }


    class FacebookFriendsHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView usernameTextView;
        TextView shortenTextView;
        ImageView profilePicImgView;
        CardView personRootCardView;
        User user;
        Button statuDisplayBtn;
        String requestedUserid;

        int position = 0;

        public FacebookFriendsHolder(final View itemView) {
            super(itemView);

            try {
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
                        User newUser = getFollowProperties();
                        listItemClickListener.onClick(v, newUser, position);
                    }
                });
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        public User getFollowProperties() {
            User tempUser = new User();
            try {
                tempUser.setEmail(user.getUsername());
                tempUser.setProfilePhotoUrl(user.getProfilePhotoUrl());
                tempUser.setUserid(user.getUserid());
                tempUser.setIsPrivateAccount(user.getIsPrivateAccount());
                tempUser.setFollowStatus(user.getFollowStatus());
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
            return tempUser;
        }

        public void checkFriendRelation() {

            try {
                if (user.getFollowStatus() != null) {
                    if (user.getFollowStatus().equals(FOLLOW_STATUS_FOLLOWING))
                        processFriendRequest(FRIEND_DELETE_FOLLOW);
                    else if (user.getFollowStatus().equals(FOLLOW_STATUS_PENDING))
                        processFriendRequest(FRIEND_DELETE_PENDING_FOLLOW_REQUEST);
                    else {
                        if (user.getIsPrivateAccount() != null && user.getIsPrivateAccount())
                            processFriendRequest(FRIEND_FOLLOW_REQUEST);
                        else
                            processFriendRequest(FRIEND_CREATE_FOLLOW_DIRECTLY);
                    }
                }
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        public void processFriendRequest(final String requestType) {

            try {
                AccountHolderFollowProcess.friendFollowRequest(requestType, AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid(), requestedUserid,
                        new CompleteCallback() {
                            @Override
                            public void onComplete(Object object) {
                                RelationProperties relationProperties = ((FriendRequestList) object).getUpdatedUserRelationInfo();

                                if (relationProperties.getFriendRelation())
                                    user.setFollowStatus(FOLLOW_STATUS_FOLLOWING);
                                else if (relationProperties.getPendingFriendRequest())
                                    user.setFollowStatus(FOLLOW_STATUS_PENDING);
                                else
                                    user.setFollowStatus(FOLLOW_STATUS_NONE);

                                userListResponse.getItems().remove(position);
                                userListResponse.getItems().add(position, user);

                                UserDataUtil.updateFollowButton(context, relationProperties.getFriendRelation(), relationProperties.getPendingFriendRequest(), statuDisplayBtn, true);
                                AccountHolderInfo.getInstance().updateAccountHolderFollowCnt(requestType);
                                statuDisplayBtn.setEnabled(true);
                            }

                            @Override
                            public void onFailed(Exception e) {
                                statuDisplayBtn.setEnabled(true);
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

        public void setData(User user, int position) {
            try {
                this.position = position;
                this.user = user;
                this.requestedUserid = user.getUserid();
                setUserName();
                UserDataUtil.setName(user.getName(), nameTextView);
                UserDataUtil.setProfilePicture(context, user.getProfilePhotoUrl(), user.getName(), user.getUsername(), shortenTextView, profilePicImgView);

                if (user.getUserid().equals(AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid()))
                    statuDisplayBtn.setVisibility(View.GONE);
                else {
                    UserDataUtil.updateFollowButton(context, user.getFollowStatus().equals(FOLLOW_STATUS_FOLLOWING),
                            user.getFollowStatus().equals(FOLLOW_STATUS_PENDING), statuDisplayBtn, false);
                    statuDisplayBtn.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        public void setUserName() {
            try {
                if (user.getUsername() != null && !user.getUsername().trim().isEmpty())
                    this.usernameTextView.setText(user.getUsername());
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
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

                FilterResults filterResults = null;
                try {
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

                    filterResults = new FilterResults();
                    filterResults.values = userListResponse;
                } catch (Exception e) {
                    ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                            new Object() {
                            }.getClass().getEnclosingMethod().getName(), e.toString());
                    e.printStackTrace();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                try {
                    userListResponse = (UserListResponse) filterResults.values;
                    notifyDataSetChanged();
                } catch (Exception e) {
                    ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                            new Object() {
                            }.getClass().getEnclosingMethod().getName(), e.toString());
                    e.printStackTrace();
                }
            }
        };
    }

    public void updateAdapter(String searchText) {
        try {
            getFilter().filter(searchText);
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
            size = userListResponse.getItems().size();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return size;
    }
}