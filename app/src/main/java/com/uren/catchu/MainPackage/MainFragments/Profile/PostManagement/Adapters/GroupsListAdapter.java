package com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.Adapters;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.internal.service.Common;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.UserPostFragment;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.List;

import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;
import catchu.model.Post;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.PROFILE_POST_TYPE_CAUGHT;
import static com.uren.catchu.Constants.StringConstants.PROFILE_POST_TYPE_GROUP;


public class GroupsListAdapter extends RecyclerView.Adapter<GroupsListAdapter.MyViewHolder> {

    private Context mContext;
    private GroupRequestResult groupRequestResult;
    private BaseFragment.FragmentNavigation fragmentNavigation;

    public GroupsListAdapter(Context context,
                             BaseFragment.FragmentNavigation fragmentNavigation, GroupRequestResult groupRequestResult) {
        this.mContext = context;
        this.fragmentNavigation = fragmentNavigation;
        this.groupRequestResult = groupRequestResult;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.groups_horizontal_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        GroupRequestResultResultArrayItem group;
        int position;
        ImageView imgGroupPic;
        TextView txtGroupName;

        public MyViewHolder(View view) {
            super(view);

            imgGroupPic = (ImageView) view.findViewById(R.id.imgGroupPic);
            txtGroupName = (TextView) view.findViewById(R.id.txtGroupName);
            cardView = (CardView) view.findViewById(R.id.cardView);

            setListeners();

        }

        private void setListeners() {

            //Card view
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String targetUid = group.getGroupid();
                    String toolbarTitle = group.getName();
                    fragmentNavigation.pushFragment(UserPostFragment.newInstance(PROFILE_POST_TYPE_GROUP, targetUid, toolbarTitle), ANIMATE_RIGHT_TO_LEFT);
                }
            });

        }

        public void setData(GroupRequestResultResultArrayItem group, int position) {

            this.group = group;
            this.position = position;

            //Group picture
            if(group.getGroupPhotoUrl()!= null && !group.getGroupPhotoUrl().isEmpty()){
                Glide.with(mContext)
                        .load(group.getGroupPhotoUrl())
                        .apply(RequestOptions.centerCropTransform())
                        .into(imgGroupPic);
            }
            //Group name
            if(group.getName() != null && !group.getName().isEmpty()){
                txtGroupName.setText(group.getName());
            }


        }

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        GroupRequestResultResultArrayItem group = groupRequestResult.getResultArray().get(position);
        holder.setData(group, position);
    }

    @Override
    public int getItemCount() {
        return groupRequestResult.getResultArray().size();
    }

    public void updateAdapterWithPosition(int position) {

        notifyItemChanged(position);
    }


}


