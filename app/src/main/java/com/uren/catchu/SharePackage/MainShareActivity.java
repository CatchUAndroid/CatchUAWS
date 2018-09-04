package com.uren.catchu.SharePackage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arsy.maps_library.MapRipple;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.uren.catchu.Adapters.LocationTrackerAdapter;
import com.uren.catchu.Adapters.SpecialSelectTabAdapter;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GroupPackage.AddGroupActivity;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters.NewsPagerAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.NewsList;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.GalleryPicker.GalleryGridListAdapter;
import com.uren.catchu.SharePackage.GalleryPicker.GalleryPickerFrag;

import java.util.List;

import butterknife.BindView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MainShareActivity extends FragmentActivity implements OnMapReadyCallback {

    TabLayout tabLayout;
    ViewPager viewPager;
    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout;

    private GoogleMap mMap;
    PermissionModule permissionModule;

    LocationManager locationManager;
    private LocationTrackerAdapter locationTrackObj;
    private MapRipple mapRipple;
    SupportMapFragment mapFragment;
    ViewGroup.LayoutParams collapsingToolbarLayoutLayoutParams;
    int collapsingLayoutDefaultHeightSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_share);

        permissionModule = new PermissionModule(MainShareActivity.this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initVariables();
        addListeners();
        checkCanGetLocation();
        getCollapsingBarHeight();
        checkWriteStoragePermission();

    }

    private void initVariables() {
        tabLayout = findViewById(R.id.htab_tabs);
        viewPager = findViewById(R.id.htab_viewpager);
        collapsingToolbarLayout = findViewById(R.id.htab_collapse_toolbar);
        appBarLayout = findViewById(R.id.htab_appbar);
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        locationTrackObj = new LocationTrackerAdapter(MainShareActivity.this);
    }

    private void getCollapsingBarHeight() {
        collapsingToolbarLayoutLayoutParams = collapsingToolbarLayout.getLayoutParams();
        collapsingLayoutDefaultHeightSize = collapsingToolbarLayoutLayoutParams.height;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == permissionModule.getAccessFineLocationCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeMap(mMap);
            }
        } else if (requestCode == permissionModule.getWriteExternalStoragePermissionCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpPager();
            } else
                setUpPager();
        }
    }

    private void addListeners() {

        // TODO: 3.09.2018 - Scroll yaptigimizda harita uzerinde current loc yukarda kaliyor.

        /*appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //Log.i("Info", "befOffsetValue:" + Integer.toString(befOffsetValue));
                Log.i("Info", "verticalOffset:" + Integer.toString(verticalOffset));

                ViewGroup.LayoutParams params = appBarLayout.getLayoutParams();

                collapsingToolbarLayoutLayoutParams.height = params.height;
                collapsingToolbarLayout.setLayoutParams(collapsingToolbarLayoutLayoutParams);


            }
        });*/

    }

    public void checkWriteStoragePermission() {

        if (!permissionModule.checkWriteExternalStoragePermission())
            ActivityCompat.requestPermissions(MainShareActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    permissionModule.getWriteExternalStoragePermissionCode());
        else
            setUpPager();
    }

    private void checkCanGetLocation() {

        if (!locationTrackObj.canGetLocation())
            locationTrackObj.showSettingsAlert();
        else {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                permissionModule.checkAccessFineLocationPermission();
        }
    }

    private void setUpPager() {

        SpecialSelectTabAdapter adapter = new SpecialSelectTabAdapter(this.getSupportFragmentManager());

        GalleryPickerFrag galleryPickerFrag = new GalleryPickerFrag();

        NewsList n1 = new NewsList();
        NewsList n2 = new NewsList();
        NewsList n3 = new NewsList();
        NewsList n4 = new NewsList();
        NewsList n5 = new NewsList();

        adapter.addFragment(n1, getResources().getString(R.string.text));
        adapter.addFragment(galleryPickerFrag, getResources().getString(R.string.gallery));
        adapter.addFragment(n2, getResources().getString(R.string.photo));
        adapter.addFragment(n3, getResources().getString(R.string.video));

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionModule.checkAccessFineLocationPermission())
                initializeMap(mMap);
            else
                ActivityCompat.requestPermissions(MainShareActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        permissionModule.getAccessFineLocationCode());
        } else
            initializeMap(mMap);
    }

    private void initializeMap(GoogleMap mMap) {
        if (mMap != null) {
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);

            if (permissionModule.checkAccessFineLocationPermission())
                mMap.setMyLocationEnabled(true);

            Location location = locationTrackObj.getLocation();

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new
                    LatLng(location.getLatitude(),
                    location.getLongitude()), 17));

            LatLng latLng;
            if (location != null)
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
            else
                latLng = new LatLng(0.0, 0.0);

            mapRipple = new MapRipple(mMap, latLng, MainShareActivity.this);
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("Info", "MainShareActivity onActivityResult");
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == permissionModule.getShareGalleryPickerPerm()) {
                GalleryGridListAdapter.manageProfilePicChoosen(data);
            } else
                CommonUtils.showToast(MainShareActivity.this, getResources().getString(R.string.technicalError) + requestCode);
        }
    }
}
