package com.uren.catchu.MainPackage.MainFragments.Feed.Adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.YesNoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.SearchResultDiffCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ListItemClickListener;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.List;

import catchu.model.User;
import catchu.model.UserListResponse;

import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_FOLLOWING;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_NONE;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_PENDING;
import static com.uren.catchu.Constants.StringConstants.FRIEND_CREATE_FOLLOW_DIRECTLY;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_FOLLOW;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_PENDING_FOLLOW_REQUEST;
import static com.uren.catchu.Constants.StringConstants.FRIEND_FOLLOW_REQUEST;

public class SearchResultAdapter extends RecyclerView.Adapter {

    public static final int VIEW_PROG = 0;
    public static final int VIEW_ITEM = 1;
    public static final int VIEW_NULL = 2;

    private Context mContext;
    private List<User> userList;
    private ListItemClickListener listItemClickListener;
    GradientDrawable imageShape;

    public SearchResultAdapter(Context context, BaseFragment.FragmentNavigation fragmentNavigation) {
        this.mContext = context;
        BaseFragment.FragmentNavigation fragmentNavigation1 = fragmentNavigation;
        this.userList = new ArrayList<>();

        imageShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                0, GradientDrawable.OVAL, 50, 0);
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
                    .inflate(R.layout.follow_vert_list_item, parent, false);

            viewHolder = new MyViewHolder(itemView);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar_item, parent, false);

            viewHolder = new ProgressViewHolder(v);
        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyViewHolder) {
            User user = userList.get(position);
            ((MyViewHolder) holder).setData(user, position);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView profileName;
        TextView profileUserName;
        TextView shortUserNameTv;
        ImageView profileImage;
        Button btnFollowStatus;
        CardView cardView;
        User user;
        int position;

        public MyViewHolder(View view) {
            super(view);

            profileName = view.findViewById(R.id.profile_name);
            profileUserName = view.findViewById(R.id.profile_user_name);
            shortUserNameTv = view.findViewById(R.id.shortUserNameTv);
            profileImage = view.findViewById(R.id.profile_image);
            btnFollowStatus = view.findViewById(R.id.btnFollowStatus);
            cardView = view.findViewById(R.id.card_view);
            profileImage.setBackground(imageShape);
            setListeners();
        }

        private void setListeners() {

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
        }

        public void manageFollowStatus() {

            //takip ediliyor ise
            switch (user.getFollowStatus()) {
                case FOLLOW_STATUS_FOLLOWING:
                    if (user.getIsPrivateAccount() != null && user.getIsPrivateAccount()) {
                        openDialogBox();
                    } else {
                        updateFollowStatus(FRIEND_DELETE_FOLLOW);
                    }
                    break;
                case FOLLOW_STATUS_PENDING:
                    //istek gonderilmis ise
                    updateFollowStatus(FRIEND_DELETE_PENDING_FOLLOW_REQUEST);
                    break;
                case FOLLOW_STATUS_NONE:
                    //takip istegi yok ise
                    if (user.getIsPrivateAccount() != null && user.getIsPrivateAccount()) {
                        updateFollowStatus(FRIEND_FOLLOW_REQUEST);
                    } else {
                        updateFollowStatus(FRIEND_CREATE_FOLLOW_DIRECTLY);
                    }
                    break;
                default:
                    //do nothing
                    break;
            }
        }

        public void setData(User user, int position) {

            this.user = user;
            this.position = position;
            UserDataUtil.setName(user.getName(), profileName);
            UserDataUtil.setUsername(user.getUsername(), profileUserName);
            UserDataUtil.setProfilePicture(mContext, user.getProfilePhotoUrl(),
                    user.getName(), user.getUsername(), shortUserNameTv, profileImage, false);

            UserDataUtil.updateFollowButton2(mContext, user.getFollowStatus(), btnFollowStatus, true);
        }


        private void openDialogBox() {

            DialogBoxUtil.removeFromFollowingsDialog(mContext, user, new YesNoDialogBoxCallback() {
                @Override
                public void yesClick() {
                    updateFollowStatus(FRIEND_DELETE_FOLLOW);
                }

                @Override
                public void noClick() {

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

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBarLoading);
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

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateAdapterWithPosition(int position) {
        notifyItemChanged(position);
    }

    public List<User> getPersonList() {
        return userList;
    }

    public void addAll(UserListResponse userListResponse) {
        if (userListResponse != null) {
            userList.addAll(userListResponse.getItems());
            notifyItemRangeInserted(userList.size(), userList.size() + userListResponse.getItems().size());
        }
    }

    public void updateListItems(List<User> newUserList) {
        final SearchResultDiffCallback diffCallback = new SearchResultDiffCallback(this.getPersonList(), newUserList);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.userList.clear();
        this.userList.addAll(newUserList);
        diffResult.dispatchUpdatesTo(this);
    }

    public void clearList() {
        userList.clear();
        notifyDataSetChanged();
    }

    public void setListItemClickListener(ListItemClickListener listItemClickListener) {
        this.listItemClickListener = listItemClickListener;
    }

}


