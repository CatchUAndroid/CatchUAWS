package com.uren.catchu.MainPackage.MainFragments.Feed.Adapters;


import android.content.Context;

import android.media.Image;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.R;

import java.util.List;

import butterknife.BindView;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MyViewHolder> {

    private Context context;
    private List<Integer> feedList;

    public FeedAdapter(Context context, List<Integer> feedList) {

        this.context = context;
        this.feedList = feedList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_vert_list_item, parent, false);

        return new MyViewHolder(itemView);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProfilePic;
        TextView txtName;
        TextView txtUserName;

        ImageView imgFeedData;

        CardView cardView;
        int position;

        public MyViewHolder(View view) {
            super(view);

            cardView = (CardView) view.findViewById(R.id.card_view);
            imgProfilePic = (ImageView) view.findViewById(R.id.imgProfilePic);
            txtName = (TextView) view.findViewById(R.id.txtName);
            txtUserName = (TextView) view.findViewById(R.id.txtUserName);

            imgFeedData = (ImageView) view.findViewById(R.id.imgFeedData);


        }


        public void setData(int rowItem, int position) {

            //this.profileName.setText(String.valueOf(rowItem));
            this.position = position;

            String geciciUrl = "https://s3.amazonaws.com/catchumobilebucket/GroupPictures/be8c169e-f61c-4066-b898-9547baf57b53.jpg";

            Glide.with(context)
                    .load(geciciUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgProfilePic);

            this.txtName.setText("Nurullah Topaloglu");
            this.txtUserName.setText("@nurullah");


            String geciciFeedData ="https://i.hizliresim.com/Q2O8gV.jpg";

            Glide.with(context)
                    .load(geciciFeedData)
                    .apply(RequestOptions.centerInsideTransform())
                    .into(imgFeedData);

            updateUIValue();

        }


        public void updateUIValue() {

        }

    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        int sayi = feedList.get(position);
        Log.i("sayi ", String.valueOf(sayi));
        holder.setData(sayi, position);

    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }


}


