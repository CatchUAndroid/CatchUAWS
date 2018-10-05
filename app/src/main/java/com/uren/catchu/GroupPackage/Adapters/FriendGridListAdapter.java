package com.uren.catchu.GroupPackage.Adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.GeneralUtils.ImageCache.ImageLoader;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.GroupPackage.SelectFriendToGroupActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.SelectedFriendList;

import java.util.ArrayList;

import catchu.model.FriendList;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.displayRounded;
import static com.uren.catchu.Constants.StringConstants.friendsCacheDirectory;

public class FriendGridListAdapter extends RecyclerView.Adapter<FriendGridListAdapter.MyViewHolder> {
    FriendList friendList;
    View view;
    LayoutInflater layoutInflater;
    Context context;
    TextView participantCntTv;
    Activity activity;
    GradientDrawable imageShape;
    GradientDrawable deleteShape;

    public FriendGridListAdapter(Context context, FriendList friendList) {
        layoutInflater = LayoutInflater.from(context);
        this.friendList = friendList;
        this.context = context;
        activity = (Activity) context;
        imageShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                0, GradientDrawable.OVAL, 50, 0);
        deleteShape = ShapeUtil.getShape(context.getResources().getColor(R.color.White, null),
                0, GradientDrawable.OVAL, 50, 0);
    }

    public Object getItem(int position) {
        return position;
    }

    @Override
    public FriendGridListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.special_grid_list_item, parent, false);
        FriendGridListAdapter.MyViewHolder holder = new FriendGridListAdapter.MyViewHolder(view);

        participantCntTv = activity.findViewById(R.id.participantSize);

        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView userNameSurname;
        TextView shortUserNameTv;
        UserProfileProperties selectedFriend;
        ImageView deletePersonImgv;
        ImageView specialProfileImgView;
        int position = 0;

        public MyViewHolder(View itemView) {
            super(itemView);

            specialProfileImgView = view.findViewById(R.id.specialPictureImgView);
            userNameSurname = view.findViewById(R.id.specialNameTextView);
            deletePersonImgv = view.findViewById(R.id.deletePersonImgv);
            shortUserNameTv = view.findViewById(R.id.shortUserNameTv);
            specialProfileImgView.setBackground(imageShape);
            deletePersonImgv.setBackground(deleteShape);

            deletePersonImgv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    removeItem(position);

                    if (getItemCount() == 0)
                        activity.finish();
                    else
                        participantCntTv.setText(Integer.toString(getItemCount()));
                }
            });
        }

        private void removeItem(int position) {
            friendList.getResultArray().remove(selectedFriend);
            SelectedFriendList.updateFriendList(friendList.getResultArray());
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
            SelectFriendToGroupActivity.adapter.notifyDataSetChanged();
        }

        public void setData(UserProfileProperties selectedFriend, int position) {
            this.position = position;
            this.selectedFriend = selectedFriend;
            setUserName();
            setProfilePicture();
        }

        private void setUserName() {
            String username = selectedFriend.getName();

            if (username.trim().length() > 16) {
                username = username.trim().substring(0, 13) + "...";
            }
            this.userNameSurname.setText(username);
        }

        public void setProfilePicture() {
            if (selectedFriend.getProfilePhotoUrl() != null && !selectedFriend.getProfilePhotoUrl().trim().isEmpty()) {
                shortUserNameTv.setVisibility(View.GONE);
                Glide.with(context)
                        .load(selectedFriend.getProfilePhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(specialProfileImgView);
            } else {
                if (selectedFriend.getName() != null && !selectedFriend.getName().trim().isEmpty()) {
                    shortUserNameTv.setVisibility(View.VISIBLE);
                    shortUserNameTv.setText(getShortenUserName());
                    specialProfileImgView.setImageDrawable(null);
                } else {
                    shortUserNameTv.setVisibility(View.GONE);
                    Glide.with(context)
                            .load(context.getResources().getIdentifier("user_icon", "drawable", context.getPackageName()))
                            .apply(RequestOptions.circleCropTransform())
                            .into(specialProfileImgView);
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
        return friendList.getResultArray().size();
    }
}