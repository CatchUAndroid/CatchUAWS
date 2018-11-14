package com.uren.catchu.MainPackage.MainFragments.Profile.SettingsManagement.Adapters;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ProgressDialogUtil.ProgressDialogUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ListItemClickListener;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.Models.ImageShareItemBox;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.List;

import catchu.model.FollowInfoListResponse;
import catchu.model.FriendRequestList;
import catchu.model.User;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_FOLLOWING;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_NONE;
import static com.uren.catchu.Constants.StringConstants.FRIEND_ACCEPT_REQUEST;

public class NotifyProblemAdapter extends RecyclerView.Adapter<NotifyProblemAdapter.MyViewHolder> {

    private Context context;

    List<ImageShareItemBox> imageShareItemBoxes;
    ItemClickListener itemClickListener;

    public NotifyProblemAdapter(Context context, List<ImageShareItemBox> imageShareItemBoxes, ItemClickListener itemClickListener) {
        this.context = context;
        this.imageShareItemBoxes = imageShareItemBoxes;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public NotifyProblemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.problem_notify_list_item, parent, false);
        return new NotifyProblemAdapter.MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImgv;
        ImageView addItemImgv;
        ImageView cancelImageView;
        ImageShareItemBox imageShareItemBox;

        int position;

        public MyViewHolder(View view) {
            super(view);

            itemImgv = view.findViewById(R.id.itemImgv);
            addItemImgv = view.findViewById(R.id.addItemImgv);
            cancelImageView = view.findViewById(R.id.cancelImageView);

            itemImgv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onClick(imageShareItemBox, position);

                }
            });
        }

        public void setData(ImageShareItemBox imageShareItemBox, int position) {
            this.imageShareItemBox = imageShareItemBox;
            this.position = position;
            setImages();
        }

        public void setImages(){
            if(imageShareItemBox != null && imageShareItemBox.getPhotoSelectUtil() != null){
                if(imageShareItemBox.getPhotoSelectUtil().getBitmap() != null){
                    Glide.with(context)
                            .load(imageShareItemBox.getPhotoSelectUtil().getBitmap())
                            .apply(RequestOptions.centerInsideTransform())
                            .into(itemImgv);
                }
            }else {

            }
        }
    }

    @Override
    public void onBindViewHolder(final NotifyProblemAdapter.MyViewHolder holder, final int position) {
        ImageShareItemBox imageShareItemBox = imageShareItemBoxes.get(position);
        holder.setData(imageShareItemBox, position);
    }

    @Override
    public int getItemCount() {
        if (imageShareItemBoxes != null)
            return imageShareItemBoxes.size();
        else
            return 0;
    }
}