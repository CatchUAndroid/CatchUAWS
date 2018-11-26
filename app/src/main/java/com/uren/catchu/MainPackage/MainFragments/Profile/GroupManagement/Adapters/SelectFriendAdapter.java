package com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Interfaces.ClickCallback;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.SelectedFriendList;

import java.util.ArrayList;
import java.util.List;

import catchu.model.FriendList;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.NumericConstants.CODE_SELECT_ALL;
import static com.uren.catchu.Constants.NumericConstants.CODE_UNSELECT_ALL;

public class SelectFriendAdapter extends RecyclerView.Adapter<SelectFriendAdapter.MyViewHolder> implements Filterable {

    View view;
    LayoutInflater layoutInflater;
    Context context;
    Activity activity;
    FriendList friendList;
    FriendList orginalFriendList;
    RecyclerView horRecyclerView;
    LinearLayoutManager linearLayoutManager;
    boolean horAdapterUpdateChk;
    SelectedItemAdapter selectedItemAdapter = null;
    GradientDrawable imageShape;
    ReturnCallback returnCallback;

    public SelectFriendAdapter(Context context, FriendList friendList, ReturnCallback returnCallback) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.friendList = new FriendList();
        this.friendList.setResultArray(friendList.getResultArray());
        orginalFriendList = new FriendList();
        orginalFriendList.setResultArray(this.friendList.getResultArray());
        activity = (Activity) context;
        this.returnCallback = returnCallback;
        horAdapterUpdateChk = false;
        imageShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                context.getResources().getColor(R.color.Orange, null), GradientDrawable.OVAL, 50, 0);
    }

    @NonNull
    @Override
    public SelectFriendAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view = layoutInflater.inflate(R.layout.friend_vert_list_item, viewGroup, false);
        final SelectFriendAdapter.MyViewHolder holder = new SelectFriendAdapter.MyViewHolder(view);
        horRecyclerView = activity.findViewById(R.id.horRecyclerView);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        horRecyclerView.setLayoutManager(linearLayoutManager);
        setHorizontalAdapter();
        return holder;
    }

    private void setHorizontalAdapter() {
        selectedItemAdapter = new SelectedItemAdapter(context, new ClickCallback() {
            @Override
            public void onItemClick() {

                SelectFriendAdapter.this.notifyDataSetChanged();

                if (SelectedFriendList.getInstance().getSize() == 0)
                    horRecyclerView.setVisibility(View.GONE);
                returnCallback.onReturn(null);
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull SelectFriendAdapter.MyViewHolder myViewHolder, int position) {
        UserProfileProperties userProfileProperties = friendList.getResultArray().get(position);
        myViewHolder.setData(userProfileProperties, position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView usernameTextView;
        TextView shortUserNameTv;
        ImageView profilePicImgView;
        RadioButton selectRadioBtn;
        LinearLayout specialListLinearLayout;
        UserProfileProperties selectedFriend;
        int position = 0;

        public MyViewHolder(final View itemView) {
            super(itemView);

            profilePicImgView = view.findViewById(R.id.profilePicImgView);
            nameTextView = view.findViewById(R.id.nameTextView);
            usernameTextView = view.findViewById(R.id.usernameTextView);
            selectRadioBtn = view.findViewById(R.id.selectRadioBtn);
            specialListLinearLayout = view.findViewById(R.id.specialListLinearLayout);
            shortUserNameTv = view.findViewById(R.id.shortUserNameTv);
            profilePicImgView.setBackground(imageShape);

            specialListLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonUtils.hideKeyBoard(context);
                    if (selectRadioBtn.isChecked()) {
                        selectRadioBtn.setChecked(false);
                        SelectedFriendList.getInstance().removeFriend(selectedFriend);
                        checkHorizontalAdapter();
                        returnCallback.onReturn(null);
                    } else {
                        selectRadioBtn.setChecked(true);
                        SelectedFriendList.getInstance().addFriend(selectedFriend);
                        checkHorizontalAdapter();
                        returnCallback.onReturn(null);
                    }
                }
            });

            selectRadioBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonUtils.hideKeyBoard(context);
                    if (selectRadioBtn.isChecked()) {
                        if (!SelectedFriendList.getInstance().isUserInList(selectedFriend.getUserid())) {
                            SelectedFriendList.getInstance().addFriend(selectedFriend);
                            checkHorizontalAdapter();
                            returnCallback.onReturn(null);
                        }
                    } else {
                        if (SelectedFriendList.getInstance().isUserInList(selectedFriend.getUserid())) {
                            SelectedFriendList.getInstance().removeFriend(selectedFriend);
                            checkHorizontalAdapter();
                            returnCallback.onReturn(null);
                        }
                    }
                }
            });
        }

        public void setData(UserProfileProperties selectedFriend, int position) {
            this.position = position;
            this.selectedFriend = selectedFriend;
            setProfileName();
            setUserName();
            UserDataUtil.setProfilePicture(context, selectedFriend.getProfilePhotoUrl(),
                    selectedFriend.getName(), selectedFriend.getUsername(), shortUserNameTv, profilePicImgView);
            updateRadioButtonValue();
        }

        public void setUserName() {
            if (selectedFriend.getUsername() != null && !selectedFriend.getUsername().trim().isEmpty())
                this.usernameTextView.setText(selectedFriend.getUsername());
        }

        public void updateRadioButtonValue() {
            if (SelectedFriendList.getInstance().isUserInList(selectedFriend.getUserid()))
                selectRadioBtn.setChecked(true);
            else
                selectRadioBtn.setChecked(false);
        }

        public void setProfileName(){
            if(selectedFriend.getName() != null && !selectedFriend.getName().isEmpty())
                UserDataUtil.setName(selectedFriend.getName(), nameTextView);
        }

        public void checkHorizontalAdapter() {
            if (horAdapterUpdateChk == false) {
                horRecyclerView.setVisibility(View.VISIBLE);
                horRecyclerView.setAdapter(selectedItemAdapter);
                horAdapterUpdateChk = true;
            } else {
                horRecyclerView.setAdapter(selectedItemAdapter);

                if (SelectedFriendList.getInstance().getSize() == 0) {
                    horRecyclerView.setVisibility(View.GONE);
                } else if (horRecyclerView.getVisibility() == View.GONE) {
                    horRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void updateAdapter(String searchText) {
        getFilter().filter(searchText);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String searchString = charSequence.toString();
                if (searchString.trim().isEmpty())
                    friendList.setResultArray(orginalFriendList.getResultArray());
                else {
                    FriendList tempFriendList = new FriendList();
                    List<UserProfileProperties> userList = new ArrayList<>();
                    tempFriendList.setResultArray(userList);
                    for (UserProfileProperties userProfileProperties : orginalFriendList.getResultArray()) {
                        if (userProfileProperties.getName().toLowerCase().contains(searchString.toLowerCase()))
                            tempFriendList.getResultArray().add(userProfileProperties);
                    }
                    friendList.setResultArray(tempFriendList.getResultArray());
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = friendList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                friendList = (FriendList) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void updateAdapterForSelectAll(int selectType) {
        if (selectType == CODE_SELECT_ALL) {
            SelectedFriendList.getInstance().clearFriendList();
            SelectedFriendList.getInstance().setSelectedFriendList(orginalFriendList);
            if (horRecyclerView != null)
                horRecyclerView.setVisibility(View.VISIBLE);
            if (selectedItemAdapter != null)
                selectedItemAdapter.notifyDataSetChanged();
            notifyDataSetChanged();
        } else if (selectType == CODE_UNSELECT_ALL) {
            SelectedFriendList.getInstance().clearFriendList();
            if (horRecyclerView != null)
                horRecyclerView.setVisibility(View.GONE);
            if (selectedItemAdapter != null)
                selectedItemAdapter.notifyDataSetChanged();
            notifyDataSetChanged();
        }
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return friendList.getResultArray().size();
    }
}