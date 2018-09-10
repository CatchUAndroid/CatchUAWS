package com.uren.catchu.GroupPackage.Adapters;

import android.app.Activity;
import android.content.Context;
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

import com.uren.catchu.GeneralUtils.ImageCache.ImageLoader;
import com.uren.catchu.GroupPackage.Interfaces.ClickCallback;
import com.uren.catchu.GroupPackage.SelectFriendToGroupActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.SelectedFriendList;

import java.util.ArrayList;
import java.util.List;

import catchu.model.FriendList;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.displayRounded;
import static com.uren.catchu.Constants.StringConstants.friendsCacheDirectory;

public class SelectFriendAdapter extends RecyclerView.Adapter<SelectFriendAdapter.MyViewHolder> implements Filterable {

    View view;
    LayoutInflater layoutInflater;
    ImageLoader imageLoader;
    Context context;
    Activity activity;
    FriendList friendList;
    SelectedFriendList selectedFriendList;
    FriendList orginalFriendList;
    RecyclerView horRecyclerView;
    LinearLayoutManager linearLayoutManager;
    boolean horAdapterUpdateChk;
    SelectedItemAdapter selectedItemAdapter = null;

    public SelectFriendAdapter(Context context, FriendList friendList) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.friendList = friendList;
        this.orginalFriendList = friendList;
        activity = (Activity) context;
        imageLoader = new ImageLoader(context.getApplicationContext(), friendsCacheDirectory);
        selectedFriendList = SelectedFriendList.getInstance();
        horAdapterUpdateChk = false;
    }

    @NonNull
    @Override
    public SelectFriendAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view = layoutInflater.inflate(R.layout.friend_vert_list_item, viewGroup, false);
        final SelectFriendAdapter.MyViewHolder holder = new SelectFriendAdapter.MyViewHolder(view);
        horRecyclerView = (RecyclerView) activity.findViewById(R.id.horRecyclerView);
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
        ImageView profilePicImgView;
        RadioButton selectRadioBtn;
        LinearLayout specialListLinearLayout;
        UserProfileProperties selectedFriend;
        int position = 0;

        public MyViewHolder(final View itemView) {
            super(itemView);

            profilePicImgView = (ImageView) view.findViewById(R.id.profilePicImgView);
            nameTextView = (TextView) view.findViewById(R.id.nameTextView);
            usernameTextView = (TextView) view.findViewById(R.id.usernameTextView);
            selectRadioBtn = (RadioButton) view.findViewById(R.id.selectRadioBtn);
            specialListLinearLayout = (LinearLayout) view.findViewById(R.id.specialListLinearLayout);

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
            imageLoader.DisplayImage(selectedFriend.getProfilePhotoUrl(), profilePicImgView, displayRounded);
            updateRadioButtonValue();
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
                // TODO: 31.08.2018 - Burada notifyDataSetChanged yemedi, nedenini bir ara inceleyelim???
                horRecyclerView.setAdapter(selectedItemAdapter);
                //selectedItemAdapter.notifyDataSetChanged();

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
                if (searchString.isEmpty())
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
                filterResults.values = (FriendList) friendList;
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