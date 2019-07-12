package com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.arsy.maps_library.MapRipple;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.uren.catchu.Adapters.LocationTrackerAdapter;
import com.uren.catchu.GeneralUtils.BitmapConversion;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Share.Interfaces.LocationCallback;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.Post;

@SuppressLint("ValidFragment")
public class SingleMapDetailFragment extends BaseFragment implements OnMapReadyCallback {

    @BindView(R.id.mapView)
    MapView mapView;
    @BindView(R.id.btnCloseMap)
    ImageView btnCloseMap;

    View view;
    GoogleMap mMap;
    Post post;
    ImageView imgProfilePic;
    PermissionModule permissionModule;
    LocationTrackerAdapter locationTrackObj;
    MapRipple mapRipple;

    public SingleMapDetailFragment(Post post, ImageView imgProfilePic) {
        this.post = post;
        this.imgProfilePic = imgProfilePic;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        try {
            Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_single_map_detail, container, false);
                ButterKnife.bind(this, view);
                initializeItems();
                addListeners();
                setMapView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initializeItems(){
        btnCloseMap.setBackground(ShapeUtil.getShape(Objects.requireNonNull(getContext()).getResources().getColor(R.color.transparentBlack, null),
                0, GradientDrawable.OVAL, 50, 0));
    }

    private void addListeners(){
        btnCloseMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        LatLng latLng = new LatLng(post.getLocation().getLatitude().doubleValue(),
                post.getLocation().getLongitude().doubleValue());

        MarkerOptions options = new MarkerOptions().position(latLng);
        Bitmap bitmap = BitmapConversion.createUserMapBitmap(getContext(), imgProfilePic);
        //Bitmap bitmap = ((BitmapDrawable)imgProfilePic.getDrawable()).getBitmap();

        if (bitmap != null) {
            options.title(post.getUser().getName());
            options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
            options.anchor(0.5f, 0.907f);
            mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
        setMyLocation();
    }

    public void setMapView() {
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    private void setMyLocation() {
        permissionModule = new PermissionModule(getContext());
        initLocationTracker();
        checkCanGetLocation();
    }

    private void initLocationTracker() {
        locationTrackObj = new LocationTrackerAdapter(getContext(), new LocationCallback() {
            @Override
            public void onLocationChanged(Location location) {
                showMyLocation();
            }
        });
    }

    private void showMyLocation() {

        Location location = locationTrackObj.getLocation();
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mapRipple = new MapRipple(mMap, latLng, Objects.requireNonNull(getContext()));
        }
    }

    private void checkCanGetLocation() {
        if (!locationTrackObj.canGetLocation())
            DialogBoxUtil.showSettingsAlert((Activity) getContext());
        else {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionModule.checkAccessFineLocationPermission()) {
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationTrackObj.removeUpdates();
    }
}
