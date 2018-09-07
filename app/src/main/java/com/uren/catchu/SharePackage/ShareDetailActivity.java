package com.uren.catchu.SharePackage;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.arsy.maps_library.MapRipple;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.uren.catchu.Adapters.LocationTrackerAdapter;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.ShareRequestProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GroupPackage.SelectFriendToGroupActivity;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.ShareItems;

import catchu.model.ShareRequest;

public class ShareDetailActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    PermissionModule permissionModule;
    private MapRipple mapRipple;
    SupportMapFragment mapFragment;
    LocationManager locationManager;
    private LocationTrackerAdapter locationTrackObj;

    private ImageView publicShareImgv;
    private ImageView friendShareImgv;
    private ImageView groupsShareImgv;
    private ImageView justShareMeImgv;

    private TextView publicShareTv;
    private TextView friendShareTv;
    private TextView groupsShareTv;
    private TextView justShareMeTv;

    private static final int CODE_PUBLIC_SHARED = 0;
    private static final int CODE_FRIEND_SHARED = 1;
    private static final int CODE_GROUP_SHARED = 2;
    private static final int CODE_JUSTME_SHARED = 3;

    private int selectedItem = 0;

    private static final int selectedColorCode = R.color.background;
    private static final int unSelectedColorCode = R.color.white;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_detail);

        permissionModule = new PermissionModule(ShareDetailActivity.this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initVariables();
        checkCanGetLocation();
        addListeners();
        setDefaultSelectedItem();

        // TODO: 7.09.2018 - animasyondan sonra renklendirmeye bakalim 
    }

    private void setDefaultSelectedItem() {
        selectedItem = CODE_PUBLIC_SHARED;
        setViewColor(publicShareImgv, publicShareTv, selectedColorCode);
    }

    private void initVariables() {
        publicShareImgv = findViewById(R.id.publicShareImgv);
        friendShareImgv = findViewById(R.id.friendShareImgv);
        groupsShareImgv = findViewById(R.id.groupsShareImgv);
        justShareMeImgv = findViewById(R.id.justShareMeImgv);
        publicShareTv = findViewById(R.id.publicShareTv);
        friendShareTv = findViewById(R.id.friendShareTv);
        groupsShareTv = findViewById(R.id.groupsShareTv);
        justShareMeTv = findViewById(R.id.justMeShareTv);
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        locationTrackObj = new LocationTrackerAdapter(ShareDetailActivity.this);
        ShareItems.setInstance(null);
        ShareItems.getInstance();
    }

    public void addListeners() {
        publicShareImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publicShareImgv.startAnimation(AnimationUtils.loadAnimation(ShareDetailActivity.this, R.anim.image_click));
                selectedItem = CODE_PUBLIC_SHARED;
                manageSelectedItem();
            }
        });

        friendShareImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendShareImgv.startAnimation(AnimationUtils.loadAnimation(ShareDetailActivity.this, R.anim.image_click));
                selectedItem = CODE_FRIEND_SHARED;
                manageSelectedItem();
                startActivity(new Intent(ShareDetailActivity.this, SelectFriendToGroupActivity.class));

            }
        });

        groupsShareImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupsShareImgv.startAnimation(AnimationUtils.loadAnimation(ShareDetailActivity.this, R.anim.image_click));
                selectedItem = CODE_GROUP_SHARED;
                manageSelectedItem();
            }
        });

        justShareMeImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                justShareMeImgv.startAnimation(AnimationUtils.loadAnimation(ShareDetailActivity.this, R.anim.image_click));
                selectedItem = CODE_JUSTME_SHARED;
                manageSelectedItem();
            }
        });
    }

    public void manageSelectedItem() {
        switch (selectedItem) {
            case CODE_PUBLIC_SHARED:
                setViewColor(publicShareImgv, publicShareTv, selectedColorCode);
                setViewColor(friendShareImgv, friendShareTv, unSelectedColorCode);
                setViewColor(groupsShareImgv, groupsShareTv, unSelectedColorCode);
                setViewColor(justShareMeImgv, justShareMeTv, unSelectedColorCode);

                break;
            case CODE_FRIEND_SHARED:
                setViewColor(publicShareImgv, publicShareTv, unSelectedColorCode);
                setViewColor(friendShareImgv, friendShareTv, selectedColorCode);
                setViewColor(groupsShareImgv, groupsShareTv, unSelectedColorCode);
                setViewColor(justShareMeImgv, justShareMeTv, unSelectedColorCode);

                break;
            case CODE_GROUP_SHARED:
                setViewColor(publicShareImgv, publicShareTv, unSelectedColorCode);
                setViewColor(friendShareImgv, friendShareTv, unSelectedColorCode);
                setViewColor(groupsShareImgv, groupsShareTv, selectedColorCode);
                setViewColor(justShareMeImgv, justShareMeTv, unSelectedColorCode);

                break;
            case CODE_JUSTME_SHARED:
                setViewColor(publicShareImgv, publicShareTv, unSelectedColorCode);
                setViewColor(friendShareImgv, friendShareTv, unSelectedColorCode);
                setViewColor(groupsShareImgv, groupsShareTv, unSelectedColorCode);
                setViewColor(justShareMeImgv, justShareMeTv, selectedColorCode);
                break;

            default:
                CommonUtils.showToast(ShareDetailActivity.this, getResources().getString(R.string.error) +
                        getResources().getString(R.string.technicalError));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == permissionModule.getAccessFineLocationCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeMap(mMap);
            }
        }
    }

    private void initializeMap(GoogleMap mMap) {
        if (mMap != null) {
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);

            if (permissionModule.checkAccessFineLocationPermission())
                mMap.setMyLocationEnabled(true);

            Location location = locationTrackObj.getLocation();

            if (location != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new
                        LatLng(location.getLatitude(),
                        location.getLongitude()), 17));

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mapRipple = new MapRipple(mMap, latLng, ShareDetailActivity.this);
            }
        }
    }

    private void checkCanGetLocation() {

        if (!locationTrackObj.canGetLocation())
            locationTrackObj.showSettingsAlert();
        else {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                permissionModule.checkAccessFineLocationPermission();
        }
    }

    public void sharePost() {
        ShareRequest shareRequest = new ShareRequest();
        shareRequest.setShare(ShareItems.getShare());
        ShareRequestProcess shareRequestProcess = new ShareRequestProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onFailure(Exception e) {

            }

            @Override
            public void onTaskContinue() {

            }
        }, shareRequest);
        shareRequestProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionModule.checkAccessFineLocationPermission())
                initializeMap(mMap);
            else
                ActivityCompat.requestPermissions(ShareDetailActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        permissionModule.getAccessFineLocationCode());
        } else
            initializeMap(mMap);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (locationManager != null)
            locationManager.removeUpdates(locationTrackObj);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationManager != null)
            locationManager.removeUpdates(locationTrackObj);
    }

    public void setViewColor(ImageView imageView, TextView textView, int colorCode){
        imageView.setColorFilter(ContextCompat.getColor(ShareDetailActivity.this, colorCode), android.graphics.PorterDuff.Mode.SRC_IN);
        textView.setTextColor(getResources().getColor(colorCode, null));
    }
}
