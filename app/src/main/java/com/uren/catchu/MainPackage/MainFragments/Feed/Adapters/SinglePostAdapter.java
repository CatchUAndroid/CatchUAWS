package com.uren.catchu.MainPackage.MainFragments.Feed.Adapters;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
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

import com.bumptech.glide.Glide;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.PostSettingsChoosenCallback;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.GeneralUtils.ViewPagerUtils;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.CommentAllowedCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.PersonListItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.PostDeletedCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostHelper;

import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.SingleMapDetailFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.UserInfoListItem;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.List;

import catchu.model.Comment;
import catchu.model.Post;
import catchu.model.Report;

import static com.uren.catchu.Constants.StringConstants.CREATE_AT_NOW;
import static com.uren.catchu.Constants.StringConstants.REPORT_PROBLEM_TYPE_INAPPROPIATE;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_ALL_FOLLOWERS;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_CUSTOM;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_EVERYONE;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_GROUP;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_SELF;

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
    private List<Comment> tempCommentList;
    private BaseFragment.FragmentNavigation fragmentNavigation;
    GradientDrawable imageShape;
    private PersonListItemClickListener personListItemClickListener;
    private CommentAllowedCallback commentAllowedCallback;
    private PostDeletedCallback postDeletedCallback;
    public static String PARTIAL_DATA_LOADING = "PARTIAL_DATA_LOADING";
    private int numberOfCallback; // callback number for feed adapter
    private int feedPosition;

    public SinglePostAdapter(Activity activity, Context context,
                             BaseFragment.FragmentNavigation fragmentNavigation, int feedPosition,
                             int numberOfCallback) {
        this.mActivity = activity;
        this.mContext = context;
        this.fragmentNavigation = fragmentNavigation;
        this.postList = new ArrayList<Post>();
        this.commentList = new ArrayList<Comment>();
        this.tempCommentList = new ArrayList<Comment>();
        this.numberOfCallback = numberOfCallback;
        this.feedPosition = feedPosition;

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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
        super.onBindViewHolder(holder, position, payloads);

        if (payloads.isEmpty()) {
            // Perform a full update
            onBindViewHolder(holder, position);
        } else {
            // Perform a partial update
            for (Object payload : payloads) {
                if (payload instanceof String) {

                    String loadType = (String) payload.toString();
                    if (loadType.equals(PARTIAL_DATA_LOADING)) {
                        if (holder instanceof PostViewHolder) {
                            post = postList.get(position);
                            ((PostViewHolder) holder).setPartialData(post, position);
                        }
                    }

                }

            }
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

        View mView;
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
        ImageView imgCommentNotAllowed;
        LinearLayout profileMainLayout;
        TextView txtLocationDistance;
        TextView txtCreateAt;
        ImageView imgTarget;

        public PostViewHolder(View view) {
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
            imgCommentNotAllowed = (ImageView) view.findViewById(R.id.imgCommentNotAllowed);
            imgBtnMore = (ImageButton) view.findViewById(R.id.imgBtnMore);
            imgBtnLocationDetail = (ImageButton) view.findViewById(R.id.imgBtnLocationDetail);
            txtCreateAt = (TextView) view.findViewById(R.id.txtCreateAt);
            imgTarget = (ImageView) view.findViewById(R.id.imgTarget);
            likeCount = 0;
            commentCount = 0;

            setListeners();

        }

        private void setListeners() {

            //init Variables
            imgCommentNotAllowed.setVisibility(View.GONE);

            //imgLike
            imgLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    imgLike.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.image_click));

                    if (isPostLiked) {
                        isPostLiked = false;
                        post.setIsLiked(isPostLiked);
                        post.setLikeCount(post.getLikeCount() - 1);
                        setLikeIconUI(R.color.black, R.mipmap.icon_like, true);
                    } else {
                        isPostLiked = true;
                        post.setIsLiked(isPostLiked);
                        post.setLikeCount(post.getLikeCount() + 1);
                        setLikeIconUI(R.color.likeButtonColor, R.mipmap.icon_like_filled, true);
                    }
                    PostHelper.LikeClicked.startProcess(mContext, post.getPostid(), null, isPostLiked);
                    PostHelper.SinglePostClicked.postLikeStatusChanged(isPostLiked, post.getLikeCount(), feedPosition, numberOfCallback);

                    //Like count
                    txtLikeCount.setText(String.valueOf(post.getLikeCount()));
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
                    DialogBoxUtil.postSettingsDialogBox(mActivity, mContext, post, new PostSettingsChoosenCallback() {

                        @Override
                        public void onReportSelected() {
                            Report report = new Report();
                            report.setType(REPORT_PROBLEM_TYPE_INAPPROPIATE);
                            PostHelper.ReportPost.startProcess(mContext, AccountHolderInfo.getUserID(), post.getPostid(), report);
                        }

                        @Override
                        public void onUnFollowSelected() {

                        }

                        @Override
                        public void onDisableCommentSelected() {
                            post.setIsCommentAllowed(!post.getIsCommentAllowed());
                            commentAllowedCallback.onCommentAllowedStatusChanged(post.getIsCommentAllowed());
                            setCommentStatus();
                            PostHelper.PostCommentPermission.startProcess(mContext, AccountHolderInfo.getUserID(), post);
                            PostHelper.SinglePostClicked.postCommentAllowedStatusChanged(feedPosition, post.getIsCommentAllowed(), numberOfCallback);
                        }

                        @Override
                        public void onDeletePostSelected() {
                            if (postDeletedCallback != null) {
                                postDeletedCallback.onPostDeleted();
                                PostHelper.SinglePostClicked.postDeleted(feedPosition, numberOfCallback);
                            }
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
                    if (fragmentNavigation != null)
                        fragmentNavigation.pushFragment(new SingleMapDetailFragment(post, imgProfilePic));
                }
            });


        }

        public void setData(Post post, int position) {

            this.position = position;
            this.post = post;
            this.isPostLiked = post.getIsLiked();
            this.likeCount = post.getLikeCount();
            this.commentCount = post.getCommentCount();

            //profile picture
            UserDataUtil.setProfilePicture(mContext, post.getUser().getProfilePhotoUrl(),
                    post.getUser().getName(), post.getUser().getUsername(), txtProfilePic, imgProfilePic);
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
                setLikeIconUI(R.color.likeButtonColor, R.mipmap.icon_like_filled, false);
            } else {
                setLikeIconUI(R.color.black, R.mipmap.icon_like, false);
            }
            //Comment
            if (post.getIsCommentAllowed() != null) {
                setCommentStatus();
            }
            //Location distance
            if (post.getDistance() != null) {
                txtLocationDistance.setText(String.valueOf(PostHelper.Utils.calculateDistance(post.getDistance().doubleValue())));
            }
            //Location show on map
            if (!post.getIsShowOnMap()) {
                imgBtnLocationDetail.setColorFilter(ContextCompat.getColor(mContext, R.color.gray), android.graphics.PorterDuff.Mode.SRC_IN);
                imgBtnLocationDetail.setEnabled(false);
            } else {
                imgBtnLocationDetail.setColorFilter(ContextCompat.getColor(mContext, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
                imgBtnLocationDetail.setEnabled(true);
            }
            //Create at
            if (post.getCreateAt() != null) {
                txtCreateAt.setText(CommonUtils.timeAgo(mContext, post.getCreateAt()));
            }
            //Target
            if (post.getPrivacyType() != null) {
                setTargetImage();
            }

        }

        private void setTargetImage() {

            int targetIcon = R.mipmap.icon_world;

            if (post.getPrivacyType().equals(SHARE_TYPE_EVERYONE)) {
                targetIcon = R.mipmap.icon_world;
                imgTarget.setColorFilter(ContextCompat.getColor(mContext, R.color.oceanBlue), android.graphics.PorterDuff.Mode.SRC_IN);
            } else if (post.getPrivacyType().equals(SHARE_TYPE_ALL_FOLLOWERS)) {
                targetIcon = R.mipmap.icon_friends;
                imgTarget.setColorFilter(ContextCompat.getColor(mContext, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
            } else if (post.getPrivacyType().equals(SHARE_TYPE_CUSTOM)) {
                targetIcon = R.drawable.groups_icon_500;
                imgTarget.setColorFilter(ContextCompat.getColor(mContext, R.color.gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else if (post.getPrivacyType().equals(SHARE_TYPE_SELF)) {
                targetIcon = R.drawable.groups_icon_500;
            } else if (post.getPrivacyType().equals(SHARE_TYPE_GROUP)) {
                targetIcon = R.drawable.groups_icon_500;
                imgTarget.setColorFilter(ContextCompat.getColor(mContext, R.color.Brown), android.graphics.PorterDuff.Mode.SRC_IN);
            }

            Glide.with(mContext)
                    .load(targetIcon)
                    .into(imgTarget);
        }

        private void setLikeIconUI(int color, int icon, boolean isClientOperation) {
            imgLike.setColorFilter(ContextCompat.getColor(mContext, color), android.graphics.PorterDuff.Mode.SRC_IN);

            Glide.with(mContext)
                    .load(icon)
                    .into(imgLike);

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

        public void setPartialData(Post post, int position) {

            this.position = position;
            this.post = post;
            this.likeCount = post.getLikeCount();
            this.commentCount = post.getCommentCount();

            //Like count
            txtLikeCount.setText(String.valueOf(likeCount));
            //Comment Count
            txtCommentCount.setText(String.valueOf(commentCount));

        }

        private void setCommentStatus() {
            if (!post.getIsCommentAllowed()) {
                imgCommentNotAllowed.setVisibility(View.VISIBLE);
                txtCommentCount.setText(String.valueOf(0));
                imgBtnComment.setEnabled(false);
            } else {
                imgCommentNotAllowed.setVisibility(View.GONE);
                txtCommentCount.setText(String.valueOf(commentCount));
                imgBtnComment.setEnabled(true);
            }
        }

        private void setViewPager(Post post) {

            viewPager.setAdapter(new ViewPagerAdapter(mActivity, mContext, post.getAttachments(), fragmentNavigation));
            viewPager.setOffscreenPageLimit(post.getAttachments().size());
            if (post.getAttachments().size() > 0) {
                ViewPagerUtils.setSliderDotsPanel(post.getAttachments().size(), mView, mContext);
            }

        }

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
        TextView txtLikeCount, txtLike, txtCreateAt;
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
            txtCreateAt = (TextView) view.findViewById(R.id.txtCreateAt);
            imgLike = (ImageView) view.findViewById(R.id.imgLike);

            setListeners();

        }

        private void setListeners() {

            //imgLike
            txtLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commentLikeClicked();
                }
            });
            //Profile layout
            llProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    personListItemClickListener.onPersonListItemClicked(v, comment.getUser(), position);
                }
            });
            txtUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    personListItemClickListener.onPersonListItemClicked(v, comment.getUser(), position);
                }
            });

        }

        private void commentLikeClicked() {
            imgLike.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.image_click));
            if (isCommentLiked) {
                isCommentLiked = false;
                comment.setIsLiked(isCommentLiked);
                comment.setLikeCount(comment.getLikeCount() - 1);
                setLikeIconUI(R.color.black, R.mipmap.icon_like, true);
            } else {
                isCommentLiked = true;
                comment.setIsLiked(isCommentLiked);
                comment.setLikeCount(comment.getLikeCount() + 1);
                setLikeIconUI(R.color.likeButtonColor, R.mipmap.icon_like_filled, true);
            }
            //server process
            PostHelper.LikeClicked.startProcess(mContext, post.getPostid(), comment.getCommentid(), isCommentLiked);
        }

        public void setData(Comment comment, int position) {

            this.position = position;
            this.comment = comment;
            this.isCommentLiked = comment.getIsLiked();
            this.likeCount = comment.getLikeCount();

            //profile picture
            UserDataUtil.setProfilePicture(mContext, comment.getUser().getProfilePhotoUrl(),
                    comment.getUser().getName(), comment.getUser().getUsername(), txtProfilePic, imgProfilePic);
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
                if (comment.getCreateAt().equals(CREATE_AT_NOW)) {
                    txtCreateAt.setText(mContext.getResources().getString(R.string.now));
                } else {
                    txtCreateAt.setText(CommonUtils.timeAgo(mContext, comment.getCreateAt()));
                }
            }
            //Like
            if (comment.getIsLiked()) {
                setLikeIconUI(R.color.likeButtonColor, R.mipmap.icon_like_filled, false);
            } else {
                setLikeIconUI(R.color.black, R.mipmap.icon_like, false);
            }

        }

        private void setLikeIconUI(int color, int icon, boolean isClientOperation) {
            imgLike.setColorFilter(ContextCompat.getColor(mContext, color), android.graphics.PorterDuff.Mode.SRC_IN);

            Glide.with(mContext).load(icon).into(imgLike);

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
        //notifyItemChanged(0);
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

    public void updatePostListItems(List<Post> newPostList) {

        this.postList.clear();
        this.postList.addAll(newPostList);
        notifyDataSetChanged();
        //notifyItemRangeChanged(0, postList.size());

        /*
        final PostDiffCallback diffCallback = new PostDiffCallback(this.postList, newPostList);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.postList.clear();
        this.postList.addAll(newPostList);
        diffResult.dispatchUpdatesTo(this);
        */
    }

    public void updateCommentListItems(List<Comment> newCommentList) {

        this.commentList.clear();
        this.commentList.addAll(newCommentList);
        notifyItemRangeChanged(postList.size(), commentList.size());

        /*
        final CommentListDiffCallback diffCallback = new CommentListDiffCallback(this.commentList, newCommentList);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.commentList.clear();
        this.commentList.addAll(newCommentList);
        diffResult.dispatchUpdatesTo(this);
        */

    }

    public void updateLikeCount(int newLikeCount) {
        postList.get(0).setLikeCount(newLikeCount);
        notifyItemRangeChanged(0, postList.size(), PARTIAL_DATA_LOADING);
    }

    public void updateCommentCount(int newCommentCount) {
        postList.get(0).setCommentCount(newCommentCount);
        notifyItemRangeChanged(0, postList.size(), PARTIAL_DATA_LOADING);
    }

    public void removeAllComments() {
        commentList.clear();
    }

    public void updateItems() {
        /**/
        notifyDataSetChanged();
    }


    public void setPersonListItemClickListener(PersonListItemClickListener personListItemClickListener) {
        SinglePostAdapter.this.personListItemClickListener = personListItemClickListener;
    }

    public void setCommentAllowedCallback(CommentAllowedCallback commentAllowedCallback) {
        SinglePostAdapter.this.commentAllowedCallback = commentAllowedCallback;
    }

    public void setPostDeletedCallback(PostDeletedCallback postDeletedCallback) {
        SinglePostAdapter.this.postDeletedCallback = postDeletedCallback;
    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBarLoading);
        }
    }


}