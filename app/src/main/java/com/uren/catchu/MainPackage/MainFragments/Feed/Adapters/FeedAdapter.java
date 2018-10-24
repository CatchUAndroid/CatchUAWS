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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.ViewPagerUtils;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostHelper;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.FollowInfoListItem;
import com.uren.catchu.R;

import java.util.ArrayList;
import java.util.List;

import catchu.model.FollowInfoResultArrayItem;
import catchu.model.Post;

public class FeedAdapter extends RecyclerView.Adapter {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private Activity mActivity;
    private Context mContext;
    private List<Post> postList;
    private BaseFragment.FragmentNavigation fragmentNavigation;

    public FeedAdapter(Activity activity, Context context,
                       BaseFragment.FragmentNavigation fragmentNavigation) {
        this.mActivity = activity;
        this.mContext = context;
        this.fragmentNavigation = fragmentNavigation;
        this.postList = new ArrayList<Post>();

    }

    @Override
    public int getItemViewType(int position) {
        return postList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.feed_vert_list_item, parent, false);

            viewHolder = new MyViewHolder(itemView);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar_item, parent, false);

            viewHolder = new ProgressViewHolder(v);
        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if( holder instanceof MyViewHolder){
            Post post = postList.get(position);
            ((MyViewHolder) holder).setData(post, position);
        }else{
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProfilePic;
        TextView txtProfilePic;
        TextView txtName;
        TextView txtUserName;
        TextView txtDetail;
        ViewPager viewPager;
        CardView cardView;
        ImageView imgLike;
        private int position;
        boolean isPostLiked = false;
        Post post;
        TextView txtLikeCount;
        TextView txtCommentCount;
        LinearLayout layoutLike;
        LinearLayout layoutComment;
        LinearLayout profileMainLayout;
        LinearLayout locationDetailLayout;
        TextView txtLocationDistance;

        View mView;

        public MyViewHolder(View view) {
            super(view);

            mView = view;
            cardView = (CardView) view.findViewById(R.id.card_view);
            imgProfilePic = (ImageView) view.findViewById(R.id.imgProfilePic);
            txtProfilePic = (TextView) view.findViewById(R.id.txtProfilePic);
            txtName = (TextView) view.findViewById(R.id.txtName);
            txtUserName = (TextView) view.findViewById(R.id.txtUserName);
            txtDetail = (TextView) view.findViewById(R.id.txtDetail);
            viewPager = (ViewPager) view.findViewById(R.id.viewPager);
            imgLike = (ImageView) view.findViewById(R.id.imgLike);
            txtLikeCount = (TextView) view.findViewById(R.id.txtLikeCount);
            txtCommentCount = (TextView) view.findViewById(R.id.txtCommentCount);
            layoutLike = (LinearLayout) view.findViewById(R.id.layoutLike);
            layoutComment = (LinearLayout) view.findViewById(R.id.layoutComment);
            profileMainLayout = (LinearLayout) view.findViewById(R.id.profileMainLayout);
            locationDetailLayout = (LinearLayout) view.findViewById(R.id.locationDetailLayout);
            txtLocationDistance = (TextView) view.findViewById(R.id.txtLocationDistance);

            setListeners();

        }

        private void setListeners() {

            //imgLike
            imgLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    imgLike.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.image_click));

                    if (isPostLiked) {
                        isPostLiked = false;
                        setLikeIconUI(R.color.black, R.mipmap.icon_like, true);
                        PostHelper.LikeClicked.startProcess(mContext, post, isPostLiked);
                    } else {
                        isPostLiked = true;
                        setLikeIconUI(R.color.oceanBlue, R.mipmap.icon_like_filled, true);
                        PostHelper.LikeClicked.startProcess(mContext, post, isPostLiked);
                    }

                }
            });

            //Like layout
            layoutLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String toolbarTitle = mContext.getResources().getString(R.string.likes);
                    CommonUtils.showToast(mContext, toolbarTitle);
                    PostHelper.LikeListClicked.startProcess(mContext, fragmentNavigation, toolbarTitle, post.getPostid());
                }
            });

            //Comment layout
            layoutComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PostHelper.CommentListClicked.startProcess(mContext, fragmentNavigation, post.getPostid());
                }
            });

            //Profile layout
            profileMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FollowInfoResultArrayItem rowItem = new FollowInfoResultArrayItem();
                    rowItem.setUserid(post.getUser().getUserid());
                    rowItem.setProfilePhotoUrl(post.getUser().getProfilePhotoUrl());
                    rowItem.setName(post.getUser().getName());
                    FollowInfoListItem followInfoListItem = new FollowInfoListItem(rowItem);
                    PostHelper.ProfileClicked.startProcess(mContext, fragmentNavigation, followInfoListItem);
                }
            });
            //Location Detail Layout
            locationDetailLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PostHelper.LocatonDetailClicked.startProcess(mActivity, mContext, fragmentNavigation, post,txtProfilePic, imgProfilePic);
                }
            });

        }

        private void setLikeIconUI(int color, int icon, boolean isClientOperation) {
            imgLike.setColorFilter(ContextCompat.getColor(mContext, color), android.graphics.PorterDuff.Mode.SRC_IN);
            imgLike.setImageResource(icon);

            if (isClientOperation) {
                if (isPostLiked) {
                    post.setLikeCount(post.getLikeCount() + 1);
                } else {
                    post.setLikeCount(post.getLikeCount() - 1);
                }
            }
            txtLikeCount.setText(String.valueOf(post.getLikeCount()));

        }

        public void setData(Post post, int position) {

            this.position = position;
            this.post = post;
            this.isPostLiked = post.getIsLiked();

            //todo NT - profil fotosu düzenlenecek
            //profile picture
            UserDataUtil.setProfilePicture(mContext, post.getUser().getProfilePhotoUrl(),
                    post.getUser().getName(), txtProfilePic, imgProfilePic);
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
                setLikeIconUI(R.color.oceanBlue, R.mipmap.icon_like_filled, false);
            } else {
                setLikeIconUI(R.color.black, R.mipmap.icon_like, false);
            }
            //Location distance
            if (post.getDistance() != null) {
                txtLocationDistance.setText(PostHelper.Utils.calculateDistance(post.getDistance().doubleValue()));
            }


        }

        private void setViewPager(Post post) {

            viewPager.setAdapter(new ViewPagerAdapter(mActivity, mContext, post.getAttachments()));
            viewPager.setOffscreenPageLimit(post.getAttachments().size());
            ViewPagerUtils.setSliderDotsPanel(post.getAttachments().size(), mView, mContext);

        }

    }

    @Override
    public int getItemCount() {
        return (postList != null ? postList.size() : 0);
    }

    public void addAll(List<Post> addedPostList) {
        if(addedPostList != null){
            postList.addAll(addedPostList);
            notifyItemRangeInserted(postList.size(), postList.size() + addedPostList.size());
        }
    }

    public void addProgressLoading() {
        postList.add(null);
        notifyItemInserted(postList.size() - 1);
    }

    public void removeProgressLoading() {
        postList.remove(postList.size() - 1);
        notifyItemRemoved(postList.size());
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBarLoading);
        }
    }

}