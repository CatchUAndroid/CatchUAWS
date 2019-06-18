package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters;

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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.CustomDialogBox;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.CustomDialogListener;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.YesNoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ListItemClickListener;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.List;

import catchu.model.User;

import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_FOLLOWING;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_NONE;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_PENDING;
import static com.uren.catchu.Constants.StringConstants.FRIEND_CREATE_FOLLOW_DIRECTLY;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_FOLLOW;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_PENDING_FOLLOW_REQUEST;
import static com.uren.catchu.Constants.StringConstants.FRIEND_FOLLOW_REQUEST;
import static com.uren.catchu.Constants.StringConstants.FRIEND_REMOVE_FROM_FOLLOWER_REQUEST;

public class FollowerAdapter extends RecyclerView.Adapter implements Filterable {

    private View mView;
    private Context mContext;
    private ListItemClickListener listItemClickListener;
    private List<User> userList;
    private List<User> orgUserList;
    private ReturnCallback searchResultCallback;
    private String userid;

    public static final int VIEW_PROG = 0;
    public static final int VIEW_ITEM = 1;
    public static final int VIEW_NULL = 2;

    public FollowerAdapter(Context context, String userid) {
        this.mContext = context;
        this.userList = new ArrayList<>();
        this.orgUserList = new ArrayList<>();
        this.userid = userid;
    }

    @Override
    public int getItemViewType(int position) {
        if (userList.size() > 0 && position >= 0) {
            return userList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
        } else {
            return VIEW_NULL;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.follower_vert_list_item, parent, false);

            viewHolder = new FollowerAdapter.FollowerViewHolder(itemView);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar_item, parent, false);

            viewHolder = new FollowerAdapter.ProgressViewHolder(v);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FollowerAdapter.FollowerViewHolder) {
            User user = userList.get(position);
            ((FollowerAdapter.FollowerViewHolder) holder).setData(user, position);
        } else {
            ((FollowerAdapter.ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public class FollowerViewHolder extends RecyclerView.ViewHolder {

        TextView profileName;
        TextView profileUserName;
        TextView shortUserNameTv;
        ImageView profileImage;
        Button btnFollowStatus;
        CardView cardView;
        //ImageView settingsImgv;
        RelativeLayout rlsettings;
        User user;
        int position;

        public FollowerViewHolder(View view) {
            super(view);

            mView = view;
            profileName = mView.findViewById(R.id.profile_name);
            profileUserName = mView.findViewById(R.id.profile_user_name);
            shortUserNameTv = mView.findViewById(R.id.shortUserNameTv);
            profileImage = mView.findViewById(R.id.profile_image);
            btnFollowStatus = mView.findViewById(R.id.btnFollowStatus);
            cardView = mView.findViewById(R.id.card_view);
            //settingsImgv = mView.findViewById(R.id.settingsImgv);
            rlsettings = mView.findViewById(R.id.rlsettings);
            setShapes();

            if(!userid.trim().equals(AccountHolderInfo.getUserID()))
                rlsettings.setVisibility(View.GONE);

            btnFollowStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnFollowStatus.setEnabled(false);
                    btnFollowStatus.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.image_click));
                    manageFollowStatus();
                }
            });

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listItemClickListener.onClick(v, user, position);
                }
            });

            rlsettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCustomDialog();
                }
            });
        }

        public void showCustomDialog() {

            new CustomDialogBox.Builder((Activity) mContext)
                    .setMessage(mContext.getResources().getString(R.string.REMOVE_FOLLOWER_MESSAGE))
                    .setTitle(mContext.getResources().getString(R.string.REMOVE_FOLLOWER_TITLE))
                    .setUser(user)
                    .setNegativeBtnVisibility(View.VISIBLE)
                    .setNegativeBtnText(mContext.getResources().getString(R.string.cancel))
                    .setNegativeBtnBackground(mContext.getResources().getColor(R.color.Silver, null))
                    .setPositiveBtnVisibility(View.VISIBLE)
                    .setPositiveBtnText(mContext.getResources().getString(R.string.REMOVE))
                    .setPositiveBtnBackground(mContext.getResources().getColor(R.color.bg_screen1, null))
                    .setDurationTime(0)
                    .isCancellable(true)
                    .OnPositiveClicked(new CustomDialogListener() {
                        @Override
                        public void OnClick() {
                            removeFollower();
                        }
                    })
                    .OnNegativeClicked(new CustomDialogListener() {
                        @Override
                        public void OnClick() {

                        }
                    }).build();
        }

        public void setShapes() {
            profileImage.setBackground(ShapeUtil.getShape(mContext.getResources().getColor(R.color.DodgerBlue, null),
                    0, GradientDrawable.OVAL, 50, 0));
        }

        private void removeFollower() {
            AccountHolderFollowProcess.friendFollowRequest(FRIEND_REMOVE_FROM_FOLLOWER_REQUEST, user.getUserid(),
                    AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid(),
                    new CompleteCallback() {
                        @Override
                        public void onComplete(Object object) {
                            AccountHolderInfo.updateAccountHolderFollowCnt(FRIEND_REMOVE_FROM_FOLLOWER_REQUEST);
                            userList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, getItemCount());
                        }

                        @Override
                        public void onFailed(Exception e) {
                            System.out.println("Exception e:" + e.toString());
                        }
                    });
        }

        private void manageFollowStatus() {

            //takip ediliyor ise
            if (user.getFollowStatus().equals(FOLLOW_STATUS_FOLLOWING)) {
                if (user.getIsPrivateAccount() != null && user.getIsPrivateAccount()) {
                    openDialogBox();
                } else {
                    updateFollowStatus(FRIEND_DELETE_FOLLOW);
                }
            } else if (user.getFollowStatus().equals(FOLLOW_STATUS_PENDING)) {
                //istek gonderilmis ise
                updateFollowStatus(FRIEND_DELETE_PENDING_FOLLOW_REQUEST);
            } else if (user.getFollowStatus().equals(FOLLOW_STATUS_NONE)) {
                //takip istegi yok ise
                if (user.getIsPrivateAccount() != null && user.getIsPrivateAccount()) {
                    updateFollowStatus(FRIEND_FOLLOW_REQUEST);
                } else {
                    updateFollowStatus(FRIEND_CREATE_FOLLOW_DIRECTLY);
                }
            } else {
                //do nothing
            }
        }

        public void setData(User user, int position) {
            this.user = user;
            this.position = position;
            UserDataUtil.setName(user.getName(), profileName);
            UserDataUtil.setUsername(user.getUsername(), profileUserName);
            UserDataUtil.setProfilePicture(mContext, user.getProfilePhotoUrl(),
                    user.getName(), user.getUsername(), shortUserNameTv, profileImage);
            UserDataUtil.updateFollowButton2(mContext, user.getFollowStatus(), btnFollowStatus, false);
        }

        private void openDialogBox() {

            DialogBoxUtil.removeFromFollowingsDialog(mContext, user, new YesNoDialogBoxCallback() {
                @Override
                public void yesClick() {
                    updateFollowStatus(FRIEND_DELETE_FOLLOW);
                }

                @Override
                public void noClick() {
                    btnFollowStatus.setEnabled(true);
                }
            });
        }

        private void updateFollowStatus(final String requestType) {

            AccountHolderFollowProcess.friendFollowRequest(requestType, AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid()
                    , user.getUserid(), new CompleteCallback() {
                        @Override
                        public void onComplete(Object object) {
                            updateFollowUI(requestType);
                            btnFollowStatus.setEnabled(true);
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

        private void updateFollowUI(String updateType) {
            AccountHolderInfo.updateAccountHolderFollowCnt(updateType);
            updateFollowTypeAfterOperation(updateType);
            notifyItemChanged(position, user.getFollowStatus());
        }

        public void updateFollowTypeAfterOperation(String updateType) {
            switch (updateType) {
                case FRIEND_DELETE_FOLLOW:
                    user.setFollowStatus(FOLLOW_STATUS_NONE);
                    break;

                case FRIEND_DELETE_PENDING_FOLLOW_REQUEST:
                    user.setFollowStatus(FOLLOW_STATUS_NONE);
                    break;

                case FRIEND_FOLLOW_REQUEST:
                    user.setFollowStatus(FOLLOW_STATUS_PENDING);
                    break;

                case FRIEND_CREATE_FOLLOW_DIRECTLY:
                    user.setFollowStatus(FOLLOW_STATUS_FOLLOWING);
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return ((userList != null) ? userList.size() : 0);
    }

    public void updateAdapterWithPosition(int position) {
        notifyItemChanged(position);
    }

    public void setListItemClickListener(ListItemClickListener listItemClickListener) {
        this.listItemClickListener = listItemClickListener;
    }

    public void addAll(List<User> addedUserList) {
        if (addedUserList != null) {
            userList.addAll(addedUserList);
            orgUserList.addAll(addedUserList);
            notifyItemRangeInserted(userList.size(), userList.size() + addedUserList.size());
        }
    }

    public void addProgressLoading() {
        if (getItemViewType(userList.size() - 1) != VIEW_PROG) {
            userList.add(null);
            notifyItemInserted(userList.size() - 1);
        }
    }

    public void removeProgressLoading() {
        if (getItemViewType(userList.size() - 1) == VIEW_PROG) {
            userList.remove(userList.size() - 1);
            notifyItemRemoved(userList.size());
        }
    }

    public boolean isShowingProgressLoading() {
        if (getItemViewType(userList.size() - 1) == VIEW_PROG)
            return true;
        else
            return false;
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBarLoading);
        }
    }

    public long getItemId(int position) {
        return position;
    }

    public void updateAdapter(String searchText, ReturnCallback searchResultCallback) {
        this.searchResultCallback = searchResultCallback;
        getFilter().filter(searchText);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();

                String searchString = charSequence.toString();

                if (searchString.trim().isEmpty()) {
                    userList.clear();
                    userList.addAll(orgUserList);
                } else {
                    List<User> tempUserList = new ArrayList<>();

                    for (User user : orgUserList) {
                        if (user.getName() != null &&
                                user.getName().toLowerCase().contains(searchString.toLowerCase()))
                            tempUserList.add(user);
                        else if (user.getUsername() != null &&
                                user.getUsername().toLowerCase().contains(searchString.toLowerCase()))
                            tempUserList.add(user);
                    }
                    userList.clear();
                    userList.addAll(tempUserList);
                }

                filterResults.values = userList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                userList = (ArrayList<User>) filterResults.values;
                notifyDataSetChanged();

                if (userList != null && userList.size() > 0)
                    searchResultCallback.onReturn(userList.size());
                else
                    searchResultCallback.onReturn(0);
            }
        };
    }

}

