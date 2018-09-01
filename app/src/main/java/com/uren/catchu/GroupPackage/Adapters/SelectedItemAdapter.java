package com.uren.catchu.GroupPackage.Adapters;


import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.ImageCache.ImageLoader;
import com.uren.catchu.GroupPackage.Interfaces.ClickCallback;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.SelectedFriendList;

import catchu.model.FriendList;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.NumericConstants.CODE_REMOVE_VALUE;
import static com.uren.catchu.Constants.StringConstants.displayRounded;
import static com.uren.catchu.Constants.StringConstants.friendsCacheDirectory;

public class SelectedItemAdapter extends RecyclerView.Adapter<SelectedItemAdapter.MyViewHolder> {

    View view;
    LayoutInflater layoutInflater;
    public ImageLoader imageLoader;

    Context context;
    Activity activity;
    SelectedFriendList selectedFriendList;
    ClickCallback clickCallback;

    public SelectedItemAdapter(Context context, ClickCallback clickCallback) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.clickCallback = clickCallback;
        activity = (Activity) context;
        imageLoader = new ImageLoader(context.getApplicationContext(), friendsCacheDirectory);
        selectedFriendList = SelectedFriendList.getInstance();
    }

    @NonNull
    @Override
    public SelectedItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view = layoutInflater.inflate(R.layout.grid_list_item_small, viewGroup, false);
        final SelectedItemAdapter.MyViewHolder holder = new SelectedItemAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedItemAdapter.MyViewHolder myViewHolder, int position) {

        UserProfileProperties userProfileProperties = selectedFriendList.getSelectedFriendList().getResultArray().get(position);
        myViewHolder.setData(userProfileProperties, position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView specialPictureImgView;
        ImageView deletePersonImgv;
        TextView specialNameTextView;
        UserProfileProperties selectedFriend;

        int position = 0;

        public MyViewHolder(final View itemView) {
            super(itemView);

            specialPictureImgView = (ImageView) view.findViewById(R.id.specialPictureImgView);
            deletePersonImgv = (ImageView) view.findViewById(R.id.deletePersonImgv);
            specialNameTextView = (TextView) view.findViewById(R.id.specialNameTextView);

            specialPictureImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeItem(position);
                    clickCallback.onItemClick();
                }
            });

            deletePersonImgv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeItem(position);
                    clickCallback.onItemClick();
                }
            });
        }

        private void removeItem(int position){
            selectedFriendList.removeFriend(selectedFriend);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
        }

        public void setData(UserProfileProperties selectedFriend, int position) {
            this.specialNameTextView.setText(selectedFriend.getName());
            this.position = position;
            this.selectedFriend = selectedFriend;
            imageLoader.DisplayImage(selectedFriend.getProfilePhotoUrl(), specialPictureImgView, displayRounded);
        }
    }

    @Override
    public int getItemCount() {
        return selectedFriendList.getSize();
    }
}