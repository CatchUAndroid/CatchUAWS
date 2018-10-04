package com.uren.catchu.GroupPackage.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.GroupPackage.Interfaces.ClickCallback;
import com.uren.catchu.GroupPackage.SelectFriendToGroupActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.SelectedFriendList;

import java.util.ArrayList;
import java.util.List;

import catchu.model.FriendList;
import catchu.model.UserProfileProperties;

public class SelectFriendAdapter extends RecyclerView.Adapter<SelectFriendAdapter.MyViewHolder> implements Filterable {

    View view;
    LayoutInflater layoutInflater;
    Context context;
    Activity activity;
    FriendList friendList;
    SelectedFriendList selectedFriendList;
    FriendList orginalFriendList;
    RecyclerView horRecyclerView;
    LinearLayoutManager linearLayoutManager;
    boolean horAdapterUpdateChk;
    SelectedItemAdapter selectedItemAdapter = null;
    GradientDrawable imageShape;

    public SelectFriendAdapter(Context context, FriendList friendList) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.friendList = friendList;
        this.orginalFriendList = friendList;
        activity = (Activity) context;
        selectedFriendList = SelectedFriendList.getInstance();
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
        return holder;
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
                    hideKeyBoard(itemView);
                    if (selectRadioBtn.isChecked()) {
                        selectRadioBtn.setChecked(false);
                        selectedFriendList.removeFriend(selectedFriend);
                        checkHorizontalAdapter();
                    } else {
                        selectRadioBtn.setChecked(true);
                        selectedFriendList.addFriend(selectedFriend);
                        checkHorizontalAdapter();
                    }
                }
            });

            selectRadioBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKeyBoard(itemView);
                    if (selectRadioBtn.isChecked()) {
                        if (!selectedFriendList.isUserInList(selectedFriend.getUserid())) {
                            selectedFriendList.addFriend(selectedFriend);
                            checkHorizontalAdapter();
                        }
                    } else {
                        if (selectedFriendList.isUserInList(selectedFriend.getUserid())) {
                            selectedFriendList.removeFriend(selectedFriend);
                            checkHorizontalAdapter();
                        }
                    }
                }
            });
        }

        public void setData(UserProfileProperties selectedFriend, int position) {
            this.nameTextView.setText(selectedFriend.getName());
            this.usernameTextView.setText(selectedFriend.getUsername());
            this.position = position;
            this.selectedFriend = selectedFriend;
            setUserName();
            setName();
            setProfilePicture();
            updateRadioButtonValue();
        }

        public void setUserName() {
            if (selectedFriend.getUsername() != null && !selectedFriend.getUsername().trim().isEmpty())
                this.usernameTextView.setText(selectedFriend.getUsername());
        }

        public void setName() {
            if (selectedFriend.getName() != null && !selectedFriend.getName().trim().isEmpty()) {
                if (selectedFriend.getName().length() > 30)
                    this.nameTextView.setText(selectedFriend.getName().trim().substring(0, 30) + "...");
                else
                    this.nameTextView.setText(selectedFriend.getName());
            }
        }

        public void setProfilePicture() {
            if (selectedFriend.getProfilePhotoUrl() != null && !selectedFriend.getProfilePhotoUrl().trim().isEmpty()) {
                shortUserNameTv.setVisibility(View.GONE);
                Glide.with(context)
                        .load(selectedFriend.getProfilePhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(profilePicImgView);
            } else {
                if (selectedFriend.getName() != null && !selectedFriend.getName().trim().isEmpty()) {
                    shortUserNameTv.setVisibility(View.VISIBLE);
                    shortUserNameTv.setText(getShortenUserName());
                    profilePicImgView.setImageDrawable(null);
                } else if (selectedFriend.getUsername() != null && !selectedFriend.getUsername().trim().isEmpty()) {
                    shortUserNameTv.setVisibility(View.VISIBLE);
                    shortUserNameTv.setText(selectedFriend.getUsername().substring(0, 1).toUpperCase());
                    profilePicImgView.setImageDrawable(null);
                } else {
                    shortUserNameTv.setVisibility(View.GONE);
                    Glide.with(context)
                            .load(context.getResources().getIdentifier("user_icon", "drawable", context.getPackageName()))
                            .apply(RequestOptions.circleCropTransform())
                            .into(profilePicImgView);
                }
            }
        }

        public String getShortenUserName() {
            String returnValue = "";
            String[] seperatedName = selectedFriend.getName().trim().split(" ");
            for (String word : seperatedName) {
                if (returnValue.length() < 5)
                    returnValue = returnValue + word.substring(0, 1).toUpperCase();
            }
            return returnValue;
        }

        public void updateRadioButtonValue() {
            if (selectedFriendList.isUserInList(selectedFriend.getUserid()))
                selectRadioBtn.setChecked(true);
            else
                selectRadioBtn.setChecked(false);
        }

        public void checkHorizontalAdapter() {
            selectedItemAdapter = new SelectedItemAdapter(context, new ClickCallback() {
                @Override
                public void onItemClick() {

                    SelectFriendAdapter.this.notifyDataSetChanged();

                    if (selectedFriendList.getSize() == 0)
                        horRecyclerView.setVisibility(View.GONE);
                }
            });

            if (horAdapterUpdateChk == false) {
                horRecyclerView.setVisibility(View.VISIBLE);
                horRecyclerView.setAdapter(selectedItemAdapter);
                horAdapterUpdateChk = true;
            } else {
                horRecyclerView.setAdapter(selectedItemAdapter);

                if (selectedFriendList.getSize() == 0) {
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
                    friendList = orginalFriendList;
                else {
                    FriendList tempFriendList = new FriendList();
                    List<UserProfileProperties> userList = new ArrayList<>();
                    tempFriendList.setResultArray(userList);
                    for (UserProfileProperties userProfileProperties : orginalFriendList.getResultArray()) {
                        if (userProfileProperties.getName().toLowerCase().contains(searchString.toLowerCase()))
                            tempFriendList.getResultArray().add(userProfileProperties);
                    }
                    friendList = tempFriendList;
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
        if (selectType == SelectFriendToGroupActivity.CODE_SELECT_ALL) {
            SelectedFriendList.getInstance().setSelectedFriendList(orginalFriendList);
            horRecyclerView.setVisibility(View.VISIBLE);
            notifyDataSetChanged();
        } else if (selectType == SelectFriendToGroupActivity.CODE_UNSELECT_ALL) {
            FriendList friendList = new FriendList();
            friendList.setResultArray(new ArrayList<UserProfileProperties>());
            SelectedFriendList.getInstance().setSelectedFriendList(friendList);
            horRecyclerView.setVisibility(View.GONE);
            notifyDataSetChanged();
        }
    }

    public void hideKeyBoard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return friendList.getResultArray().size();
    }
}