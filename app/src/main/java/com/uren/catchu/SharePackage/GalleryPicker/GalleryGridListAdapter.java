package com.uren.catchu.SharePackage.GalleryPicker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.uren.catchu.GeneralUtils.BitmapConversion;
import com.uren.catchu.GeneralUtils.UriAdapter;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.MainShareActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.PhotoSelectAdapter;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.ShareItems;

import java.io.File;
import java.util.ArrayList;

import static com.uren.catchu.Constants.StringConstants.CAMERA_TEXT;
import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;

public class GalleryGridListAdapter extends RecyclerView.Adapter<GalleryGridListAdapter.MyViewHolder> implements
        ActivityCompat.OnRequestPermissionsResultCallback, PreferenceManager.OnActivityResultListener {

    private ArrayList<File> fileList;
    View view;

    LayoutInflater layoutInflater;

    public static Context context;

    int selectedPosition = 0;

    PermissionModule permissionModule;
    GalleryPickerFrag galleryPickerFrag;

    private static final int CODE_GALLERY_POSITION = 0;
    private static final int CODE_CAMERA_POSITION = 1;

    PhotoSelectAdapter photoSelectAdapter;

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
                ShareItems.getInstance().setPhotoSelectAdapter(null);
            }
        });
    }

    @Override
    public GalleryGridListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.media_item_view, parent, false);
        GalleryGridListAdapter.MyViewHolder holder = new GalleryGridListAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == permissionModule.getWriteExternalStoragePermissionCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (selectedPosition == CODE_GALLERY_POSITION)
                    startGalleryProcess();
                else if(selectedPosition == CODE_CAMERA_POSITION)
                    startCameraProcess();
                else
                    startGalleryProcess();
            }
        } else if (requestCode == permissionModule.getCameraPermissionCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCameraProcess();
            }
        } else
            CommonUtils.showToast(context, context.getString(R.string.technicalError) + requestCode);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == permissionModule.getImageGalleryPermission()) {
                photoSelectAdapter = new PhotoSelectAdapter(context, data, GALLERY_TEXT);
                setSelectedImageView(photoSelectAdapter.getPhotoBitmapOrjinal());
                ShareItems.getInstance().setPhotoSelectAdapter(photoSelectAdapter);

            } else if (requestCode == permissionModule.getCameraPermissionCode()) {
                photoSelectAdapter = new PhotoSelectAdapter(context, data, CAMERA_TEXT);
                setSelectedImageView(photoSelectAdapter.getPhotoBitmapOrjinal());
                ShareItems.getInstance().setPhotoSelectAdapter(photoSelectAdapter);
            } else
                CommonUtils.showToast(context, context.getResources().getString(R.string.technicalError) + requestCode);
        }

        return false;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

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

            Picasso.with(context)
                    .load(Uri.fromFile(selectedFile))
                    .resize(500, 500)
                    .centerCrop()
                    .noFade()
                    .into(galleryPickerFrag.imageView);

            galleryPickerFrag.specialRecyclerView.setVisibility(View.GONE);
            galleryPickerFrag.photoRelLayout.setVisibility(View.VISIBLE);
            photoSelectAdapter = new PhotoSelectAdapter(context, Uri.fromFile(selectedFile));
            ShareItems.getInstance().setPhotoSelectAdapter(photoSelectAdapter);
        }

        public void setData(File selectedFile, int position) {
            this.position = position;
            this.selectedFile = selectedFile;

            if (position == CODE_GALLERY_POSITION) {
                specialProfileImgView.setImageResource(R.drawable.gallery);
            } else if (position == CODE_CAMERA_POSITION) {
                specialProfileImgView.setImageResource(R.drawable.camera);
            } else
                Picasso.with(context)
                        .load(Uri.fromFile(selectedFile))
                        .resize(500, 500)
                        .centerCrop()
                        .noFade()
                        .into(specialProfileImgView);
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
            ActivityCompat.requestPermissions((FragmentActivity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
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
            ActivityCompat.requestPermissions((FragmentActivity) context,
                    new String[]{Manifest.permission.CAMERA},
                    permissionModule.getCameraPermissionCode());
    }

    public void setSelectedImageView(Bitmap bitmap) {
        galleryPickerFrag.imageView.setImageBitmap(bitmap);
        galleryPickerFrag.specialRecyclerView.setVisibility(View.GONE);
        galleryPickerFrag.photoRelLayout.setVisibility(View.VISIBLE);
    }

    private void startGalleryProcess() {
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
            ActivityCompat.requestPermissions((FragmentActivity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    permissionModule.getWriteExternalStoragePermissionCode());
    }
}