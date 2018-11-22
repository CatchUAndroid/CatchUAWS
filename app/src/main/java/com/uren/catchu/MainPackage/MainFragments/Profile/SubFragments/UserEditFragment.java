package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import com.squareup.picasso.Picasso;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.UpdateUserProfile;
import com.uren.catchu.GeneralUtils.CircleTransform;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.PhotoChosenCallback;
import com.uren.catchu.GeneralUtils.FileAdapter;
import com.uren.catchu.GeneralUtils.IntentUtil.IntentSelectUtil;
import com.uren.catchu.GeneralUtils.PhotoSelectUtils;
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

import java.io.File;

import java.util.Calendar;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.UserProfile;
import catchu.model.UserProfileProperties;

import static android.app.Activity.RESULT_OK;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.CAMERA_TEXT;
import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;
import static com.uren.catchu.Constants.StringConstants.IMAGE_TYPE;
import static com.uren.catchu.Constants.StringConstants.VIDEO_TYPE;


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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        addPhotoImgv.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.DodgerBlue, null),
                getResources().getColor(R.color.White, null), GradientDrawable.OVAL, 50, 5));
        imgProfile.setBackground(ShapeUtil.getShape(getActivity().getResources().getColor(R.color.DodgerBlue, null),
                0, GradientDrawable.OVAL, 50, 0));
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
        genderSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, GENDERS);
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderSpinnerAdapter);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGender = genderSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateUI();
    }

    private void updateUI() {

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
                genderSpinner.setSelection(genderSpinnerAdapter.getPosition(AccountHolderInfo.getInstance().getUser().getUserInfo().getGender()));
                selectedGender = AccountHolderInfo.getInstance().getUser().getUserInfo().getGender();
            }

            if(AccountHolderInfo.getInstance().getUser().getUserInfo().getProfilePhotoUrl() != null &&
                    !AccountHolderInfo.getInstance().getUser().getUserInfo().getProfilePhotoUrl().isEmpty())
                photoExist = true;
        }

        UserDataUtil.setProfilePicture2(getContext(),
                AccountHolderInfo.getInstance().getUser().getUserInfo().getProfilePhotoUrl(),
                AccountHolderInfo.getInstance().getUser().getUserInfo().getName(),
                AccountHolderInfo.getInstance().getUser().getUserInfo().getUsername(),
                shortUserNameTv, imgProfile);
    }

    private void setUserPhoto(Uri photoUri) {
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
                    .load(getActivity().getResources().getIdentifier("user_icon", "drawable", getActivity().getPackageName()))
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgProfile);
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
    }

    private void editProfileCancelClicked() {
        ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
        getActivity().onBackPressed();
    }

    private void editProfileConfirmClicked() {

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
            userProfileProperties.setGender("");
        } else {
            userProfileProperties.setGender(selectedGender);
        }

        updateOperation(userProfileProperties);
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
                photoSelectUtil = new PhotoSelectUtil();
                photoExist = false;
                setUserPhoto(null);
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
            CommonUtils.showToast(getContext(), getContext().getResources().getString(R.string.deviceHasNoCamera));
            return;
        }

        if (permissionModule.checkCameraPermission()) {
            startActivityForResult(IntentSelectUtil.getCameraIntent(), ACTIVITY_REQUEST_CODE_OPEN_CAMERA);
        } else
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    permissionModule.PERMISSION_CAMERA);
    }

    /*private void getCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat
                    .checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat
                    .checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                    CommonUtils.showToast(getContext(), "Bir kere reddettin/Ayarlardan açınız");
                }
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS_REQUEST_CODE);
            } else {//Fotoğraf çekmek için sistem kamerasını doğrudan arama izni var
                if (PhotoSelectUtils.hasExternalStorage()) {
                    imageUri = Uri.fromFile(fileUri);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        imageUri = FileProvider.getUriForFile(getActivity(), "com.uren.catchu", fileUri);
                    }
                    PhotoSelectUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
                } else {
                    CommonUtils.showToast(getContext(), "Cihazda bir SD kart yok");
                }
            }
        }
    }*/

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


        /*switch (requestCode) {

         *//*Fotoğraf iznini geri aramak için sistem kamerasını çağırın.*//*
            case CAMERA_PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (PhotoSelectUtils.hasExternalStorage()) {
                        imageUri = Uri.fromFile(fileUri);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            imageUri = FileProvider.getUriForFile(getActivity(), "com.uren.catchu", fileUri);
                        }
                        PhotoSelectUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
                    } else {
                        CommonUtils.showToast(getContext(), "Cihazda bir SD kart yok");
                    }
                } else {
                    CommonUtils.showToast(getContext(), "Lütfen kameranın açılmasına izin verin!!");
                }
                break;
            }
            //Sistem fotoğraf albümü uygulamasını çağırın -Sdcard - İzin geri araması
            case STORAGE_PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PhotoSelectUtils.openPic(this, CODE_GALLERY_REQUEST);
                } else {
                    CommonUtils.showToast(getContext(), "Lütfen işleme izin verin storage-izni!!");
                }
                break;
            default:
        }*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

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



       /* if (resultCode != RESULT_OK) {
            Log.e(TAG, "onActivityResult: resultCode!=RESULT_OK");
            return;
        }
        switch (requestCode) {
            //Kamera dönüş
            case CODE_CAMERA_REQUEST:
                cropImageUri = Uri.fromFile(FileAdapter.getCropMediaFile());
                PhotoSelectUtils.cropImageUri(this, imageUri, cropImageUri, 1, 1, OUTPUT_X, OUTPUT_Y, CODE_RESULT_REQUEST);
                break;
            //Albüm dönüşü
            case CODE_GALLERY_REQUEST:

                if (PhotoSelectUtils.hasExternalStorage()) {
                    cropImageUri = Uri.fromFile(FileAdapter.getCropMediaFile());
                    Uri newUri = Uri.parse(PhotoSelectUtils.getPath(getActivity(), data.getData()));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        newUri = FileProvider.getUriForFile(getActivity(), "com.uren.catchu", new File(newUri.getPath()));
                    }
                    PhotoSelectUtils.cropImageUri(this, newUri, cropImageUri, 1, 1, OUTPUT_X, OUTPUT_Y, CODE_RESULT_REQUEST);
                } else {
                    CommonUtils.showToast(getContext(), "External storage bulunamadı");
                }
                break;
            //result
            case CODE_RESULT_REQUEST:
                bitmap = PhotoSelectUtils.getBitmapFromUri(cropImageUri, getActivity());
                showImages(bitmap);
                break;

            default:
        }*/
    }

    /*private void showImages(Bitmap bitmap) {
        if (bitmap != null) {
            photoExist = true;
            profilPicChanged = true;
            shortUserNameTv.setVisibility(View.GONE);
            Glide.with(getActivity())
                    .load(bitmap)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgProfile);
        }
    }*/
}