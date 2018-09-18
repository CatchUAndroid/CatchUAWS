package com.uren.catchu.SharePackage.GalleryPicker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;

import java.io.File;
import java.util.ArrayList;

import android.preference.PreferenceManager;

import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.PhotoSelectAdapter;
import com.uren.catchu.SharePackage.Models.ImageShareItemBox;
import com.uren.catchu.Singleton.Share.ShareItems;

import catchu.model.Media;

import static com.uren.catchu.Constants.NumericConstants.CODE_CAMERA_POSITION;
import static com.uren.catchu.Constants.NumericConstants.CODE_GALLERY_POSITION;
import static com.uren.catchu.Constants.StringConstants.CAMERA_TEXT;
import static com.uren.catchu.Constants.StringConstants.FROM_FILE_TEXT;
import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;

public class GalleryGridListAdapter extends RecyclerView.Adapter<GalleryGridListAdapter.MyViewHolder> implements PreferenceManager.OnActivityResultListener {

    private ArrayList<File> fileList;
    View view;

    LayoutInflater layoutInflater;

    public static Context context;

    int selectedPosition = 0;

    PermissionModule permissionModule;
    GalleryPickerFrag galleryPickerFrag;

    //PhotoSelectAdapter photoSelectAdapter;
    PhotoSelectUtil photoSelectUtil;

    public GalleryGridListAdapter(Context context, ArrayList<File> fileList, GalleryPickerFrag galleryPickerFrag) {
        layoutInflater = LayoutInflater.from(context);
        this.fileList = fileList;
        this.context = context;
        this.galleryPickerFrag = galleryPickerFrag;
        permissionModule = new PermissionModule(context);
        addListeners();
    }

    private void addListeners() {
        galleryPickerFrag.cancelImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryPickerFrag.photoRelLayout.setVisibility(View.GONE);
                galleryPickerFrag.specialRecyclerView.setVisibility(View.VISIBLE);
                ShareItems.getInstance().clearImageShareItemBox();
            }
        });
    }

    @Override
    public GalleryGridListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = layoutInflater.inflate(R.layout.media_item_view, parent, false);
        GalleryGridListAdapter.MyViewHolder holder = new GalleryGridListAdapter.MyViewHolder(view);
        return holder;

       /* int width = parent.getMeasuredHeight() / 4;
        view.setMinimumHeight(width);
        return new GalleryGridListAdapter.MyViewHolder(view);*/
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == permissionModule.getImageGalleryPermission()) {
                photoSelectUtil = new PhotoSelectUtil(context, data, GALLERY_TEXT);
                fillImageShareItemBox();
                setSelectedImageView();
            } else if (requestCode == permissionModule.getCameraPermissionCode()) {
                photoSelectUtil = new PhotoSelectUtil(context, data, CAMERA_TEXT);
                fillImageShareItemBox();
                setSelectedImageView();
            } else
                DialogBoxUtil.showErrorDialog(context,  "GalleryGridListAdapter:resultCode:" + Integer.toString(resultCode) + "-requestCode:" + Integer.toString(requestCode), new InfoDialogBoxCallback() {
                    @Override
                    public void okClick() { }
                });
        }
        return false;
    }

    public void fillImageShareItemBox() {
        ImageShareItemBox imageShareItemBox = new ImageShareItemBox(photoSelectUtil);
        ShareItems.getInstance().clearImageShareItemBox();
        ShareItems.getInstance().addImageShareItemBox(imageShareItemBox);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        File selectedFile;
        int position = 0;
        ImageView specialProfileImgView;

        public MyViewHolder(final View itemView) {
            super(itemView);
            specialProfileImgView = view.findViewById(R.id.mMediaThumb);

            specialProfileImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPosition = getAdapterPosition();

                    if (selectedPosition == CODE_GALLERY_POSITION)
                        checkGalleryProcess();
                    else if (selectedPosition == CODE_CAMERA_POSITION)
                        checkCameraProcess();
                    else
                        showSelectedPicture();
                }
            });
        }

        public void showSelectedPicture() {
            Log.i("Info", "Position:" + position + " - filelen:" + selectedFile.length());
            galleryPickerFrag.specialRecyclerView.setVisibility(View.GONE);
            galleryPickerFrag.photoRelLayout.setVisibility(View.VISIBLE);
            galleryPickerFrag.initImageView();
            Glide.with(context).load(Uri.fromFile(selectedFile)).into(galleryPickerFrag.imageView);
            photoSelectUtil = new PhotoSelectUtil(context, Uri.fromFile(selectedFile), FROM_FILE_TEXT);
            fillImageShareItemBox();
        }

        public void setData(File selectedFile, int position) {
            this.position = position;
            this.selectedFile = selectedFile;

            if (position == CODE_GALLERY_POSITION) {
                specialProfileImgView.setImageResource(R.drawable.gallery);
            } else if (position == CODE_CAMERA_POSITION) {
                specialProfileImgView.setImageResource(R.drawable.camera);
            } else {
                Log.i("Info", "selectedFile:" + Uri.fromFile(selectedFile).toString() + "-position:" + position);
                Glide.with(context).load(selectedFile).into(specialProfileImgView);
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

    private void checkGalleryProcess() {
        if (permissionModule.checkWriteExternalStoragePermission())
            startGalleryProcess();
        else
            galleryPickerFrag.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    permissionModule.getWriteExternalStoragePermissionCode());
    }

    private void checkCameraProcess() {
        if (!CommonUtils.checkCameraHardware(context)) {
            CommonUtils.showToast(context, context.getResources().getString(R.string.deviceHasNoCamera));
            return;
        }

        if (permissionModule.checkCameraPermission())
            startCameraProcess();
        else
            galleryPickerFrag.requestPermissions(new String[]{Manifest.permission.CAMERA},
                    permissionModule.getCameraPermissionCode());
    }

    public void setSelectedImageView() {
        galleryPickerFrag.initImageView();
        Glide.with(context).load(photoSelectUtil.getMediaUri()).into(galleryPickerFrag.imageView);
        galleryPickerFrag.specialRecyclerView.setVisibility(View.GONE);
        galleryPickerFrag.photoRelLayout.setVisibility(View.VISIBLE);
    }

    public void startGalleryProcess() {
        Activity origin = (Activity) context;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        origin.startActivityForResult(Intent.createChooser(intent,
                context.getResources().getString(R.string.selectPicture)), permissionModule.getImageGalleryPermission());
    }

    public void startCameraProcess() {
        if (permissionModule.checkWriteExternalStoragePermission()) {
            Activity origin = (Activity) context;
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            origin.startActivityForResult(intent, permissionModule.getCameraPermissionCode());
        } else
            galleryPickerFrag.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    permissionModule.getWriteExternalStoragePermissionCode());
    }
}