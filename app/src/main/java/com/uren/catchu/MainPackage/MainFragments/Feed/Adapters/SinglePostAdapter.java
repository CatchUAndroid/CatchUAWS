package com.uren.catchu.MainPackage.MainFragments.Feed.Adapters;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.GeneralUtils.ViewPagerUtils;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.CommentAddCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.PersonListItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.FeedContextMenu;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.FeedContextMenuManager;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostDiffCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostHelper;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.SinglePost;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.FollowInfoListItem;
import com.uren.catchu.R;

import java.util.ArrayList;
import java.util.List;

import catchu.model.Comment;
import catchu.model.FollowInfoResultArrayItem;
import catchu.model.Post;

public class SinglePostAdapter extends RecyclerView.Adapter {

    public static final int VIEW_PROG = 0;    //holds progress loading
    public static final int VIEW_POST = 1;    // holds post view
    public static final int VIEW_COMMENT = 2; //holds comment view

    private Activity mActivity;
    private Context mContext;
    private Post post;
    private Comment comment;
    private List<Post> postList;
    private List<Comment> commentList;
    private BaseFragment.FragmentNavigation fragmentNavigation;
    GradientDrawable imageShape;
    private PersonListItemClickListener personListItemClickListener;

    public SinglePostAdapter(Activity activity, Context context,
                             BaseFragment.FragmentNavigation fragmentNavigation) {
        this.mActivity = activity;
        this.mContext = context;
        this.fragmentNavigation = fragmentNavigation;
        this.postList = new ArrayList<Post>();
        this.commentList = new ArrayList<Comment>();

        imageShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                0, GradientDrawable.OVAL, 50, 0);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_POST) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_post_item_post, parent, false);

            viewHolder = new PostViewHolder(itemView);
        } else if (viewType == VIEW_COMMENT) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.single_post_item_comment, parent, false);

            viewHolder = new CommentViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar_item, parent, false);

            viewHolder = new ProgressViewHolder(v);
        }
        return viewHolder;

    }

    @Override
    public int getItemViewType(int position) {
        if (postList.size() > position) {
            return VIEW_POST;
        } else if (commentList.get(position - postList.size()) != null) {
            return VIEW_COMMENT;
        } else {
            return VIEW_PROG;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof PostViewHolder) {
            post = postList.get(position);
            ((PostViewHolder) holder).setData(post, position);
        } else if (holder instanceof CommentViewHolder) {
            comment = commentList.get(position - postList.size());
            ((CommentViewHolder) holder).setData(comment, position);


        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

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
        int likeCount;
        int commentCount;
        ImageButton imgBtnLike, imgBtnComment, imgBtnMore, imgBtnLocationDetail;
        LinearLayout profileMainLayout;
        TextView txtLocationDistance;

        View mView;

        public PostViewHolder(View view) {
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
            profileMainLayout = (LinearLayout) view.findViewById(R.id.profileMainLayout);
            txtLocationDistance = (TextView) view.findViewById(R.id.txtLocationDistance);
            imgBtnLike = (ImageButton) view.findViewById(R.id.imgBtnLike);
            imgBtnComment = (ImageButton) view.findViewById(R.id.imgBtnComment);
            imgBtnMore = (ImageButton) view.findViewById(R.id.imgBtnMore);
            imgBtnLocationDetail = (ImageButton) view.findViewById(R.id.imgBtnLocationDetail);
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
                    int tempLikeCount = post.getLikeCount();
                    if (isPostLiked) {
                        isPostLiked = false;
                        post.setIsLiked(false);
                        tempLikeCount--;
                        post.setLikeCount(tempLikeCount);
                        setLikeIconUI(R.color.black, R.mipmap.icon_like, true);
                        PostHelper.LikeClicked.startProcess(mContext, post.getPostid(), null, isPostLiked);
                    } else {
                        isPostLiked = true;
                        post.setIsLiked(true);
                        tempLikeCount++;
                        post.setLikeCount(tempLikeCount);
                        setLikeIconUI(R.color.oceanBlue, R.mipmap.icon_like_filled, true);
                        PostHelper.LikeClicked.startProcess(mContext, post.getPostid(), null, isPostLiked);
                    }

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
/*
            //Comment layout
            imgBtnComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PostHelper.CommentListClicked.startProcess(mContext, fragmentNavigation, post.getPostid(), position);
                }
            });
*/
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
                    FollowInfoResultArrayItem rowItem = new FollowInfoResultArrayItem();
                    rowItem.setUserid(post.getUser().getUserid());
                    rowItem.setProfilePhotoUrl(post.getUser().getProfilePhotoUrl());
                    rowItem.setName(post.getUser().getName());
                    FollowInfoListItem followInfoListItem = new FollowInfoListItem(rowItem);
                    PostHelper.ProfileClicked.startProcess(mContext, fragmentNavigation, followInfoListItem);
                }
            });
            //Location Detail Layout
            imgBtnLocationDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PostHelper.LocatonDetailClicked.startProcess(mActivity, mContext, fragmentNavigation, post, txtProfilePic, imgProfilePic);
                }
            });
/*
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonUtils.showToast(mContext, "Card clicked");
                    SinglePost.getInstance().setPost(post);
                    String toolbarTitle = post.getUser().getUsername();
                    PostHelper.SinglePostClicked.startProcess(mContext, fragmentNavigation, toolbarTitle, post.getPostid());
                    SinglePost.getInstance().setPost(null);
                }
            });
*/
        }

        public void setData(Post post, int position) {

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
            //Comment Count
            txtCommentCount.setText(String.valueOf(commentCount));
            //Location distance
            if (post.getDistance() != null) {
                txtLocationDistance.setText(PostHelper.Utils.calculateDistance(post.getDistance().doubleValue()));
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

        public void setLikeCount() {

        }

    }

    @Override
    public int getItemCount() {

        if (postList != null && commentList != null) {
            return postList.size() + commentList.size();
        } else if (postList != null && commentList == null) {
            return postList.size();
        } else if (postList == null && commentList != null) {
            return commentList.size();
        } else { // postList == null && commentList == null
            return 0;
        }

    }

    public void addAll(List<Post> addedPostList, List<Comment> addedCommentList) {

        int totalSize = postList.size() + commentList.size();

        if (addedPostList != null) {
            postList.addAll(addedPostList);
            notifyItemRangeInserted(totalSize, totalSize + addedPostList.size());
            return;
        }

        if (addedCommentList != null) {
            commentList.addAll(addedCommentList);
            notifyItemRangeInserted(totalSize, totalSize + addedCommentList.size());
            return;
        }
    }

    public void addComment(Comment comment) {
        int totalSize = postList.size() + commentList.size();
        commentList.add(comment);
        notifyItemRangeInserted(totalSize, totalSize + 1);
    }

    public void addProgressLoading() {
        commentList.add(null);
        notifyItemInserted(postList.size() + commentList.size() - 1);
    }

    public void removeProgressLoading() {
        commentList.remove(commentList.size() - 1);
        notifyItemRemoved(postList.size() + commentList.size());
    }

    public boolean isShowingProgressLoading() {
        if (getItemViewType(postList.size() + commentList.size() - 1) == VIEW_PROG)
            return true;
        else
            return false;
    }

    public void removeAll() {
        int initalSize = postList.size() + commentList.size();
        notifyItemRangeChanged(0, initalSize);
    }

    public void updatePostListItems(List<Post> newPostList) {
        final PostDiffCallback diffCallback = new PostDiffCallback(this.postList, newPostList);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.postList.addAll(newPostList);
        diffResult.dispatchUpdatesTo(this);
    }

    public void updateLikeCount(int newLikeCount) {
        post.setLikeCount(newLikeCount);
         notifyItemChanged(0, post);
    }

    public void updateItems() {
        /**/
        notifyDataSetChanged();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProfilePic;
        TextView txtProfilePic;
        TextView profileName;
        TextView txtUsername;
        TextView commentMessage;
        CardView cardView;
        Comment comment;
        int position;
        boolean isCommentLiked = false;

        LinearLayout llProfile;
        TextView txtLikeCount, txtLike, txtCreateDate;
        ImageView imgLike;
        int likeCount = 0;


        public CommentViewHolder(View view) {
            super(view);

            profileName = (TextView) view.findViewById(R.id.profile_name);
            txtUsername = (TextView) view.findViewById(R.id.txtUsername);
            imgProfilePic = (ImageView) view.findViewById(R.id.imgProfilePic);
            txtProfilePic = (TextView) view.findViewById(R.id.txtProfilePic);
            cardView = (CardView) view.findViewById(R.id.card_view);
            imgProfilePic.setBackground(imageShape);
            commentMessage = (TextView) view.findViewById(R.id.commentMessage);
            llProfile = (LinearLayout) view.findViewById(R.id.llProfile);
            txtLikeCount = (TextView) view.findViewById(R.id.txtLikeCount);
            txtLike = (TextView) view.findViewById(R.id.txtLike);
            txtCreateDate = (TextView) view.findViewById(R.id.txtCreateDate);
            imgLike = (ImageView) view.findViewById(R.id.imgLike);

            setListeners();

        }

        private void setListeners() {

            //imgLike
            txtLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    imgLike.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.image_click));
                    int tempLikeCount = comment.getLikeCount();
                    if (isCommentLiked) {
                        isCommentLiked = false;
                        comment.setIsLiked(false);
                        tempLikeCount--;
                        comment.setLikeCount(tempLikeCount);
                        setLikeIconUI(R.color.black, R.mipmap.icon_like, true);
                        PostHelper.LikeClicked.startProcess(mContext, post.getPostid(), comment.getCommentid(), isCommentLiked);
                    } else {
                        isCommentLiked = true;
                        comment.setIsLiked(true);
                        tempLikeCount++;
                        comment.setLikeCount(tempLikeCount);
                        setLikeIconUI(R.color.oceanBlue, R.mipmap.icon_like_filled, true);
                        PostHelper.LikeClicked.startProcess(mContext, post.getPostid(), comment.getCommentid(), isCommentLiked);
                    }
                }
            });
            //Profile layout
            llProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    personListItemClickListener.onPersonListItemClicked(v, comment.getUser(), position);
                }
            });

        }

        public void setData(Comment comment, int position) {

            this.position = position;
            this.comment = comment;
            this.isCommentLiked = comment.getIsLiked();
            this.likeCount = comment.getLikeCount();

            //profile picture
            UserDataUtil.setProfilePicture(mContext, comment.getUser().getProfilePhotoUrl(),
                    comment.getUser().getName(), txtProfilePic, imgProfilePic);
            //Username
            if (comment.getUser().getUsername() != null && !comment.getUser().getUsername().isEmpty()) {
                this.txtUsername.setText(comment.getUser().getUsername());
            }
            //Comment message
            if (comment.getMessage() != null && !comment.getMessage().isEmpty()) {
                this.commentMessage.setText(comment.getMessage());
            }
            //Date
            if (comment.getCreateAt() != null) {
                txtCreateDate.setText(comment.getCreateAt());
            }
            //Like
            if (comment.getIsLiked()) {
                setLikeIconUI(R.color.oceanBlue, R.mipmap.icon_like_filled, false);
            } else {
                setLikeIconUI(R.color.black, R.mipmap.icon_like, false);
            }

        }

        private void setLikeIconUI(int color, int icon, boolean isClientOperation) {
            imgLike.setColorFilter(ContextCompat.getColor(mContext, color), android.graphics.PorterDuff.Mode.SRC_IN);
            imgLike.setImageResource(icon);

            if (isClientOperation) {
                if (isCommentLiked) {
                    likeCount++;
                    comment.setLikeCount(likeCount);
                } else {
                    likeCount--;
                    comment.setLikeCount(likeCount);

                }
            }

            txtLikeCount.setText(String.valueOf(likeCount));

        }


    }

    public void setPersonListItemClickListener(PersonListItemClickListener personListItemClickListener) {
        SinglePostAdapter.this.personListItemClickListener = personListItemClickListener;
    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBarLoading);
        }
    }


}