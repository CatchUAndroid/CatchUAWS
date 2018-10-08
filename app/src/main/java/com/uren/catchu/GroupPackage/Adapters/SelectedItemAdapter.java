package com.uren.catchu.GroupPackage.Adapters;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.GroupPackage.Interfaces.ClickCallback;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.SelectedFriendList;

import catchu.model.UserProfileProperties;

public class SelectedItemAdapter extends RecyclerView.Adapter<SelectedItemAdapter.MyViewHolder> {

    View view;
    LayoutInflater layoutInflater;
    Context context;
    Activity activity;
    SelectedFriendList selectedFriendList;
    ClickCallback clickCallback;
    GradientDrawable imageShape;
    GradientDrawable deleteImgvShape;

    public SelectedItemAdapter(Context context, ClickCallback clickCallback) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.clickCallback = clickCallback;
        activity = (Activity) context;
        selectedFriendList = SelectedFriendList.getInstance();
        imageShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                0, GradientDrawable.OVAL, 50, 0);
        deleteImgvShape = ShapeUtil.getShape(context.getResources().getColor(R.color.White, null),
                context.getResources().getColor(R.color.White, null), GradientDrawable.OVAL, 50, 0);
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
        TextView shortenTextView;
        UserProfileProperties selectedFriend;

        int position = 0;

        public MyViewHolder(final View itemView) {
            super(itemView);

            specialPictureImgView = view.findViewById(R.id.specialPictureImgView);
            deletePersonImgv = view.findViewById(R.id.deletePersonImgv);
            specialNameTextView = view.findViewById(R.id.specialNameTextView);
            shortenTextView = view.findViewById(R.id.shortenTextView);
            specialPictureImgView.setBackground(imageShape);
            deletePersonImgv.setBackground(deleteImgvShape);

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

        private void removeItem(int position) {
            selectedFriendList.removeFriend(selectedFriend);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
        }

        public void setData(UserProfileProperties selectedFriend, int position) {
            this.position = position;
            this.selectedFriend = selectedFriend;
            setName();
            UserDataUtil.setProfilePicture(context, selectedFriend.getProfilePhotoUrl(),
                    selectedFriend.getName(), shortenTextView, specialPictureImgView);
        }

        public void setName() {
            if (selectedFriend.getName() != null && !selectedFriend.getName().trim().isEmpty()) {
                if (selectedFriend.getName().trim().length() > 30)
                    this.specialNameTextView.setText(selectedFriend.getName().trim().substring(0, 30) + "...");
                else
                    this.specialNameTextView.setText(selectedFriend.getName());
            }
        }
    }

    @Override
    public int getItemCount() {
        return selectedFriendList.getSize();
    }
}