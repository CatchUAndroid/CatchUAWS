package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uren.catchu.GeneralUtils.CircleTransform;
import com.uren.catchu.R;

import java.util.List;

import catchu.model.UserProfileProperties;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.MyViewHolder> {

    private Context context;
    private List<UserProfileProperties> followList;

    public FollowAdapter(Context context, List<UserProfileProperties> followList) {

        this.context = context;
        this.followList = followList;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView profileName;
        ImageView profileImage;
        Button btnFollowStatus;

        public MyViewHolder(View view) {
            super(view);
            profileName = (TextView) view.findViewById(R.id.profile_name);
            profileImage = (ImageView) view.findViewById(R.id.profile_image);
            btnFollowStatus = (Button)  view.findViewById(R.id.btnFollowStatus);

            btnFollowStatus.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            if(v == btnFollowStatus){

                btnFollowStatus.setBackgroundColor(Color.BLUE);
            }

        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.follow_vert_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UserProfileProperties userProfileProperties = followList.get(position);

        holder.profileName.setText(userProfileProperties.getName());

        Picasso.with(context)
                .load(userProfileProperties.getProfilePhotoUrl())
                .transform(new CircleTransform())
                .into(holder.profileImage);


    }

    @Override
    public int getItemCount() {
        return followList.size();
    }
}


