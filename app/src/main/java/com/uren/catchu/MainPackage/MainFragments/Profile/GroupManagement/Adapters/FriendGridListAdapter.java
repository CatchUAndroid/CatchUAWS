package com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Adapters;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.SelectedFriendList;

import catchu.model.FriendList;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.CHAR_AMPERSAND;

public class FriendGridListAdapter extends RecyclerView.Adapter<FriendGridListAdapter.FriendGridListHolder> {
    FriendList friendList;
    View view;
    LayoutInflater layoutInflater;
    Context context;
    GradientDrawable imageShape;
    GradientDrawable deleteShape;
    ReturnCallback returnCallback;

    public FriendGridListAdapter(Context context, FriendList friendList, ReturnCallback returnCallback) {
        layoutInflater = LayoutInflater.from(context);
        this.friendList = friendList;
        this.context = context;
        this.returnCallback = returnCallback;
        imageShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                0, GradientDrawable.OVAL, 50, 0);
        deleteShape = ShapeUtil.getShape(context.getResources().getColor(R.color.White, null),
                0, GradientDrawable.OVAL, 50, 0);
    }

    public Object getItem(int position) {
        return position;
    }

    @Override
    public FriendGridListAdapter.FriendGridListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.special_grid_list_item, parent, false);
        FriendGridListAdapter.FriendGridListHolder holder = new FriendGridListAdapter.FriendGridListHolder(view);
        return holder;
    }

    class FriendGridListHolder extends RecyclerView.ViewHolder {

        TextView userNameSurname;
        TextView shortUserNameTv;
        UserProfileProperties selectedFriend;
        ImageView deletePersonImgv;
        ImageView specialProfileImgView;
        int position = 0;

        public FriendGridListHolder(View itemView) {
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
                    returnCallback.onReturn(getItemCount());
                }
            });
        }

        private void removeItem(int position) {
            friendList.getResultArray().remove(selectedFriend);
            SelectedFriendList.updateFriendList(friendList.getResultArray());
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
        }

        public void setData(UserProfileProperties selectedFriend, int position) {
            this.position = position;
            this.selectedFriend = selectedFriend;
            setProfileName();
            UserDataUtil.setProfilePicture(context, selectedFriend.getProfilePhotoUrl(),
                    selectedFriend.getName(), selectedFriend.getUsername(), shortUserNameTv, specialProfileImgView);
        }

        public void setProfileName() {
            if (selectedFriend.getName() != null && !selectedFriend.getName().isEmpty())
                UserDataUtil.setName(selectedFriend.getName(), userNameSurname);
            else if (selectedFriend.getUsername() != null && !selectedFriend.getUsername().isEmpty())
                UserDataUtil.setName(CHAR_AMPERSAND + selectedFriend.getUsername(), userNameSurname);
        }
    }

    @Override
    public void onBindViewHolder(FriendGridListAdapter.FriendGridListHolder holder, int position) {
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