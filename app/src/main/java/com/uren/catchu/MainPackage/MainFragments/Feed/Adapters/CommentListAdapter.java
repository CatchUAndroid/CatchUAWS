package com.uren.catchu.MainPackage.MainFragments.Feed.Adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.uren.catchu.ApiGatewayFunctions.FriendRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.YesNoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.PersonListItemClickListener;
import com.uren.catchu.R;

import catchu.model.Comment;
import catchu.model.CommentListResponse;
import catchu.model.FollowInfoResultArrayItem;
import catchu.model.FriendRequestList;
import catchu.model.User;
import catchu.model.UserListResponse;

import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_FOLLOWING;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_NONE;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_PENDING;
import static com.uren.catchu.Constants.StringConstants.FRIEND_CREATE_FOLLOW_DIRECTLY;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_FOLLOW;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_PENDING_FOLLOW_REQUEST;
import static com.uren.catchu.Constants.StringConstants.FRIEND_FOLLOW_REQUEST;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.MyViewHolder> {

    private Context context;
    private CommentListResponse commentList;

    private PersonListItemClickListener personListItemClickListener;
    GradientDrawable imageShape;
    GradientDrawable buttonShape;

    public CommentListAdapter(Context context, CommentListResponse commentList) {
        this.context = context;
        this.commentList = commentList;

        imageShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                0, GradientDrawable.OVAL, 50, 0);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_vert_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView profileName;
        TextView shortUserNameTv;
        ImageView profileImage;
        CardView cardView;
        Comment comment;
        int position;

        public MyViewHolder(View view) {
            super(view);

            profileName = (TextView) view.findViewById(R.id.profile_name);
            shortUserNameTv = view.findViewById(R.id.shortUserNameTv);
            profileImage = (ImageView) view.findViewById(R.id.profile_image);
            cardView = (CardView) view.findViewById(R.id.card_view);
            profileImage.setBackground(imageShape);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    personListItemClickListener.onClick(v, comment.getUser(), position);
                }
            });
        }


        public void setData(Comment comment, int position) {

            this.profileName.setText(comment.getUser().getName());
            this.comment = comment;
            this.position = position;
            UserDataUtil.setProfilePicture(context, comment.getUser().getProfilePhotoUrl(),
                    comment.getUser().getName(), shortUserNameTv, profileImage);

        }

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        Comment comment = commentList.getItems().get(position);
        holder.setData(comment, position);
    }

    @Override
    public int getItemCount() {
        return commentList.getItems().size();
    }

    public void updateAdapterWithPosition(int position) {

        notifyItemChanged(position);
    }

    public CommentListResponse getPersonList() {
        return commentList;
    }

    public void setPersonListItemClickListener(PersonListItemClickListener personListItemClickListener) {
        this.personListItemClickListener = personListItemClickListener;
    }

    public void add(Comment comment) {
        commentList.getItems().add(comment);
        notifyItemInserted(commentList.getItems().size() - 1);
    }

}


