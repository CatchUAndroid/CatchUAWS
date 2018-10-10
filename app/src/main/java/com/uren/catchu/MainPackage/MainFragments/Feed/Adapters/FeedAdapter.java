package com.uren.catchu.MainPackage.MainFragments.Feed.Adapters;


import android.app.Activity;
import android.content.Context;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.GeneralUtils.ViewPagerUtils;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.PostLikeClickCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.ViewPagerClickCallBack;
import com.uren.catchu.R;

import java.util.List;

import catchu.model.Post;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MyViewHolder> {

    private Activity mActivity;
    private Context mContext;
    private List<Post> postList;
    private ViewPagerClickCallBack viewPagerClickCallBack;
    private PostLikeClickCallback postLikeClickCallback;

    public FeedAdapter(Activity activity, Context context, List<Post> postList, ViewPagerClickCallBack viewPagerClickCallBack, PostLikeClickCallback postLikeClickCallback) {
        this.mActivity = activity;
        this.mContext = context;
        this.postList = postList;
        this.viewPagerClickCallBack = viewPagerClickCallBack;
        this.postLikeClickCallback = postLikeClickCallback;
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
        TextView txtDetail;
        ViewPager viewPager;
        CardView cardView;
        ImageView imgLike;
        private int position;
        boolean isPostLiked = false;
        Post post;
        int likeColor;
        TextView txtLikeCount;
        TextView txtCommentCount;
        LinearLayout layoutLike;
        LinearLayout layoutComment;


        View mView;

        public MyViewHolder(View view) {
            super(view);

            mView = view;
            cardView = (CardView) view.findViewById(R.id.card_view);
            imgProfilePic = (ImageView) view.findViewById(R.id.imgProfilePic);
            txtName = (TextView) view.findViewById(R.id.txtName);
            txtUserName = (TextView) view.findViewById(R.id.txtUserName);
            txtDetail = (TextView) view.findViewById(R.id.txtDetail);
            viewPager = (ViewPager) view.findViewById(R.id.viewPager);
            imgLike = (ImageView) view.findViewById(R.id.imgLike);
            txtLikeCount = (TextView) view.findViewById(R.id.txtLikeCount);
            txtCommentCount = (TextView) view.findViewById(R.id.txtCommentCount);
            layoutLike = (LinearLayout) view.findViewById(R.id.layoutLike);
            layoutComment = (LinearLayout) view.findViewById(R.id.layoutComment);


            setListeners();

        }

        private void setListeners() {

            //imgLike
            imgLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    imgLike.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.image_click));

                    if (isPostLiked) {
                        likeColor = R.color.black;
                        imgLike.setColorFilter(ContextCompat.getColor(mContext, likeColor), android.graphics.PorterDuff.Mode.SRC_IN);
                        imgLike.setImageResource(R.mipmap.icon_like);
                        isPostLiked = false;
                        postLikeClickCallback.onPostLikeClicked(post, false);
                    } else {
                        likeColor = R.color.oceanBlue;
                        imgLike.setColorFilter(ContextCompat.getColor(mContext, likeColor), android.graphics.PorterDuff.Mode.SRC_IN);
                        imgLike.setImageResource(R.mipmap.icon_like_filled);
                        isPostLiked = true;
                        postLikeClickCallback.onPostLikeClicked(post, true);
                    }

                }
            });

            //Like layout
            layoutLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            //Comment layout
            layoutComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }

        public void setData(Post post, int position) {

            this.position = position;
            this.post = post;
            this.isPostLiked = post.getIsLiked();


            //todo NT - profil fotosu düzenlenecek
            //profile picture
            Glide.with(mContext)
                    .load("https://i.hizliresim.com/Q2O8gV.jpg")
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgProfilePic);
            //Name
            if (post.getUser().getName() != null && !post.getUser().getName().isEmpty()) {
                this.txtName.setText(post.getUser().getName());
            }
            //Username
            if (post.getUser().getUsername() != null && !post.getUser().getUsername().isEmpty()) {
                this.txtUserName.setText(post.getUser().getUsername());
            }
            //Text
            if (post.getMessage() != null && !post.getMessage().isEmpty()) {
                this.txtDetail.setText(post.getMessage());
                this.txtDetail.setVisibility(View.VISIBLE);
            } else {
                this.txtDetail.setVisibility(View.GONE);
            }
            //Attachments
            if (post.getAttachments().size() > 0) {
                setViewPager(post);
                viewPager.setVisibility(View.VISIBLE);
            } else {
                viewPager.setVisibility(View.GONE);
            }
            //Like
            if (post.getIsLiked()) {
                likeColor = R.color.oceanBlue;
                imgLike.setColorFilter(ContextCompat.getColor(mContext, likeColor), android.graphics.PorterDuff.Mode.SRC_IN);
                imgLike.setImageResource(R.mipmap.icon_like_filled);
            }
            //Like Count
            txtLikeCount.setText(String.valueOf(post.getLikeCount()));
            //Comment Count
            txtCommentCount.setText(String.valueOf(post.getCommentCount()));

        }

        private void setViewPager(Post post) {

            viewPager.setAdapter(new ViewPagerAdapter(mActivity, mContext, post.getAttachments(), viewPagerClickCallBack));
            viewPager.setOffscreenPageLimit(post.getAttachments().size());
            ViewPagerUtils.setSliderDotsPanel(post.getAttachments().size(), mView, mContext);

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