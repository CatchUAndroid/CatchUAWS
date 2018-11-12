package com.uren.catchu.MainPackage.MainFragments.Profile.UserShareManagement.Adapters;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.GeneralUtils.ApiModelsProcess.UserGroupsProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.SelectedGroupList;

import java.util.ArrayList;
import java.util.List;

import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;

import static com.uren.catchu.Constants.NumericConstants.GROUP_NAME_MAX_LENGTH;
import static com.uren.catchu.Constants.StringConstants.GROUP_OP_CHOOSE_TYPE;
import static com.uren.catchu.Constants.StringConstants.GROUP_OP_VIEW_TYPE;

public class UserGroupsPostAdapter extends RecyclerView.Adapter<UserGroupsPostAdapter.MyViewHolder> {

    View view;
    LayoutInflater layoutInflater;
    Context context;
    GroupRequestResult groupRequestResult;
    ItemClickListener itemClickListener;

    public UserGroupsPostAdapter(Context context, GroupRequestResult groupRequestResult, ItemClickListener itemClickListener) {
        layoutInflater = LayoutInflater.from(context);
        this.groupRequestResult = groupRequestResult;
        this.context = context;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public UserGroupsPostAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.custom_media_item_view, parent, false);
        UserGroupsPostAdapter.MyViewHolder holder = new UserGroupsPostAdapter.MyViewHolder(view);
        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImgv;
        TextView itemNameTv;
        GroupRequestResultResultArrayItem groupRequestResultResultArrayItem;
        int position = 0;

        public MyViewHolder(final View itemView) {
            super(itemView);

            itemImgv = view.findViewById(R.id.itemImgv);
            itemNameTv = view.findViewById(R.id.itemNameTv);

            itemImgv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onClick(groupRequestResultResultArrayItem, position);
                }
            });
        }

        public void setData(GroupRequestResultResultArrayItem groupRequestResultResultArrayItem, int position) {
            this.groupRequestResultResultArrayItem = groupRequestResultResultArrayItem;
            this.position = position;
            setGroupName();
            setGroupPhoto();
        }

        public void setGroupName() {
            if (groupRequestResultResultArrayItem.getName() != null && !groupRequestResultResultArrayItem.getName().trim().isEmpty()) {
                if (groupRequestResultResultArrayItem.getName().trim().length() > GROUP_NAME_MAX_LENGTH)
                    this.itemNameTv.setText(groupRequestResultResultArrayItem.getName().trim().substring(0, GROUP_NAME_MAX_LENGTH) + "...");
                else
                    this.itemNameTv.setText(groupRequestResultResultArrayItem.getName());
            }
        }

        public void setGroupPhoto() {
            if (groupRequestResultResultArrayItem.getGroupPhotoUrl() != null && !groupRequestResultResultArrayItem.getGroupPhotoUrl().trim().isEmpty()) {
                Glide.with(context)
                        .load(groupRequestResultResultArrayItem.getGroupPhotoUrl())
                        .apply(RequestOptions.centerCropTransform())
                        .into(itemImgv);
            } else
                Glide.with(context)
                        .load(context.getResources().getIdentifier("groups_icon_500", "drawable", context.getPackageName()))
                        .apply(RequestOptions.circleCropTransform())
                        .into(itemImgv);
        }
    }

    @Override
    public void onBindViewHolder(UserGroupsPostAdapter.MyViewHolder holder, int position) {

        GroupRequestResultResultArrayItem groupRequestResultResultArrayItem = groupRequestResult.getResultArray().get(position);
        holder.setData(groupRequestResultResultArrayItem, position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return groupRequestResult.getResultArray().size();
    }
}
