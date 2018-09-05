package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.UpdateUserProfile;
import com.uren.catchu.GeneralUtils.BitmapConversion;
import com.uren.catchu.GeneralUtils.CircleTransform;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.UriAdapter;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.ProfileFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Calendar;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.UserProfile;
import catchu.model.UserProfileProperties;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.uren.catchu.Constants.StringConstants.AnimateLeftToRight;

public class UserEditFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private UserProfile userProfile;

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

    String selectedGender;
    ArrayAdapter<String> genderSpinnerAdapter;

    //Change Image variables
    private int adapterCameraSelected = 0;
    private int adapterGallerySelected = 1;
    public int photoChoosenType;
    PermissionModule permissionModule;
    private Bitmap groupPhotoBitmap = null;
    private Bitmap getGroupPhotoBitmapOrjinal = null;
    private Uri groupPictureUri = null;
    private String imageRealPath;
    private InputStream profileImageStream;

    public UserEditFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.profile_subfragment_user_edit, container, false);
        ButterKnife.bind(this, mView);

        setUpToolbar();

        return mView;
    }

    private void setUpToolbar() {

        txtSave.setOnClickListener(this);
        txtCancel.setOnClickListener(this);

        //rlCoverPicture.setOnClickListener(this);
        rlProfilePicture.setOnClickListener(this);

        edtBirthDay.setOnClickListener(this);


        setBirthDayDataSetListener();
        setGenderClickListener();


    }

    private void setBirthDayDataSetListener() {

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                //Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

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
        //setUpRecyclerView();

        updateUI();

        /*
        Button btn = (Button) view.findViewById(R.id.btnBack);
        Button btnbtnNextFrag = (Button) view.findViewById(R.id.btnNextFrag);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnbtnNextFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mFragmentNavigation != null) {

                    mFragmentNavigation.pushFragment(new UserEditFragment(), AnimateRightToLeft);

                    //mFragmentNavigation.pushFragment(new UserEditFragment());

                }
            }
        });
        */
    }

    private void updateUI() {

        userProfile = AccountHolderInfo.getInstance().getUser();

        edtName.setText(userProfile.getUserInfo().getName());
        edtUserName.setText(userProfile.getUserInfo().getUsername());
        edtWebsite.setText(userProfile.getUserInfo().getWebsite());
        edtBirthDay.setText(userProfile.getUserInfo().getBirthday());
        edtEmail.setText(userProfile.getUserInfo().getEmail());
        edtPhone.setText(userProfile.getUserInfo().getPhone());

        genderSpinner.setSelection(genderSpinnerAdapter.getPosition(userProfile.getUserInfo().getGender()));
        selectedGender = userProfile.getUserInfo().getGender();


        // TODO : Update Cover picture


        //Profile picture
        Picasso.with(getActivity())
                //.load(userProfile.getResultArray().get(0).getProfilePhotoUrl())
                .load(AccountHolderInfo.getInstance().getUser().getUserInfo().getProfilePhotoUrl())
                .transform(new CircleTransform())
                .into(imgProfile);


    }

    @Override
    public void onClick(View v) {

        if (v == txtCancel) {
            editProfileCancelClicked();
        }

        if (v == txtSave) {
            editProfileConfirmClicked();
        }

/*
        if (v == rlCoverPicture) {
            coverPictureClicked();
        }
*/
        if (v == rlProfilePicture) {
            profilePictureClicked();
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

        ((NextActivity) getActivity()).ANIMATION_TAG = AnimateLeftToRight;
        getActivity().onBackPressed();

    }

    UserProfileProperties userProfileProperties = new UserProfileProperties();
    private void editProfileConfirmClicked() {

        final UserProfile userProfile = AccountHolderInfo.getInstance().getUser();

        userProfileProperties.setUserid(userProfile.getUserInfo().getUserid());
        userProfileProperties.setProfilePhotoUrl(userProfile.getUserInfo().getProfilePhotoUrl());

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

        UserProfile tempUser = new UserProfile();
        tempUser.setUserInfo(userProfileProperties);
        tempUser.setRequestType("USER_PROFILE_UPDATE");

        updateUserProfile(tempUser);

    }

    private void updateUserProfile(UserProfile tempUser) {

        //Asenkron Task başlatır.
        UpdateUserProfile updateUserProfile = new UpdateUserProfile(getActivity(), new OnEventListener<UserProfile>() {

            @Override
            public void onSuccess(UserProfile up) {

                if (up != null) {

                    userProfile.getUserInfo().setName(up.getUserInfo().getName());
                    userProfile.getUserInfo().setUsername(up.getUserInfo().getUsername());
                    userProfile.getUserInfo().setWebsite(up.getUserInfo().getWebsite());
                    userProfile.getUserInfo().setBirthday(up.getUserInfo().getBirthday());
                    userProfile.getUserInfo().setEmail(up.getUserInfo().getEmail());
                    userProfile.getUserInfo().setPhone(up.getUserInfo().getPhone());
                    userProfile.getUserInfo().setGender(up.getUserInfo().getGender());
                    userProfile.getUserInfo().setProfilePhotoUrl(up.getUserInfo().getProfilePhotoUrl());


                }

                progressBar.setVisibility(View.GONE);

                //Go back
                ((NextActivity) getActivity()).ANIMATION_TAG = AnimateLeftToRight;
                getActivity().onBackPressed();
            }

            @Override
            public void onFailure(Exception e) {
                Log.i("update", "fail");
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onTaskContinue() {
                progressBar.setVisibility(View.VISIBLE);
            }
        }, tempUser);

        updateUserProfile.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


    }


    private void coverPictureClicked() {


    }

    private void profilePictureClicked() {

        permissionModule = new PermissionModule(getActivity());

        startChooseImageProc();

    }


    private void startChooseImageProc() {

        Log.i("Info", "startChooseImageProc++++++++++++++++++++++++++++++++");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        adapter.add("  " + getResources().getString(R.string.openCamera));
        adapter.add("  " + getResources().getString(R.string.openGallery));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getResources().getString(R.string.chooseProfilePhoto));

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                if (item == adapterCameraSelected) {

                    photoChoosenType = adapterCameraSelected;
                    startCameraProcess();

                } else if (item == adapterGallerySelected) {

                    photoChoosenType = adapterGallerySelected;
                    startGalleryProcess();

                } else
                    CommonUtils.showToast(getActivity(), getResources().getString(R.string.technicalError));
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void startCameraProcess() {

        if (!CommonUtils.checkCameraHardware(getActivity())) {
            CommonUtils.showToast(getActivity(), getResources().getString(R.string.deviceHasNoCamera));
            return;
        }

        if (!permissionModule.checkWriteExternalStoragePermission())
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionModule.getWriteExternalStoragePermissionCode());
        else
            checkCameraPermission();
    }

    public void checkCameraPermission() {
        if (!permissionModule.checkCameraPermission())
            requestPermissions(new String[]{Manifest.permission.CAMERA}, permissionModule.getCameraPermissionCode());
        else {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(intent, permissionModule.getCameraPermissionCode());
        }
    }

    private void startGalleryProcess() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                getResources().getString(R.string.selectPicture)), permissionModule.getImageGalleryPermission());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == permissionModule.getCameraPermissionCode()) {
                manageProfilePicChoosen(data);
            } else if (requestCode == permissionModule.getImageGalleryPermission()) {
                manageProfilePicChoosen(data);
            } else
                CommonUtils.showToast(getActivity(), getResources().getString(R.string.technicalError) + requestCode);
        }
    }


    private void manageProfilePicChoosen(Intent data) {

        Log.i("Info", "manageProfilePicChoosen++++++++++++++++++++++++++++++++");

        if (photoChoosenType == adapterCameraSelected) {

            groupPhotoBitmap = (Bitmap) data.getExtras().get("data");
            getGroupPhotoBitmapOrjinal = groupPhotoBitmap;
            groupPictureUri = UriAdapter.getImageUri(getApplicationContext(), groupPhotoBitmap);
            imageRealPath = UriAdapter.getRealPathFromCameraURI(groupPictureUri, getActivity());
            groupPhotoBitmap = BitmapConversion.getRoundedShape(groupPhotoBitmap, 600, 600, imageRealPath);
            groupPhotoBitmap = BitmapConversion.getBitmapOriginRotate(groupPhotoBitmap, imageRealPath);

        } else if (photoChoosenType == adapterGallerySelected) {

            groupPictureUri = data.getData();
            imageRealPath = UriAdapter.getPathFromGalleryUri(getApplicationContext(), groupPictureUri);
            try {
                profileImageStream = getActivity().getContentResolver().openInputStream(groupPictureUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            groupPhotoBitmap = BitmapFactory.decodeStream(profileImageStream);
            getGroupPhotoBitmapOrjinal = groupPhotoBitmap;
            groupPhotoBitmap = BitmapConversion.getRoundedShape(groupPhotoBitmap, 600, 600, imageRealPath);
        }


        //Profile picture
        Picasso.with(getActivity())
                //.load(userProfile.getResultArray().get(0).getProfilePhotoUrl())
                .load(groupPictureUri)
                .transform(new CircleTransform())
                .into(imgProfile);

        userProfileProperties.setProfilePhotoUrl(groupPictureUri.toString());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.i("Info", "onRequestPermissionsResult+++++++++++++++++++++++++++++++++++++");

        if (requestCode == permissionModule.getWriteExternalStoragePermissionCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkCameraPermission();
            }
        } else if (requestCode == permissionModule.getCameraPermissionCode()) {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(intent, permissionModule.getCameraPermissionCode());
        } else
            CommonUtils.showToast(getActivity(), getResources().getString(R.string.technicalError) + requestCode);
    }


}