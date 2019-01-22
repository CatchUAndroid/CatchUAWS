package com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Interfaces.ClickCallback;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.SelectedFriendList;

import java.util.ArrayList;
import java.util.List;

import catchu.model.FriendList;
import catchu.model.UserProfileProperties;

public class FriendVerticalListAdapter extends RecyclerView.Adapter implements Filterable {

    View view;
    LayoutInflater layoutInflater;
    private Context context;
    private FriendList friendList;
    private FriendList orginalFriendList;
    private RecyclerView horRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private boolean horAdapterUpdateChk = false;
    private SelectFriendHorizontalAdapter selectFriendHorizontalAdapter = null;
    private List<UserProfileProperties> groupParticipantList;

    public static final int VIEW_PROG = 0;
    public static final int VIEW_ITEM = 1;
    public static final int VIEW_NULL = 2;

    public static final int CODE_ADD_ITEM = 0;
    public static final int CODE_REMOVE_ITEM = 1;

    public FriendVerticalListAdapter(Context context, List<UserProfileProperties> groupParticipantList) {
        try {
            layoutInflater = LayoutInflater.from(context);
            this.context = context;
            this.friendList = new FriendList();
            this.friendList.setResultArray(new ArrayList<UserProfileProperties>());
            this.orginalFriendList = new FriendList();
            this.orginalFriendList.setResultArray(new ArrayList<UserProfileProperties>());
            this.groupParticipantList = groupParticipantList;
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (friendList.getResultArray().size() > 0 && position >= 0) {
            return friendList.getResultArray().get(position) != null ? VIEW_ITEM : VIEW_PROG;
        } else {
            return VIEW_NULL;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.friend_vert_list_item, parent, false);

            viewHolder = new FriendVerticalListAdapter.SelectFriendHolder(itemView);
            horRecyclerView = ((Activity) context).findViewById(R.id.horRecyclerView);
            linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            horRecyclerView.setLayoutManager(linearLayoutManager);
            setHorizontalAdapter();
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar_item, parent, false);

            viewHolder = new FriendVerticalListAdapter.ProgressViewHolder(v);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof SelectFriendHolder) {
                UserProfileProperties userProfileProperties = friendList.getResultArray().get(position);
                ((SelectFriendHolder) holder).setData(userProfileProperties, position);
            } else {
                ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void setHorizontalAdapter() {
        try {
            selectFriendHorizontalAdapter = new SelectFriendHorizontalAdapter(context, new ClickCallback() {
                @Override
                public void onItemClick() {
                    try {
                        FriendVerticalListAdapter.this.notifyDataSetChanged();

                        if (SelectedFriendList.getInstance().getSize() == 0)
                            horRecyclerView.setVisibility(View.GONE);
                    } catch (Exception e) {
                        ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                                new Object() {
                                }.getClass().getEnclosingMethod().getName(), e.toString());
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    class SelectFriendHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView usernameTextView;
        TextView shortUserNameTv;
        ImageView profilePicImgView;
        ImageView tickImgv;
        LinearLayout specialListLinearLayout;
        UserProfileProperties selectedFriend;
        TextView inGroupTv;
        int position = 0;

        public SelectFriendHolder(final View itemView) {
            super(itemView);

            view = itemView;
            profilePicImgView = view.findViewById(R.id.profilePicImgView);
            nameTextView = view.findViewById(R.id.nameTextView);
            usernameTextView = view.findViewById(R.id.usernameTextView);
            specialListLinearLayout = view.findViewById(R.id.specialListLinearLayout);
            shortUserNameTv = view.findViewById(R.id.shortUserNameTv);
            tickImgv = view.findViewById(R.id.tickImgv);
            inGroupTv = view.findViewById(R.id.inGroupTv);
            setShapes();

            specialListLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonUtils.hideKeyBoard(context);
                    if (tickImgv.getVisibility() == View.VISIBLE) {
                        tickImgv.setVisibility(View.GONE);
                        SelectedFriendList.getInstance().removeFriend(selectedFriend);
                        checkHorizontalAdapter(CODE_REMOVE_ITEM);
                    } else {
                        tickImgv.setVisibility(View.VISIBLE);
                        SelectedFriendList.getInstance().addFriend(selectedFriend);
                        checkHorizontalAdapter(CODE_ADD_ITEM);
                    }
                }
            });
        }

        private void setShapes() {
            profilePicImgView.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                    context.getResources().getColor(R.color.Orange, null), GradientDrawable.OVAL, 50, 0));
            tickImgv.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.DarkTurquoise, null),
                    context.getResources().getColor(R.color.White, null), GradientDrawable.OVAL, 50, 3));
        }

        public void setData(UserProfileProperties selectedFriend, int position) {
            try {
                this.position = position;
                this.selectedFriend = selectedFriend;
                UserDataUtil.setName(selectedFriend.getName(), nameTextView);
                UserDataUtil.setUsername(selectedFriend.getUsername(), usernameTextView);
                UserDataUtil.setProfilePicture(context, selectedFriend.getProfilePhotoUrl(),
                        selectedFriend.getName(), selectedFriend.getUsername(), shortUserNameTv, profilePicImgView);
                updateTickImgv();
                updateItemEnableValue();
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        private void updateItemEnableValue(){
            if(isInParticipantList(selectedFriend)){
                specialListLinearLayout.setEnabled(false);
                specialListLinearLayout.setBackgroundColor(context.getResources().getColor(R.color.AliceBlue, null));
                inGroupTv.setVisibility(View.VISIBLE);
            }else {
                specialListLinearLayout.setEnabled(true);
                specialListLinearLayout.setBackgroundColor(context.getResources().getColor(R.color.White, null));
                inGroupTv.setVisibility(View.GONE);
            }
        }

        public void updateTickImgv() {
            try {
                if (SelectedFriendList.getInstance().isUserInList(selectedFriend.getUserid()))
                    tickImgv.setVisibility(View.VISIBLE);
                else
                    tickImgv.setVisibility(View.GONE);
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        public void checkHorizontalAdapter(int type) {
            try {
                if (horAdapterUpdateChk == false) {
                    horRecyclerView.setVisibility(View.VISIBLE);
                    horRecyclerView.setAdapter(selectFriendHorizontalAdapter);
                    horAdapterUpdateChk = true;
                } else {
                    horRecyclerView.setAdapter(selectFriendHorizontalAdapter);

                    /*if (type == CODE_ADD_ITEM) {
                        selectFriendHorizontalAdapter.notifyItemInserted(SelectedFriendList.getInstance().getSize() - 1);
                        horRecyclerView.smoothScrollToPosition(SelectedFriendList.getInstance().getSize());
                    } else if (type == CODE_REMOVE_ITEM) {
                        selectFriendHorizontalAdapter.notifyDataSetChanged();
                    }*/

                    if (SelectedFriendList.getInstance().getSize() == 0) {
                        horRecyclerView.setVisibility(View.GONE);
                    } else if (horRecyclerView.getVisibility() == View.GONE) {
                        horRecyclerView.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }
    }

    public boolean isInParticipantList(UserProfileProperties userProfileProperties) {

        try {
            if(groupParticipantList != null) {
                for (UserProfileProperties userProfileProperties1 : groupParticipantList) {
                    if (userProfileProperties.getUserid().equals(userProfileProperties1.getUserid()))
                        return true;
                }
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public void updateAdapter(String searchText) {
        getFilter().filter(searchText);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                try {
                    String searchString = charSequence.toString();
                    if (searchString.trim().isEmpty())
                        friendList.setResultArray(orginalFriendList.getResultArray());
                    else {
                        FriendList tempFriendList = new FriendList();
                        List<UserProfileProperties> userList = new ArrayList<>();
                        tempFriendList.setResultArray(userList);

                        for (UserProfileProperties userProfileProperties : orginalFriendList.getResultArray()) {
                            if (userProfileProperties.getName() != null &&
                                    userProfileProperties.getName().toLowerCase().contains(searchString.toLowerCase()))
                                tempFriendList.getResultArray().add(userProfileProperties);
                            else if (userProfileProperties.getUsername() != null &&
                                    userProfileProperties.getUsername().toLowerCase().contains(searchString.toLowerCase()))
                                tempFriendList.getResultArray().add(userProfileProperties);
                        }
                        friendList.setResultArray(tempFriendList.getResultArray());
                    }

                    filterResults.values = friendList;
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
                    friendList = (FriendList) filterResults.values;
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

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return ((friendList != null && friendList.getResultArray() != null) ? friendList.getResultArray().size() : 0);
    }

    public void addAll(List<UserProfileProperties> addedUserList) {
        if (addedUserList != null) {
            friendList.getResultArray().addAll(addedUserList);
            orginalFriendList.getResultArray().addAll(addedUserList);
            notifyItemRangeInserted(friendList.getResultArray().size(), friendList.getResultArray().size() + addedUserList.size());
        }
    }

    public void addProgressLoading() {
        if (getItemViewType(friendList.getResultArray().size() - 1) != VIEW_PROG) {
            friendList.getResultArray().add(null);
            notifyItemInserted(friendList.getResultArray().size() - 1);
        }
    }

    public void removeProgressLoading() {
        if (getItemViewType(friendList.getResultArray().size() - 1) == VIEW_PROG) {
            friendList.getResultArray().remove(friendList.getResultArray().size() - 1);
            notifyItemRemoved(friendList.getResultArray().size());
        }
    }

    public boolean isShowingProgressLoading() {
        if (getItemViewType(friendList.getResultArray().size() - 1) == VIEW_PROG)
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
}