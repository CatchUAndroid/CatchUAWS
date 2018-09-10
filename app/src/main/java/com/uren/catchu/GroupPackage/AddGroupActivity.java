package com.uren.catchu.GroupPackage;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.http.HttpClient;
import com.amazonaws.http.HttpRequest;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.uren.catchu.Adapters.SpecialSelectTabAdapter;
import com.uren.catchu.ApiGatewayFunctions.GroupResultProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.SignedUrlGetProcess;
import com.uren.catchu.ApiGatewayFunctions.SingletonApiClient;
import com.uren.catchu.ApiGatewayFunctions.UploadImageToS3;
import com.uren.catchu.GeneralUtils.BitmapConversion;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.ExifUtil;
import com.uren.catchu.GeneralUtils.HttpHandler;
import com.uren.catchu.GeneralUtils.UriAdapter;
import com.uren.catchu.GroupPackage.Adapters.FriendGridListAdapter;
import com.uren.catchu.MainPackage.MainFragments.SearchTab.SearchFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.SelectedFriendList;
import com.uren.catchu.Singleton.UserGroups;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import butterknife.BindView;
import catchu.CatchUMobileAPIClient;
import catchu.model.BucketUploadResult;
import catchu.model.FriendList;
import catchu.model.GroupRequest;
import catchu.model.GroupRequestGroupParticipantArrayItem;
import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;
import catchu.model.UserProfileProperties;

import static com.amazonaws.auth.policy.Principal.WebIdentityProviders.Amazon;
import static com.uren.catchu.Constants.StringConstants.CREATE_GROUP;
import static com.uren.catchu.Constants.StringConstants.JPG_TYPE;
import static com.uren.catchu.Constants.StringConstants.SPACE_VALUE;
import static com.uren.catchu.Constants.StringConstants.gridShown;

public class AddGroupActivity extends AppCompatActivity {

    Toolbar mToolBar;
    //ViewPager viewPager;
    //TabLayout tabLayout;
    public static RecyclerView recyclerView;
    public static FriendGridListAdapter adapter;
    FloatingActionButton saveGroupInfoFab;
    EditText groupNameEditText;

    private ImageView groupPictureImgv;

    private static SelectedFriendList selectedFriendListInstance;

    public int photoChoosenType;

    private Bitmap groupPhotoBitmap = null;
    private Bitmap getGroupPhotoBitmapOrjinal = null;

    private Uri groupPictureUri = null;
    private String imageRealPath;
    private InputStream profileImageStream;

    ProgressDialog mProgressDialog;

    RelativeLayout addGroupDtlRelLayout;
    Context context;
    String downloadUrl = SPACE_VALUE;

    List<GroupRequestGroupParticipantArrayItem> participantArrayItems;
    GroupRequest groupRequest;

    PermissionModule permissionModule;

    private static final int adapterCameraSelected = 0;
    private static final int adapterGallerySelected = 1;

    TextView participantSize;

    //ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        mToolBar = (Toolbar) findViewById(R.id.toolbarLayout);
        mToolBar.setTitle(getResources().getString(R.string.addNewGroup));
        mToolBar.setSubtitle(getResources().getString(R.string.addGroupName));
        mToolBar.setNavigationIcon(R.drawable.back_arrow);
        mToolBar.setBackgroundColor(getResources().getColor(R.color.background, null));
        mToolBar.setTitleTextColor(getResources().getColor(R.color.background_white, null));
        mToolBar.setSubtitleTextColor(getResources().getColor(R.color.background_white, null));
        setSupportActionBar(mToolBar);

        mProgressDialog = new ProgressDialog(this);
        context = this;

        permissionModule = new PermissionModule(context);

        selectedFriendListInstance = SelectedFriendList.getInstance();

        initUI();
        addListeners();
        openPersonSelectionPage();
    }

    public void initUI(){
        participantSize = (TextView) findViewById(R.id.participantSize);
        groupPictureImgv = (ImageView) findViewById(R.id.groupPictureImgv);
        saveGroupInfoFab = (FloatingActionButton) findViewById(R.id.saveGroupInfoFab);
        groupNameEditText = (EditText) findViewById(R.id.groupNameEditText);
        addGroupDtlRelLayout = (RelativeLayout) findViewById(R.id.addGroupDtlRelLayout);
        recyclerView = findViewById(R.id.recyclerView);

        participantSize.setText(Integer.toString(selectedFriendListInstance.getSize()));
    }

    public void addListeners() {

        groupPictureImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChooseImageProc();
            }
        });

        saveGroupInfoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupNameEditText.getText().toString().equals("") || groupNameEditText.getText() == null) {
                    CommonUtils.showToast(AddGroupActivity.this, getResources().getString(R.string.pleaseWriteGroupName));
                    return;
                }

                mProgressDialog.setMessage(getResources().getString(R.string.groupIsCreating));
                dialogShow();

                if (getGroupPhotoBitmapOrjinal != null)
                    saveGroupToAmazon();
                else
                    processSaveGroup();
            }
        });

        addGroupDtlRelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard();
            }
        });
    }

    public void hideKeyBoard() {

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private void openPersonSelectionPage() {

        adapter = new FriendGridListAdapter(this, selectedFriendListInstance.getSelectedFriendList());
        recyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return super.onOptionsItemSelected(item);
    }

    private void startChooseImageProc() {

        Log.i("Info", "startChooseImageProc++++++++++++++++++++++++++++++++");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        adapter.add("  " + getResources().getString(R.string.openCamera));
        adapter.add("  " + getResources().getString(R.string.openGallery));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

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
                    CommonUtils.showToast(AddGroupActivity.this, getResources().getString(R.string.technicalError));
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void startCameraProcess() {

        if (!CommonUtils.checkCameraHardware(this)) {
            CommonUtils.showToast(AddGroupActivity.this, getResources().getString(R.string.deviceHasNoCamera));
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == permissionModule.getCameraPermissionCode()) {
                manageProfilePicChoosen(data);
            } else if (requestCode == permissionModule.getImageGalleryPermission()) {
                manageProfilePicChoosen(data);
            } else
                CommonUtils.showToast(AddGroupActivity.this, getResources().getString(R.string.technicalError) + requestCode);
        }
    }

    private void manageProfilePicChoosen(Intent data) {

        Log.i("Info", "manageProfilePicChoosen++++++++++++++++++++++++++++++++");

        if (photoChoosenType == adapterCameraSelected) {

            groupPhotoBitmap = (Bitmap) data.getExtras().get("data");
            groupPictureUri = data.getData();
            imageRealPath = UriAdapter.getPathFromGalleryUri(getApplicationContext(), groupPictureUri);
            getGroupPhotoBitmapOrjinal = ExifUtil.rotateImageIfRequired(imageRealPath, groupPhotoBitmap);
            groupPhotoBitmap = BitmapConversion.getRoundedShape(groupPhotoBitmap, 600, 600, imageRealPath);
        } else if (photoChoosenType == adapterGallerySelected) {

            groupPictureUri = data.getData();
            imageRealPath = UriAdapter.getPathFromGalleryUri(getApplicationContext(), groupPictureUri);
            try {
                profileImageStream = getContentResolver().openInputStream(groupPictureUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            groupPhotoBitmap = BitmapFactory.decodeStream(profileImageStream);
            getGroupPhotoBitmapOrjinal = ExifUtil.rotateImageIfRequired(imageRealPath, groupPhotoBitmap);
            groupPhotoBitmap = BitmapConversion.getRoundedShape(groupPhotoBitmap, 600, 600, imageRealPath);
        }

        groupPictureImgv.setImageBitmap(groupPhotoBitmap);
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
            CommonUtils.showToast(this, getResources().getString(R.string.technicalError) + requestCode);
    }

    public void processSaveGroup() {

        fillGroupParticipants();
        fillGroupDetail();
        saveGroupToNeoJ();
    }

    private void fillGroupParticipants() {

        participantArrayItems = new ArrayList<GroupRequestGroupParticipantArrayItem>();

        for (UserProfileProperties userProfileProperties : selectedFriendListInstance.getSelectedFriendList().getResultArray()) {
            GroupRequestGroupParticipantArrayItem group = new GroupRequestGroupParticipantArrayItem();
            group.setParticipantUserid(userProfileProperties.getUserid());
            participantArrayItems.add(group);
        }
    }

    private void fillGroupDetail() {

        groupRequest = new GroupRequest();
        groupRequest.setUserid(AccountHolderInfo.getUserID());
        groupRequest.setGroupName(groupNameEditText.getText().toString());
        groupRequest.setRequestType(CREATE_GROUP);
        groupRequest.setGroupParticipantArray(participantArrayItems);
        groupRequest.setGroupPhotoUrl(downloadUrl);
    }

    public void dialogShow() {
        if (!mProgressDialog.isShowing()) mProgressDialog.show();
    }

    public void dialogDismiss() {
        if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
    }

    public void saveGroupToAmazon() {

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
                                processSaveGroup();
                            } else {
                                InputStream is = urlConnection.getErrorStream();
                                CommonUtils.showToast(context, is.toString());
                            }
                        } catch (IOException e) {
                            dialogDismiss();
                            CommonUtils.showToastLong(context, getResources().getString(R.string.error) + e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        dialogDismiss();
                        CommonUtils.showToastLong(context, getResources().getString(R.string.error) + e.getMessage());
                    }

                    @Override
                    public void onTaskContinue() {

                    }
                }, getGroupPhotoBitmapOrjinal, commonS3BucketResult.getImages().get(0).getUploadUrl());

                uploadImageToS3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onFailure(Exception e) {
                dialogDismiss();
                CommonUtils.showToastLong(context, getResources().getString(R.string.error) + e.getMessage());
            }

            @Override
            public void onTaskContinue() {

            }
        }, 1, 0);

        signedUrlGetProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void saveGroupToNeoJ() {

        Log.i("Info", "saveGroupToAWS+++++++++++++++++++++++++++++++");


        GroupResultProcess groupResultProcess = new GroupResultProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                GroupRequestResult groupRequestResult = (GroupRequestResult) object;
                addGroupToUsersGroup(groupRequestResult);
                returnPreviousActivity();
            }

            @Override
            public void onFailure(Exception e) {
                //progressBar.setVisibility(View.GONE);
                dialogDismiss();
                CommonUtils.showToast(context, context.getResources().getString(R.string.error) + e.getMessage());
            }

            @Override
            public void onTaskContinue() {
                //progressBar.setVisibility(View.VISIBLE);
            }
        }, groupRequest);

        groupResultProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void addGroupToUsersGroup(GroupRequestResult groupRequestResult) {
        GroupRequestResultResultArrayItem groupRequestResultResultArrayItem = new GroupRequestResultResultArrayItem();
        groupRequestResultResultArrayItem.setGroupAdmin(groupRequestResult.getResultArray().get(0).getGroupAdmin());
        groupRequestResultResultArrayItem.setGroupid(groupRequestResult.getResultArray().get(0).getGroupid());
        groupRequestResultResultArrayItem.setGroupPhotoUrl(groupRequestResult.getResultArray().get(0).getGroupPhotoUrl());
        groupRequestResultResultArrayItem.setName(groupRequestResult.getResultArray().get(0).getName());
        groupRequestResultResultArrayItem.setCreateAt(groupRequestResult.getResultArray().get(0).getCreateAt());
        UserGroups.addGroupToRequestResult(groupRequestResultResultArrayItem);
        SearchFragment.reloadAdapter();
    }

    private void returnPreviousActivity() {

        // TODO: 9.08.2018 - Burada  NextActivity ye mi gitmeli yoksa acik 2 activity kill mi edilmeli bakalim...

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //progressBar.setVisibility(View.GONE);
                dialogDismiss();
                //Intent intent = new Intent(getApplicationContext(), NextActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(intent);
                finish();
                SelectFriendToGroupActivity.thisActivity.finish();
            }
        }, 1000);
    }
}
