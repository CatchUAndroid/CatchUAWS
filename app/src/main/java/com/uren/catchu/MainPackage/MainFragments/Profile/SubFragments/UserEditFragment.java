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
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.PhotoChosenCallback;
import com.uren.catchu.GeneralUtils.FileAdapter;
import com.uren.catchu.GeneralUtils.PhotoSelectUtils;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.GeneralUtils.UriAdapter;
import com.uren.catchu.Interfaces.ServiceCompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.Utils.UpdateUserProfileProcess;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.ShareDetailActivity;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.UserProfile;
import catchu.model.UserProfileProperties;

import static android.app.Activity.RESULT_OK;
import static com.uren.catchu.Constants.NumericConstants.UPDATE_RESULT_FAIL;
import static com.uren.catchu.Constants.NumericConstants.UPDATE_RESULT_OK;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;

import static com.uren.catchu.Constants.StringConstants.SPACE_VALUE;
import static com.uren.catchu.Constants.StringConstants.USER_PROFILE_UPDATE;

public class UserEditFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private UserProfile userProfile;
    private UserProfileProperties userProfileProperties;

    private String selectedGender;
    private ArrayAdapter<String> genderSpinnerAdapter;

    //Change Image variables
    private boolean profilPicChanged;
    Bitmap bitmap;
    boolean photoExist = false;

    private static final String TAG = "PhotoImageFragment";

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.toolbarLayout)
    Toolbar mToolBar;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    //@BindView(R.id.rlCoverPicture)
    //RelativeLayout rlCoverPicture;
    @BindView(R.id.rlProfilePicture)
    RelativeLayout rlProfilePicture;

    @BindView(R.id.imgProfile)
    ImageView imgProfile;

    @BindView(R.id.txtCancel)
    TextView txtCancel;
    @BindView(R.id.txtSave)
    TextView txtSave;
    @BindView(R.id.shortUserNameTv)
    TextView shortUserNameTv;

    //Fields
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

    //@BindView(R.id.edtGender)
    EditText edtGender;

    @BindView(R.id.genderSpinner)
    Spinner genderSpinner;

    @BindArray(R.array.gender)
    String[] GENDERS;

    GradientDrawable imageShape;

    //todo : NT - request kodları permissionConstanstan çekilmeli
    // photo select variables
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;
    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 0x04;
    private File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/photo.jpg");
    //private File fileCropUri = new File(Environment.getExternalStorageDirectory().getPath() + "/crop_photo.jpg");
    private Uri imageUri;
    private Uri cropImageUri;
    private static final int OUTPUT_X = 480;
    private static final int OUTPUT_Y = 480;

    public UserEditFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.profile_subfragment_user_edit, container, false);
        ButterKnife.bind(this, mView);
        init();
        return mView;
    }

    private void init() {
        txtSave.setOnClickListener(this);
        txtCancel.setOnClickListener(this);
        rlProfilePicture.setOnClickListener(this);
        edtBirthDay.setOnClickListener(this);
        setBirthDayDataSetListener();
        setGenderClickListener();
        profilPicChanged = false;
        userProfileProperties = new UserProfileProperties();
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
                Log.i("Info", "selectedGender:" + selectedGender);
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
        userProfile = AccountHolderInfo.getInstance().getUser();
        UserProfileProperties userInfo = userProfile.getUserInfo();

        if(userInfo.getName() != null && !userInfo.getName().isEmpty()){
            edtName.setText(userInfo.getName());
            shortUserNameTv.setText(UserDataUtil.getShortenUserName(userProfile.getUserInfo().getName()));
        }
        if(userInfo.getUsername() != null && !userInfo.getUsername().isEmpty()){
            edtUserName.setText(userInfo.getUsername());
        }
        if(userInfo.getWebsite() != null && !userInfo.getWebsite().isEmpty()){
            edtWebsite.setText(userInfo.getWebsite());
        }
        if(userInfo.getBirthday() != null && !userInfo.getBirthday().isEmpty()){
            edtBirthDay.setText(userInfo.getUsername());
        }

        if(userInfo.getEmail() != null && !userInfo.getEmail().isEmpty()){
            edtEmail.setText(userInfo.getEmail());
        }
        if(userInfo.getPhone() != null && !userInfo.getPhone().isEmpty()){
            edtPhone.setText(userInfo.getPhone());
        }
        if(userInfo.getBirthday() != null && !userInfo.getBirthday().isEmpty()){
            edtBirthDay.setText(userInfo.getUsername());
        }
        if(userInfo.getGender() != null && !userInfo.getGender().isEmpty()){
            genderSpinner.setSelection(genderSpinnerAdapter.getPosition(userProfile.getUserInfo().getGender()));
            selectedGender = userProfile.getUserInfo().getGender();
        }

        imageShape = ShapeUtil.getShape(getActivity().getResources().getColor(R.color.DodgerBlue, null),
                0, GradientDrawable.OVAL, 50, 0);
        imgProfile.setBackground(imageShape);
        setUserPhoto(AccountHolderInfo.getInstance().getUser().getUserInfo().getProfilePhotoUrl());
        // TODO : NT: Update Cover picture
    }

    private void setUserPhoto(String url) {
        if (url != null && !url.trim().isEmpty()) {
            photoExist = true;
            shortUserNameTv.setVisibility(View.GONE);
            Glide.with(getActivity())
                    .load(url)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgProfile);
        } else if (userProfile.getUserInfo().getName() != null && !userProfile.getUserInfo().getName().trim().isEmpty()) {
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

        if (v == txtCancel) {
            editProfileCancelClicked();
        }

        if (v == txtSave) {
            CommonUtils.hideKeyBoard(getActivity());
            editProfileConfirmClicked();
        }

        if (v == rlProfilePicture) {
            chooseImageProcess();
        }

        if (v == edtBirthDay) {
            birthDayClicked();
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

        final UserProfile userProfile = AccountHolderInfo.getInstance().getUser();

        userProfileProperties.setUserid(userProfile.getUserInfo().getUserid());

        if (!photoExist)
            userProfileProperties.setProfilePhotoUrl("");
        else if (profilPicChanged) {
            userProfileProperties.setProfilePhotoUrl(cropImageUri.toString());
        } else {
            userProfileProperties.setProfilePhotoUrl(userProfile.getUserInfo().getProfilePhotoUrl());
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

        if (edtPhone.getText().toString().isEmpty()) {
            userProfileProperties.setPhone("");
        } else {
            userProfileProperties.setPhone(edtPhone.getText().toString());
        }

        if (selectedGender.isEmpty()) {
            userProfileProperties.setGender("");
        } else {
            userProfileProperties.setGender(selectedGender);
        }

        updateOperation();
    }

    private void updateOperation() {
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
        }, profilPicChanged, userProfileProperties, bitmap);
    }

    private void chooseImageProcess() {
        PhotoChosenCallback photoChosenCallback = new PhotoChosenCallback() {
            @Override
            public void onGallerySelected() {
                getGalleryPermission();
            }

            @Override
            public void onCameraSelected() {
                getCameraPermission();
            }

            @Override
            public void onPhotoRemoved() {
                setUserPhoto(null);
            }
        };

        DialogBoxUtil.photoChosenDialogBox(getContext(), getActivity().getResources().getString(R.string.chooseProfilePhoto), photoExist, photoChosenCallback);
    }

    private void getGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                PhotoSelectUtils.openPic(this, CODE_GALLERY_REQUEST);
            }
        } else {
            PhotoSelectUtils.openPic(this, CODE_GALLERY_REQUEST);
        }
    }

    private void getCameraPermission() {
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: ");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            /*Fotoğraf iznini geri aramak için sistem kamerasını çağırın.*/
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
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: requestCode: " + requestCode + "  resultCode:" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
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
        }
    }

    private void showImages(Bitmap bitmap) {
        if (bitmap != null) {
            photoExist = true;
            profilPicChanged = true;
            shortUserNameTv.setVisibility(View.GONE);
            Glide.with(getActivity())
                    .load(bitmap)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgProfile);
        }
    }
}