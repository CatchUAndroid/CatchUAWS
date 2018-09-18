package com.uren.catchu.SharePackage;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arsy.maps_library.MapRipple;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.uren.catchu.Adapters.LocationTrackerAdapter;
import com.uren.catchu.GeneralUtils.BitmapConversion;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.PhotoChosenCallback;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.GroupPackage.SelectFriendToGroupActivity;
import com.uren.catchu.Interfaces.ServiceCompleteCallback;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.Models.ImageShareItemBox;
import com.uren.catchu.SharePackage.Utils.CheckShareItems;
import com.uren.catchu.SharePackage.Utils.SharePostProcess;
import com.uren.catchu.Singleton.SelectedGroupList;
import com.uren.catchu.Singleton.Share.ShareItems;

import java.math.BigDecimal;


import catchu.model.Media;

import static com.uren.catchu.Constants.NumericConstants.CODE_CAMERA_POSITION;
import static com.uren.catchu.Constants.NumericConstants.CODE_GALLERY_POSITION;
import static com.uren.catchu.Constants.StringConstants.CAMERA_TEXT;
import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_ACTIVITY_NAME;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_SHARE_FRIEND_COUNT;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_SHARE_GROUP_COUNT;

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
    private TextView selFriCntTv;
    private TextView groupnameTv;

    private ImageView textImgv;
    private ImageView deleteTextImgv;
    private ImageView addTextImgv;
    private ImageView galleryImgv;
    private ImageView deleteGalleryImgv;
    private ImageView addGalleryImgv;
    private ImageView videoImgv;
    private ImageView deleteVideoImgv;
    private ImageView addVideoImgv;

    private LinearLayout mapLayout;
    private FrameLayout shareMainLayout;
    private Button shareButton;

    public static final int CODE_PUBLIC_SHARED = 0;
    public static final int CODE_FRIEND_SHARED = 1;
    public static final int CODE_GROUP_SHARED = 2;
    public static final int CODE_JUSTME_SHARED = 3;

    private int selectedItem = 0;
    private int selectedFriendCount = 0;
    private int selectedGroupCount = 0;

    private static final int selectedColorCode = R.color.background;
    private static final int unSelectedColorCode = R.color.white;

    private static final int REQUEST_CODE_FRIEND_SELECTION = 3001;
    private static final int REQUEST_CODE_GROUP_SELECTION = 3002;
    public static final int REQUEST_CODE_ENABLE_LOCATION = 3003;

    private static final int textDefaultImageId = R.drawable.notetext_icon_500;
    private static final int galleryDefaultImageId = R.drawable.gallery_icon_512;
    private static final int videoDefaultImageId = R.drawable.video_icon_96;


    int selectedPosition = -1;
    PhotoSelectUtil photoSelectUtil;
    View noteTextLayout = null;
    CheckShareItems checkShareItems;

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
        setShareItems();
    }

    private void setDefaultSelectedItem() {
        selectedItem = CODE_PUBLIC_SHARED;
        setViewColor(publicShareImgv, publicShareTv, selectedColorCode);
    }

    private void initVariables() {
        mapLayout = findViewById(R.id.mapLayout);
        publicShareImgv = findViewById(R.id.publicShareImgv);
        friendShareImgv = findViewById(R.id.friendShareImgv);
        groupsShareImgv = findViewById(R.id.groupsShareImgv);
        justShareMeImgv = findViewById(R.id.justShareMeImgv);
        publicShareTv = findViewById(R.id.publicShareTv);
        friendShareTv = findViewById(R.id.friendShareTv);
        groupsShareTv = findViewById(R.id.groupsShareTv);
        justShareMeTv = findViewById(R.id.justMeShareTv);
        selFriCntTv = findViewById(R.id.selFriCntTv);
        groupnameTv = findViewById(R.id.groupnameTv);
        textImgv = findViewById(R.id.textImgv);
        deleteTextImgv = findViewById(R.id.deleteTextImgv);
        addTextImgv = findViewById(R.id.addTextImgv);
        galleryImgv = findViewById(R.id.galleryImgv);
        deleteGalleryImgv = findViewById(R.id.deleteGalleryImgv);
        addGalleryImgv = findViewById(R.id.addGalleryImgv);
        videoImgv = findViewById(R.id.videoImgV);
        deleteVideoImgv = findViewById(R.id.deleteVideoImgv);
        addVideoImgv = findViewById(R.id.addVideoImgv);
        shareMainLayout = findViewById(R.id.shareMainLayout);
        shareButton = findViewById(R.id.shareButton);
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        locationTrackObj = new LocationTrackerAdapter(ShareDetailActivity.this);
        checkShareItems = new CheckShareItems(ShareDetailActivity.this);
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
                startSelectFriendActivity();
            }
        });

        groupsShareImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupsShareImgv.startAnimation(AnimationUtils.loadAnimation(ShareDetailActivity.this, R.anim.image_click));
                selectedItem = CODE_GROUP_SHARED;
                manageSelectedItem();
                startSelectGroupActivity();
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

        deleteTextImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTextImgv.setVisibility(View.GONE);
                addTextImgv.setVisibility(View.VISIBLE);
                textImgv.setImageResource(textDefaultImageId);
                ShareItems.getInstance().getPost().setMessage(" ");
            }
        });

        deleteGalleryImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteGalleryImgv.setVisibility(View.GONE);
                addGalleryImgv.setVisibility(View.VISIBLE);
                galleryImgv.setImageResource(galleryDefaultImageId);
                ShareItems.getInstance().clearImageShareItemBox();
            }
        });

        deleteVideoImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteVideoImgv.setVisibility(View.GONE);
                addVideoImgv.setVisibility(View.VISIBLE);
                videoImgv.setImageResource(videoDefaultImageId);
                ShareItems.getInstance().clearVideoShareItemBox();
            }
        });

        galleryImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoChosenManage();
            }
        });

        addGalleryImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoChosenManage();
            }
        });

        textImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNoteText();
            }
        });

        addTextImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNoteText();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkShareItems.isLocationLoaded()) {
                    if (!permissionModule.checkAccessFineLocationPermission()) {
                        giveInfoLocationPermission();
                    } else
                        DialogBoxUtil.showInfoDialogBox(ShareDetailActivity.this, checkShareItems.getErrMessage(), null, new InfoDialogBoxCallback() {
                            @Override
                            public void okClick() {
                            }
                        });
                    return;
                }

                if (!checkShareItems.shareIsPossible()) {
                    DialogBoxUtil.showInfoDialogBox(ShareDetailActivity.this, checkShareItems.getErrMessage(), null, new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {
                        }
                    });
                    return;
                }
                sharePost();
            }
        });
    }

    private void giveInfoLocationPermission() {
        DialogBoxUtil.showInfoDialogBox(ShareDetailActivity.this, getResources().getString(R.string.locationIsEmpty), null, new InfoDialogBoxCallback() {
            @Override
            public void okClick() {
                ActivityCompat.requestPermissions(ShareDetailActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        permissionModule.getAccessFineLocationCode());
            }
        });
    }

    private void startSelectGroupActivity() {
        Intent intent = new Intent(getApplicationContext(), SelectGroupActivity.class);
        intent.putExtra(PUTEXTRA_ACTIVITY_NAME, ShareDetailActivity.class.getSimpleName());
        startActivityForResult(intent, REQUEST_CODE_GROUP_SELECTION);
    }

    private void startSelectFriendActivity() {
        Intent intent = new Intent(getApplicationContext(), SelectFriendToGroupActivity.class);
        intent.putExtra(PUTEXTRA_ACTIVITY_NAME, ShareDetailActivity.class.getSimpleName());
        startActivityForResult(intent, REQUEST_CODE_FRIEND_SELECTION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_FRIEND_SELECTION) {
                selectedFriendCount = (int) data.getSerializableExtra(PUTEXTRA_SHARE_FRIEND_COUNT);
                selFriCntTv.setText(Integer.toString(selectedFriendCount));
                if (selectedFriendCount > 0)
                    selFriCntTv.setVisibility(View.VISIBLE);
                else {
                    selFriCntTv.setVisibility(View.GONE);
                    publicShareImgv.startAnimation(AnimationUtils.loadAnimation(ShareDetailActivity.this, R.anim.image_click));
                    selectedItem = CODE_PUBLIC_SHARED;
                    setViewColor(publicShareImgv, publicShareTv, selectedColorCode);
                    setViewColor(friendShareImgv, friendShareTv, unSelectedColorCode);
                }
            } else if (requestCode == REQUEST_CODE_GROUP_SELECTION) {
                selectedGroupCount = (int) data.getSerializableExtra(PUTEXTRA_SHARE_GROUP_COUNT);
                if (selectedGroupCount == 0) {
                    publicShareImgv.startAnimation(AnimationUtils.loadAnimation(ShareDetailActivity.this, R.anim.image_click));
                    selectedItem = CODE_PUBLIC_SHARED;
                    setViewColor(publicShareImgv, publicShareTv, selectedColorCode);
                    setViewColor(groupsShareImgv, groupsShareTv, unSelectedColorCode);
                    groupnameTv.setVisibility(View.GONE);
                } else if (selectedGroupCount == 1) {
                    groupnameTv.setVisibility(View.VISIBLE);
                    groupnameTv.setText(SelectedGroupList.getInstance().getGroupRequestResult().getResultArray().get(0).getName());
                }
            } else if (requestCode == permissionModule.getImageGalleryPermission()) {
                photoSelectUtil = new PhotoSelectUtil(ShareDetailActivity.this, data, GALLERY_TEXT);
                setGalleryImageView(data.getData());
                fillImageShareItemBox();
            } else if (requestCode == permissionModule.getCameraPermissionCode()) {
                photoSelectUtil = new PhotoSelectUtil(ShareDetailActivity.this, data, CAMERA_TEXT);
                setGalleryImageView(data.getData());
                fillImageShareItemBox();
            }
        }

        if (requestCode == REQUEST_CODE_ENABLE_LOCATION) {
            if (locationTrackObj.canGetLocation())
                initializeMap(mMap);
        }
    }

    public void fillImageShareItemBox() {
        ImageShareItemBox imageShareItemBox = new ImageShareItemBox(photoSelectUtil);
        ShareItems.getInstance().clearImageShareItemBox();
        ShareItems.getInstance().addImageShareItemBox(imageShareItemBox);
    }

    public void setGalleryImageView(Uri uri) {
        Glide.with(ShareDetailActivity.this).load(uri).apply(RequestOptions.circleCropTransform()).into(galleryImgv);
        deleteGalleryImgv.setVisibility(View.VISIBLE);
        addGalleryImgv.setVisibility(View.GONE);
    }

    public void manageSelectedItem() {
        switch (selectedItem) {
            case CODE_PUBLIC_SHARED:
                setViewColor(publicShareImgv, publicShareTv, selectedColorCode);
                setViewColor(friendShareImgv, friendShareTv, unSelectedColorCode);
                setViewColor(groupsShareImgv, groupsShareTv, unSelectedColorCode);
                setViewColor(justShareMeImgv, justShareMeTv, unSelectedColorCode);
                selFriCntTv.setVisibility(View.GONE);
                groupnameTv.setVisibility(View.GONE);
                selectedFriendCount = 0;
                selectedGroupCount = 0;
                break;
            case CODE_FRIEND_SHARED:
                setViewColor(publicShareImgv, publicShareTv, unSelectedColorCode);
                setViewColor(friendShareImgv, friendShareTv, selectedColorCode);
                setViewColor(groupsShareImgv, groupsShareTv, unSelectedColorCode);
                setViewColor(justShareMeImgv, justShareMeTv, unSelectedColorCode);
                groupnameTv.setVisibility(View.GONE);
                selectedGroupCount = 0;
                break;
            case CODE_GROUP_SHARED:
                setViewColor(publicShareImgv, publicShareTv, unSelectedColorCode);
                setViewColor(friendShareImgv, friendShareTv, unSelectedColorCode);
                setViewColor(groupsShareImgv, groupsShareTv, selectedColorCode);
                setViewColor(justShareMeImgv, justShareMeTv, unSelectedColorCode);
                selFriCntTv.setVisibility(View.GONE);
                selectedFriendCount = 0;
                break;
            case CODE_JUSTME_SHARED:
                setViewColor(publicShareImgv, publicShareTv, unSelectedColorCode);
                setViewColor(friendShareImgv, friendShareTv, unSelectedColorCode);
                setViewColor(groupsShareImgv, groupsShareTv, unSelectedColorCode);
                setViewColor(justShareMeImgv, justShareMeTv, selectedColorCode);
                selFriCntTv.setVisibility(View.GONE);
                groupnameTv.setVisibility(View.GONE);
                selectedFriendCount = 0;
                selectedGroupCount = 0;
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
        } else if (requestCode == permissionModule.getWriteExternalStoragePermissionCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (selectedPosition == CODE_GALLERY_POSITION)
                    startGalleryProcess();
                else if (selectedPosition == CODE_CAMERA_POSITION)
                    startCameraProcess();
                else
                    startGalleryProcess();
            }
        } else if (requestCode == permissionModule.getCameraPermissionCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCameraProcess();
            }
        } else
            CommonUtils.showToast(ShareDetailActivity.this, getString(R.string.technicalError) + requestCode);
    }

    private void initializeMap(GoogleMap mMap) {
        if (mMap != null) {
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);

            if (permissionModule.checkAccessFineLocationPermission())
                mMap.setMyLocationEnabled(true);

            Location location = locationTrackObj.getLocation();
            if (location != null) {
                setShareItemsLocation(location);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new
                        LatLng(location.getLatitude(),
                        location.getLongitude()), 17));
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mapRipple = new MapRipple(mMap, latLng, ShareDetailActivity.this);
            }
        }
    }

    public void setShareItemsLocation(Location location) {
        catchu.model.Location tempLoc = new catchu.model.Location();
        tempLoc.setLongitude(BigDecimal.valueOf(location.getLongitude()));
        tempLoc.setLatitude(BigDecimal.valueOf(location.getLatitude()));
        ShareItems.getInstance().getPost().setLocation(tempLoc);
    }

    private void checkCanGetLocation() {
        if (!locationTrackObj.canGetLocation())
            showSettingsAlert();
        else {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                permissionModule.checkAccessFineLocationPermission();
        }
    }

    public void sharePost() {
        new SharePostProcess(ShareDetailActivity.this, selectedItem, new ServiceCompleteCallback() {
            @Override
            public void onSuccess() {
                CommonUtils.showToastLong(ShareDetailActivity.this, "Share is SUCCESSFUL");
            }

            @Override
            public void onFailed(Exception e) {
                DialogBoxUtil.showErrorDialog(ShareDetailActivity.this, e.getMessage(), new InfoDialogBoxCallback() {
                    @Override
                    public void okClick() {
                    }
                });
                e.printStackTrace();
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

    public void setViewColor(ImageView imageView, TextView textView, int colorCode) {
        imageView.setColorFilter(ContextCompat.getColor(ShareDetailActivity.this, colorCode), android.graphics.PorterDuff.Mode.SRC_IN);
        textView.setTextColor(getResources().getColor(colorCode, null));
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShareDetailActivity.this);
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

    private void setShareItems() {
        if (ShareItems.getInstance().getTextBitmap() != null) {
            Glide.with(ShareDetailActivity.this).load(ShareItems.getInstance().getTextBitmap()).apply(RequestOptions.circleCropTransform()).into(textImgv);
            addTextImgv.setVisibility(View.GONE);
            deleteTextImgv.setVisibility(View.VISIBLE);
        } else {
            addTextImgv.setVisibility(View.VISIBLE);
            deleteTextImgv.setVisibility(View.GONE);
        }

        if (ShareItems.getInstance().getImageShareItemBoxes().size() > 0) {
            Uri imageUri = ShareItems.getInstance().getImageShareItemBoxes().get(0).getPhotoSelectUtil().getMediaUri();
            if (imageUri != null) {
                Glide.with(ShareDetailActivity.this).load(imageUri).apply(RequestOptions.circleCropTransform()).into(galleryImgv);
                addGalleryImgv.setVisibility(View.GONE);
                deleteGalleryImgv.setVisibility(View.VISIBLE);
            } else {
                addGalleryImgv.setVisibility(View.VISIBLE);
                deleteGalleryImgv.setVisibility(View.GONE);
            }
        }else {
            addGalleryImgv.setVisibility(View.VISIBLE);
            deleteGalleryImgv.setVisibility(View.GONE);
        }

        if(ShareItems.getInstance().getVideoShareItemBoxes().size() > 0){
            Uri videoUri = ShareItems.getInstance().getVideoShareItemBoxes().get(0).getVideoSelectUtil().getVideoUri();
            Bitmap videoBitmap = ShareItems.getInstance().getVideoShareItemBoxes().get(0).getVideoSelectUtil().getVideoBitmap();
            if (videoUri != null) {
                Glide.with(ShareDetailActivity.this).load(videoBitmap).apply(RequestOptions.circleCropTransform()).into(videoImgv);
                addVideoImgv.setVisibility(View.GONE);
                deleteVideoImgv.setVisibility(View.VISIBLE);
            } else {
                addVideoImgv.setVisibility(View.VISIBLE);
                deleteVideoImgv.setVisibility(View.GONE);
            }
        }else {
            addVideoImgv.setVisibility(View.VISIBLE);
            deleteVideoImgv.setVisibility(View.GONE);
        }
    }

    public void photoChosenManage() {
        DialogBoxUtil.photoChosenDialogBox(ShareDetailActivity.this, null, new PhotoChosenCallback() {
            @Override
            public void onGallerySelected() {
                checkGalleryProcess();
            }

            @Override
            public void onCameraSelected() {
                checkCameraProcess();
            }
        });
    }

    private void checkGalleryProcess() {
        if (permissionModule.checkWriteExternalStoragePermission())
            startGalleryProcess();
        else
            ActivityCompat.requestPermissions(ShareDetailActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    permissionModule.getWriteExternalStoragePermissionCode());
    }

    private void startGalleryProcess() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                getResources().getString(R.string.selectPicture)), permissionModule.getImageGalleryPermission());
    }

    private void checkCameraProcess() {
        if (!CommonUtils.checkCameraHardware(ShareDetailActivity.this)) {
            CommonUtils.showToast(ShareDetailActivity.this, getResources().getString(R.string.deviceHasNoCamera));
            return;
        }

        if (permissionModule.checkCameraPermission())
            startCameraProcess();
        else
            ActivityCompat.requestPermissions(ShareDetailActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    permissionModule.getCameraPermissionCode());
    }

    public void startCameraProcess() {
        if (permissionModule.checkWriteExternalStoragePermission()) {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(intent, permissionModule.getCameraPermissionCode());
        } else
            ActivityCompat.requestPermissions(ShareDetailActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    permissionModule.getWriteExternalStoragePermissionCode());
    }

    public void updateNoteText() {
        noteTextLayout = getLayoutInflater().inflate(R.layout.default_notetext_layout, shareMainLayout, false);
        ImageView approveImgv = noteTextLayout.findViewById(R.id.approveImgv);
        ImageView cancelTextImgv = noteTextLayout.findViewById(R.id.cancelTextImgv);
        final EditText noteTextEditText = noteTextLayout.findViewById(R.id.noteTextEditText);
        noteTextEditText.setText(ShareItems.getInstance().getPost().getMessage());
        shareMainLayout.addView(noteTextLayout);
        approveImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap editTextBitmap = null;
                if (noteTextEditText.getText().toString().isEmpty()) {
                    textImgv.setImageResource(textDefaultImageId);
                    addTextImgv.setVisibility(View.VISIBLE);
                    deleteTextImgv.setVisibility(View.GONE);
                } else {
                    editTextBitmap = BitmapConversion.getScreenShot(noteTextEditText);
                    Glide.with(ShareDetailActivity.this).load(editTextBitmap).apply(RequestOptions.circleCropTransform()).into(textImgv);
                    addTextImgv.setVisibility(View.GONE);
                    deleteTextImgv.setVisibility(View.VISIBLE);
                }
                ShareItems.getInstance().getPost().setMessage(noteTextEditText.getText().toString());
                ShareItems.getInstance().setTextBitmap(editTextBitmap);
                shareMainLayout.removeView(noteTextLayout);
            }
        });

        cancelTextImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareMainLayout.removeView(noteTextLayout);
            }
        });
    }
}
