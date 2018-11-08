package com.uren.catchu.SharePackage.GalleryPicker;

import android.content.Context;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.PhotoChosenCallback;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.R;

import java.io.File;
import java.util.ArrayList;

import com.uren.catchu.SharePackage.GalleryPicker.Interfaces.PhotoSelectCallback;

import static com.uren.catchu.Constants.NumericConstants.CODE_CAMERA_POSITION;
import static com.uren.catchu.Constants.NumericConstants.CODE_GALLERY_POSITION;
import static com.uren.catchu.Constants.StringConstants.FROM_FILE_TEXT;

public class GalleryGridListAdapter extends RecyclerView.Adapter<GalleryGridListAdapter.MyViewHolder> {

    ArrayList<File> fileList;
    View view;
    PhotoSelectCallback photoSelectCallback;

    LayoutInflater layoutInflater;

    Context context;

    int selectedPosition = 0;
    private static final int paddingSize = 80;

    PhotoSelectUtil photoSelectUtil;
    PhotoChosenCallback photoChosenCallback;

    public GalleryGridListAdapter(Context context, ArrayList<File> fileList,
                                  PhotoSelectCallback photoSelectCallback, PhotoChosenCallback photoChosenCallback) {
        layoutInflater = LayoutInflater.from(context);
        this.fileList = fileList;
        this.context = context;
        this.photoSelectCallback = photoSelectCallback;
        this.photoChosenCallback = photoChosenCallback;
    }

    @Override
    public GalleryGridListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = layoutInflater.inflate(R.layout.media_item_view, parent, false);
        GalleryGridListAdapter.MyViewHolder holder = new GalleryGridListAdapter.MyViewHolder(view);
        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        File selectedFile;
        int position = 0;
        ImageView mMediaThumb;
        ConstraintLayout constraintLayout;

        public MyViewHolder(final View itemView) {
            super(itemView);
            mMediaThumb = view.findViewById(R.id.mMediaThumb);
            constraintLayout = view.findViewById(R.id.constraintLayout);

            mMediaThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPosition = getAdapterPosition();

                    if (selectedPosition == CODE_GALLERY_POSITION)
                        photoChosenCallback.onGallerySelected();
                    else if (selectedPosition == CODE_CAMERA_POSITION)
                        photoChosenCallback.onCameraSelected();
                    else {
                        photoSelectUtil = new PhotoSelectUtil(context, Uri.fromFile(selectedFile), FROM_FILE_TEXT);
                        photoSelectCallback.onSelect(photoSelectUtil);
                    }
                }
            });
        }

        public void setData(File selectedFile, int position) {
            this.position = position;
            this.selectedFile = selectedFile;

            if (position == CODE_GALLERY_POSITION) {
                mMediaThumb.setImageResource(R.drawable.gallery_picker_icon);
                constraintLayout.setBackgroundColor(context.getResources().getColor(R.color.white, null));
                mMediaThumb.setPadding(paddingSize, paddingSize, paddingSize, paddingSize);
            } else if (position == CODE_CAMERA_POSITION) {
                mMediaThumb.setImageResource(R.drawable.camera_picker_icon);
                constraintLayout.setBackgroundColor(context.getResources().getColor(R.color.white, null));
                mMediaThumb.setPadding(paddingSize, paddingSize, paddingSize, paddingSize);
            } else {
                mMediaThumb.setPadding(0, 0, 0, 0);
                Glide.with(context).load(selectedFile).into(mMediaThumb);
            }
        }
    }

    @Override
    public void onBindViewHolder(GalleryGridListAdapter.MyViewHolder holder, int position) {
        File selectedImage = null;

        if (position > 1)
            selectedImage = fileList.get(position - 2);

        holder.setData(selectedImage, position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return fileList.size() + 2;
    }
}