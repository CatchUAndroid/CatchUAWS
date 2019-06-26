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

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            return false;
        }else
            return true;
    }

    //WriteExternalStorage permission =================================================
    public boolean checkWriteExternalStoragePermission(){

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
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

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return false;
        }else
            return true;
    }

    //READ_PHONE_STATE permission =================================================
    public boolean checkReadPhoneStatePermission(){

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED) {
            return false;
        }else
            return true;
    }

    //READ_PHONE_NUMBERS permission =================================================
    public boolean checkReadPhoneNumbersPermission(){

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_NUMBERS) !=
                PackageManager.PERMISSION_GRANTED) {
            return false;
        }else
            return true;
    }

    //READ_CONTACTS permission =================================================
    public boolean checkReadContactsPermission(){

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS) !=
                PackageManager.PERMISSION_GRANTED) {
            return false;
        }else
            return true;
    }
}
