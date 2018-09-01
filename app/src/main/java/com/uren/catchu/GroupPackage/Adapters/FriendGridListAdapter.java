package com.uren.catchu.GroupPackage.Adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.ImageCache.ImageLoader;
import com.uren.catchu.GroupPackage.SelectFriendToGroupActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.SelectedFriendList;

import java.util.ArrayList;

import catchu.model.FriendList;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.displayRounded;
import static com.uren.catchu.Constants.StringConstants.friendsCacheDirectory;

public class FriendGridListAdapter extends RecyclerView.Adapter<FriendGridListAdapter.MyViewHolder>{

    private FriendList friendList;
    public ImageLoader imageLoader;
    View view;

    LayoutInflater layoutInflater;

    Context context;
    TextView participantCntTv;
    Activity activity;

    public FriendGridListAdapter(Context context, FriendList friendList) {
        layoutInflater = LayoutInflater.from(context);
        this.friendList = friendList;
        this.context = context;
        activity = (Activity) context;
        imageLoader=new ImageLoader(context.getApplicationContext(), friendsCacheDirectory);
    }

    public Object getItem(int position) {
        return position;
    }

    @Override
    public FriendGridListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.special_grid_list_item, parent, false);
        FriendGridListAdapter.MyViewHolder holder = new FriendGridListAdapter.MyViewHolder(view);

        participantCntTv = (TextView) activity.findViewById(R.id.participantSize);

        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView userNameSurname;
        UserProfileProperties selectedFriend;
        ImageView deletePersonImgv;
        ImageView specialProfileImgView;
        int position = 0;

        public MyViewHolder(View itemView) {
            super(itemView);

            specialProfileImgView = (ImageView) view.findViewById(R.id.specialPictureImgView);
            userNameSurname = (TextView) view.findViewById(R.id.specialNameTextView);
            deletePersonImgv = (ImageView) view.findViewById(R.id.deletePersonImgv);

            deletePersonImgv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    removeItem(position);

                    if(getItemCount() == 0)
                        activity.finish();
                    else
                        participantCntTv.setText(Integer.toString(getItemCount()));
                }
            });
        }

        private void removeItem(int position){
            friendList.getResultArray().remove(selectedFriend);
            SelectedFriendList.updateFriendList(friendList.getResultArray());
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
            SelectFriendToGroupActivity.adapter.notifyDataSetChanged();
        }

        public void setData(UserProfileProperties selectedFriend, int position) {

            String username = selectedFriend.getName();

            if(username.trim().length() > 16){
                username = username.trim().substring(0, 13) + "...";
            }

            this.userNameSurname.setText(username);
            this.position = position;
            this.selectedFriend = selectedFriend;
            imageLoader.DisplayImage(selectedFriend.getProfilePhotoUrl(), specialProfileImgView, displayRounded);
        }
    }

    @Override
    public void onBindViewHolder(FriendGridListAdapter.MyViewHolder holder, int position) {
        UserProfileProperties selectedFriend = friendList.getResultArray().get(position);
        holder.setData(selectedFriend, position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return  friendList.getResultArray().size();
    }
}