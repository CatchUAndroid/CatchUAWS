package com.uren.catchu.Permissions;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class PermissionModule {

    public static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 1001;
    public static final int PERMISSION_CAMERA = 1002;
    public static final int PERMISSION_ACCESS_FINE_LOCATION = 1003;
    public static final int PERMISSION_READ_CONTACTS = 1007;

    private Context mContext;

    public PermissionModule(Context context) {
        mContext = context;
    }

    //camera permission =================================================
    public boolean checkCameraPermission(){

        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    //WriteExternalStorage permission =================================================
    public boolean checkWriteExternalStoragePermission(){

        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    //RecordAudio permission =================================================
    public boolean checkRecordAudioPermission(){

        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    //AccessFineLocation permission =================================================
    public boolean checkAccessFineLocationPermission(){

        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    //READ_PHONE_STATE permission =================================================
    public boolean checkReadPhoneStatePermission(){

        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    //READ_PHONE_NUMBERS permission =================================================
    public boolean checkReadPhoneNumbersPermission(){

        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED;
    }

    //READ_CONTACTS permission =================================================
    public boolean checkReadContactsPermission(){

        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }
}
