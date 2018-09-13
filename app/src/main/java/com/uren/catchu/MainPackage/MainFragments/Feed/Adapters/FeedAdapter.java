package com.uren.catchu.MainPackage.MainFragments.Feed.Adapters;


import android.content.Context;

import android.media.Image;
import android.support.v4.view.ViewPager;
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
import java.util.Random;

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
        ViewPager viewPager;
        ImageView imgFeedData;

        CardView cardView;
        int position;

        public MyViewHolder(View view) {
            super(view);

            cardView = (CardView) view.findViewById(R.id.card_view);
            imgProfilePic = (ImageView) view.findViewById(R.id.imgProfilePic);
            txtName = (TextView) view.findViewById(R.id.txtName);
            txtUserName = (TextView) view.findViewById(R.id.txtUserName);

            //imgFeedData = (ImageView) view.findViewById(R.id.imgFeedData);

            viewPager = (ViewPager) view.findViewById(R.id.viewPager);
            viewPager.setAdapter(new ViewPagerAdapter(context));


        }


        public void setData(int rowItem, int position) {

            //this.profileName.setText(String.valueOf(rowItem));
            this.position = position;

            String geciciUrl = "https://s3.amazonaws.com/catchumobilebucket/UserProfile/30.jpg";

            Glide.with(context)
                    .load(geciciUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgProfilePic);

            this.txtName.setText(getRandomUser());
            this.txtUserName.setText(getRandomUsername());


            /*
            String geciciFeedData ="https://i.hizliresim.com/Q2O8gV.jpg";

            Glide.with(context)
                    .load(geciciFeedData)
                    .apply(RequestOptions.centerInsideTransform())
                    .into(imgFeedData);
            */


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

    private String getRandomUrl() {

        String[] link = {
                "https://i.hizliresim.com/Q2O8gV.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/30.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/31.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/32.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/33.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/34.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/35.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/36.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/37.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/38.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/39.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/40.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/41.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/42.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/43.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/44.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/45.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/46.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/47.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/48.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/49.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/50.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/51.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/52.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/53.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/54.jpg",
                "https://s3.amazonaws.com/catchumobilebucket/UserProfile/55.jpg"
        };

        Random rand = new Random();
        int  n = rand.nextInt(26);

        return link[n];

    }

    private String getRandomUser() {

        String[] username = {
                "Nurullah Topaloglu",
                "Remzi Yıldırım",
                "Erkut Baş",
                "Uğur Göğebakan"
        };

        Random rand = new Random();
        int  n = rand.nextInt(4);

        return username[n];

    }

    private String getRandomUsername() {

        String[] username = {
                "@besiktas",
                "@fenerbahce",
                "@trabzonspor",
                "@galatasaray",
                "@sivasspor",
        };

        Random rand = new Random();
        int  n = rand.nextInt(4);

        return username[n];

    }





}


