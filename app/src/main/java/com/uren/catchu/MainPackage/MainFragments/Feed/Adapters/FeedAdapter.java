package com.uren.catchu.MainPackage.MainFragments.Feed.Adapters;


import android.app.Activity;
import android.content.Context;

import android.os.AsyncTask;
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

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.PostLikeProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.ViewPagerUtils;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.CommentListClickCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.LikeListClickCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.PostLikeClickCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.ViewPagerClickCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostHelper;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.PersonListFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ListItemClickListener;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.List;

import catchu.model.BaseRequest;
import catchu.model.BaseResponse;
import catchu.model.Post;
import catchu.model.User;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MyViewHolder> {

    private Activity mActivity;
    private Context mContext;
    private List<Post> postList;
    BaseFragment.FragmentNavigation fragmentNavigation;

    public FeedAdapter(Activity activity, Context context, List<Post> postList,
                       BaseFragment.FragmentNavigation fragmentNavigation) {
        this.mActivity = activity;
        this.mContext = context;
        this.postList = postList;
        this.fragmentNavigation = fragmentNavigation;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_vert_list_item, parent, false);

        return new MyViewHolder(itemView);
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
                    String toolbarTitle = mContext.getResources().getString(R.string.likes) ;
                    CommonUtils.showToast(mContext, toolbarTitle);
                    PostHelper.LikeListClicked.startProcess(mContext, fragmentNavigation, toolbarTitle);
                }
            });

            //Comment layout
            layoutComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String toolbarTitle = mContext.getResources().getString(R.string.comments) ;
                    PostHelper.CommentListClicked.startProcess(mContext, fragmentNavigation, toolbarTitle);
                }
            });

            //Profile layout
            profileMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String selectedProfileId = post.getUser().getUserid();
                    PostHelper.ProfileClicked.startProcess(mContext, fragmentNavigation, selectedProfileId);
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


        }

        private void setViewPager(Post post) {

            viewPager.setAdapter(new ViewPagerAdapter(mActivity, mContext, post.getAttachments()));
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