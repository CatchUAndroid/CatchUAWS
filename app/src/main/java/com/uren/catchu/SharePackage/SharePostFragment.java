package com.uren.catchu.SharePackage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.arsy.maps_library.MapRipple;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.uren.catchu.Adapters.LocationTrackerAdapter;
import com.uren.catchu.Adapters.SpecialSelectTabAdapter;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.KeyboardUtils;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.Operations.SettingOperation;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.GalleryPicker.GalleryPickerFrag;
import com.uren.catchu.SharePackage.GalleryPicker.Interfaces.LocationCallback;
import com.uren.catchu.SharePackage.VideoPicker.Utils.VideoFileListForDelete;
import com.uren.catchu.SharePackage.VideoPicker.fragment.VideoPickerFrag;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.Share.ShareItems;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.UserProfile;

import static android.content.Context.LOCATION_SERVICE;
import static com.uren.catchu.Constants.NumericConstants.REQUEST_CODE_ENABLE_LOCATION;

public class SharePostFragment extends BaseFragment implements OnMapReadyCallback {

    View view;

    @BindView(R.id.ShareItemsDescTv)
    TextView ShareItemsDescTv;
    @BindView(R.id.profilePicImgView)
    ImageView profilePicImgView;
    @BindView(R.id.shortUserNameTv)
    TextView shortUserNameTv;

    @BindView(R.id.photoSelectImgv)
    ImageView photoSelectImgv;
    @BindView(R.id.videoSelectImgv)
    ImageView videoSelectImgv;
    @BindView(R.id.textSelectImgv)
    ImageView textSelectImgv;
    @BindView(R.id.showMapImgv)
    ImageView showMapImgv;

    @BindView(R.id.shareMsgEditText)
    EditText shareMsgEditText;

    @BindView(R.id.publicImgv)
    ImageView publicImgv;
    @BindView(R.id.allFollowersImgv)
    ImageView allFollowersImgv;
    @BindView(R.id.specialImgv)
    ImageView specialImgv;
    @BindView(R.id.groupsImgv)
    ImageView groupsImgv;
    @BindView(R.id.justMeImgv)
    ImageView justMeImgv;

    @BindView(R.id.cancelButton)
    Button cancelButton;
    @BindView(R.id.shareButton)
    Button shareButton;

    @BindView(R.id.mapLayout)
    RelativeLayout mapLayout;
    @BindView(R.id.shareMainLayout)
    RelativeLayout shareMainLayout;
   /* @BindView(R.id.scrollView)
    ScrollView scrollView;*/

    @BindView(R.id.map)
    MapView mapView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    PermissionModule permissionModule;
    GoogleMap mMap;
    LocationTrackerAdapter locationTrackObj;
    MapRipple mapRipple;
    LocationManager locationManager;

    private static final int CODE_ENABLE_LOCATION = 407;

    boolean edittextFocused;

    public SharePostFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        NextActivity.bottomTabLayout.setVisibility(View.GONE);
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        view = inflater.inflate(R.layout.fragment_share_post, container, false);
        ButterKnife.bind(this, view);
        initializeItems();
        addListeners();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //super.onViewCreated(view, savedInstanceState);
        setMapView();
    }

    private void initializeItems() {
        permissionModule = new PermissionModule(getContext());
        ShareItems.setInstance(null);
        getUserInfo();
        initLocationTracker();
        checkCanGetLocation();
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addListeners() {

        /*shareMsgEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mapLayout.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });*/

        /*shareMsgEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittextFocused = true;
            }
        });*/

        shareMsgEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    edittextFocused = true;
                    //mapLayout.setVisibility(View.GONE);
                } else {
                    edittextFocused = false;
                    //mapLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        KeyboardUtils.addKeyboardToggleListener((Activity) getContext(), new KeyboardUtils.SoftKeyboardToggleListener()
        {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible, int heightDiff, float dp)
            {
                if (isVisible) {
                    //mapLayout.setVisibility(View.GONE);

                    mapLayout.animate()
                            .translationY(mapLayout.getHeight())
                            .alpha(0.0f)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    //super.onAnimationEnd(animation);
                                    mapLayout.setVisibility(View.GONE);
                                }
                            });
                }else
                    mapLayout.setVisibility(View.VISIBLE);
                Log.d("keyboard", "keyboard visible: "+isVisible);
            }
        });

    }

    private void getUserInfo() {

        if (AccountHolderInfo.getInstance() != null && AccountHolderInfo.getInstance().getUser() != null &&
                AccountHolderInfo.getInstance().getUser().getUserInfo() != null)
            setToolbarInfo(AccountHolderInfo.getInstance().getUser().getUserInfo().getProfilePhotoUrl(),
                    AccountHolderInfo.getInstance().getUser().getUserInfo().getName());
        else {
            AccountHolderInfo.getToken(new TokenCallback() {
                @Override
                public void onTokenTaken(String token) {
                    getProfileDetail(token);
                }
            });
        }
    }

    private void getProfileDetail(String token) {

        UserDetail loadUserDetail = new UserDetail(new OnEventListener<UserProfile>() {

            @Override
            public void onSuccess(UserProfile up) {
                progressBar.setVisibility(View.GONE);
                if (up != null) {
                    setToolbarInfo(up.getUserInfo().getProfilePhotoUrl(),
                            up.getUserInfo().getName());
                }
            }

            @Override
            public void onFailure(Exception e) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onTaskContinue() {
                progressBar.setVisibility(View.VISIBLE);
            }
        }, AccountHolderInfo.getUserID(), AccountHolderInfo.getUserID(), token);

        loadUserDetail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setToolbarInfo(String url, String name) {
        UserDataUtil.setProfilePicture(getContext(), url, name, shortUserNameTv, profilePicImgView);
        ShareItemsDescTv.setText(getResources().getString(R.string.WHO_WILL_YOU_SHARE));
    }

    public void setMapView() {
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionModule.checkAccessFineLocationPermission())
                initializeMap(mMap);
            else
                requestPermissions(
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        permissionModule.PERMISSION_ACCESS_FINE_LOCATION);
        } else
            initializeMap(mMap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == permissionModule.PERMISSION_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeMap(mMap);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CODE_ENABLE_LOCATION) {
            if (locationTrackObj.canGetLocation())
                initializeMap(mMap);
        }
    }

    private void initializeMap(final GoogleMap mMap) {
        if (mMap != null) {
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);

            if (permissionModule.checkAccessFineLocationPermission())
                mMap.setMyLocationEnabled(true);

            Location location = locationTrackObj.getLocation();
            if (location != null) {
                setShareItemsLocation(location);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mapRipple = new MapRipple(mMap, latLng, getContext());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new
                        LatLng(location.getLatitude(),
                        location.getLongitude()), 17));
            }
        }
    }

    public void setShareItemsLocation(Location location) {
        catchu.model.Location tempLoc = new catchu.model.Location();
        tempLoc.setLongitude(BigDecimal.valueOf(location.getLongitude()));
        tempLoc.setLatitude(BigDecimal.valueOf(location.getLatitude()));
        ShareItems.getInstance().getPost().setLocation(tempLoc);
    }

    private void initLocationTracker() {
        locationTrackObj = new LocationTrackerAdapter(getContext(), new LocationCallback() {
            @Override
            public void onLocationChanged(Location location) {
                catchu.model.Location locationModel = new catchu.model.Location();
                locationModel.setLatitude(BigDecimal.valueOf(location.getLatitude()));
                locationModel.setLongitude(BigDecimal.valueOf(location.getLongitude()));
                ShareItems.getInstance().getPost().setLocation(locationModel);
            }
        });
    }

    private void checkCanGetLocation() {
        if (locationTrackObj != null && !locationTrackObj.canGetLocation())
            DialogBoxUtil.showDialogWithJustPositiveButton(getContext(), getResources().getString(R.string.gpsSettings),
                    getResources().getString(R.string.gpsSettingMessage),
                    getResources().getString(R.string.settings), new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), CODE_ENABLE_LOCATION);
                        }
                    });
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


}
