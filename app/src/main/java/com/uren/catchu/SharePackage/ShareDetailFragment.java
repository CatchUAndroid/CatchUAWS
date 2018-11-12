package com.uren.catchu.SharePackage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.arsy.maps_library.MapRipple;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.uren.catchu.Adapters.LocationTrackerAdapter;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.GeneralUtils.ViewPagerUtils;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.GroupManagementFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.SelectFriendFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.FollowingFragment;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.Adapters.ShareItemsDisplayAdapter;
import com.uren.catchu.SharePackage.GalleryPicker.GalleryGridListAdapter;
import com.uren.catchu.SharePackage.GalleryPicker.Interfaces.LocationCallback;
import com.uren.catchu.SharePackage.Utils.CheckShareItems;
import com.uren.catchu.Singleton.SelectedFriendList;
import com.uren.catchu.Singleton.SelectedGroupList;
import com.uren.catchu.Singleton.Share.ShareItems;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.LOCATION_SERVICE;
import static com.uren.catchu.Constants.NumericConstants.CODE_FRIEND_SHARED;
import static com.uren.catchu.Constants.NumericConstants.CODE_GROUP_SHARED;
import static com.uren.catchu.Constants.NumericConstants.CODE_JUSTME_SHARED;
import static com.uren.catchu.Constants.NumericConstants.CODE_PUBLIC_SHARED;
import static com.uren.catchu.Constants.NumericConstants.REQUEST_CODE_ENABLE_LOCATION;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.GROUP_OP_CHOOSE_TYPE;

@SuppressLint("ValidFragment")
public class ShareDetailFragment extends BaseFragment implements OnMapReadyCallback {

    View mView;

    @BindView(R.id.publicShareImgv)
    ImageView publicShareImgv;
    @BindView(R.id.friendShareImgv)
    ImageView friendShareImgv;
    @BindView(R.id.groupsShareImgv)
    ImageView groupsShareImgv;
    @BindView(R.id.justShareMeImgv)
    ImageView justShareMeImgv;

    @BindView(R.id.publicShareTv)
    TextView publicShareTv;
    @BindView(R.id.friendShareTv)
    TextView friendShareTv;
    @BindView(R.id.groupsShareTv)
    TextView groupsShareTv;
    @BindView(R.id.justMeShareTv)
    TextView justMeShareTv;
    @BindView(R.id.selFriCntTv)
    TextView selFriCntTv;
    @BindView(R.id.groupnameTv)
    TextView groupnameTv;
    @BindView(R.id.mediaWarningMsg)
    TextView mediaWarningMsg;

    @BindView(R.id.map)
    MapView mapView;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.SliderDots)
    LinearLayout SliderDots;
    @BindView(R.id.mediaLayout)
    LinearLayout mediaLayout;

    @BindView(R.id.commonToolbarbackImgv)
    ImageView commonToolbarbackImgv;
    @BindView(R.id.commonToolbarNextImgv)
    ImageView commonToolbarNextImgv;
    @BindView(R.id.toolbarTitleTv)
    TextView toolbarTitleTv;

    PermissionModule permissionModule;
    GoogleMap mMap;
    LocationTrackerAdapter locationTrackObj;
    MapRipple mapRipple;
    LocationManager locationManager;
    CheckShareItems checkShareItems;

    int selectedItem = 0;
    private int selectedFriendCount = 0;
    private int selectedGroupCount = 0;

    private static final int selectedColorCode = R.color.background;
    private static final int unSelectedColorCode = R.color.white;

    ReturnCallback returnCallback;

    public ShareDetailFragment(ReturnCallback returnCallback) {
        this.returnCallback = returnCallback;
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
        if (mView == null) {
            mView = inflater.inflate(R.layout.activity_share_detail, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            addListeners();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setMapView();
    }

    public void initVariables() {
        permissionModule = new PermissionModule(getContext());
        initLocationTracker();
        checkCanGetLocation();
        setDefaultSelectedItem();
        setViewPager();
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        checkShareItems = new CheckShareItems(getContext());
        toolbarTitleTv.setText(getResources().getString(R.string.POST_DETAIL));
        commonToolbarNextImgv.setVisibility(View.VISIBLE);
    }

    public void setMapView() {
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    private void addListeners() {
        commonToolbarbackImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        commonToolbarNextImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkShareItems.isLocationLoaded()) {
                    if (!permissionModule.checkAccessFineLocationPermission()) {
                        giveInfoLocationPermission();
                    } else
                        DialogBoxUtil.showInfoDialogBox(getContext(), checkShareItems.getErrMessage(), null, new InfoDialogBoxCallback() {
                            @Override
                            public void okClick() {
                            }
                        });
                    return;
                }

                if (mFragmentNavigation != null) {
                    mFragmentNavigation.pushFragment(new AddMessageToPostFragment(checkShareItems, new ReturnCallback() {
                        @Override
                        public void onReturn(Object object) {
                            getActivity().onBackPressed();
                            returnCallback.onReturn(null);
                        }
                    }), ANIMATE_RIGHT_TO_LEFT);
                }
            }
        });


        publicShareImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AnimationUtil.setShareItemAnimation(publicShareLayout);
                publicShareImgv.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                selectedItem = CODE_PUBLIC_SHARED;
                ShareItems.getInstance().setSelectedShareType(selectedItem);
                manageSelectedItem();
            }
        });

        friendShareImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendShareImgv.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                selectedItem = CODE_FRIEND_SHARED;
                ShareItems.getInstance().setSelectedShareType(selectedItem);
                manageSelectedItem();
                startSelectFriendFragment();
            }
        });

        groupsShareImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupsShareImgv.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                selectedItem = CODE_GROUP_SHARED;
                ShareItems.getInstance().setSelectedShareType(selectedItem);
                manageSelectedItem();
                startGroupManagementFragment();
            }
        });

        justShareMeImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                justShareMeImgv.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                selectedItem = CODE_JUSTME_SHARED;
                ShareItems.getInstance().setSelectedShareType(selectedItem);
                manageSelectedItem();
            }
        });
    }

    private void startGroupManagementFragment() {

        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new GroupManagementFragment(GROUP_OP_CHOOSE_TYPE,
                            new ReturnCallback() {
                                @Override
                                public void onReturn(Object object) {
                                    selectedGroupCount = SelectedGroupList.getInstance().getSize();
                                    if (selectedGroupCount == 0) {
                                        publicShareImgv.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                                        selectedItem = CODE_PUBLIC_SHARED;
                                        ShareItems.getInstance().setSelectedShareType(selectedItem);
                                        setViewColor(publicShareImgv, publicShareTv, selectedColorCode);
                                        setViewColor(groupsShareImgv, groupsShareTv, unSelectedColorCode);
                                        groupnameTv.setVisibility(View.GONE);
                                    } else if (selectedGroupCount == 1) {
                                        groupnameTv.setVisibility(View.VISIBLE);
                                        groupnameTv.setText(SelectedGroupList.getInstance().getGroupRequestResult().getResultArray().get(0).getName());
                                    }
                                }
                            }),
                    ANIMATE_RIGHT_TO_LEFT);
        }
    }

    private void startSelectFriendFragment() {

        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new SelectFriendFragment(null, null,
                    ShareDetailFragment.class.getName(),
                    new ReturnCallback() {
                        @Override
                        public void onReturn(Object object) {
                            selectedFriendCount = SelectedFriendList.getInstance().getSize();
                            selFriCntTv.setText(Integer.toString(selectedFriendCount));
                            if (selectedFriendCount > 0)
                                selFriCntTv.setVisibility(View.VISIBLE);
                            else {
                                selFriCntTv.setVisibility(View.GONE);
                                publicShareImgv.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                                selectedItem = CODE_PUBLIC_SHARED;
                                ShareItems.getInstance().setSelectedShareType(selectedItem);
                                setViewColor(publicShareImgv, publicShareTv, selectedColorCode);
                                setViewColor(friendShareImgv, friendShareTv, unSelectedColorCode);
                            }
                        }
                    }), ANIMATE_RIGHT_TO_LEFT);
        }
    }

    public void manageSelectedItem() {
        switch (selectedItem) {
            case CODE_PUBLIC_SHARED:
                setViewColor(publicShareImgv, publicShareTv, selectedColorCode);
                setViewColor(friendShareImgv, friendShareTv, unSelectedColorCode);
                setViewColor(groupsShareImgv, groupsShareTv, unSelectedColorCode);
                setViewColor(justShareMeImgv, justMeShareTv, unSelectedColorCode);
                selFriCntTv.setVisibility(View.GONE);
                groupnameTv.setVisibility(View.GONE);
                selectedFriendCount = 0;
                selectedGroupCount = 0;
                break;
            case CODE_FRIEND_SHARED:
                setViewColor(publicShareImgv, publicShareTv, unSelectedColorCode);
                setViewColor(friendShareImgv, friendShareTv, selectedColorCode);
                setViewColor(groupsShareImgv, groupsShareTv, unSelectedColorCode);
                setViewColor(justShareMeImgv, justMeShareTv, unSelectedColorCode);
                groupnameTv.setVisibility(View.GONE);
                selectedGroupCount = 0;
                break;
            case CODE_GROUP_SHARED:
                setViewColor(publicShareImgv, publicShareTv, unSelectedColorCode);
                setViewColor(friendShareImgv, friendShareTv, unSelectedColorCode);
                setViewColor(groupsShareImgv, groupsShareTv, selectedColorCode);
                setViewColor(justShareMeImgv, justMeShareTv, unSelectedColorCode);
                selFriCntTv.setVisibility(View.GONE);
                selectedFriendCount = 0;
                break;
            case CODE_JUSTME_SHARED:
                setViewColor(publicShareImgv, publicShareTv, unSelectedColorCode);
                setViewColor(friendShareImgv, friendShareTv, unSelectedColorCode);
                setViewColor(groupsShareImgv, groupsShareTv, unSelectedColorCode);
                setViewColor(justShareMeImgv, justMeShareTv, selectedColorCode);
                selFriCntTv.setVisibility(View.GONE);
                groupnameTv.setVisibility(View.GONE);
                selectedFriendCount = 0;
                selectedGroupCount = 0;
                break;
            default:
                break;
        }
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

        if (resultCode == Activity.RESULT_OK) {

        }

        if (requestCode == REQUEST_CODE_ENABLE_LOCATION) {
            if (locationTrackObj.canGetLocation())
                initializeMap(mMap);
        }
    }

    private void giveInfoLocationPermission() {
        DialogBoxUtil.showInfoDialogBox(getContext(), getResources().getString(R.string.locationIsEmpty), null, new InfoDialogBoxCallback() {
            @Override
            public void okClick() {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        permissionModule.PERMISSION_ACCESS_FINE_LOCATION);
            }
        });
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

    private void setViewPager() {
        if (ShareItems.getInstance() != null) {
            if (ShareItems.getInstance().getTotalMediaCount() != 0) {
                viewPager.setAdapter(new ShareItemsDisplayAdapter((Activity) getContext(), getContext()));
                viewPager.setOffscreenPageLimit(ShareItems.getInstance().getTotalMediaCount());
                ViewPagerUtils.setSliderDotsPanelWithTextView(ShareItems.getInstance().getTotalMediaCount(), R.color.Orange,
                        R.color.White, getContext(), viewPager, SliderDots);
            } else {
                mediaLayout.setVisibility(View.GONE);
                mediaWarningMsg.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setShareItemsLocation(Location location) {
        catchu.model.Location tempLoc = new catchu.model.Location();
        tempLoc.setLongitude(BigDecimal.valueOf(location.getLongitude()));
        tempLoc.setLatitude(BigDecimal.valueOf(location.getLatitude()));
        ShareItems.getInstance().getPost().setLocation(tempLoc);
    }

    private void setDefaultSelectedItem() {
        selectedItem = CODE_PUBLIC_SHARED;
        ShareItems.getInstance().setSelectedShareType(selectedItem);
        setViewColor(publicShareImgv, publicShareTv, selectedColorCode);
    }

    public void setViewColor(ImageView imageView, TextView textView, int colorCode) {
        imageView.setColorFilter(ContextCompat.getColor(getContext(), colorCode), android.graphics.PorterDuff.Mode.SRC_IN);
        textView.setTextColor(getResources().getColor(colorCode, null));
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
            showSettingsAlert();
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle(getResources().getString(R.string.gpsSettings));
        alertDialog.setMessage(getResources().getString(R.string.gpsSettingMessage));
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(getResources().getString(R.string.settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CODE_ENABLE_LOCATION);
            }
        });
        alertDialog.show();
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
