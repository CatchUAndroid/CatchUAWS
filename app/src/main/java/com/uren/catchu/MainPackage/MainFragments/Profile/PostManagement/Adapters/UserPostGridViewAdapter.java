package com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.PostFeaturesCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostDiffCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostHelper;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.SingletonSinglePost;
import com.uren.catchu.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import catchu.model.Post;

import static com.uren.catchu.Constants.StringConstants.IMAGE_TYPE;
import static com.uren.catchu.Constants.StringConstants.VIDEO_TYPE;


public class UserPostGridViewAdapter extends RecyclerView.Adapter {

    public static final int VIEW_ITEM = 1;
    public static final int VIEW_PROG = 0;

    private Activity mActivity;
    private Context mContext;
    private List<Post> postList;
    private BaseFragment.FragmentNavigation fragmentNavigation;

    public UserPostGridViewAdapter(Activity activity, Context context, BaseFragment.FragmentNavigation fragmentNavigation) {
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
                    .inflate(R.layout.user_media_grid_item, parent, false);

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

        View mView;
        private Post post;
        private int position;

        //View items
        ImageView imgPost;
        ProgressBar progressLoading;
        RelativeLayout rlContent;
        LinearLayout llProgress, llError;
        LinearLayout llPostImage;
        LinearLayout llExplanation;
        ClickableImageView imgRetry;
        TextView txtExplanation;
        ImageView imgVideoIcon, imgGridMore;

        public MyViewHolder(View view) {
            super(view);

            mView = view;
            imgPost = (ImageView) view.findViewById(R.id.imgPost);
            progressLoading = (ProgressBar) view.findViewById(R.id.progressLoading);
            llProgress = (LinearLayout) view.findViewById(R.id.llProgress);
            llError = (LinearLayout) view.findViewById(R.id.llError);
            llExplanation = (LinearLayout) view.findViewById(R.id.llExplanation);
            llPostImage = (LinearLayout) view.findViewById(R.id.llPostImage);
            imgRetry = (ClickableImageView) view.findViewById(R.id.imgRetry);
            txtExplanation = (TextView) view.findViewById(R.id.txtExplanation);
            rlContent = (RelativeLayout) view.findViewById(R.id.rlContent);
            imgVideoIcon = (ImageView) view.findViewById(R.id.imgVideoCamera);
            imgGridMore = (ImageView) view.findViewById(R.id.imgGridMore);

            setListeners();

        }

        private void setListeners() {
            rlContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setSinglePostFragmentItems();

                    Log.i("clickedPostId ", postList.get(position).getPostid());
                    CommonUtils.showToast(mContext, postList.get(position).getPostid());
                }
            });

            imgRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setData(post, position);
                }
            });

        }

        private void setSinglePostFragmentItems() {

            SingletonSinglePost.getInstance().setPost(post);
            String toolbarTitle = post.getUser().getUsername();

            PostHelper.SinglePostClicked singlePostClickedInstance = PostHelper.SinglePostClicked.getInstance();
            singlePostClickedInstance.setSinglePostItems(mContext, fragmentNavigation, toolbarTitle, post.getPostid(), position);
            singlePostClickedInstance.setPostFeaturesCallback(new PostFeaturesCallback() {
                @Override
                public void onPostLikeClicked(boolean isPostLiked, int newLikeCount, int position) {
                    postList.get(position).setLikeCount(newLikeCount);
                    postList.get(position).setIsLiked(isPostLiked);
                    notifyItemChanged(position);
                }

                @Override
                public void onCommentAdd(int position, int newCommentCount) {
                    postList.get(position).setCommentCount(newCommentCount);
                    notifyItemChanged(position);
                }

                @Override
                public void onCommentAllowedStatusChanged(int position, boolean commentAllowed) {
                    postList.get(position).setIsCommentAllowed(commentAllowed);
                    notifyItemChanged(position);
                }
            });

            singlePostClickedInstance.startSinglePostProcess();

        }

        public void setData(Post post, int position) {

            this.post = post;
            this.position = position;
            boolean sourceFound = false;
            String loadUrl = "empty";

            llProgress.setVisibility(View.VISIBLE);
            progressLoading.setVisibility(View.VISIBLE);

            llPostImage.setVisibility(View.GONE);
            llError.setVisibility(View.GONE);
            llExplanation.setVisibility(View.GONE);
            imgVideoIcon.setVisibility(View.GONE);
            imgGridMore.setVisibility(View.GONE);

            if(post.getAttachments().size() > 1){
                imgGridMore.setVisibility(View.VISIBLE);
            }

            //varsa video ThumbNail
            if (!sourceFound) {
                for (int i = 0; i < post.getAttachments().size(); i++) {
                    if (post.getAttachments().get(i).getType().equals(VIDEO_TYPE)) {
                        loadUrl = post.getAttachments().get(i).getThumbnail();
                        sourceFound = true;
                        imgVideoIcon.setVisibility(View.VISIBLE);
                    }
                }
            }
            //varsa image
            if (!sourceFound) {
                for (int i = 0; i < post.getAttachments().size(); i++) {
                    if (post.getAttachments().get(i).getType().equals(IMAGE_TYPE)) {
                        loadUrl = post.getAttachments().get(i).getUrl();
                        sourceFound = true;
                    }
                }
            }
            //varsa post açıklaması
            if (!sourceFound) {
                setGridBackgroundColor();
                if (post.getMessage() != null && !post.getMessage().isEmpty()) {
                    llExplanation.setVisibility(View.VISIBLE);
                    llProgress.setVisibility(View.GONE);
                    progressLoading.setVisibility(View.GONE);
                    llPostImage.setVisibility(View.GONE);
                    llError.setVisibility(View.GONE);

                    txtExplanation.setText(post.getMessage());
                }
            } else {
                llProgress.setVisibility(View.VISIBLE);
                progressLoading.setVisibility(View.VISIBLE);
                llPostImage.setVisibility(View.VISIBLE);
                llExplanation.setVisibility(View.GONE);
                llError.setVisibility(View.GONE);

                Glide.with(mContext)
                        .load(loadUrl)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                llError.setVisibility(View.VISIBLE);
                                llProgress.setVisibility(View.GONE);
                                progressLoading.setVisibility(View.GONE);;
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                llPostImage.setVisibility(View.VISIBLE);
                                progressLoading.setVisibility(View.GONE);
                                llProgress.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .apply(RequestOptions.centerCropTransform())
                        .into(imgPost);
            }
        }

        private void setGridBackgroundColor() {
            int colorCode = getRandomColor();
            llExplanation.setBackgroundColor(mContext.getResources().getColor(colorCode, null));
        }


    }

    private int getRandomColor() {

        Resources resources = mContext.getResources();

        int colorList[] = {
                R.color.green,
                R.color.green,
                R.color.PeachPuff,
                R.color.Gold,
                R.color.Pink,
                R.color.LightPink,
                R.color.Orange,
                R.color.LightSalmon,
                R.color.DarkOrange,
                R.color.Coral,
                R.color.HotPink,
                R.color.Tomato,
                R.color.OrangeRed,
                R.color.DeepPink,
                R.color.Fuchsia,
                R.color.Magenta,
                R.color.LightCoral,
                R.color.PaleGoldenrod,
                R.color.Violet,
                R.color.DarkSalmon,
                R.color.Lavender,
                R.color.Yellow,
                R.color.LightBlue,
                R.color.DarkGray,
                R.color.Brown,
                R.color.Sienna,
                R.color.Yellow,
                R.color.DarkOrchid,
                R.color.PaleGreen,
                R.color.DarkViolet
        };

        Random rand = new Random();
        return colorList[rand.nextInt(colorList.length)];
    }

    public void updateItems() {
        /**/
        notifyDataSetChanged();
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


