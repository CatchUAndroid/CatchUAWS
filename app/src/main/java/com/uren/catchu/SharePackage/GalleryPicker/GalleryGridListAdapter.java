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

public class GalleryGridListAdapter extends RecyclerView.Adapter<GalleryGridListAdapter.MyViewHolder> {

    private ArrayList<File> fileList;
    View view;

    LayoutInflater layoutInflater;

    Context context;
    FragmentActivity fragmentActivity;

    int selectedPosition = 0;
    int beforeSelectedPosition = -1;

    PermissionModule permissionModule;

    public GalleryGridListAdapter(Context context, ArrayList<File> fileList) {
        layoutInflater = LayoutInflater.from(context);
        this.fileList = fileList;
        this.context = context;
        permissionModule = new PermissionModule(context);
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
        ImageView specialProfileImgView;

        public MyViewHolder(final View itemView) {
            super(itemView);
            specialProfileImgView = view.findViewById(R.id.mMediaThumb);

            specialProfileImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPosition = getAdapterPosition();
                    notifyItemChanged(selectedPosition);

                    if (beforeSelectedPosition > -1)
                        notifyItemChanged(beforeSelectedPosition);

                    beforeSelectedPosition = selectedPosition;

                    if (selectedPosition == 0) {
                        if (permissionModule.checkWriteExternalStoragePermission())
                            startGalleryProcess();
                    }
                }
            });
        }

        public void setData(File selectedFile, int position) {
            this.position = position;
            this.selectedFile = selectedFile;

            if (position == 0)
                specialProfileImgView.setImageResource(R.drawable.gallery);
            else
                Picasso.with(context)
                        .load(Uri.fromFile(selectedFile))
                        .resize(500, 500)
                        .centerCrop()
                        .noFade()
                        .into(specialProfileImgView);

            if (position != 0) {
                if (position == selectedPosition)
                    specialProfileImgView.setAlpha(0.3f);
                else
                    specialProfileImgView.setAlpha(1.0f);
            }
        }
    }

    @Override
    public void onBindViewHolder(GalleryGridListAdapter.MyViewHolder holder, int position) {

        File selectedImage = null;

        if (position > 0)
            selectedImage = fileList.get(position - 1);

        holder.setData(selectedImage, position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return fileList.size() + 1;
    }

    private void startGalleryProcess() {

        Activity origin = (Activity) context;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        origin.startActivityForResult(Intent.createChooser(intent,
                context.getResources().getString(R.string.selectPicture)), permissionModule.getShareGalleryPickerPerm());
    }

    public static void manageProfilePicChoosen(Intent data) {

        Log.i("Info", "manageProfilePicChoosen++++++++++++++++++++++++++++++++");


        Uri pictureUri = data.getData();
        imageRealPath = UriAdapter.getPathFromGalleryUri(getApplicationContext(), groupPictureUri);
        try {
            profileImageStream = getContentResolver().openInputStream(groupPictureUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        groupPhotoBitmap = BitmapFactory.decodeStream(profileImageStream);
        getGroupPhotoBitmapOrjinal = groupPhotoBitmap;
        groupPhotoBitmap = BitmapConversion.getRoundedShape(groupPhotoBitmap, 600, 600, imageRealPath);

        groupPictureImgv.setImageBitmap(groupPhotoBitmap);
    }

}