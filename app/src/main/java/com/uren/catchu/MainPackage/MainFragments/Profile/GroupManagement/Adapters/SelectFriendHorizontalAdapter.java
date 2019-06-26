package com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Adapters;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
/*import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;*/
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Interfaces.ClickCallback;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.SelectedFriendList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.CHAR_AMPERSAND;

public class SelectFriendHorizontalAdapter extends RecyclerView.Adapter<SelectFriendHorizontalAdapter.SelectFriendHorizontalHolder> {

    View view;
    LayoutInflater layoutInflater;
    Context context;
    Activity activity;
    ClickCallback clickCallback;
    GradientDrawable imageShape;
    GradientDrawable deleteImgvShape;

    public SelectFriendHorizontalAdapter(Context context, ClickCallback clickCallback) {
            layoutInflater = LayoutInflater.from(context);
            this.context = context;
            this.clickCallback = clickCallback;
            activity = (Activity) context;
            imageShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                    0, GradientDrawable.OVAL, 50, 0);
            deleteImgvShape = ShapeUtil.getShape(context.getResources().getColor(R.color.White, null),
                    context.getResources().getColor(R.color.White, null), GradientDrawable.OVAL, 50, 0);
    }

    @NonNull
    @Override
    public SelectFriendHorizontalAdapter.SelectFriendHorizontalHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view = layoutInflater.inflate(R.layout.grid_list_item_small, viewGroup, false);
        final SelectFriendHorizontalAdapter.SelectFriendHorizontalHolder holder = new SelectFriendHorizontalAdapter.SelectFriendHorizontalHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SelectFriendHorizontalAdapter.SelectFriendHorizontalHolder myViewHolder, int position) {

        UserProfileProperties userProfileProperties = SelectedFriendList.getInstance().getSelectedFriendList().getResultArray().get(position);
        myViewHolder.setData(userProfileProperties, position);
    }

    class SelectFriendHorizontalHolder extends RecyclerView.ViewHolder {

        ImageView specialPictureImgView;
        ImageView deletePersonImgv;
        TextView specialNameTextView;
        TextView shortenTextView;
        UserProfileProperties selectedFriend;
        int position = 0;

        public SelectFriendHorizontalHolder(final View itemView) {
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
                SelectedFriendList.getInstance().removeFriend(selectedFriend);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
        }

        public void setData(UserProfileProperties selectedFriend, int position) {
                this.position = position;
                this.selectedFriend = selectedFriend;
                setProfileName();
                UserDataUtil.setProfilePicture(context, selectedFriend.getProfilePhotoUrl(),
                        selectedFriend.getName(), selectedFriend.getUsername(), shortenTextView, specialPictureImgView);
        }

        public void setProfileName(){
                if(selectedFriend.getName() != null && !selectedFriend.getName().isEmpty())
                    UserDataUtil.setName(selectedFriend.getName(), specialNameTextView);
                else if(selectedFriend.getUsername() != null && !selectedFriend.getUsername().isEmpty())
                    UserDataUtil.setName(CHAR_AMPERSAND + selectedFriend.getUsername(), specialNameTextView);
        }
    }

    @Override
    public int getItemCount() {
        int size = 0;
        try {
            size = SelectedFriendList.getInstance().getSize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }
}