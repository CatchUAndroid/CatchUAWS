package com.uren.catchu.MainPackage.MainFragments.Share;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
import com.google.android.gms.maps.model.LatLng;
import com.uren.catchu.Adapters.LocationTrackerAdapter;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.SignedUrlDeleteProcess;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.PhotoChosenForShareCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.VideoChosenForShareCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.YesNoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.IntentUtil.IntentSelectUtil;
import com.uren.catchu.GeneralUtils.KeyboardUtils;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.GeneralUtils.UriAdapter;
import com.uren.catchu.GeneralUtils.VideoUtil.VideoSelectUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.Interfaces.ServiceCompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.GroupManagementFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.SelectFriendFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.MainPackage.MainFragments.Share.Interfaces.LocationCallback;
import com.uren.catchu.MainPackage.MainFragments.Share.SubFragments.PhotoSelectedFragment;
import com.uren.catchu.MainPackage.MainFragments.Share.Models.ImageShareItemBox;
import com.uren.catchu.MainPackage.MainFragments.Share.Models.VideoShareItemBox;
import com.uren.catchu.MainPackage.MainFragments.Share.Utils.CheckShareItems;
import com.uren.catchu.MainPackage.MainFragments.Share.Utils.ResizeAnimation;
import com.uren.catchu.MainPackage.MainFragments.Share.Utils.SharePostProcess;
import com.uren.catchu.MainPackage.MainFragments.Share.SubFragments.VideoViewFragment;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.SelectedFriendList;
import com.uren.catchu.Singleton.Share.ShareItems;

import java.math.BigDecimal;
import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.FriendList;
import catchu.model.GroupRequestResultResultArrayItem;
import catchu.model.UserProfile;

import static android.content.Context.LOCATION_SERVICE;
import static com.uren.catchu.Constants.NumericConstants.MAX_VIDEO_DURATION;
import static com.uren.catchu.Constants.NumericConstants.SHARE_TRY_COUNT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.CAMERA_TEXT;
import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;
import static com.uren.catchu.Constants.StringConstants.GROUP_OP_CHOOSE_TYPE;
import static com.uren.catchu.Constants.StringConstants.IMAGE_TYPE;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_ALL_FOLLOWERS;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_CUSTOM;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_EVERYONE;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_GROUP;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_SELF;
import static com.uren.catchu.Constants.StringConstants.VIDEO_TYPE;

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
    @BindView(R.id.buttonsLayout)
    LinearLayout buttonsLayout;

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

    ResizeAnimation resizeMapAnimationShow;
    ResizeAnimation resizeMapAnimationHide;

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
    boolean keyboardIsVisible;
    boolean shareWhomOpened;

    private static final int REQUEST_CODE_ENABLE_LOCATION = 407;

    private static final int REQUEST_CODE_PHOTO_GALLERY_SELECT = 592;
    private static final int REQUEST_CODE_PHOTO_CAMERA_SELECT = 676;

    private static final int REQUEST_CODE_VIDEO_GALLERY_SELECT = 661;
    private static final int REQUEST_CODE_VIDEO_CAMERA_SELECT = 225;

    private static final int ANIMATION_DURATION_FOR_MAP = 500;
    private static final int ANIMATION_DURATION_FOR_OTHERVIEWS = 300;

    PhotoSelectUtil photoSelectUtil;
    VideoSelectUtil videoSelectUtil;
    CheckShareItems checkShareItems;

    String selectedType = "";
    String selectedWhomType = "";

    //boolean viewCreateAgain;

    public SharePostFragment() {
        //this.viewCreateAgain = viewCreateAgain;
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

        if(view == null) {
            view = inflater.inflate(R.layout.fragment_share_post, container, false);
            ButterKnife.bind(this, view);
            initializeItems();
            addListeners();
            setMapView();
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initializeItems() {
        permissionModule = new PermissionModule(getContext());
        ShareItems.setInstance(null);
        getUserInfo();
        initLocationTracker();
        checkCanGetLocation();
        setShapes();
        setViewsDefaultValues();
        setAnimations();
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        photoSelectUtil = new PhotoSelectUtil();
        checkShareItems = new CheckShareItems(getContext());
    }

    private void setAnimations() {
        mapLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mapLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                resizeMapAnimationShow = new ResizeAnimation(mapLayout, mapLayout.getHeight(), 0);
                resizeMapAnimationHide = new ResizeAnimation(mapLayout, 0, mapLayout.getHeight());
                resizeMapAnimationShow.setDuration(ANIMATION_DURATION_FOR_MAP);
                resizeMapAnimationHide.setDuration(ANIMATION_DURATION_FOR_MAP);
            }
        });

        publicSelectLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                publicSelectLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                publicAnimationShow = new ResizeAnimation(publicSelectLayout, publicSelectLayout.getHeight(), 0);
                publicAnimationHide = new ResizeAnimation(publicSelectLayout, 0, publicSelectLayout.getHeight());
                publicAnimationShow.setDuration(ANIMATION_DURATION_FOR_OTHERVIEWS);
                publicAnimationHide.setDuration(ANIMATION_DURATION_FOR_OTHERVIEWS);
            }
        });

        allFollowersSelectLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                allFollowersSelectLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                allFollowersAnimationShow = new ResizeAnimation(allFollowersSelectLayout, allFollowersSelectLayout.getHeight(), 0);
                allFollowersAnimationHide = new ResizeAnimation(allFollowersSelectLayout, 0, allFollowersSelectLayout.getHeight());
                allFollowersAnimationShow.setDuration(ANIMATION_DURATION_FOR_OTHERVIEWS);
                allFollowersAnimationHide.setDuration(ANIMATION_DURATION_FOR_OTHERVIEWS);
                allFollowersSelectLayout.startAnimation(allFollowersAnimationHide);
            }
        });

        specialSelectLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                specialSelectLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                specialAnimationShow = new ResizeAnimation(specialSelectLayout, specialSelectLayout.getHeight(), 0);
                specialAnimationHide = new ResizeAnimation(specialSelectLayout, 0, specialSelectLayout.getHeight());
                specialAnimationShow.setDuration(ANIMATION_DURATION_FOR_OTHERVIEWS);
                specialAnimationHide.setDuration(ANIMATION_DURATION_FOR_OTHERVIEWS);
                specialSelectLayout.startAnimation(specialAnimationHide);
            }
        });

        groupsSelectLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                groupsSelectLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                groupAnimationShow = new ResizeAnimation(groupsSelectLayout, groupsSelectLayout.getHeight(), 0);
                groupAnimationHide = new ResizeAnimation(groupsSelectLayout, 0, groupsSelectLayout.getHeight());
                groupAnimationShow.setDuration(ANIMATION_DURATION_FOR_OTHERVIEWS);
                groupAnimationHide.setDuration(ANIMATION_DURATION_FOR_OTHERVIEWS);
                groupsSelectLayout.startAnimation(groupAnimationHide);
            }
        });

        justMeSelectLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                justMeSelectLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                justMeAnimationShow = new ResizeAnimation(justMeSelectLayout, justMeSelectLayout.getHeight(), 0);
                justMeAnimationHide = new ResizeAnimation(justMeSelectLayout, 0, justMeSelectLayout.getHeight());
                justMeAnimationShow.setDuration(ANIMATION_DURATION_FOR_OTHERVIEWS);
                justMeAnimationHide.setDuration(ANIMATION_DURATION_FOR_OTHERVIEWS);
                justMeSelectLayout.startAnimation(justMeAnimationHide);
            }
        });
    }

    private void setShapes() {
        cancelButton.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.Red, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 15, 2));
        shareButton.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.MediumSeaGreen, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 15, 2));
        showMapImgv.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.DeepSkyBlue, null),
                getResources().getColor(R.color.Red, null), GradientDrawable.OVAL, 20, 3));
    }

    public void setViewsDefaultValues() {
        clearPhotoSelectImgvFilled();
        clearVideoSelectImgvFilled();
        clearTextSelectImgvFilled();
        setWhomItemsImgvFilled();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addListeners() {
        shareMsgEditText.setOnTouchListener(new View.OnTouchListener() {
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
        });

        KeyboardUtils.addKeyboardToggleListener((Activity) getContext(), new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                keyboardIsVisible = isVisible;
                if (!isVisible) {
                    if (resizeMapAnimationShow != null) {
                        mapLayout.setVisibility(View.VISIBLE);
                        mapLayout.startAnimation(resizeMapAnimationShow);
                    }
                }
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
                    ShareItems.getInstance().getPost().setMessage(s.toString());
                } else {
                    clearTextSelectImgvFilled();
                    ShareItems.getInstance().getPost().setMessage("");
                }
            }
        });

        photoSelectImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedType = IMAGE_TYPE;
                DialogBoxUtil.photoChosenForShareDialogBox(getContext(), isPhotoSelected, new PhotoChosenForShareCallback() {
                    @Override
                    public void onGallerySelected() {
                        checkGalleryProcess();
                    }

                    @Override
                    public void onCameraSelected() {
                        checkCameraProcess();
                    }

                    @Override
                    public void onPhotoRemoved() {
                        photoSelectUtil = null;
                        isPhotoSelected = false;
                        ShareItems.getInstance().clearImageShareItemBox();
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
                getActivity().onBackPressed();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                if (!checkShareItems.shareIsPossible()) {
                    CommonUtils.showToast(getContext(), checkShareItems.getErrMessage());
                    return;
                }
                sharePost();
            }
        });

        showMapImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resizeMapAnimationShow != null && keyboardIsVisible) {
                    CommonUtils.hideKeyBoard(getContext());
                    mapLayout.startAnimation(resizeMapAnimationShow);
                }
            }
        });

        videoSelectImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedType = VIDEO_TYPE;
                DialogBoxUtil.videoChosenForShareDialogBox(getContext(), isVideoSelected, new VideoChosenForShareCallback() {
                    @Override
                    public void onGallerySelected() {
                        checkGalleryProcess();
                    }

                    @Override
                    public void onCameraSelected() {
                        checkCameraProcess();
                    }

                    @Override
                    public void onVideoRemoved() {
                        videoSelectUtil = null;
                        isVideoSelected = false;
                        ShareItems.getInstance().clearVideoShareItemBox();
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
                ShareItems.getInstance().setSelectedShareType(selectedWhomType);
                ShareItemsDescTv.setText(getResources().getString(R.string.publicShareText));
            }
        });

        allFollowersSelectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedWhomType = SHARE_TYPE_ALL_FOLLOWERS;
                openWhomSelection();
                ShareItems.getInstance().setSelectedShareType(selectedWhomType);
                ShareItemsDescTv.setText(getResources().getString(R.string.allFollowersShareText));
            }
        });

        specialSelectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedWhomType = SHARE_TYPE_CUSTOM;

                if (resizeMapAnimationShow != null && keyboardIsVisible) {
                    CommonUtils.hideKeyBoard(getContext());
                    mapLayout.startAnimation(resizeMapAnimationShow);
                }

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

                if (resizeMapAnimationShow != null && keyboardIsVisible) {
                    CommonUtils.hideKeyBoard(getContext());
                    mapLayout.startAnimation(resizeMapAnimationShow);
                }

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
                ShareItems.getInstance().setSelectedShareType(selectedWhomType);
                ShareItemsDescTv.setText(getResources().getString(R.string.justMeShareText));
            }
        });
    }

    public void sharePost() {
        ShareItems.getShareItemsInstance().setShareStartedValue(true);
        getActivity().onBackPressed();
        startToShare();
    }

    public void startToShare() {
        int tryCount = ShareItems.getInstance().getShareTryCount();
        ShareItems.getInstance().setShareTryCount(tryCount + 1);

        new SharePostProcess(NextActivity.thisActivity, new ServiceCompleteCallback() {
            @Override
            public void onSuccess() {
                ShareItems.setInstance(null);
            }

            @Override
            public void onFailed(Exception e) {
                if (ShareItems.getInstance().getShareTryCount() <= SHARE_TRY_COUNT) {
                    if (NextActivity.thisActivity != null && ShareItems.getInstance() != null) {
                        DialogBoxUtil.showYesNoDialog(NextActivity.thisActivity, null,
                                NextActivity.thisActivity.getResources().getString(R.string.DEFAULT_POST_ERROR_MESSAGE)
                                , new YesNoDialogBoxCallback() {
                                    @Override
                                    public void yesClick() {
                                        startToShare();
                                    }

                                    @Override
                                    public void noClick() {
                                        deleteUploadedItems();
                                    }
                                });
                    }
                } else {
                    CommonUtils.showToast(NextActivity.thisActivity,
                            NextActivity.thisActivity.getResources().getString(R.string.SHARE_IS_UNSUCCESSFUL));
                    deleteUploadedItems();
                }
            }
        });
    }

    public void deleteUploadedItems() {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                if (ShareItems.getInstance() != null && ShareItems.getInstance().getBucketUploadResponse() != null) {
                    SignedUrlDeleteProcess signedUrlDeleteProcess = new SignedUrlDeleteProcess(new OnEventListener() {
                        @Override
                        public void onSuccess(Object object) {
                            ShareItems.setInstance(null);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            ShareItems.setInstance(null);
                        }

                        @Override
                        public void onTaskContinue() {

                        }
                    }, AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid(),
                            token,
                            ShareItems.getInstance().getBucketUploadResponse());
                    signedUrlDeleteProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });
    }

    public void openWhomSelection() {
        if (resizeMapAnimationShow != null && keyboardIsVisible) {
            CommonUtils.hideKeyBoard(getContext());
            mapLayout.startAnimation(resizeMapAnimationShow);
        }

        if (!shareWhomOpened) {
            setShowAnimations();
            shareWhomOpened = true;
        } else
            setHideAnimations();
    }

    public void setShowAnimations() {
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
        shareWhomOpened = false;
    }

    private void startGroupManagementFragment() {
        if (mFragmentNavigation != null) {
            //viewCreateAgain = false;
            mFragmentNavigation.pushFragment(new GroupManagementFragment(GROUP_OP_CHOOSE_TYPE,
                            new ReturnCallback() {
                                @Override
                                public void onReturn(Object object) {
                                    GroupRequestResultResultArrayItem selectedGroup = (GroupRequestResultResultArrayItem) object;

                                    if (selectedGroup != null) {
                                        selectedWhomType = SHARE_TYPE_GROUP;
                                        ShareItems.getInstance().setSelectedShareType(selectedWhomType);
                                        ShareItems.getInstance().setSelectedGroup(selectedGroup);
                                        ShareItemsDescTv.setText(selectedGroup.getName());
                                        setHideAnimations();
                                    }


                                }
                            }),
                    ANIMATE_RIGHT_TO_LEFT);
        }
    }

    private void startSelectFriendFragment() {
        if (mFragmentNavigation != null) {
            //viewCreateAgain = false;
            mFragmentNavigation.pushFragment(new SelectFriendFragment(null, null,
                    SharePostFragment.class.getName(),
                    new ReturnCallback() {
                        @Override
                        public void onReturn(Object object) {

                            AccountHolderFollowProcess.getFollowers(new CompleteCallback() {
                                @Override
                                public void onComplete(Object object) {
                                    if (object != null) {
                                        FriendList friendList = (FriendList) object;
                                        if (friendList != null && friendList.getResultArray() != null) {
                                            if (friendList.getResultArray().size() == SelectedFriendList.getInstance().getSize()) {
                                                selectedWhomType = SHARE_TYPE_ALL_FOLLOWERS;
                                                ShareItems.getInstance().setSelectedShareType(selectedWhomType);
                                                ShareItemsDescTv.setText(getResources().getString(R.string.allFollowersShareText));
                                                setHideAnimations();
                                            } else {
                                                selectedWhomType = SHARE_TYPE_CUSTOM;
                                                ShareItems.getInstance().setSelectedShareType(selectedWhomType);
                                                ShareItemsDescTv.setText(getSelectedFriendsText());
                                                setHideAnimations();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onFailed(Exception e) {
                                }
                            });
                        }
                    }), ANIMATE_RIGHT_TO_LEFT);
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
            if (selectedType.equals(IMAGE_TYPE))
                startActivityForResult(Intent.createChooser(IntentSelectUtil.getGalleryIntent(),
                        getContext().getResources().getString(R.string.selectPicture)), REQUEST_CODE_PHOTO_GALLERY_SELECT);
            else if (selectedType.equals(VIDEO_TYPE))
                startGalleryForVideos();
        } else
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    permissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE);
    }

    private void checkCameraProcess() {
        if (!CommonUtils.checkCameraHardware(getContext())) {
            CommonUtils.showToast(getContext(), getContext().getResources().getString(R.string.deviceHasNoCamera));
            return;
        }

        if (permissionModule.checkCameraPermission()) {
            if (selectedType.equals(IMAGE_TYPE))
                startActivityForResult(IntentSelectUtil.getCameraIntent(), REQUEST_CODE_PHOTO_CAMERA_SELECT);
            else if (selectedType.equals(VIDEO_TYPE)) {
                startCameraForVideos();
            }
        } else
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    permissionModule.PERMISSION_CAMERA);
    }

    public void startGalleryForVideos() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                getContext().getResources().getString(R.string.SELECT_VIDEO)), REQUEST_CODE_VIDEO_GALLERY_SELECT);
    }

    public void startCameraForVideos() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAX_VIDEO_DURATION);

        if (takeVideoIntent.resolveActivity(getContext().getPackageManager()) != null)
            startActivityForResult(takeVideoIntent, REQUEST_CODE_VIDEO_CAMERA_SELECT);
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
        ShareItemsDescTv.setText(getResources().getString(R.string.publicShareText));
        selectedWhomType = SHARE_TYPE_EVERYONE;
        ShareItems.getInstance().setSelectedShareType(selectedWhomType);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == permissionModule.PERMISSION_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeMap(mMap);
            }
        } else if (requestCode == permissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (selectedType.equals(IMAGE_TYPE))
                    startActivityForResult(Intent.createChooser(IntentSelectUtil.getGalleryIntent(),
                            getContext().getResources().getString(R.string.selectPicture)), REQUEST_CODE_PHOTO_GALLERY_SELECT);
                else if (selectedType.equals(VIDEO_TYPE))
                    startGalleryForVideos();
            }
        } else if (requestCode == permissionModule.PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (selectedType.equals(IMAGE_TYPE))
                    startActivityForResult(IntentSelectUtil.getCameraIntent(), REQUEST_CODE_PHOTO_CAMERA_SELECT);
                else if (selectedType.equals(VIDEO_TYPE)) {
                    startCameraForVideos();
                }
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
                photoSelectUtil = new PhotoSelectUtil(getContext(), data, CAMERA_TEXT);
                startPhotoSelectedFragment();
            } else if (requestCode == REQUEST_CODE_VIDEO_GALLERY_SELECT) {
                checkVideoDuration(data);
            } else if (requestCode == REQUEST_CODE_VIDEO_CAMERA_SELECT) {
                setVideoFromCameraSelection(data);
            }
        }

        if (requestCode == REQUEST_CODE_ENABLE_LOCATION) {
            if (locationTrackObj.canGetLocation())
                initializeMap(mMap);
        }
    }

    public void checkVideoDuration(Intent data) {
        Uri uri = data.getData();
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(UriAdapter.getPathFromGalleryUri(getActivity(), uri));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time);

        if (timeInMillisec > ((MAX_VIDEO_DURATION + 1) * 1000)) {
            DialogBoxUtil.showInfoDialogBox(getContext(),
                    getActivity().getResources().getString(R.string.videoDurationWarning) + Integer.toString(MAX_VIDEO_DURATION) +
                            getContext().getResources().getString(R.string.secondShort)
                    , null, new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {

                        }
                    });
        } else {
            isVideoSelected = true;
            videoSelectUtil = new VideoSelectUtil(getActivity(), data.getData(), null, GALLERY_TEXT);
            addVideoShareItemList();
            setVideoSelectImgvFilled();
            startVideoViewFragment();
        }
    }

    public void setVideoFromCameraSelection(Intent data) {
        isVideoSelected = true;
        videoSelectUtil = new VideoSelectUtil(getActivity(), data.getData(), null, CAMERA_TEXT);
        addVideoShareItemList();
        setVideoSelectImgvFilled();
        startVideoViewFragment();
    }

    public void startVideoViewFragment() {
        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new VideoViewFragment(videoSelectUtil.getVideoUri()), ANIMATE_RIGHT_TO_LEFT);
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
                        location.getLongitude()), 12));
            }
        }
    }

    public void setShareItemsLocation(Location location) {
        catchu.model.Location tempLoc = new catchu.model.Location();
        tempLoc.setLongitude(BigDecimal.valueOf(location.getLongitude()));
        tempLoc.setLatitude(BigDecimal.valueOf(location.getLatitude()));
        ShareItems.getInstance().getPost().setLocation(tempLoc);
    }

    public void fillImageShareItemBox() {
        ImageShareItemBox imageShareItemBox = new ImageShareItemBox(photoSelectUtil);
        ShareItems.getInstance().clearImageShareItemBox();
        ShareItems.getInstance().addImageShareItemBox(imageShareItemBox);
    }

    public void addVideoShareItemList() {
        ShareItems.getInstance().clearVideoShareItemBox();
        VideoShareItemBox videoShareItemBox = new VideoShareItemBox(videoSelectUtil);
        ShareItems.getInstance().addVideoShareItemBox(videoShareItemBox);
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
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CODE_ENABLE_LOCATION);
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

    public void startPhotoSelectedFragment() {
        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new PhotoSelectedFragment(photoSelectUtil, new ReturnCallback() {
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
        publicImgv.setColorFilter(getContext().getResources().getColor(R.color.DarkYellow, null), PorterDuff.Mode.SRC_IN);
        allFollowersImgv.setColorFilter(getContext().getResources().getColor(R.color.DarkYellow, null), PorterDuff.Mode.SRC_IN);
        specialImgv.setColorFilter(getContext().getResources().getColor(R.color.DarkYellow, null), PorterDuff.Mode.SRC_IN);
        groupsImgv.setColorFilter(getContext().getResources().getColor(R.color.DarkYellow, null), PorterDuff.Mode.SRC_IN);
        justMeImgv.setColorFilter(getContext().getResources().getColor(R.color.DarkYellow, null), PorterDuff.Mode.SRC_IN);
    }

    public void setPhotoSelectImgvFilled() {
        //photoSelectImgv.setColorFilter(getContext().getResources().getColor(R.color.LimeGreen, null), PorterDuff.Mode.SRC_IN);
        photoSelectImgv.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.MediumSeaGreen, null),
                0,
                GradientDrawable.RECTANGLE, 20, 3));
    }

    public void setVideoSelectImgvFilled() {
        videoSelectImgv.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.MediumSeaGreen, null),
                0,
                GradientDrawable.RECTANGLE, 20, 3));
    }

    public void setTextSelectImgvFilled() {
        textSelectImgv.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.MediumSeaGreen, null),
                0,
                GradientDrawable.RECTANGLE, 20, 3));
    }

    public void clearPhotoSelectImgvFilled() {
        photoSelectImgv.setBackground(null);
        /*photoSelectImgv.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.Gray, null),
                0,
                GradientDrawable.RECTANGLE, 20, 3));*/
    }

    public void clearVideoSelectImgvFilled() {
        videoSelectImgv.setBackground(null);
        /*videoSelectImgv.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.Gray, null),
                0,
                GradientDrawable.RECTANGLE, 20, 3));*/
    }

    public void clearTextSelectImgvFilled() {
        textSelectImgv.setBackground(null);
        /*textSelectImgv.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.Gray, null),
                0,
                GradientDrawable.RECTANGLE, 20, 3));*/
    }
}
