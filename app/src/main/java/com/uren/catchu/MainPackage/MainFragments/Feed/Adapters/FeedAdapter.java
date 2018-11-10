package com.uren.catchu.MainPackage.MainFragments.Feed.Adapters;


import android.app.Activity;
import android.content.Context;

import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.ViewPagerUtils;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.CommentAddCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.PostLikeClickCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.FeedContextMenu;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.FeedContextMenuManager;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostDiffCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostHelper;

import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.SinglePost;

import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.UserInfoListItem;
import com.uren.catchu.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import catchu.model.Post;

public class FeedAdapter extends RecyclerView.Adapter {

    public static final int VIEW_ITEM = 1;
    public static final int VIEW_PROG = 0;

    private Activity mActivity;
    private Context mContext;
    private List<Post> postList;
    private BaseFragment.FragmentNavigation fragmentNavigation;
    private HashMap<String, Integer> postPositionHashMap;


    public FeedAdapter(Activity activity, Context context,
                       BaseFragment.FragmentNavigation fragmentNavigation) {
        this.mActivity = activity;
        this.mContext = context;
        this.fragmentNavigation = fragmentNavigation;
        this.postList = new ArrayList<Post>();
        this.postPositionHashMap = new HashMap<String, Integer>();
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

        if (holder instanceof MyViewHolder) {
            Post post = postList.get(position);
            ((MyViewHolder) holder).setData(post, position);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProfilePic;
        TextView txtProfilePic;
        //TextView txtName;
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
        int likeCount;
        int commentCount;
        ImageButton imgBtnLike, imgBtnComment, imgBtnMore, imgBtnLocationDetail;
        LinearLayout profileMainLayout;
        TextView txtLocationDistance;
        TextView txtCreateAt;

        View mView;

        public MyViewHolder(View view) {
            super(view);

            mView = view;
            cardView = (CardView) view.findViewById(R.id.card_view);
            imgProfilePic = (ImageView) view.findViewById(R.id.imgProfilePic);
            txtProfilePic = (TextView) view.findViewById(R.id.txtProfilePic);
            //txtName = (TextView) view.findViewById(R.id.txtName);
            txtUserName = (TextView) view.findViewById(R.id.txtUserName);
            txtDetail = (TextView) view.findViewById(R.id.txtDetail);
            viewPager = (ViewPager) view.findViewById(R.id.viewPager);
            imgLike = (ImageView) view.findViewById(R.id.imgLike);
            txtLikeCount = (TextView) view.findViewById(R.id.txtLikeCount);
            txtCommentCount = (TextView) view.findViewById(R.id.txtCommentCount);
            profileMainLayout = (LinearLayout) view.findViewById(R.id.profileMainLayout);
            txtLocationDistance = (TextView) view.findViewById(R.id.txtLocationDistance);
            imgBtnLike = (ImageButton) view.findViewById(R.id.imgBtnLike);
            imgBtnComment = (ImageButton) view.findViewById(R.id.imgBtnComment);
            imgBtnMore = (ImageButton) view.findViewById(R.id.imgBtnMore);
            imgBtnLocationDetail = (ImageButton) view.findViewById(R.id.imgBtnLocationDetail);
            txtCreateAt = (TextView) view.findViewById(R.id.txtCreateAt);
            likeCount = 0;
            commentCount = 0;

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
                        post.setIsLiked(false);
                        post.setLikeCount(post.getLikeCount() - 1);
                        setLikeIconUI(R.color.black, R.mipmap.icon_like, true);
                    } else {
                        isPostLiked = true;
                        post.setIsLiked(true);
                        post.setLikeCount(post.getLikeCount() + 1);
                        setLikeIconUI(R.color.oceanBlue, R.mipmap.icon_like_filled, true);
                    }

                    PostHelper.LikeClicked.startProcess(mContext, post.getPostid(), null, isPostLiked);

                }
            });

            //Like layout
            imgBtnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String toolbarTitle = mContext.getResources().getString(R.string.likes);
                    PostHelper.LikeListClicked.startProcess(mContext, fragmentNavigation, toolbarTitle, post.getPostid());
                }
            });


            //More layout
            imgBtnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FeedContextMenuManager.getInstance().toggleContextMenuFromView(v, position, new FeedContextMenu.OnFeedContextMenuItemClickListener() {
                        @Override
                        public void onReportClick(int feedItem) {

                        }

                        @Override
                        public void onSharePhotoClick(int feedItem) {

                        }

                        @Override
                        public void onCopyShareUrlClick(int feedItem) {

                        }

                        @Override
                        public void onCancelClick(int feedItem) {
                            FeedContextMenuManager.getInstance().hideContextMenu();
                        }
                    });
                }
            });
            //Profile layout
            profileMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserInfoListItem userInfoListItem = new UserInfoListItem(post.getUser());
                    PostHelper.ProfileClicked.startProcess(mContext, fragmentNavigation, userInfoListItem);
                }
            });
            //Location Detail Layout
            imgBtnLocationDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PostHelper.LocatonDetailClicked.startProcess(mActivity, mContext, fragmentNavigation, post, txtProfilePic, imgProfilePic);
                }
            });

            //Comment layout
            imgBtnComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SinglePost.getInstance().setPost(post);
                    String toolbarTitle = post.getUser().getUsername();
                    PostHelper.SinglePostClicked.startProcess(mContext, fragmentNavigation, toolbarTitle, post.getPostid(), postPositionHashMap.get(post.getPostid()));
                }
            });
            //CardView
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SinglePost.getInstance().setPost(post);
                    String toolbarTitle = post.getUser().getUsername();
                    PostHelper.SinglePostClicked.startProcess(mContext, fragmentNavigation, toolbarTitle, post.getPostid(), postPositionHashMap.get(post.getPostid()));
                }
            });

            /**
             * Like Callback
             */
            PostHelper.SinglePostClicked.setPostLikeClickCallback(new PostLikeClickCallback() {
                @Override
                public void onPostLikeClicked(boolean isPostLiked, int newLikeCount, int position) {
                    postList.get(position).setLikeCount(newLikeCount);
                    postList.get(position).setIsLiked(isPostLiked);
                    notifyItemChanged(position);
                }
            });

            /**
             * Comment Callback
             */
            PostHelper.AddComment.setCommentAddCallback(new CommentAddCallback() {
                @Override
                public void onCommentAdd(int position) {
                    postList.get(position).setCommentCount(postList.get(position).getCommentCount() + 1);
                    notifyItemChanged(position);
                }
            });

        }

        public void setData(Post post, int position) {

            //her postID bir position ile entegre halde...
            postPositionHashMap.put(post.getPostid(), position);

            this.position = position;
            this.post = post;
            this.isPostLiked = post.getIsLiked();
            this.likeCount = post.getLikeCount();
            this.commentCount = post.getCommentCount();

            //profile picture
            UserDataUtil.setProfilePicture(mContext, post.getUser().getProfilePhotoUrl(),
                    post.getUser().getName(), txtProfilePic, imgProfilePic);
            //Name
            if (post.getUser().getName() != null && !post.getUser().getName().isEmpty()) {
                //this.txtName.setText(post.getUser().getName());
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
            //Comment Count
            txtCommentCount.setText(String.valueOf(commentCount));
            //Location distance
            if (post.getDistance() != null) {
                txtLocationDistance.setText(String.valueOf(PostHelper.Utils.calculateDistance(post.getDistance().doubleValue())));
            }
            //Create at
            if (post.getCreateAt() != null) {
                txtCreateAt.setText(CommonUtils.timeAgo(mContext, post.getCreateAt()));
            }
        }





        private void setLikeIconUI(int color, int icon, boolean isClientOperation) {
            imgLike.setColorFilter(ContextCompat.getColor(mContext, color), android.graphics.PorterDuff.Mode.SRC_IN);
            imgLike.setImageResource(icon);

            if (isClientOperation) {
                if (isPostLiked) {
                    likeCount++;
                    post.setLikeCount(likeCount);
                } else {
                    likeCount--;
                    post.setLikeCount(likeCount);
                }
            }

            txtLikeCount.setText(String.valueOf(likeCount));

        }

        private void setViewPager(Post post) {

            viewPager.setAdapter(new ViewPagerAdapter(mActivity, mContext, post.getAttachments()));
            viewPager.setOffscreenPageLimit(post.getAttachments().size());
            if (post.getAttachments().size() > 0) {
                ViewPagerUtils.setSliderDotsPanel(post.getAttachments().size(), mView, mContext);
            }

        }

    }

    @Override
    public int getItemCount() {
        return (postList != null ? postList.size() : 0);
    }

    public void addAll(List<Post> addedPostList) {
        if (addedPostList != null) {
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

    public boolean isShowingProgressLoading() {
        if (getItemViewType(postList.size() - 1) == VIEW_PROG)
            return true;
        else
            return false;
    }

    public void removeAll() {
        int initalSize = postList.size();
        notifyItemRangeChanged(0, initalSize);

    }

    public void updatePostListItems(List<Post> newPostList) {
        final PostDiffCallback diffCallback = new PostDiffCallback(this.postList, newPostList);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.postList.clear();
        this.postList.addAll(newPostList);
        diffResult.dispatchUpdatesTo(this);
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBarLoading);
        }
    }


}