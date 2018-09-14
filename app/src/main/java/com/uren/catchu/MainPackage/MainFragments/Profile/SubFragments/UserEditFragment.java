package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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

import com.squareup.picasso.Picasso;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.UpdateUserProfile;
import com.uren.catchu.GeneralUtils.CircleTransform;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.PhotoSelectAdapter;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.denemeee.ToastUtils;
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

import static com.uren.catchu.Constants.NumericConstants.UPDATE_RESULT_FAIL;
import static com.uren.catchu.Constants.NumericConstants.UPDATE_RESULT_OK;
import static com.uren.catchu.Constants.StringConstants.AnimateLeftToRight;

import static com.uren.catchu.Constants.StringConstants.SPACE_VALUE;
import static com.uren.catchu.Constants.StringConstants.USER_PROFILE_UPDATE;

public class UserEditFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private UserProfile userProfile;
    private UserProfile tempUser;
    private UserProfileProperties userProfileProperties ;

    private String selectedGender;
    private ArrayAdapter<String> genderSpinnerAdapter;

    //Change Image variables
    private String downloadUrl = SPACE_VALUE;
    private PhotoSelectAdapter photoSelectAdapter;
    private int adapterCameraSelected = 0;
    private int adapterGallerySelected = 1;
    private int photoChoosenType;
    private PermissionModule permissionModule;
    private  boolean profilPicChanged;

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

        //rlCoverPicture.setOnClickListener(this);
        rlProfilePicture.setOnClickListener(this);

        edtBirthDay.setOnClickListener(this);


        setBirthDayDataSetListener();
        setGenderClickListener();

        profilPicChanged = false;
        tempUser = new UserProfile();
        userProfileProperties = new UserProfileProperties();


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

        updateUI();

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


        // TODO : NT: Update Cover picture

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

        updateOperation();

    }

    private void updateOperation() {

        if(profilPicChanged){
            //savePicToS3_and_updateUserProfile(); //UNT3
        }else{
            updateUserProfile();
        }
    }
/*
    // TODO: 11.9.2018 NT: uğur buraları class yaptığını söyledi, implemente edilebliyosa et
    private void savePicToS3_and_updateUserProfile() {

        SignedUrlGetProcess signedUrlGetProcess = new SignedUrlGetProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                final BucketUploadResult commonS3BucketResult = (BucketUploadResult) object;

                Log.i("Info", "  >>commonS3BucketResult.getFileExtention():" + commonS3BucketResult.getImages().get(0).getExtension());
                Log.i("Info", "  >>commonS3BucketResult.getSignedUrl()    :" + commonS3BucketResult.getImages().get(0).getUploadUrl());
                Log.i("Info", "  >>commonS3BucketResult.getDownloadUrl()  :" + commonS3BucketResult.getImages().get(0).getDownloadUrl());
                Log.i("Info", "  >>commonS3BucketResult.getError()        :" + commonS3BucketResult.getError().getMessage());

                UploadImageToS3 uploadImageToS3 = new UploadImageToS3(new OnEventListener() {
                    @Override
                    public void onSuccess(Object object) {
                        HttpURLConnection urlConnection = (HttpURLConnection) object;

                        try {
                            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                                downloadUrl = commonS3BucketResult.getImages().get(0).getDownloadUrl();
                                userProfileProperties.setProfilePhotoUrl(downloadUrl);
                                Log.i("downloadUrl ", downloadUrl);
                                updateUserProfile();

                            } else {
                                InputStream is = urlConnection.getErrorStream();
                                //CommonUtils.showToast(context, is.toString());
                            }
                        } catch (IOException e) {
                            //dialogDismiss();
                            //CommonUtils.showToastLong(context, getResources().getString(R.string.error) + e.getMessage());
                        }

                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onTaskContinue() {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }, commonS3BucketResult.getImages().get(0).getUploadUrl(), );

                uploadImageToS3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onFailure(Exception e) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onTaskContinue() {
                progressBar.setVisibility(View.VISIBLE);
            }
        }, 1, 0);

        signedUrlGetProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
*/
    private void updateUserProfile() {

        tempUser.setUserInfo(userProfileProperties);
        tempUser.setRequestType(USER_PROFILE_UPDATE);

        Log.i("tempUser-Url ", tempUser.getUserInfo().getProfilePhotoUrl() );

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
                showResultDialog(UPDATE_RESULT_OK);

            }

            @Override
            public void onFailure(Exception e) {
                Log.i("update", "fail");
                progressBar.setVisibility(View.GONE);
                showResultDialog(UPDATE_RESULT_FAIL);

            }

            @Override
            public void onTaskContinue() {
                progressBar.setVisibility(View.VISIBLE);
            }
        }, tempUser);

        updateUserProfile.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void hideKeyBoard(){

        Log.i("Info", "hideKeyBoard");

        if(getActivity().getCurrentFocus() != null){
            InputMethodManager inputMethodManager =(InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }

    }

    private void showResultDialog(final int result) {

        hideKeyBoard();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("OPPS!");

        if(result == UPDATE_RESULT_OK){
            builder.setMessage("Profil başarıyla güncellendi");
        }
        else{
            builder.setIcon(R.drawable.toast_error_icon);
            builder.setMessage("Profil güncelleme başarısız");
        }

        builder.setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                if(result == UPDATE_RESULT_OK){
                    //Go back
                    ((NextActivity) getActivity()).ANIMATION_TAG = AnimateLeftToRight;
                    getActivity().onBackPressed();
                }
                else{
                    //do nothing
                }

            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }


    private void coverPictureClicked() {
    }

    private void profilePictureClicked() {

        /** #unt3
        permissionModule = new PermissionModule(getActivity());
        chooseImageProcess();
        */

        yeniTasarımDeneme();

    }

    private void yeniTasarımDeneme() {

        chooseImageProcess();
    }


    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;
    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 0x04;
    private File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/photo.jpg");
    private File fileCropUri = new File(Environment.getExternalStorageDirectory().getPath() + "/crop_photo.jpg");
    private Uri imageUri;
    private Uri cropImageUri;
    private static final int OUTPUT_X = 480;
    private static final int OUTPUT_Y = 480;

    private static final String TAG = "PhotoImageFragment";



    private void chooseImageProcess() {

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
                    autoObtainCameraPermission();

                } else if (item == adapterGallerySelected) {

                    photoChoosenType = adapterGallerySelected;
                    autoObtainStoragePermission();

                } else
                    CommonUtils.showToast(getActivity(), getResources().getString(R.string.technicalError));
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    /**
     * Dinamik uygulama -sdcard -Okuma ve yazma izinleri
     */
    private void autoObtainStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                openPic(getActivity(), CODE_GALLERY_REQUEST);
            }
        } else {
            openPic(getActivity(), CODE_GALLERY_REQUEST);
        }
    }

    /**
     * Kamera izinlerine erişim isteme
     */
    private void autoObtainCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat
                    .checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat
                    .checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                    ToastUtils.showShort(getActivity(), "Bir kere reddettin/Ayarlardan açınız");
                }
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS_REQUEST_CODE);
            } else {//Fotoğraf çekmek için sistem kamerasını doğrudan arama izni var
                if (hasSdcard()) {
                    imageUri = Uri.fromFile(fileUri);
                    //通过FileProvider创建一个content类型的Uri
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        imageUri = FileProvider.getUriForFile(getActivity(), "com.uren.catchu", fileUri);
                    }
                    takePicture(getActivity(), imageUri, CODE_CAMERA_REQUEST);
                } else {
                    ToastUtils.showShort(getActivity(), "Cihazda bir SD kart yok！");
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            /*Fotoğraf iznini geri aramak için sistem kamerasını çağırın.*/
            case CAMERA_PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (hasSdcard()) {
                        imageUri = Uri.fromFile(fileUri);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            //通过FileProvider创建一个content类型的Uri
                            imageUri = FileProvider.getUriForFile(getActivity(), "com.uren.catchu", fileUri);
                        }
                        takePicture(getActivity(), imageUri, CODE_CAMERA_REQUEST);
                    } else {
                        ToastUtils.showShort(getActivity(), "Cihazda external storage yok！");
                    }
                } else {
                    ToastUtils.showShort(getActivity(), "Lütfen kameranın açılmasına izin verin！！");
                }
                break;
            }
            //Sistem fotoğraf albümü uygulamasını çağırın -Sdcard - İzin geri araması
            case STORAGE_PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openPic(getActivity(), CODE_GALLERY_REQUEST);
                } else {
                    ToastUtils.showShort(getActivity(), "Lütfen işleme izin verin SDCard！！");
                }
                break;
            default:
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != -1) {
            Log.e(TAG, "onActivityResult: resultCode!=RESULT_OK");
            return;
        }
        switch (requestCode) {
            //Kamera dönüş
            case CODE_CAMERA_REQUEST:
                cropImageUri = Uri.fromFile(fileCropUri);
                cropImageUri(getActivity(), imageUri, cropImageUri, 1, 1, OUTPUT_X, OUTPUT_Y, CODE_RESULT_REQUEST);
                break;
            //Albüm dönüşü
            case CODE_GALLERY_REQUEST:

                if (hasSdcard()) {
                    cropImageUri = Uri.fromFile(fileCropUri);
                    Uri newUri = Uri.parse(getPath(getActivity(), data.getData()));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        newUri = FileProvider.getUriForFile(getContext(), "com.uren.catchu", new File(newUri.getPath()));
                    }
                    cropImageUri(getActivity(), newUri, cropImageUri, 1, 1, OUTPUT_X, OUTPUT_Y, CODE_RESULT_REQUEST);
                } else {
                    ToastUtils.showShort(getActivity(), "Ekipman yok SD卡！");
                }
                break;
            //result
            case CODE_RESULT_REQUEST:
                Bitmap bitmap = getBitmapFromUri(cropImageUri, getActivity());
                if (bitmap != null) {
                    showImages(bitmap);
                }
                break;
            default:
        }
    }

    private void showImages(Bitmap bitmap) {
        imgProfile.setImageBitmap(bitmap);
    }

    /**
     * Cihazın mevcut olup olmadığını kontrol et SDCard
     Araç yöntemi
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }


    public void takePicture(Activity activity, Uri imageUri, int requestCode) {
        //调用系统相机
        Intent intentCamera = new Intent();
        intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        //Fotoğraf sonuçlarını kaydet photo_file的Uri中，Albümde saklanmıyor
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intentCamera, requestCode);
    }

    /**
     * @param fragment    当前fragment
     * @param requestCode Albüm için istek kodunu aç
     */
    public void openPic(Activity fragment, int requestCode) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, requestCode);
    }
    /**
     * @param fragment    当前fragment
     * @param orgUri      剪裁原图的Uri
     * @param desUri      剪裁后的图片的Uri
     * @param aspectX     X方向的比例
     * @param aspectY     Y方向的比例
     * @param width       剪裁图片的宽度
     * @param height      剪裁图片高度
     * @param requestCode 剪裁图片的请求码
     */
    public  void cropImageUri(Activity fragment, Uri orgUri, Uri desUri, int aspectX, int aspectY, int width, int height, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(orgUri, "image/*");
        //发送裁剪信号
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("scale", true);
        //将剪切的图片保存到目标Uri中
        intent.putExtra(MediaStore.EXTRA_OUTPUT, desUri);
        //1-false用uri返回图片
        //2-true直接用bitmap返回图片（此种只适用于小图片，返回图片过大会报错）
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 读取uri所在的图片
     *
     * @param uri      图片对应的Uri
     * @param mContext 上下文对象
     * @return 获取图像的Bitmap
     */
    public static Bitmap getBitmapFromUri(Uri uri, Context mContext) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param context 上下文对象
     * @param uri     当前相册照片的Uri
     * @return 解析后的Uri对应的String
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        String pathHead = "file:///";
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return pathHead + Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);

                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return pathHead + getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return pathHead + getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return pathHead + getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return pathHead + uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }




/** denemek için kapatıldı
 *
    private void chooseImageProcess() {

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

            profilPicChanged = true;

            if (requestCode == permissionModule.getCameraPermissionCode()) {
                photoSelectAdapter = new PhotoSelectAdapter((NextActivity) getActivity(), data, CAMERA_TEXT);
                manageProfilePicChoosen();
            } else if (requestCode == permissionModule.getImageGalleryPermission()) {
                photoSelectAdapter = new PhotoSelectAdapter((NextActivity) getActivity(), data, GALLERY_TEXT);
                manageProfilePicChoosen();
            } else
                CommonUtils.showToast(getActivity(), getResources().getString(R.string.technicalError) + requestCode);
        }
    }


    private void manageProfilePicChoosen() {


        Glide.with(getActivity())
                .load(photoSelectAdapter.getPictureUri())
                .apply(RequestOptions.circleCropTransform())
                .into(imgProfile);


    }


    public void savePicToS3() {


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

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
*/
}