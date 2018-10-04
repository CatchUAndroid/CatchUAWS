package com.uren.catchu.GroupPackage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.PhotoChosenCallback;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.GroupPackage.Adapters.FriendGridListAdapter;
import com.uren.catchu.GroupPackage.Interfaces.SaveGroupCallback;
import com.uren.catchu.GroupPackage.Utils.SaveGroupProcess;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.SelectedFriendList;


import static com.uren.catchu.Constants.NumericConstants.GROUP_NAME_MAX_LENGTH;
import static com.uren.catchu.Constants.StringConstants.CAMERA_TEXT;
import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;

public class AddGroupActivity extends AppCompatActivity {

    Toolbar mToolBar;
    public static RecyclerView recyclerView;
    public static FriendGridListAdapter adapter;
    FloatingActionButton saveGroupInfoFab;
    EditText groupNameEditText;
    ImageView groupPictureImgv;
    RelativeLayout addGroupDtlRelLayout;
    PermissionModule permissionModule;
    TextView participantSize;
    TextView textSizeCntTv;
    PhotoSelectUtil photoSelectUtil;
    int groupNameSize = 0;
    GradientDrawable imageShape;

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
        participantSize = findViewById(R.id.participantSize);
        groupPictureImgv = findViewById(R.id.groupPictureImgv);
        saveGroupInfoFab = findViewById(R.id.saveGroupInfoFab);
        groupNameEditText = findViewById(R.id.groupNameEditText);
        addGroupDtlRelLayout = findViewById(R.id.addGroupDtlRelLayout);
        textSizeCntTv = findViewById(R.id.textSizeCntTv);
        recyclerView = findViewById(R.id.recyclerView);
        participantSize.setText(Integer.toString(SelectedFriendList.getInstance().getSize()));
        permissionModule = new PermissionModule(AddGroupActivity.this);
        setGroupTextSize();
        imageShape = ShapeUtil.getShape(getResources().getColor(R.color.LightGrey, null),
                0, GradientDrawable.OVAL, 50, 0);
        groupPictureImgv.setBackground(imageShape);
    }

    private void setGroupTextSize() {
        groupNameSize = GROUP_NAME_MAX_LENGTH;
        textSizeCntTv.setText(Integer.toString(GROUP_NAME_MAX_LENGTH));
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

        groupNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                groupNameSize = GROUP_NAME_MAX_LENGTH - s.toString().length();

                if (groupNameSize >= 0)
                    textSizeCntTv.setText(Integer.toString(groupNameSize));
                else
                    textSizeCntTv.setText(Integer.toString(0));
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
        DialogBoxUtil.photoChosenDialogBox(AddGroupActivity.this, getResources().
                getString(R.string.chooseProfilePhoto), new PhotoChosenCallback() {
            @Override
            public void onGallerySelected() {
                startGalleryProcess();
            }

            @Override
            public void onCameraSelected() {
                startCameraProcess();
            }
        });
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
                photoSelectUtil = new PhotoSelectUtil(AddGroupActivity.this, data, CAMERA_TEXT);
                Glide.with(AddGroupActivity.this).load(photoSelectUtil.getMediaUri()).apply(RequestOptions.circleCropTransform()).into(groupPictureImgv);
            } else if (requestCode == permissionModule.getImageGalleryPermission()) {
                photoSelectUtil = new PhotoSelectUtil(AddGroupActivity.this, data, GALLERY_TEXT);
                Glide.with(AddGroupActivity.this).load(photoSelectUtil.getMediaUri()).apply(RequestOptions.circleCropTransform()).into(groupPictureImgv);
            } else
                DialogBoxUtil.showErrorDialog(AddGroupActivity.this, "AddGroupActivity:resultCode:" + Integer.toString(resultCode) + "-requestCode:" + Integer.toString(requestCode), new InfoDialogBoxCallback() {
                    @Override
                    public void okClick() {
                    }
                });
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
        new SaveGroupProcess(AddGroupActivity.this, photoSelectUtil, groupNameEditText.getText().toString(), new SaveGroupCallback() {
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
