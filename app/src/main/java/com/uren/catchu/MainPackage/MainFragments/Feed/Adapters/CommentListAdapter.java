package com.uren.catchu.MainPackage.MainFragments.Feed.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.PersonListItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostHelper;
import com.uren.catchu.R;

import java.util.ArrayList;
import java.util.List;

import catchu.model.Comment;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.MyViewHolder> {

    private Context mContext;
    private String postId;
    private List<Comment> commentList;

    private PersonListItemClickListener personListItemClickListener;
    GradientDrawable imageShape;
    GradientDrawable buttonShape;

    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;
    private int lastAnimatedPosition = -1;


    public CommentListAdapter(Context context, String postId) {
        this.mContext = context;
        this.postId = postId;
        this.commentList = new ArrayList<Comment>();

        imageShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                0, GradientDrawable.OVAL, 50, 0);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_post_item_comment, parent, false);

        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

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


        public MyViewHolder(View view) {
            super(view);

            profileName = view.findViewById(R.id.profile_name);
            txtUsername = view.findViewById(R.id.txtUsername);
            imgProfilePic = view.findViewById(R.id.imgProfilePic);
            txtProfilePic = view.findViewById(R.id.txtProfilePic);
            cardView = view.findViewById(R.id.card_view);
            imgProfilePic.setBackground(imageShape);
            commentMessage = view.findViewById(R.id.commentMessage);
            llProfile = view.findViewById(R.id.llProfile);
            txtLikeCount = view.findViewById(R.id.txtLikeCount);
            txtLike = view.findViewById(R.id.txtLike);
            txtCreateAt = view.findViewById(R.id.txtCreateAt);
            imgLike = view.findViewById(R.id.imgLike);

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
                        PostHelper.LikeClicked.startProcess(mContext, postId ,comment.getCommentid(), isCommentLiked);
                    } else {
                        isCommentLiked = true;
                        comment.setIsLiked(true);
                        tempLikeCount++;
                        comment.setLikeCount(tempLikeCount);
                        setLikeIconUI(R.color.oceanBlue, R.mipmap.icon_like_filled, true);
                        PostHelper.LikeClicked.startProcess(mContext, postId ,comment.getCommentid(), isCommentLiked);
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
                    comment.getUser().getName(), comment.getUser().getUsername(),
                    txtProfilePic, imgProfilePic, false);
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
                txtCreateAt.setText(CommonUtils.timeAgo(mContext, comment.getCreateAt()));
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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        //runEnterAnimation(holder.itemView, position);
        Comment comment = commentList.get(position);
        holder.setData(comment, position);
    }

    private void runEnterAnimation(View view, int position) {
        if (animationsLocked) return;

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(100);
            view.setAlpha(0.f);
            view.animate()
                    .translationY(0).alpha(1.f)
                    .setStartDelay(delayEnterAnimation ? 20 * (position) : 0)
                    .setInterpolator(new DecelerateInterpolator(2.f))
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animationsLocked = true;
                        }
                    })
                    .start();
        }
    }

    public void setAnimationsLocked(boolean animationsLocked) {
        this.animationsLocked = animationsLocked;
    }

    public void setDelayEnterAnimation(boolean delayEnterAnimation) {
        this.delayEnterAnimation = delayEnterAnimation;
    }

    public void updateItems() {
        /**/
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (commentList != null ? commentList.size() : 0);
    }

    public void updateAdapterWithPosition(int position) {

        notifyItemChanged(position);
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setPersonListItemClickListener(PersonListItemClickListener personListItemClickListener) {
        this.personListItemClickListener = personListItemClickListener;
    }

    public void add(Comment comment) {
        commentList.add(comment);
        notifyItemInserted(commentList.size() - 1);
    }

    public void addAll(List<Comment> addedCommentList) {
        if (addedCommentList != null) {
            commentList.addAll(addedCommentList);
            notifyItemRangeInserted(commentList.size(), commentList.size() + addedCommentList.size());
        }
    }

}


