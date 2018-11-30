package com.uren.catchu.MainPackage.MainFragments.Share.SubFragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;
import com.uren.catchu.GeneralUtils.IntentUtil.IntentSelectUtil;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Share.Adapters.ColorPaletteAdapter;
import com.uren.catchu.MainPackage.MainFragments.Share.Interfaces.BrushCompleteCallback;
import com.uren.catchu.ModelViews.PaintView;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.uren.catchu.Constants.StringConstants.CAMERA_TEXT;
import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;

public class ARTestFragment extends BaseFragment {

    View mView;

    PermissionModule permissionModule;
    Session mSession;
    boolean mUserRequestedInstall = true;

    private static final int ACTIVITY_REQUEST_CODE_OPEN_CAMERA = 85;

    public ARTestFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_group_management, container, false);
        ButterKnife.bind(this, mView);
        permissionModule = new PermissionModule(getContext());
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        maybeEnableArButton();
        checkCameraPermission();
    }

    void maybeEnableArButton() {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(getContext());
        if (availability.isTransient()) {
            // Re-query at 5Hz while compatibility is checked in the background.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    maybeEnableArButton();
                }
            }, 200);
        }
        if (availability.isSupported()) {
            Toast.makeText(getContext(), "availability is suppoted", Toast.LENGTH_SHORT);
        } else {
            Toast.makeText(getContext(), "availability is not suppoted", Toast.LENGTH_SHORT);
        }
    }

    public void checkCameraPermission(){
        if (permissionModule.checkCameraPermission()) {
            checkARCOREIsInstalled();
        } else
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    permissionModule.PERMISSION_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == permissionModule.PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkARCOREIsInstalled();
            }
        }
    }

    public void checkARCOREIsInstalled(){
        // Make sure ARCore is installed and up to date.
        try {
            if (mSession == null) {
                switch (ArCoreApk.getInstance().requestInstall((Activity) getContext(), mUserRequestedInstall)) {
                    case INSTALLED:
                        // Success, create the AR session.
                        mSession = new Session(getContext());
                        break;
                    case INSTALL_REQUESTED:
                        // Ensures next invocation of requestInstall() will either return
                        // INSTALLED or throw an exception.

                        mUserRequestedInstall = false;
                        return;
                }
            }
        } catch (UnavailableUserDeclinedInstallationException e) {
            // Display an appropriate message to the user and return gracefully.
            Toast.makeText(getContext(), "TODO: handle exception " + e, Toast.LENGTH_LONG).show();
            return;
        } catch (UnavailableArcoreNotInstalledException e) {
            e.printStackTrace();
        } catch (UnavailableDeviceNotCompatibleException e) {
            e.printStackTrace();
        } catch (UnavailableSdkTooOldException e) {
            e.printStackTrace();
        } catch (UnavailableApkTooOldException e) {
            e.printStackTrace();
        }
    }

}