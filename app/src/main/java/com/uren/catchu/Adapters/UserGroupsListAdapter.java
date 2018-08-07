package com.uren.catchu.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.ImageCache.ImageLoader;
import com.uren.catchu.R;

import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;

import static com.uren.catchu.Constants.StringConstants.displayRounded;
import static com.uren.catchu.Constants.StringConstants.groupsCacheDirectory;

public class UserGroupsListAdapter extends RecyclerView.Adapter<UserGroupsListAdapter.MyViewHolder>{

    public ImageLoader imageLoader;
    View view;
    LayoutInflater layoutInflater;
    Context context;

    private GroupRequestResult groupRequestResult;
    Activity activity;

    public UserGroupsListAdapter(Context context, GroupRequestResult groupRequestResult) {
        layoutInflater = LayoutInflater.from(context);
        this.groupRequestResult = groupRequestResult;
        this.context = context;
        activity = (Activity) context;
        imageLoader = new ImageLoader(context.getApplicationContext(), groupsCacheDirectory);
    }

    @Override
    public UserGroupsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.group_vert_list_item, parent, false);
        UserGroupsListAdapter.MyViewHolder holder = new UserGroupsListAdapter.MyViewHolder(view);
        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView groupnameTextView;
        ImageView groupPicImgView;
        GroupRequestResultResultArrayItem groupRequestResultResultArrayItem;
        int position = 0;

        public MyViewHolder(final View itemView) {
            super(itemView);

            groupPicImgView = (ImageView) view.findViewById(R.id.groupPicImgView);
            groupnameTextView = (TextView) view.findViewById(R.id.groupnameTextView);
        }

        public void setData(GroupRequestResultResultArrayItem groupRequestResultResultArrayItem, int position) {

            this.groupRequestResultResultArrayItem = groupRequestResultResultArrayItem;
            this.position = position;
            this.groupnameTextView.setText(groupRequestResultResultArrayItem.getName());
            imageLoader.DisplayImage(groupRequestResultResultArrayItem.getGroupPhotoUrl(),
                    groupPicImgView, displayRounded);
        }
    }

    @Override
    public void onBindViewHolder(UserGroupsListAdapter.MyViewHolder holder, int position) {

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