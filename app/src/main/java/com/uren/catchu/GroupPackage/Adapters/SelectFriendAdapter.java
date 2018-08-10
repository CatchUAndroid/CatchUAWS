package com.uren.catchu.GroupPackage.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.uren.catchu.GeneralUtils.ImageCache.ImageLoader;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.SelectedFriendList;
import com.uren.catchu.Singleton.UserFriends;

import java.util.ArrayList;
import java.util.List;

import catchu.model.FriendList;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.displayRounded;
import static com.uren.catchu.Constants.StringConstants.friendsCacheDirectory;

public class SelectFriendAdapter extends RecyclerView.Adapter<SelectFriendAdapter.MyViewHolder>{

    View view;
    LayoutInflater layoutInflater;
    public ImageLoader imageLoader;

    Context context;
    Activity activity;
    FriendList friendList;
    SelectedFriendList selectedFriendList;

    ViewPager viewPagerHorizontal;
    RecyclerView horRecyclerView;

    public SelectFriendAdapter(Context context, FriendList friendList) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.friendList = friendList;
        activity = (Activity) context;
        imageLoader=new ImageLoader(context.getApplicationContext(), friendsCacheDirectory);
        selectedFriendList = SelectedFriendList.getInstance();
    }

    @NonNull
    @Override
    public SelectFriendAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view = layoutInflater.inflate(R.layout.friend_vert_list_item, viewGroup, false);
        final SelectFriendAdapter.MyViewHolder holder = new SelectFriendAdapter.MyViewHolder(view);

        viewPagerHorizontal = (ViewPager) activity.findViewById(R.id.viewpagerHorizontal);
        horRecyclerView = (RecyclerView) activity.findViewById(R.id.horRecyclerView);

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
        CheckBox selectCheckBox;
        LinearLayout specialListLinearLayout;
        UserProfileProperties selectedFriend;

        int position = 0;

        public MyViewHolder(final View itemView) {
            super(itemView);

            profilePicImgView = (ImageView) view.findViewById(R.id.profilePicImgView);
            nameTextView = (TextView) view.findViewById(R.id.nameTextView);
            usernameTextView = (TextView) view.findViewById(R.id.usernameTextView);
            selectCheckBox = (CheckBox) view.findViewById(R.id.selectCheckBox);
            specialListLinearLayout = (LinearLayout) view.findViewById(R.id.specialListLinearLayout);

            specialListLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    hideKeyBoard(itemView);
                    if(selectCheckBox.isChecked()) {
                        selectCheckBox.setChecked(false);
                        selectedFriendList.removeFriend(selectedFriend);
                    }
                    else {
                        selectCheckBox.setChecked(true);
                        selectedFriendList.addFriend(selectedFriend);
                    }
                }
            });

            selectCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKeyBoard(itemView);
                    if(selectCheckBox.isChecked()) {
                        selectedFriendList.addFriend(selectedFriend);
                    }
                    else {
                        selectedFriendList.removeFriend(selectedFriend);
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
            updateSelectedPerson();
        }

        public void updateSelectedPerson(){

            if(selectedFriendList.getSelectedFriendList().getResultArray() == null)
                return;

            for(int index = 0; index < selectedFriendList.getSize(); index++){
               if(selectedFriend.getUserid().equals(selectedFriendList.getFriend(index).getUserid()))
                   selectCheckBox.setChecked(true);
            }
        }
    }

    public void hideKeyBoard(View view){
        InputMethodManager inputMethodManager =(InputMethodManager)context.getSystemService(Activity.INPUT_METHOD_SERVICE);
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