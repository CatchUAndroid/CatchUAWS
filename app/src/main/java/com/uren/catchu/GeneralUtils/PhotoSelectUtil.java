package com.uren.catchu.GeneralUtils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.uren.catchu.GroupPackage.AddGroupActivity;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class PhotoSelectUtil extends Activity {

    Context context;
    int width;
    int heigth;
    ImageView imageView;
    Activity activity;

    PermissionModule permissionModule;
    public int photoChoosenType;

    private static final int adapterCameraSelected = 0;
    private static final int adapterGallerySelected = 1;

    private Bitmap photoBitmap = null;
    private Bitmap photoBitmapOrjinal = null;
    private Uri groupPictureUri = null;
    private String imageRealPath;

    public PhotoSelectUtil(Context context, PermissionModule permissionModule,Activity activity, int width, int height, ImageView imageView) {
        this.context = context;
        this.activity = activity;
        this.width = width;
        this.heigth = height;
        this.imageView = imageView;
        this.permissionModule = permissionModule;
        startChooseImageProc();
    }


    private void startChooseImageProc() {

        Log.i("Info", "startChooseImageProc++++++++++++++++++++++++++++++++");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
        adapter.add("  " + context.getResources().getString(R.string.openCamera));
        adapter.add("  " + context.getResources().getString(R.string.openGallery));
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(context.getResources().getString(R.string.chooseProfilePhoto));

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                if (item == adapterCameraSelected) {

                    photoChoosenType = adapterCameraSelected;
                    startCameraProcess();

                } else if (item == adapterGallerySelected) {

                    photoChoosenType = adapterGallerySelected;
                    startGalleryProcess();

                } else
                    CommonUtils.showToast(context, context.getResources().getString(R.string.technicalError));
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void startCameraProcess() {

        if (!CommonUtils.checkCameraHardware(context)) {
            CommonUtils.showToast(context, context.getResources().getString(R.string.deviceHasNoCamera));
            return;
        }

        if (!permissionModule.checkWriteExternalStoragePermission())
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionModule.getWriteExternalStoragePermissionCode());
        else
            checkCameraPermission();
    }

    public void checkCameraPermission() {

        if (!permissionModule.checkCameraPermission()) {
            requestPermissions( new String[]{Manifest.permission.CAMERA}, permissionModule.getCameraPermissionCode());
        }
        else {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(intent, permissionModule.getCameraPermissionCode());
        }
    }

    private void startGalleryProcess() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                context.getResources().getString(R.string.selectPicture)), permissionModule.getImageGalleryPermission());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == permissionModule.getCameraPermissionCode() ||
                    requestCode == permissionModule.getImageGalleryPermission())
                manageProfilePicChoosen(data);
            else
                CommonUtils.showToastLong(context, getResources().getString(R.string.technicalError) + requestCode);
        } else
            CommonUtils.showToastLong(context, getResources().getString(R.string.technicalError) + requestCode + resultCode);
    }

    private void manageProfilePicChoosen(Intent data) {

        Log.i("Info", "manageProfilePicChoosen++++++++++++++++++++++++++++++++");

        if (photoChoosenType == adapterCameraSelected) {

            photoBitmap = (Bitmap) data.getExtras().get("data");
            photoBitmapOrjinal = photoBitmap;
            groupPictureUri = UriAdapter.getImageUri(getApplicationContext(), photoBitmap);
            imageRealPath = UriAdapter.getRealPathFromCameraURI(groupPictureUri, this);
            photoBitmap = BitmapConversion.getRoundedShape(photoBitmap, width, heigth, imageRealPath);
            photoBitmap = BitmapConversion.getBitmapOriginRotate(photoBitmap, imageRealPath);

        } else if (photoChoosenType == adapterGallerySelected) {

            InputStream profileImageStream = null;
            groupPictureUri = data.getData();
            imageRealPath = UriAdapter.getPathFromGalleryUri(getApplicationContext(), groupPictureUri);
            try {
                profileImageStream = getContentResolver().openInputStream(groupPictureUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            photoBitmap = BitmapFactory.decodeStream(profileImageStream);
            photoBitmapOrjinal = photoBitmap;
            photoBitmap = BitmapConversion.getRoundedShape(photoBitmap, width, heigth, imageRealPath);
        }

        imageView.setImageBitmap(photoBitmap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.i("Info", "onRequestPermissionsResult+++++++++++++++++++++++++++++++++++++");

        if (requestCode == permissionModule.getWriteExternalStoragePermissionCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkCameraPermission();
            }
        } else if (requestCode == permissionModule.getCameraPermissionCode()) {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(intent, permissionModule.getCameraPermissionCode());
        } else
            CommonUtils.showToast(this, getResources().getString(R.string.technicalError) + requestCode);
    }

    public Bitmap getGroupPhotoBitmap() {
        return photoBitmap;
    }

    public Bitmap getGetGroupPhotoBitmapOrjinal() {
        return photoBitmapOrjinal;
    }

    public Uri getGroupPictureUri() {
        return groupPictureUri;
    }

    public String getImageRealPath() {
        return imageRealPath;
    }
}
