package com.uren.catchu.MainPackage.MainFragments.Feed.Adapters;


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

    private Context context;
    private List<Post> postList;


    public FeedAdapter(Context context, List<Post> postList) {

        this.context = context;
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
        private boolean viewPagerOk = false;

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

        private void setViewPager() {

            viewPagerOk=true;

            List<String> imageList;
            imageList = new ArrayList<>();
            List<String> videoList;
            videoList = new ArrayList<>();
            imageList.add("https://i.hizliresim.com/Q2O8gV.jpg");
            imageList.add("https://i.hizliresim.com/RDzzka.jpg");
            imageList.add("https://i.hizliresim.com/Q2O8gV.jpg");
            imageList.add("https://i.hizliresim.com/RDzzka.jpg");

            viewPager.setAdapter(new ViewPagerAdapter(context, imageList, videoList));
            viewPager.setOffscreenPageLimit(imageList.size() + videoList.size());

            int totalDots = imageList.size() + videoList.size();
            setSliderDotsPanel(totalDots);

        }

        private void setSliderDotsPanel(int totalDots) {

            final int dotscount;
            final ImageView[] dots;
            LinearLayout sliderDotspanel;

            dotscount = totalDots;
            dots = new ImageView[dotscount];
            sliderDotspanel = (LinearLayout) view.findViewById(R.id.SliderDots);

            for (int i = 0; i < dotscount; i++) {

                dots[i] = new ImageView(context);
                dots[i].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.non_active_dot));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(8, 0, 8, 0);
                sliderDotspanel.addView(dots[i], params);

            }

            dots[0].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.active_dot));

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    for (int i = 0; i < dotscount; i++) {
                        dots[i].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.non_active_dot));
                    }

                    dots[position].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.active_dot));

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        }


        public void setData(Post post, int position) {

            //this.profileName.setText(String.valueOf(rowItem));
            this.position = position;

            Glide.with(context)
                    .load(post.getUser().getProfilePhotoUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgProfilePic);

            this.txtName.setText(post.getUser().getUsername());
            this.txtUserName.setText(post.getUser().getUsername());

            if (!viewPagerOk)
                setViewPager();


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