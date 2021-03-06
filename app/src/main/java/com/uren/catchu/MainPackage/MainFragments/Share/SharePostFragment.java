package com.uren.catchu.MainPackage.MainFragments.Share;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.util.Pair;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arsy.maps_library.MapRipple;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lai.library.ButtonStyle;
import com.uren.catchu.Adapters.LocationTrackerAdapter;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.GeneralUtils.BitmapConversion;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.PhotoChosenForShareCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.VideoChosenForShareCallback;
import com.uren.catchu.GeneralUtils.FileAdapter;
import com.uren.catchu.GeneralUtils.IntentUtil.IntentSelectUtil;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.GeneralUtils.TransitionHelper;
import com.uren.catchu.GeneralUtils.UriAdapter;
import com.uren.catchu.GeneralUtils.VideoUtil.VideoSelectUtil;
import com.uren.catchu.InfoActivity;
import com.uren.catchu.Interfaces.PermissionCallback;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.GroupManagementFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.SelectFriendFragment;
import com.uren.catchu.MainPackage.MainFragments.Share.Interfaces.KeyboardHeightObserver;
import com.uren.catchu.MainPackage.MainFragments.Share.Interfaces.VideoTrimmedCallback;
import com.uren.catchu.MainPackage.MainFragments.Share.SubFragments.PhotoEditorFragment;
import com.uren.catchu.MainPackage.MainFragments.Share.SubFragments.ShareAdvanceSettingsFragment;
import com.uren.catchu.MainPackage.MainFragments.Share.SubFragments.VideoRecordFragment;
import com.uren.catchu.MainPackage.MainFragments.Share.SubFragments.VideoTrimmerFragment;
import com.uren.catchu.MainPackage.MainFragments.Share.Utils.KeyboardHeightProvider;
import com.uren.catchu.MainPackage.MainFragments.Share.Utils.ShareDeleteProcess;
import com.uren.catchu.MainPackage.MainFragments.Share.Utils.ShareUtil;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.MainPackage.MainFragments.Share.Interfaces.LocationCallback;
import com.uren.catchu.MainPackage.MainFragments.Share.Models.ImageShareItemBox;
import com.uren.catchu.MainPackage.MainFragments.Share.Models.VideoShareItemBox;
import com.uren.catchu.MainPackage.MainFragments.Share.Utils.CheckShareItems;
import com.uren.catchu.MainPackage.MainFragments.Share.Utils.ResizeAnimation;
import com.uren.catchu.MainPackage.MainFragments.Share.SubFragments.VideoViewFragment;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.SelectedFriendList;
import com.uren.catchu.MainPackage.MainFragments.Share.Models.ShareItems;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.GroupRequestResultResultArrayItem;
import catchu.model.UserProfile;

import static android.content.Context.LOCATION_SERVICE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static com.uren.catchu.Constants.NumericConstants.KEYBOARD_CHECK_VALUE;
import static com.uren.catchu.Constants.NumericConstants.MAX_IMAGE_SIZE_1MB;
import static com.uren.catchu.Constants.NumericConstants.MAX_VIDEO_DURATION;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.CAMERA_TEXT;
import static com.uren.catchu.Constants.StringConstants.FROM_FILE_TEXT;
import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;
import static com.uren.catchu.Constants.StringConstants.GROUP_OP_CHOOSE_TYPE;
import static com.uren.catchu.Constants.StringConstants.IMAGE_TYPE;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_ALL_FOLLOWERS;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_CUSTOM;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_EVERYONE;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_GROUP;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_SELF;
import static com.uren.catchu.Constants.StringConstants.VIDEO_TYPE;
import static com.uren.catchu.MainPackage.MainFragments.Share.Utils.ResizeAnimation.widthType;

public class SharePostFragment extends BaseFragment implements OnMapReadyCallback,
        KeyboardHeightObserver {

    View view;

    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.toolbarSubTitle)
    TextView toolbarSubTitle;
    @BindView(R.id.profilePicImgView)
    ImageView profilePicImgView;
    @BindView(R.id.shortUserNameTv)
    TextView shortUserNameTv;
    @BindView(R.id.selectedDescTv)
    TextView selectedDescTv;

    @BindView(R.id.photoSelectImgv)
    ClickableImageView photoSelectImgv;
    @BindView(R.id.videoSelectImgv)
    ClickableImageView videoSelectImgv;
    @BindView(R.id.textSelectImgv)
    ImageView textSelectImgv;
    @BindView(R.id.showMapImgv)
    ClickableImageView showMapImgv;
    @BindView(R.id.moreSettingsImgv)
    ClickableImageView moreSettingsImgv;

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

    @BindView(R.id.photoCheckedImgv)
    ImageView photoCheckedImgv;
    @BindView(R.id.videoCheckedImgv)
    ImageView videoCheckedImgv;
    @BindView(R.id.textCheckedImgv)
    ImageView textCheckedImgv;

    @BindView(R.id.cancelButton)
    ButtonStyle cancelButton;
    @BindView(R.id.shareButton)
    ButtonStyle shareButton;

    @BindView(R.id.mapLayout)
    RelativeLayout mapLayout;

    @BindView(R.id.publicSelectLayout)
    LinearLayout publicSelectLayout;
    @BindView(R.id.allFollowersSelectLayout)
    LinearLayout allFollowersSelectLayout;
    @BindView(R.id.specialSelectLayout)
    LinearLayout specialSelectLayout;
    @BindView(R.id.groupsSelectLayout)
    LinearLayout groupsSelectLayout;
    @BindView(R.id.justMeSelectLayout)
    LinearLayout justMeSelectLayout;

    @BindView(R.id.map)
    MapView mapView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    PermissionModule permissionModule;
    GoogleMap mMap;
    LocationTrackerAdapter locationTrackObj;
    MapRipple mapRipple;
    LocationManager locationManager;
    private KeyboardHeightProvider keyboardHeightProvider;

    ResizeAnimation publicAnimationShow;
    ResizeAnimation publicAnimationHide;

    ResizeAnimation allFollowersAnimationShow;
    ResizeAnimation allFollowersAnimationHide;

    ResizeAnimation specialAnimationShow;
    ResizeAnimation specialAnimationHide;

    ResizeAnimation groupAnimationShow;
    ResizeAnimation groupAnimationHide;

    ResizeAnimation justMeAnimationShow;
    ResizeAnimation justMeAnimationHide;

    boolean isPhotoSelected;
    boolean isVideoSelected;
    boolean shareWhomOpened;
    boolean keyboardResized;

    private static final int REQUEST_CODE_ENABLE_LOCATION = 407;

    private static final int REQUEST_CODE_PHOTO_GALLERY_SELECT = 592;
    private static final int REQUEST_CODE_PHOTO_CAMERA_SELECT = 676;

    private static final int REQUEST_CODE_VIDEO_GALLERY_SELECT = 661;
    private static final int REQUEST_CODE_VIDEO_CAMERA_SELECT = 225;

    private static final int ANIMATION_DURATION_FOR_OTHERVIEWS = 300;

    PhotoSelectUtil photoSelectUtil;
    VideoSelectUtil videoSelectUtil;
    CheckShareItems checkShareItems;
    ShareItems shareItems;
    Uri photoUri;

    String selectedShareType = "";
    String galleryOrCameraSelect = "";
    String selectedWhomType = "";

    public SharePostFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        getActivity().findViewById(R.id.tabMainLayout).setVisibility(View.GONE);
        if (getContext() != null)
            CommonUtils.hideKeyBoard(getContext());
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        keyboardHeightProvider.setKeyboardHeightObserver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        try {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_share_post, container, false);
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

    private void initializeItems() {
        permissionModule = new PermissionModule(getContext());
        shareItems = new ShareItems();
        setSelectedWhomType();
        getUserInfo();
        initLocationTracker();
        checkCanGetLocation();
        setShapes();
        setViewsDefaultValues();
        setAnimations();
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        photoSelectUtil = new PhotoSelectUtil();
        keyboardHeightProvider = new KeyboardHeightProvider((Activity) getContext());

        view.post(new Runnable() {
            public void run() {
                keyboardHeightProvider.start();
            }
        });
    }

    private void setSelectedWhomType() {
        selectedDescTv.setText(getResources().getString(R.string.PUBLIC_SHARE_DESC));
        selectedWhomType = SHARE_TYPE_EVERYONE;
        shareItems.setSelectedShareType(selectedWhomType);
        shareItems.getPost().setIsCommentAllowed(true);
        shareItems.getPost().setIsShowOnMap(true);
    }

    private void setAnimations() {

        publicSelectLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                publicSelectLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                publicAnimationShow = new ResizeAnimation(publicSelectLayout, publicSelectLayout.getWidth(), 0, widthType);
                publicAnimationHide = new ResizeAnimation(publicSelectLayout, 0, publicSelectLayout.getWidth(), widthType);
                publicAnimationShow.setDuration(ANIMATION_DURATION_FOR_OTHERVIEWS);
                publicAnimationHide.setDuration(ANIMATION_DURATION_FOR_OTHERVIEWS);
            }
        });

        allFollowersSelectLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                allFollowersSelectLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                allFollowersAnimationShow = new ResizeAnimation(allFollowersSelectLayout, allFollowersSelectLayout.getWidth(), 0, widthType);
                allFollowersAnimationHide = new ResizeAnimation(allFollowersSelectLayout, 0, allFollowersSelectLayout.getWidth(), widthType);
                allFollowersAnimationShow.setDuration(ANIMATION_DURATION_FOR_OTHERVIEWS);
                allFollowersAnimationHide.setDuration(ANIMATION_DURATION_FOR_OTHERVIEWS);
                allFollowersSelectLayout.startAnimation(allFollowersAnimationHide);
            }
        });

        specialSelectLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                specialSelectLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                specialAnimationShow = new ResizeAnimation(specialSelectLayout, specialSelectLayout.getWidth(), 0, widthType);
                specialAnimationHide = new ResizeAnimation(specialSelectLayout, 0, specialSelectLayout.getWidth(), widthType);
                specialAnimationShow.setDuration(ANIMATION_DURATION_FOR_OTHERVIEWS);
                specialAnimationHide.setDuration(ANIMATION_DURATION_FOR_OTHERVIEWS);
                specialSelectLayout.startAnimation(specialAnimationHide);
            }
        });

        groupsSelectLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                groupsSelectLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                groupAnimationShow = new ResizeAnimation(groupsSelectLayout, groupsSelectLayout.getWidth(), 0, widthType);
                groupAnimationHide = new ResizeAnimation(groupsSelectLayout, 0, groupsSelectLayout.getWidth(), widthType);
                groupAnimationShow.setDuration(ANIMATION_DURATION_FOR_OTHERVIEWS);
                groupAnimationHide.setDuration(ANIMATION_DURATION_FOR_OTHERVIEWS);
                groupsSelectLayout.startAnimation(groupAnimationHide);
            }
        });

        justMeSelectLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                justMeSelectLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                justMeAnimationShow = new ResizeAnimation(justMeSelectLayout, justMeSelectLayout.getWidth(), 0, widthType);
                justMeAnimationHide = new ResizeAnimation(justMeSelectLayout, 0, justMeSelectLayout.getWidth(), widthType);
                justMeAnimationShow.setDuration(ANIMATION_DURATION_FOR_OTHERVIEWS);
                justMeAnimationHide.setDuration(ANIMATION_DURATION_FOR_OTHERVIEWS);
                justMeSelectLayout.startAnimation(justMeAnimationHide);
            }
        });
    }

    private void setShapes() {
        moreSettingsImgv.setColorFilter(this.getResources().getColor(R.color.White, null), PorterDuff.Mode.SRC_IN);
    }

    public void setViewsDefaultValues() {
        clearPhotoSelectImgvFilled();
        clearVideoSelectImgvFilled();
        clearTextSelectImgvFilled();
        setWhomItemsImgvFilled();
    }

    private void addListeners() {

        moreSettingsImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAdvancedSettingsFragment();
            }
        });

        shareMsgEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.toString() != null && !s.toString().isEmpty()) {
                    setTextSelectImgvFilled();
                    shareItems.getPost().setMessage(s.toString());
                } else {
                    clearTextSelectImgvFilled();
                    shareItems.getPost().setMessage("");
                }
            }
        });

        photoSelectImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedShareType = IMAGE_TYPE;
                DialogBoxUtil.photoChosenForShareDialogBox(getContext(), isPhotoSelected, new PhotoChosenForShareCallback() {
                    @Override
                    public void onGallerySelected() {
                        galleryOrCameraSelect = GALLERY_TEXT;
                        checkGalleryProcess();
                    }

                    @Override
                    public void onCameraSelected() {
                        galleryOrCameraSelect = CAMERA_TEXT;
                        checkCameraProcess();
                    }

                    @Override
                    public void onPhotoRemoved() {
                        ShareDeleteProcess.deleteSharedPhoto(getContext(), permissionModule, shareItems);
                        photoSelectUtil = null;
                        isPhotoSelected = false;
                        shareItems.clearImageShareItemBox();
                        clearPhotoSelectImgvFilled();
                    }

                    @Override
                    public void onEditted() {
                        startPhotoSelectedFragment();
                    }
                });
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancelButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                getActivity().onBackPressed();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkShareItems = new CheckShareItems(getContext(), shareItems);
                //shareButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));

                if (!checkShareItems.shareIsPossible()) {
                    CommonUtils.showToastShort(getContext(), checkShareItems.getErrMessage());
                    return;
                }
                sharePost();
            }
        });

        showMapImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.hideKeyBoard(getContext());
            }
        });

        videoSelectImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedShareType = VIDEO_TYPE;
                DialogBoxUtil.videoChosenForShareDialogBox(getContext(), isVideoSelected, new VideoChosenForShareCallback() {
                    @Override
                    public void onGallerySelected() {
                        galleryOrCameraSelect = GALLERY_TEXT;
                        checkGalleryProcess();
                    }

                    @Override
                    public void onCameraSelected() {
                        galleryOrCameraSelect = CAMERA_TEXT;
                        mFragmentNavigation.pushFragment(new VideoRecordFragment(new ReturnCallback() {
                            @Override
                            public void onReturn(Object object) {
                                videoSelectUtil = (VideoSelectUtil) object;
                                isVideoSelected = true;
                                addVideoShareItemList();
                                setVideoSelectImgvFilled();
                            }
                        }));
                    }

                    @Override
                    public void onVideoRemoved() {
                        ShareDeleteProcess.deleteSharedVideo(getContext(), permissionModule, shareItems);
                        videoSelectUtil = null;
                        isVideoSelected = false;
                        shareItems.clearVideoShareItemBox();
                        clearVideoSelectImgvFilled();
                    }

                    @Override
                    public void onPlayed() {
                        startVideoViewFragment();
                    }
                });
            }
        });


        publicSelectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedWhomType = SHARE_TYPE_EVERYONE;
                openWhomSelection();
                shareItems.setSelectedShareType(selectedWhomType);
                selectedDescTv.setText(getResources().getString(R.string.PUBLIC_SHARE_DESC));
            }
        });

        allFollowersSelectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedWhomType = SHARE_TYPE_ALL_FOLLOWERS;
                openWhomSelection();
                shareItems.setSelectedShareType(selectedWhomType);
                selectedDescTv.setText(getResources().getString(R.string.ALL_FOLLOWERS_SHARE_DESC));
            }
        });

        specialSelectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedWhomType = SHARE_TYPE_CUSTOM;

                if (!shareWhomOpened) {
                    setShowAnimations();
                    shareWhomOpened = true;
                } else
                    startSelectFriendFragment();
            }
        });

        groupsSelectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedWhomType = SHARE_TYPE_GROUP;

                if (!shareWhomOpened) {
                    setShowAnimations();
                    shareWhomOpened = true;
                } else
                    startGroupManagementFragment();
            }
        });

        justMeSelectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedWhomType = SHARE_TYPE_SELF;
                openWhomSelection();
                shareItems.setSelectedShareType(selectedWhomType);
                selectedDescTv.setText(getResources().getString(R.string.JUSTME_SHARE_DESC));
            }
        });
    }

    public void sharePost() {

        getActivity().onBackPressed();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ShareUtil shareUtil = new ShareUtil(shareItems);
                shareUtil.startToShare();
            }
        }, 300);
    }

    public void openWhomSelection() {

        if (!shareWhomOpened) {
            setShowAnimations();
            shareWhomOpened = true;
        } else
            setHideAnimations();
    }

    public void setShowAnimations() {
        selectedDescTv.setVisibility(View.GONE);
        if (publicAnimationShow != null)
            publicSelectLayout.startAnimation(publicAnimationShow);

        if (allFollowersAnimationShow != null)
            allFollowersSelectLayout.startAnimation(allFollowersAnimationShow);

        if (specialAnimationShow != null)
            specialSelectLayout.startAnimation(specialAnimationShow);

        if (groupAnimationShow != null)
            groupsSelectLayout.startAnimation(groupAnimationShow);

        if (justMeAnimationShow != null)
            justMeSelectLayout.startAnimation(justMeAnimationShow);
    }

    public void setHideAnimations() {
        switch (selectedWhomType) {
            case SHARE_TYPE_EVERYONE:
                if (allFollowersAnimationShow != null)
                    allFollowersSelectLayout.startAnimation(allFollowersAnimationHide);

                if (specialAnimationShow != null)
                    specialSelectLayout.startAnimation(specialAnimationHide);

                if (groupAnimationShow != null)
                    groupsSelectLayout.startAnimation(groupAnimationHide);

                if (justMeAnimationShow != null)
                    justMeSelectLayout.startAnimation(justMeAnimationHide);
                break;

            case SHARE_TYPE_ALL_FOLLOWERS:
                if (publicAnimationShow != null)
                    publicSelectLayout.startAnimation(publicAnimationHide);

                if (specialAnimationShow != null)
                    specialSelectLayout.startAnimation(specialAnimationHide);

                if (groupAnimationShow != null)
                    groupsSelectLayout.startAnimation(groupAnimationHide);

                if (justMeAnimationShow != null)
                    justMeSelectLayout.startAnimation(justMeAnimationHide);
                break;

            case SHARE_TYPE_CUSTOM:
                if (publicAnimationShow != null)
                    publicSelectLayout.startAnimation(publicAnimationHide);

                if (allFollowersAnimationShow != null)
                    allFollowersSelectLayout.startAnimation(allFollowersAnimationHide);

                if (groupAnimationShow != null)
                    groupsSelectLayout.startAnimation(groupAnimationHide);

                if (justMeAnimationShow != null)
                    justMeSelectLayout.startAnimation(justMeAnimationHide);
                break;

            case SHARE_TYPE_GROUP:
                if (publicAnimationShow != null)
                    publicSelectLayout.startAnimation(publicAnimationHide);

                if (allFollowersAnimationShow != null)
                    allFollowersSelectLayout.startAnimation(allFollowersAnimationHide);

                if (specialAnimationShow != null)
                    specialSelectLayout.startAnimation(specialAnimationHide);

                if (justMeAnimationShow != null)
                    justMeSelectLayout.startAnimation(justMeAnimationHide);
                break;

            case SHARE_TYPE_SELF:
                if (publicAnimationShow != null)
                    publicSelectLayout.startAnimation(publicAnimationHide);

                if (allFollowersAnimationShow != null)
                    allFollowersSelectLayout.startAnimation(allFollowersAnimationHide);

                if (specialAnimationShow != null)
                    specialSelectLayout.startAnimation(specialAnimationHide);

                if (groupAnimationShow != null)
                    groupsSelectLayout.startAnimation(groupAnimationHide);
                break;

            default:
                break;
        }
        selectedDescTv.setVisibility(View.VISIBLE);
        shareWhomOpened = false;
    }

    private void startGroupManagementFragment() {
        CommonUtils.hideKeyBoard(getContext());
        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new GroupManagementFragment(GROUP_OP_CHOOSE_TYPE,
                            new ReturnCallback() {
                                @Override
                                public void onReturn(Object object) {
                                    GroupRequestResultResultArrayItem selectedGroup = (GroupRequestResultResultArrayItem) object;

                                    if (selectedGroup != null) {
                                        selectedWhomType = SHARE_TYPE_GROUP;
                                        shareItems.setSelectedShareType(selectedWhomType);
                                        shareItems.setSelectedGroup(selectedGroup);
                                        selectedDescTv.setText(selectedGroup.getName());
                                        setHideAnimations();
                                    }
                                }
                            }),
                    ANIMATE_RIGHT_TO_LEFT);
        }
    }

    private void startSelectFriendFragment() {
        CommonUtils.hideKeyBoard(getContext());
        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new SelectFriendFragment(null, null,
                    SharePostFragment.class.getName(),
                    new ReturnCallback() {
                        @Override
                        public void onReturn(Object object) {
                            selectedWhomType = SHARE_TYPE_CUSTOM;
                            shareItems.setSelectedShareType(selectedWhomType);
                            selectedDescTv.setText(getSelectedFriendsText());
                            setHideAnimations();
                        }
                    }), ANIMATE_RIGHT_TO_LEFT);
        }
    }

    public void startAdvancedSettingsFragment() {
        CommonUtils.hideKeyBoard(getContext());
        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new ShareAdvanceSettingsFragment(shareItems, new ReturnCallback() {
                @Override
                public void onReturn(Object object) {
                    shareItems = (ShareItems) object;
                }
            }), ANIMATE_LEFT_TO_RIGHT);
        }
    }

    public String getSelectedFriendsText() {
        String returnText = "";
        if (SelectedFriendList.getInstance().getSize() > 1) {

            if (SelectedFriendList.getInstance().getFriend(0) != null) {

                if (SelectedFriendList.getInstance().getFriend(0).getName() != null &&
                        !SelectedFriendList.getInstance().getFriend(0).getName().isEmpty()) {
                    returnText = SelectedFriendList.getInstance().getFriend(0).getName() + " +" +
                            Integer.toString(SelectedFriendList.getInstance().getSize() - 1) + " others";
                } else if (SelectedFriendList.getInstance().getFriend(0).getUsername() != null &&
                        !SelectedFriendList.getInstance().getFriend(0).getUsername().isEmpty()) {
                    returnText = SelectedFriendList.getInstance().getFriend(0).getUsername() + " +" +
                            Integer.toString(SelectedFriendList.getInstance().getSize() - 1) + " others";
                }
            }
        } else {
            if (SelectedFriendList.getInstance().getFriend(0) != null) {

                if (SelectedFriendList.getInstance().getFriend(0).getName() != null &&
                        !SelectedFriendList.getInstance().getFriend(0).getName().isEmpty()) {
                    returnText = SelectedFriendList.getInstance().getFriend(0).getName();
                } else if (SelectedFriendList.getInstance().getFriend(0).getUsername() != null &&
                        !SelectedFriendList.getInstance().getFriend(0).getUsername().isEmpty()) {
                    returnText = SelectedFriendList.getInstance().getFriend(0).getUsername();
                }
            }
        }

        return returnText;
    }

    private void checkGalleryProcess() {
        if (permissionModule.checkWriteExternalStoragePermission()) {
            if (selectedShareType.equals(IMAGE_TYPE))
                startActivityForResult(Intent.createChooser(IntentSelectUtil.getGalleryIntent(),
                        getContext().getResources().getString(R.string.selectPicture)), REQUEST_CODE_PHOTO_GALLERY_SELECT);
            else if (selectedShareType.equals(VIDEO_TYPE))
                startActivityForResult(Intent.createChooser(IntentSelectUtil.getGalleryIntentForVideo(getContext()),
                        getContext().getResources().getString(R.string.SELECT_VIDEO)), REQUEST_CODE_VIDEO_GALLERY_SELECT);
        } else
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    permissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE);
    }

    private void checkCameraProcess() {
        if (!CommonUtils.checkCameraHardware(getContext())) {
            CommonUtils.showToastShort(getContext(), getContext().getResources().getString(R.string.deviceHasNoCamera));
            return;
        }

        if (permissionModule.checkCameraPermission() && permissionModule.checkWriteExternalStoragePermission())
            openCameraForPhotoSelect();
        else if (permissionModule.checkCameraPermission())
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    permissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE);
        else if (permissionModule.checkWriteExternalStoragePermission())
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    permissionModule.PERMISSION_CAMERA);
        else
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    permissionModule.PERMISSION_CAMERA);
    }

    public void openCameraForPhotoSelect() {
        photoUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider",
                FileAdapter.getOutputMediaFile(MEDIA_TYPE_IMAGE));

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, (long) MAX_IMAGE_SIZE_1MB);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, REQUEST_CODE_PHOTO_CAMERA_SELECT);
    }

    private void getUserInfo() {

        if (AccountHolderInfo.getInstance() != null && AccountHolderInfo.getInstance().getUser() != null &&
                AccountHolderInfo.getInstance().getUser().getUserInfo() != null)
            setToolbarInfo(AccountHolderInfo.getInstance().getUser().getUserInfo().getProfilePhotoUrl(),
                    AccountHolderInfo.getInstance().getUser().getUserInfo().getName(),
                    AccountHolderInfo.getInstance().getUser().getUserInfo().getUsername());
        else {
            AccountHolderInfo.getToken(new TokenCallback() {
                @Override
                public void onTokenTaken(String token) {
                    getProfileDetail(token);
                }

                @Override
                public void onTokenFail(String message) {
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
                            up.getUserInfo().getName(),
                            up.getUserInfo().getUsername());
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
        }, AccountHolderInfo.getUserID(), AccountHolderInfo.getUserID(), "true", token);

        loadUserDetail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setToolbarInfo(String url, String name, String username) {
        UserDataUtil.setProfilePicture(getContext(), url, name, username, shortUserNameTv, profilePicImgView);

        if (AccountHolderInfo.getInstance().getUser() != null && AccountHolderInfo.getInstance().getUser().getUserInfo() != null) {
            if (AccountHolderInfo.getInstance().getUser().getUserInfo().getName() != null &&
                    !AccountHolderInfo.getInstance().getUser().getUserInfo().getName().isEmpty()) {
                UserDataUtil.setName(AccountHolderInfo.getInstance().getUser().getUserInfo().getName(), toolbarTitle);
            }

            if (AccountHolderInfo.getInstance().getUser().getUserInfo().getUsername() != null &&
                    !AccountHolderInfo.getInstance().getUser().getUserInfo().getUsername().isEmpty()) {
                UserDataUtil.setUsername(AccountHolderInfo.getInstance().getUser().getUserInfo().getUsername(), toolbarSubTitle);
            }
        }
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionModule.checkAccessFineLocationPermission())
                initializeMap(mMap);
            else
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        permissionModule.PERMISSION_ACCESS_FINE_LOCATION);
        } else
            initializeMap(mMap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == permissionModule.PERMISSION_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeMap(mMap);
            }
        } else if (requestCode == permissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (galleryOrCameraSelect.equals(CAMERA_TEXT) && selectedShareType.equals(IMAGE_TYPE)) {
                    if (permissionModule.checkCameraPermission())
                        openCameraForPhotoSelect();
                    else
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                permissionModule.PERMISSION_CAMERA);
                } else if (galleryOrCameraSelect.equals(GALLERY_TEXT) && selectedShareType.equals(IMAGE_TYPE)) {
                    startActivityForResult(Intent.createChooser(IntentSelectUtil.getGalleryIntent(),
                            getContext().getResources().getString(R.string.selectPicture)), REQUEST_CODE_PHOTO_GALLERY_SELECT);
                } else if (galleryOrCameraSelect.equals(GALLERY_TEXT) && selectedShareType.equals(VIDEO_TYPE)) {
                    startActivityForResult(Intent.createChooser(IntentSelectUtil.getGalleryIntentForVideo(getContext()),
                            getContext().getResources().getString(R.string.SELECT_VIDEO)), REQUEST_CODE_VIDEO_GALLERY_SELECT);
                }
            }
        } else if (requestCode == permissionModule.PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (permissionModule.checkWriteExternalStoragePermission())
                    openCameraForPhotoSelect();
                else
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            permissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_PHOTO_GALLERY_SELECT) {
                photoSelectUtil = new PhotoSelectUtil(getContext(), data, GALLERY_TEXT);
                startPhotoSelectedFragment();
            } else if (requestCode == REQUEST_CODE_PHOTO_CAMERA_SELECT) {
                photoSelectUtil = new PhotoSelectUtil(getContext(), photoUri, FROM_FILE_TEXT);
                startPhotoSelectedFragment();
            } else if (requestCode == REQUEST_CODE_VIDEO_GALLERY_SELECT) {
                checkVideoDuration(data);
            }
        } else if (requestCode == REQUEST_CODE_ENABLE_LOCATION) {
            if (locationTrackObj.canGetLocation())
                initializeMap(mMap);
        }
    }

    public void checkVideoDuration(Intent data) {
        if (data == null) return;
        Uri uri = data.getData();
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(UriAdapter.getPathFromGalleryUri((Activity) getContext(), uri));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time);

        if (timeInMillisec > ((MAX_VIDEO_DURATION + 1) * 1000)) {
            startVideoTrimmerFragment(data.getData());
        } else {
            isVideoSelected = true;
            videoSelectUtil = new VideoSelectUtil(getActivity(), data.getData(), null, false);
            addVideoShareItemList();
            setVideoSelectImgvFilled();
            startVideoViewFragment();
        }
    }

    public void startVideoTrimmerFragment(Uri uri) {
        mFragmentNavigation.pushFragment(new VideoTrimmerFragment(uri, new VideoTrimmedCallback() {
            @Override
            public void onTrimmed(Uri uri, String realPath) {
                isVideoSelected = true;
                videoSelectUtil = new VideoSelectUtil(getActivity(), uri, realPath, true);
                addVideoShareItemList();
                setVideoSelectImgvFilled();
            }
        }));
    }

    public void addVideoShareItemList() {
        shareItems.clearVideoShareItemBox();
        VideoShareItemBox videoShareItemBox = new VideoShareItemBox(videoSelectUtil);
        shareItems.addVideoShareItemBox(videoShareItemBox);
    }

    public void startVideoViewFragment() {
        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new VideoViewFragment(videoSelectUtil.getVideoUri(),
                    new PermissionCallback() {
                        @Override
                        public void OnPermGranted() {

                        }

                        @Override
                        public void OnPermNotAllowed() {
                            ShareDeleteProcess.deleteSharedVideo(getContext(), permissionModule, shareItems);
                            videoSelectUtil = null;
                            isVideoSelected = false;
                            shareItems.clearVideoShareItemBox();
                            clearVideoSelectImgvFilled();
                        }
                    }), ANIMATE_RIGHT_TO_LEFT);
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

                MarkerOptions options = new MarkerOptions().position(latLng);
                Bitmap bitmap = BitmapConversion.createUserMapBitmap(getContext(), profilePicImgView);

                if (bitmap != null) {
                    options.title(AccountHolderInfo.getInstance().getUser().getUserInfo().getName());
                    options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                    options.anchor(0.5f, 0.907f);
                    mMap.addMarker(options);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                } else
                    mapRipple = new MapRipple(mMap, latLng, getContext());

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new
                        LatLng(location.getLatitude(),
                        location.getLongitude()), 12));
            }
        }
    }

    public void setShareItemsLocation(Location location) {
        catchu.model.Location tempLoc = new catchu.model.Location();
        tempLoc.setLongitude(BigDecimal.valueOf(location.getLongitude()));
        tempLoc.setLatitude(BigDecimal.valueOf(location.getLatitude()));
        shareItems.getPost().setLocation(tempLoc);
    }

    public void fillImageShareItemBox() {
        ImageShareItemBox imageShareItemBox = new ImageShareItemBox(photoSelectUtil);
        shareItems.clearImageShareItemBox();
        shareItems.addImageShareItemBox(imageShareItemBox);
    }

    private void initLocationTracker() {
        locationTrackObj = new LocationTrackerAdapter(getContext(), new LocationCallback() {
            @Override
            public void onLocationChanged(Location location) {
                catchu.model.Location locationModel = new catchu.model.Location();
                locationModel.setLatitude(BigDecimal.valueOf(location.getLatitude()));
                locationModel.setLongitude(BigDecimal.valueOf(location.getLongitude()));
                shareItems.getPost().setLocation(locationModel);
            }
        });
    }

    private void checkCanGetLocation() {
        if (locationTrackObj != null && !locationTrackObj.canGetLocation()) {
            final int TYPE_XML = 1;
            Intent i = new Intent(getActivity(), InfoActivity.class);
            i.putExtra("EXTRA_TYPE", TYPE_XML);
            transitionTo(i);
        }
    }

    @SuppressWarnings("unchecked")
    void transitionTo(Intent i) {
        final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(getActivity(), false);
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), pairs);
        startActivity(i, transitionActivityOptions.toBundle());
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
        keyboardHeightProvider.setKeyboardHeightObserver(null);
        if (locationManager != null)
            locationManager.removeUpdates(locationTrackObj);
    }

    public void startPhotoSelectedFragment() {
        if (mFragmentNavigation != null) {
                /*mFragmentNavigation.pushFragment(new PhotoSelectedFragment(photoSelectUtil, new ReturnCallback() {
                    @Override
                    public void onReturn(Object object) {
                        photoSelectUtil = (PhotoSelectUtil) object;
                        isPhotoSelected = true;
                        setPhotoSelectImgvFilled();
                        fillImageShareItemBox();
                    }
                }));*/

            mFragmentNavigation.pushFragment(new PhotoEditorFragment(photoSelectUtil, new ReturnCallback() {
                @Override
                public void onReturn(Object object) {
                    photoSelectUtil = (PhotoSelectUtil) object;
                    isPhotoSelected = true;
                    setPhotoSelectImgvFilled();
                    fillImageShareItemBox();
                }
            }));

        }
    }


    private void setWhomItemsImgvFilled() {
        publicImgv.setColorFilter(getContext().getResources().getColor(R.color.RoyalBlue, null), PorterDuff.Mode.SRC_IN);
        allFollowersImgv.setColorFilter(getContext().getResources().getColor(R.color.RoyalBlue, null), PorterDuff.Mode.SRC_IN);
        specialImgv.setColorFilter(getContext().getResources().getColor(R.color.RoyalBlue, null), PorterDuff.Mode.SRC_IN);
        groupsImgv.setColorFilter(getContext().getResources().getColor(R.color.RoyalBlue, null), PorterDuff.Mode.SRC_IN);
        justMeImgv.setColorFilter(getContext().getResources().getColor(R.color.RoyalBlue, null), PorterDuff.Mode.SRC_IN);
    }

    public void setPhotoSelectImgvFilled() {
        photoSelectImgv.setBackground(ShapeUtil.getShape(0,
                getContext().getResources().getColor(R.color.RoyalBlue, null),
                GradientDrawable.OVAL, 50, 3));
        photoCheckedImgv.setVisibility(View.VISIBLE);
    }

    public void setVideoSelectImgvFilled() {
        videoSelectImgv.setBackground(ShapeUtil.getShape(0,
                getContext().getResources().getColor(R.color.RoyalBlue, null),
                GradientDrawable.OVAL, 50, 3));
        videoCheckedImgv.setVisibility(View.VISIBLE);
    }

    public void setTextSelectImgvFilled() {
        textSelectImgv.setBackground(ShapeUtil.getShape(0,
                getContext().getResources().getColor(R.color.RoyalBlue, null),
                GradientDrawable.OVAL, 50, 3));
        textCheckedImgv.setVisibility(View.VISIBLE);
    }

    public void clearPhotoSelectImgvFilled() {
        photoSelectImgv.setBackground(null);
        photoCheckedImgv.setVisibility(View.GONE);
    }

    public void clearVideoSelectImgvFilled() {
        videoSelectImgv.setBackground(null);
        videoCheckedImgv.setVisibility(View.GONE);
    }

    public void clearTextSelectImgvFilled() {
        textSelectImgv.setBackground(null);
        textCheckedImgv.setVisibility(View.GONE);
    }

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        String or = orientation == Configuration.ORIENTATION_PORTRAIT ? "portrait" : "landscape";

        if (height > KEYBOARD_CHECK_VALUE && mapLayout != null && !keyboardResized) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mapLayout.getLayoutParams();
            params.height = height;
            mapLayout.setLayoutParams(params);
            keyboardResized = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (keyboardHeightProvider != null)
            keyboardHeightProvider.close();
    }
}