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
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
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


public class GroupsListAdapter extends RecyclerView.Adapter<GroupsListAdapter.GroupsListHolder> {

    private Context mContext;
    private GroupRequestResult groupRequestResult;
    private BaseFragment.FragmentNavigation fragmentNavigation;

    public GroupsListAdapter(Context context,
                             BaseFragment.FragmentNavigation fragmentNavigation, GroupRequestResult groupRequestResult) {
        try {
            this.mContext = context;
            this.fragmentNavigation = fragmentNavigation;
            this.groupRequestResult = groupRequestResult;
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public GroupsListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.groups_horizontal_list_item, parent, false);

        return new GroupsListHolder(itemView);
    }

    public class GroupsListHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        GroupRequestResultResultArrayItem group;
        int position;
        ImageView imgGroupPic;
        TextView txtGroupName;

        public GroupsListHolder(View view) {
            super(view);

            try {
                imgGroupPic = (ImageView) view.findViewById(R.id.imgGroupPic);
                txtGroupName = (TextView) view.findViewById(R.id.txtGroupName);
                cardView = (CardView) view.findViewById(R.id.cardView);

                setListeners();
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }

        }

        private void setListeners() {

            try {
                //Card view
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String targetUid = group.getGroupid();
                        String toolbarTitle = group.getName();
                        fragmentNavigation.pushFragment(UserPostFragment.newInstance(PROFILE_POST_TYPE_GROUP, targetUid, toolbarTitle), ANIMATE_RIGHT_TO_LEFT);
                    }
                });
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }

        }

        public void setData(GroupRequestResultResultArrayItem group, int position) {

            try {
                this.group = group;
                this.position = position;
                setGroupName();
                setGroupPhoto();

            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        private void setGroupName(){
            if(group.getName() != null && !group.getName().isEmpty()){
                txtGroupName.setText(group.getName());
            }
        }

        private void setGroupPhoto(){
            if(group.getGroupPhotoUrl()!= null && !group.getGroupPhotoUrl().isEmpty()){
                imgGroupPic.setPadding(0, 0, 0, 0);
                Glide.with(mContext)
                        .load(group.getGroupPhotoUrl())
                        .apply(RequestOptions.centerCropTransform())
                        .into(imgGroupPic);
            }else {
                imgGroupPic.setPadding(30, 30, 30, 30);
                Glide.with(mContext)
                        .load(R.drawable.groups_icon_500)
                        .apply(RequestOptions.centerInsideTransform())
                        .into(imgGroupPic);
            }
        }
    }

    @Override
    public void onBindViewHolder(GroupsListHolder holder, final int position) {
        try {
            GroupRequestResultResultArrayItem group = groupRequestResult.getResultArray().get(position);
            holder.setData(group, position);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        int size = 0;
        try {
            size = groupRequestResult.getResultArray().size();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(mContext, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return size;
    }
}


