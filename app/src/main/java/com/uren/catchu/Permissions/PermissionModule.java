package com.uren.catchu.Permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;

import static com.uren.catchu.Permissions.PermissionConstants.codeAccessFineLocationPermission;
import static com.uren.catchu.Permissions.PermissionConstants.codeCameraPermission;
import static com.uren.catchu.Permissions.PermissionConstants.codeImageGalleryPermission;
import static com.uren.catchu.Permissions.PermissionConstants.codeRecordAudioPermission;
import static com.uren.catchu.Permissions.PermissionConstants.codeWriteExternalStoragePermission;

public class PermissionModule {

    private Context mContext;

    public PermissionModule(Context context) {
        mContext = context;
    }

    /*public void checkPermissions() {
        ArrayList<String> permissionsNeeded = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!permissionsNeeded.isEmpty()) {
            requestPermission(permissionsNeeded.toArray(new String[permissionsNeeded.size()]));
        }
    }

    private void requestPermission(String[] permissions) {
        ActivityCompat.requestPermissions((Activity) mContext, permissions, 125);
    }*/

    //camera permission =================================================
    public boolean checkCameraPermission(){

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }else
            return true;
    }

    //WriteExternalStorage permission =================================================
    public boolean checkWriteExternalStoragePermission(){

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }else
            return true;
    }

    //RecordAudio permission =================================================
    public boolean checkRecordAudioPermission(){

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }else
            return true;
    }

    //AccessFineLocation permission =================================================
    public boolean checkAccessFineLocationPermission(){

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }else
            return true;
    }

    public int getWriteExternalStoragePermissionCode(){
        return codeWriteExternalStoragePermission;
    }
    public int getAccessFineLocationCode(){
        return codeAccessFineLocationPermission;
    }

    public int getCameraPermissionCode(){
        return codeCameraPermission;
    }

    public int getRecordAudioPermissionCode(){
        return codeRecordAudioPermission;
    }

    public int getImageGalleryPermission(){
        return codeImageGalleryPermission;
    }

}
