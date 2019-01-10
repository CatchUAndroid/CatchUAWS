package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.PhotoChosenCallback;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.GeneralUtils.IntentUtil.IntentSelectUtil;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;

import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ServiceCompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;

import com.uren.catchu.MainPackage.MainFragments.Profile.Utils.UpdateUserProfileProcess;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.Calendar;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.CAMERA_TEXT;
import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;


public class UserEditFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.rlProfilePicture)
    RelativeLayout rlProfilePicture;
    @BindView(R.id.commonToolbarbackImgv)
    ClickableImageView commonToolbarbackImgv;
    @BindView(R.id.commonToolbarTickImgv)
    ImageView commonToolbarTickImgv;
    @BindView(R.id.toolbarTitleTv)
    TextView toolbarTitleTv;
    @BindView(R.id.imgProfile)
    ImageView imgProfile;
    @BindView(R.id.addPhotoImgv)
    ImageView addPhotoImgv;
    @BindView(R.id.shortUserNameTv)
    TextView shortUserNameTv;
    @BindView(R.id.edtName)
    EditText edtName;
    @BindView(R.id.edtUserName)
    EditText edtUserName;
    @BindView(R.id.edtWebsite)
    EditText edtWebsite;
    @BindView(R.id.edtBio)
    EditText edtBio;
    @BindView(R.id.edtBirthDay)
    EditText edtBirthDay;
    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.edtPhone)
    EditText edtPhone;
    @BindView(R.id.genderSpinner)
    Spinner genderSpinner;
    @BindArray(R.array.gender)
    String[] GENDERS;
    @BindArray(R.array.genderForServer)
    String[] GENDERS_FOR_SERVER;

    PermissionModule permissionModule;
    PhotoSelectUtil photoSelectUtil;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private String selectedGender;
    private ArrayAdapter<String> genderSpinnerAdapter;

    boolean profilPicChanged = false;
    boolean photoExist = false;

    private static final int ACTIVITY_REQUEST_CODE_OPEN_GALLERY = 385;
    private static final int ACTIVITY_REQUEST_CODE_OPEN_CAMERA = 85;

    public UserEditFragment() {
    }

    @Override
    public void onStart() {
        NextActivity.bottomTabLayout.setVisibility(View.GONE);
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_RIGHT_TO_LEFT;
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mView = inflater.inflate(R.layout.profile_subfragment_user_edit, container, false);
        ButterKnife.bind(this, mView);
        init();
        return mView;
    }

    private void init() {
        initListeners();
        setShapes();
        profilPicChanged = false;
        toolbarTitleTv.setText(getContext().getResources().getString(R.string.editProfile));
        commonToolbarTickImgv.setVisibility(View.VISIBLE);
        permissionModule = new PermissionModule(getContext());
        photoSelectUtil = new PhotoSelectUtil();
    }

    public void initListeners() {
        commonToolbarTickImgv.setOnClickListener(this);
        commonToolbarbackImgv.setOnClickListener(this);
        rlProfilePicture.setOnClickListener(this);
        edtBirthDay.setOnClickListener(this);
        edtPhone.setOnClickListener(this);
        setBirthDayDataSetListener();
        setGenderClickListener();
    }

    public void setShapes() {
        try {
            addPhotoImgv.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.DodgerBlue, null),
                    getResources().getColor(R.color.White, null), GradientDrawable.OVAL, 50, 5));
            imgProfile.setBackground(ShapeUtil.getShape(getActivity().getResources().getColor(R.color.DodgerBlue, null),
                    0, GradientDrawable.OVAL, 50, 0));
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void setBirthDayDataSetListener() {
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = month + "/" + day + "/" + year;
                edtBirthDay.setText(date);
            }
        };
    }

    private void setGenderClickListener() {
        try {
            genderSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, GENDERS);
            genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            genderSpinner.setAdapter(genderSpinnerAdapter);

            genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedGender = GENDERS[position];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateUI();
    }

    private void updateUI() {

        try {
            if (AccountHolderInfo.getInstance() != null && AccountHolderInfo.getInstance().getUser() != null &&
                    AccountHolderInfo.getInstance().getUser().getUserInfo() != null) {

                if (AccountHolderInfo.getInstance().getUser().getUserInfo().getName() != null &&
                        !AccountHolderInfo.getInstance().getUser().getUserInfo().getName().isEmpty()) {
                    edtName.setText(AccountHolderInfo.getInstance().getUser().getUserInfo().getName());
                    shortUserNameTv.setText(UserDataUtil.getShortenUserName(AccountHolderInfo.getInstance().getUser().getUserInfo().getName()));
                }
                if (AccountHolderInfo.getInstance().getUser().getUserInfo().getUsername() != null &&
                        !AccountHolderInfo.getInstance().getUser().getUserInfo().getUsername().isEmpty()) {
                    edtUserName.setText(AccountHolderInfo.getInstance().getUser().getUserInfo().getUsername());
                }
                if (AccountHolderInfo.getInstance().getUser().getUserInfo().getWebsite() != null &&
                        !AccountHolderInfo.getInstance().getUser().getUserInfo().getWebsite().isEmpty()) {
                    edtWebsite.setText(AccountHolderInfo.getInstance().getUser().getUserInfo().getWebsite());
                }

                if (AccountHolderInfo.getInstance().getUser().getUserInfo().getEmail() != null &&
                        !AccountHolderInfo.getInstance().getUser().getUserInfo().getEmail().isEmpty()) {
                    edtEmail.setText(AccountHolderInfo.getInstance().getUser().getUserInfo().getEmail());
                }
                if (AccountHolderInfo.getInstance().getUser().getUserInfo().getPhone() != null) {
                    if (AccountHolderInfo.getInstance().getUser().getUserInfo().getPhone().getDialCode() != null &&
                            !AccountHolderInfo.getInstance().getUser().getUserInfo().getPhone().getDialCode().isEmpty() &&
                            AccountHolderInfo.getInstance().getUser().getUserInfo().getPhone().getPhoneNumber() != null &&
                            !AccountHolderInfo.getInstance().getUser().getUserInfo().getPhone().getPhoneNumber().toString().trim().isEmpty())
                        edtPhone.setText(AccountHolderInfo.getInstance().getUser().getUserInfo().getPhone().getDialCode() +
                                AccountHolderInfo.getInstance().getUser().getUserInfo().getPhone().getPhoneNumber());
                }
                if (AccountHolderInfo.getInstance().getUser().getUserInfo().getBirthday() != null &&
                        !AccountHolderInfo.getInstance().getUser().getUserInfo().getBirthday().isEmpty()) {
                    edtBirthDay.setText(AccountHolderInfo.getInstance().getUser().getUserInfo().getBirthday());
                }
                if (AccountHolderInfo.getInstance().getUser().getUserInfo().getGender() != null &&
                        !AccountHolderInfo.getInstance().getUser().getUserInfo().getGender().isEmpty()) {
                    for(int i=0; i<GENDERS_FOR_SERVER.length; i++){
                        if(AccountHolderInfo.getInstance().getUser().getUserInfo().getGender().equals(GENDERS_FOR_SERVER[i])){
                            selectedGender = GENDERS[i];
                        }
                    }
                    genderSpinner.setSelection(genderSpinnerAdapter.getPosition(selectedGender));
                }

                if(AccountHolderInfo.getInstance().getUser().getUserInfo().getProfilePhotoUrl() != null &&
                        !AccountHolderInfo.getInstance().getUser().getUserInfo().getProfilePhotoUrl().isEmpty())
                    photoExist = true;
            }

            UserDataUtil.setProfilePicture(getContext(),
                    AccountHolderInfo.getInstance().getUser().getUserInfo().getProfilePhotoUrl(),
                    AccountHolderInfo.getInstance().getUser().getUserInfo().getName(),
                    AccountHolderInfo.getInstance().getUser().getUserInfo().getUsername(),
                    shortUserNameTv, imgProfile);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void setUserPhoto(Uri photoUri) {
        try {
            if (photoUri != null && !photoUri.toString().trim().isEmpty()) {
                photoExist = true;
                shortUserNameTv.setVisibility(View.GONE);
                Glide.with(getActivity())
                        .load(photoUri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imgProfile);
            } else if (AccountHolderInfo.getInstance().getUser().getUserInfo().getName() != null &&
                    !AccountHolderInfo.getInstance().getUser().getUserInfo().getName().trim().isEmpty()) {
                photoExist = false;
                shortUserNameTv.setVisibility(View.VISIBLE);
                imgProfile.setImageDrawable(null);
            } else {
                photoExist = false;
                shortUserNameTv.setVisibility(View.GONE);
                Glide.with(getActivity())
                        .load(getActivity().getResources().getIdentifier("icon_user_profile", "mipmap", getActivity().getPackageName()))
                        .apply(RequestOptions.circleCropTransform())
                        .into(imgProfile);
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        if (v == commonToolbarbackImgv) {
            editProfileCancelClicked();
        }

        if (v == commonToolbarTickImgv) {
            CommonUtils.hideKeyBoard(getActivity());
            editProfileConfirmClicked();
        }

        if (v == rlProfilePicture) {
            chooseImageProcess();
        }

        if (v == edtBirthDay) {
            birthDayClicked();
        }

        if (v == edtPhone) {
            startEditPhoneNumber();
        }
    }

    public void startEditPhoneNumber() {

        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new PhoneNumEditFragment(AccountHolderInfo.getInstance().getUser().getUserInfo().getPhone(), new CompleteCallback() {
                @Override
                public void onComplete(Object object) {
                    if (object != null) {
                        //updateUI();
                        /*String phoneNum = (String) object;
                        edtPhone.setText(phoneNum, TextView.BufferType.EDITABLE);*/
                    }
                }

                @Override
                public void onFailed(Exception e) {

                }
            }), ANIMATE_RIGHT_TO_LEFT);
        }
    }

    private void birthDayClicked() {
        try {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    getActivity(),
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    mDateSetListener,
                    year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void editProfileCancelClicked() {
        getActivity().onBackPressed();
    }

    private void editProfileConfirmClicked() {

        try {
            UserProfileProperties userProfileProperties = AccountHolderInfo.getInstance().getUser().getUserInfo();

            if (!photoExist)
                userProfileProperties.setProfilePhotoUrl("");
            else {
                userProfileProperties.setProfilePhotoUrl(AccountHolderInfo.getInstance().getUser().getUserInfo().getProfilePhotoUrl());
            }

            if (edtName.getText().toString().isEmpty()) {
                userProfileProperties.setName("");
            } else {
                userProfileProperties.setName(edtName.getText().toString());
            }

            if (edtUserName.getText().toString().isEmpty()) {
                userProfileProperties.setUsername("");
            } else {
                userProfileProperties.setUsername(edtUserName.getText().toString());
            }

            if (edtWebsite.getText().toString().isEmpty()) {
                userProfileProperties.setWebsite("");
            } else {
                userProfileProperties.setWebsite(edtWebsite.getText().toString());
            }

            if (edtBirthDay.getText().toString().isEmpty()) {
                userProfileProperties.setBirthday("");
            } else {
                userProfileProperties.setBirthday(edtBirthDay.getText().toString());
            }

            if (edtEmail.getText().toString().isEmpty()) {
                userProfileProperties.setEmail("");
            } else {
                userProfileProperties.setEmail(edtEmail.getText().toString());
            }

            userProfileProperties.setPhone(AccountHolderInfo.getInstance().getUser().getUserInfo().getPhone());

            if (selectedGender.isEmpty()) {
                userProfileProperties.setGender(GENDERS_FOR_SERVER[GENDERS_FOR_SERVER.length -1]);
            } else {

                for(int i=0; i<GENDERS.length; i++){
                    if(selectedGender.equals(GENDERS[i])){
                        userProfileProperties.setGender(GENDERS_FOR_SERVER[i]);
                    }
                }
            }

            updateOperation(userProfileProperties);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void updateOperation(UserProfileProperties userProfileProperties) {
        new UpdateUserProfileProcess(getActivity(), new ServiceCompleteCallback() {
            @Override
            public void onSuccess() {
                DialogBoxUtil.showSuccessDialogBox(getActivity(), getActivity().getResources().getString(R.string.profileUpdateSuccessful), null, new InfoDialogBoxCallback() {
                    @Override
                    public void okClick() {
                        editProfileCancelClicked();
                    }
                });
            }

            @Override
            public void onFailed(Exception e) {
                DialogBoxUtil.showErrorDialog(getActivity(), e.getMessage(), new InfoDialogBoxCallback() {
                    @Override
                    public void okClick() {
                    }
                });
                e.printStackTrace();
            }
        }, profilPicChanged, userProfileProperties, photoSelectUtil.getBitmap());
    }

    private void chooseImageProcess() {
        PhotoChosenCallback photoChosenCallback = new PhotoChosenCallback() {
            @Override
            public void onGallerySelected() {
                getGalleryPermission();
            }

            @Override
            public void onCameraSelected() {
                checkCameraProcess();
            }

            @Override
            public void onPhotoRemoved() {
                try {
                    photoSelectUtil = new PhotoSelectUtil();
                    photoExist = false;
                    setUserPhoto(null);
                } catch (Exception e) {
                    ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                            new Object() {
                            }.getClass().getEnclosingMethod().getName(), e.toString());
                    e.printStackTrace();
                }
            }
        };

        DialogBoxUtil.photoChosenDialogBox(getContext(), getActivity().getResources().getString(R.string.chooseProfilePhoto), photoExist, photoChosenCallback);
    }

    private void getGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!permissionModule.checkWriteExternalStoragePermission())
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE);
            else
                startActivityForResult(Intent.createChooser(IntentSelectUtil.getGalleryIntent(),
                        getResources().getString(R.string.selectPicture)), ACTIVITY_REQUEST_CODE_OPEN_GALLERY);
        } else
            startActivityForResult(Intent.createChooser(IntentSelectUtil.getGalleryIntent(),
                    getResources().getString(R.string.selectPicture)), ACTIVITY_REQUEST_CODE_OPEN_GALLERY);
    }

    private void checkCameraProcess() {
        if (!CommonUtils.checkCameraHardware(getContext())) {
            CommonUtils.showToastShort(getContext(), getContext().getResources().getString(R.string.deviceHasNoCamera));
            return;
        }

        if (permissionModule.checkCameraPermission()) {
            startActivityForResult(IntentSelectUtil.getCameraIntent(), ACTIVITY_REQUEST_CODE_OPEN_CAMERA);
        } else
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    permissionModule.PERMISSION_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == permissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(Intent.createChooser(IntentSelectUtil.getGalleryIntent(),
                        getResources().getString(R.string.selectPicture)), ACTIVITY_REQUEST_CODE_OPEN_GALLERY);
            }
        } else if (requestCode == permissionModule.PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(IntentSelectUtil.getCameraIntent(), ACTIVITY_REQUEST_CODE_OPEN_CAMERA);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == ACTIVITY_REQUEST_CODE_OPEN_GALLERY) {
                    photoSelectUtil = new PhotoSelectUtil(getActivity(), data, GALLERY_TEXT);
                    setUserPhoto(data.getData());
                    profilPicChanged = true;
                } else if (requestCode == ACTIVITY_REQUEST_CODE_OPEN_CAMERA) {
                    photoSelectUtil = new PhotoSelectUtil(getActivity(), data, CAMERA_TEXT);
                    setUserPhoto(data.getData());
                    profilPicChanged = true;
                }
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }
}