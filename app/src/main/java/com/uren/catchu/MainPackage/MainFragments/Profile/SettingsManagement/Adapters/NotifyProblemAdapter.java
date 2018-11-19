package com.uren.catchu.MainPackage.MainFragments.Profile.SettingsManagement.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.R;
import com.uren.catchu.MainPackage.MainFragments.Share.Models.ImageShareItemBox;

import java.util.List;

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