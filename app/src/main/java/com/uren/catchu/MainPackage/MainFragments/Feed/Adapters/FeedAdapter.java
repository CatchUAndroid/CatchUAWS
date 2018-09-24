package com.uren.catchu.MainPackage.MainFragments.Feed.Adapters;


import android.app.Activity;
import android.content.Context;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.GeneralUtils.ViewPagerUtils;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import catchu.model.Media;
import catchu.model.Post;

import static com.uren.catchu.Constants.StringConstants.IMAGE_TYPE;
import static com.uren.catchu.Constants.StringConstants.VIDEO_TYPE;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MyViewHolder> {

    private Activity mActivity;
    private Context mContext;
    private List<Post> postList;

    public FeedAdapter(Activity activity, Context context, List<Post> postList) {
        this.mActivity=activity;
        this.mContext = context;
        this.postList = postList;
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
        CardView cardView;
        private int position;

        View view;

        public MyViewHolder(View view) {
            super(view);

            cardView = (CardView) view.findViewById(R.id.card_view);
            imgProfilePic = (ImageView) view.findViewById(R.id.imgProfilePic);
            txtName = (TextView) view.findViewById(R.id.txtName);
            txtUserName = (TextView) view.findViewById(R.id.txtUserName);
            viewPager = (ViewPager) view.findViewById(R.id.viewPager);

            this.view = view;

        }




        public void setData(Post post, int position) {

            this.position = position;

            Glide.with(mContext)
                    .load("https://i.hizliresim.com/Q2O8gV.jpg")
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgProfilePic);
            this.txtName.setText("Name");
            this.txtUserName.setText("@name");

            setViewPager(post);

        }

        private void setViewPager(Post post) {

            viewPager.setAdapter(new ViewPagerAdapter(mActivity, mContext, post.getAttachments()));
            viewPager.setOffscreenPageLimit(post.getAttachments().size());
            ViewPagerUtils.setSliderDotsPanel(post.getAttachments().size(), view, mContext);

        }

    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Post post = postList.get(position);
        holder.setData(post, position);

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


}