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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.uren.catchu.GeneralUtils.PhotoSelectAdapter;
import com.uren.catchu.GeneralUtils.UriAdapter;
import com.uren.catchu.GroupPackage.Adapters.FriendGridListAdapter;
import com.uren.catchu.GroupPackage.Interfaces.SaveGroupCallback;
import com.uren.catchu.GroupPackage.Utils.SaveGroupProcess;
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
import static com.uren.catchu.Constants.StringConstants.CAMERA_TEXT;
import static com.uren.catchu.Constants.StringConstants.CREATE_GROUP;
import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;
import static com.uren.catchu.Constants.StringConstants.JPG_TYPE;
import static com.uren.catchu.Constants.StringConstants.SPACE_VALUE;
import static com.uren.catchu.Constants.StringConstants.gridShown;

public class AddGroupActivity extends AppCompatActivity {

    Toolbar mToolBar;
    public static RecyclerView recyclerView;
    public static FriendGridListAdapter adapter;
    FloatingActionButton saveGroupInfoFab;
    EditText groupNameEditText;

    private ImageView groupPictureImgv;

    public int photoChoosenType;

    RelativeLayout addGroupDtlRelLayout;
    PermissionModule permissionModule;

    private static final int adapterCameraSelected = 0;
    private static final int adapterGallerySelected = 1;

    TextView participantSize;
    PhotoSelectAdapter photoSelectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        initToolbar();
        initUI();
        addListeners();
        openPersonSelectionPage();
    }

    public void initToolbar() {
        mToolBar = (Toolbar) findViewById(R.id.toolbarLayout);
        mToolBar.setTitle(getResources().getString(R.string.addNewGroup));
        mToolBar.setSubtitle(getResources().getString(R.string.addGroupName));
        mToolBar.setNavigationIcon(R.drawable.back_arrow);
        mToolBar.setBackgroundColor(getResources().getColor(R.color.background, null));
        mToolBar.setTitleTextColor(getResources().getColor(R.color.background_white, null));
        mToolBar.setSubtitleTextColor(getResources().getColor(R.color.background_white, null));
        setSupportActionBar(mToolBar);
    }

    public void initUI() {
        participantSize = (TextView) findViewById(R.id.participantSize);
        groupPictureImgv = (ImageView) findViewById(R.id.groupPictureImgv);
        saveGroupInfoFab = (FloatingActionButton) findViewById(R.id.saveGroupInfoFab);
        groupNameEditText = (EditText) findViewById(R.id.groupNameEditText);
        addGroupDtlRelLayout = (RelativeLayout) findViewById(R.id.addGroupDtlRelLayout);
        recyclerView = findViewById(R.id.recyclerView);
        participantSize.setText(Integer.toString(SelectedFriendList.getInstance().getSize()));
        permissionModule = new PermissionModule(AddGroupActivity.this);
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
                saveGroup();
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
        adapter = new FriendGridListAdapter(this, SelectedFriendList.getInstance().getSelectedFriendList());
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
                photoSelectAdapter = new PhotoSelectAdapter(AddGroupActivity.this, data, CAMERA_TEXT);
                Glide.with(AddGroupActivity.this).load(photoSelectAdapter.getPictureUri()).apply(RequestOptions.circleCropTransform()).into(groupPictureImgv);
            } else if (requestCode == permissionModule.getImageGalleryPermission()) {
                photoSelectAdapter = new PhotoSelectAdapter(AddGroupActivity.this, data, GALLERY_TEXT);
                Glide.with(AddGroupActivity.this).load(photoSelectAdapter.getPictureUri()).apply(RequestOptions.circleCropTransform()).into(groupPictureImgv);
            } else
                CommonUtils.showToast(AddGroupActivity.this, getResources().getString(R.string.technicalError) + requestCode);
        }
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


    public void saveGroup() {
        SaveGroupProcess saveGroupProcess = new SaveGroupProcess(AddGroupActivity.this, photoSelectAdapter, groupNameEditText.getText().toString(), new SaveGroupCallback() {
            @Override
            public void onSuccess() {
                returnPreviousActivity();
            }

            @Override
            public void onFailed(Exception e) {
                CommonUtils.showToastLong(AddGroupActivity.this, getResources().getString(R.string.error) + e.getMessage());
            }
        });
    }

    private void returnPreviousActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                SelectFriendToGroupActivity.thisActivity.finish();
            }
        }, 1000);
    }
}
