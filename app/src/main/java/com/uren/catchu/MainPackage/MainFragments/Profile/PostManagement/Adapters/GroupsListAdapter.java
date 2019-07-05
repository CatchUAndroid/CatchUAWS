package com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.Adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.UserPostFragment;
import com.uren.catchu.R;

import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.PROFILE_POST_TYPE_GROUP;


public class GroupsListAdapter extends RecyclerView.Adapter<GroupsListAdapter.GroupsListHolder> {

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

            imgGroupPic = view.findViewById(R.id.imgGroupPic);
            txtGroupName = view.findViewById(R.id.txtGroupName);
            cardView = view.findViewById(R.id.cardView);

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
            setGroupName();
            setGroupPhoto();
        }

        private void setGroupName() {
            if (group.getName() != null && !group.getName().isEmpty()) {
                txtGroupName.setText(group.getName());
            }
        }

        private void setGroupPhoto() {
            if (group.getGroupPhotoUrl() != null && !group.getGroupPhotoUrl().trim().isEmpty()) {
                imgGroupPic.setPadding(0, 0, 0, 0);
                Glide.with(mContext)
                        .load(group.getGroupPhotoUrl())
                        .apply(RequestOptions.centerCropTransform())
                        .into(imgGroupPic);
            } else {
                imgGroupPic.setPadding(30, 30, 30, 30);
                Glide.with(mContext)
                        .load(R.drawable.groups_icon_500)
                        .apply(RequestOptions.centerInsideTransform())
                        .into(imgGroupPic);
            }
            int groupColor = CommonUtils.getDarkRandomColor(mContext);
            imgGroupPic.setBackground(ShapeUtil.getShape(mContext.getResources().getColor(groupColor, null),
                    0, GradientDrawable.RECTANGLE, 15, 0));
            txtGroupName.setTextColor(mContext.getResources().getColor(groupColor, null));
        }
    }

    @Override
    public void onBindViewHolder(GroupsListHolder holder, final int position) {
        GroupRequestResultResultArrayItem group = groupRequestResult.getResultArray().get(position);
        holder.setData(group, position);
    }

    @Override
    public int getItemCount() {
        if (groupRequestResult != null && groupRequestResult.getResultArray() != null)
            return groupRequestResult.getResultArray().size();
        else
            return 0;
    }
}


