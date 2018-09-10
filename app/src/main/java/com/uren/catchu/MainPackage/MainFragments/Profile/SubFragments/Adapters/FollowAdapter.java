package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
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

import catchu.model.FollowInfo;
import catchu.model.FollowInfoResultArrayItem;
import catchu.model.UserProfileProperties;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.MyViewHolder> {

    private Context context;
    private List<FollowInfoResultArrayItem> followList;

    public FollowAdapter(Context context, List<FollowInfoResultArrayItem> followList) {

        this.context = context;
        this.followList = followList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.follow_vert_list_item, parent, false);

        return new MyViewHolder(itemView);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView profileName;
        ImageView profileImage;
        Button btnFollowStatus;
        CardView cardView;
        UserProfileProperties u;

        int position;

        public MyViewHolder(View view) {
            super(view);
            profileName = (TextView) view.findViewById(R.id.profile_name);
            profileImage = (ImageView) view.findViewById(R.id.profile_image);
            btnFollowStatus = (Button) view.findViewById(R.id.btnFollowStatus);
            cardView = (CardView) view.findViewById(R.id.card_view);

            btnFollowStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*
                    btnFollowStatus.setText(R.string.takip_ediliyor);
                    u.setName("X");
                    followList.get(position).setName("X");
                    notifyItemChanged(position, u.getName());
                    */

                }
            });

        }

        public void setData(FollowInfoResultArrayItem selectedFriend, int position) {

            /*this.profileName.setText(selectedFriend);
            this.u= selectedFriend;
            this.position=position;

            Picasso.with(context)
                    .load(u.getProfilePhotoUrl())
                    .transform(new CircleTransform())
                    .into(profileImage);

            updateUIValue();
            */
        }



        public void updateUIValue(){


                if(u.getName().equals("X")){
                    btnFollowStatus.setText(R.string.takip_ediliyor);
                }else{
                    btnFollowStatus.setText(R.string.takip_et);
                }

        }

    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {






        FollowInfoResultArrayItem followInfoResultArrayItem = followList.get(position);
        holder.setData(followInfoResultArrayItem, position);









    }

    @Override
    public int getItemCount() {
        return followList.size();
    }
}


