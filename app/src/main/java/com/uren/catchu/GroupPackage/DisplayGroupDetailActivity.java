package com.uren.catchu.GroupPackage;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.SubtitleCollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.ApiGatewayFunctions.GroupResultProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.SignedUrlGetProcess;
import com.uren.catchu.ApiGatewayFunctions.UploadImageToS3;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.PhotoChosenCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.YesNoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ExifUtil;
import com.uren.catchu.GeneralUtils.ImageCache.ImageLoader;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.GeneralUtils.UriAdapter;
import com.uren.catchu.GroupPackage.Adapters.GroupDetailListAdapter;
import com.uren.catchu.MainPackage.MainFragments.SearchTab.SearchFragment;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.UserGroups;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import catchu.model.BucketUploadResponse;
import catchu.model.GroupRequest;
import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.CAMERA_TEXT;
import static com.uren.catchu.Constants.StringConstants.EXIT_GROUP;
import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;
import static com.uren.catchu.Constants.StringConstants.GET_GROUP_PARTICIPANT_LIST;
import static com.uren.catchu.Constants.StringConstants.JPG_TYPE;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_ACTIVITY_NAME;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_GROUP_ID;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_GROUP_NAME;
import static com.uren.catchu.Constants.StringConstants.UPDATE_GROUP_INFO;
import static com.uren.catchu.Constants.StringConstants.displayRectangle;
import static com.uren.catchu.Constants.StringConstants.groupsCacheDirectory;

public class DisplayGroupDetailActivity extends AppCompatActivity implements GroupDetailListAdapter.ItemClickListener {


    ImageView groupPictureImgV;
    ImageView editImageView;
    //ImageLoader imageLoader;
    public static TextView personCntTv;
    public static SubtitleCollapsingToolbarLayout subtitleCollapsingToolbarLayout;
    boolean photoExistOnImgv = false;

    String groupId;
    public static Context context;

    public static GroupDetailListAdapter adapter;

    CardView addFriendCardView;
    CardView deleteGroupCardView;

    public static List<UserProfileProperties> groupParticipantList;
    GroupRequestResult groupRequestResult;
    public static GroupRequestResultResultArrayItem groupRequestResultResultArrayItem;

    private static final int CODE_CAMERA_SELECTED = 0;
    private static final int CODE_GALLERY_SELECTED = 1;

    PermissionModule permissionModule;

    private Bitmap groupPhotoBitmap = null;
    private Bitmap getGroupPhotoBitmapOrjinal = null;

    private Uri groupPictureUri = null;
    private String imageRealPath;
    private InputStream profileImageStream;

    ProgressDialog mProgressDialog;

    ProgressBar progressBar;
    public static RecyclerView recyclerView;
    PhotoSelectUtil photoSelectUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_group_detail);

        context = this;

        groupParticipantList = new ArrayList<>();

        Intent i = getIntent();
        groupId = (String) i.getSerializableExtra(PUTEXTRA_GROUP_ID);

        setGUIVariables();
        getGroupInformation();
        getGroupParticipants();
        addListeners();
    }

    public void setGUIVariables() {
        groupPictureImgV = findViewById(R.id.groupPictureImgv);
        editImageView = findViewById(R.id.editImageView);
        personCntTv = findViewById(R.id.personCntTv);
        addFriendCardView = findViewById(R.id.addFriendCardView);
        deleteGroupCardView = findViewById(R.id.deleteGroupCardView);
        progressBar = findViewById(R.id.progressBar);
        subtitleCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        permissionModule = new PermissionModule(context);
        mProgressDialog = new ProgressDialog(this);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void getGroupInformation() {

        groupRequestResultResultArrayItem = UserGroups.getInstance().getGroupWithId(this.groupId);

        if (groupRequestResultResultArrayItem == null)
            CommonUtils.showToast(DisplayGroupDetailActivity.this,
                    getResources().getString(R.string.error) + getResources().getString(R.string.technicalError));
        else {
            setCardViewVisibility();
            setGroupTitle();
            setGroupImage(groupRequestResultResultArrayItem.getGroupPhotoUrl());
        }
    }

    public void getGroupParticipants() {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetGroupParticipants(token);
            }
        });
    }

    private void startGetGroupParticipants(String token) {

        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setGroupid(this.groupId);
        groupRequest.setRequestType(GET_GROUP_PARTICIPANT_LIST);

        GroupResultProcess groupResultProcess = new GroupResultProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                progressBar.setVisibility(View.GONE);
                groupRequestResult = (GroupRequestResult) object;
                groupParticipantList.addAll(groupRequestResult.getResultArrayParticipantList());
                setParticipantCount();
                setupViewRecyclerView();
            }

            @Override
            public void onFailure(Exception e) {
                progressBar.setVisibility(View.GONE);
                CommonUtils.showToast(DisplayGroupDetailActivity.this, getResources().getString(R.string.error) + e.getMessage());
            }

            @Override
            public void onTaskContinue() {
                progressBar.setVisibility(View.VISIBLE);
            }
        }, groupRequest, token);

        groupResultProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void setParticipantCount() {
        personCntTv.setText(Integer.toString(groupParticipantList.size()));
    }

    public static int getParticipantCount() {
        return groupParticipantList.size();
    }

    public void setCardViewVisibility() {
        if (AccountHolderInfo.getUserID().equals(groupRequestResultResultArrayItem.getGroupAdmin()))
            addFriendCardView.setVisibility(View.VISIBLE);
    }

    public void setGroupTitle() {
        subtitleCollapsingToolbarLayout.setTitle(groupRequestResultResultArrayItem.getName());
        subtitleCollapsingToolbarLayout.setSubtitle(getToolbarSubtitle());
    }

    public String getToolbarSubtitle() {
        return getResources().getString(R.string.createdAt) + " " +
                groupRequestResultResultArrayItem.getCreateAt();
    }

    public void setGroupImage(String photoUrl) {
        if (photoUrl != null && !photoUrl.trim().isEmpty()) {
            photoExistOnImgv = true;
            groupPictureImgV.setPadding(0, 0, 0, 0 );
            Glide.with(this)
                    .load(photoUrl)
                    .apply(RequestOptions.centerInsideTransform())
                    .into(groupPictureImgV);
        }else {
            photoExistOnImgv = false;
            groupPictureImgV.setPadding(200, 200, 200, 200 );
            Glide.with(this)
                    .load(getResources().getIdentifier("groups_icon_500", "drawable", context.getPackageName()))
                    .apply(RequestOptions.centerInsideTransform())
                    .into(groupPictureImgV);
        }
    }

    public void addListeners() {

        addFriendCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectFriendToGroupActivity.class);
                intent.putExtra(PUTEXTRA_ACTIVITY_NAME, DisplayGroupDetailActivity.class.getSimpleName());
                intent.putExtra(PUTEXTRA_GROUP_ID, groupId);
                startActivity(intent);
            }
        });

        deleteGroupCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogBoxUtil.showYesNoDialog(DisplayGroupDetailActivity.this, null, getResources().getString(R.string.areYouSureExitFromGroup), new YesNoDialogBoxCallback() {
                    @Override
                    public void yesClick() {
                        exitFromGroup(AccountHolderInfo.getUserID());
                    }

                    @Override
                    public void noClick() {

                    }
                });
            }
        });

        editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DisplayGroupDetailActivity.this, EditGroupNameActivity.class);
                intent.putExtra(PUTEXTRA_GROUP_ID, groupRequestResultResultArrayItem.getGroupid());
                intent.putExtra(PUTEXTRA_GROUP_NAME, groupRequestResultResultArrayItem.getName());
                startActivity(intent);
            }
        });

        groupPictureImgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChooseImageProc();
            }
        });
    }

    private void setupViewRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GroupDetailListAdapter(this, groupParticipantList, groupRequestResultResultArrayItem);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public static void reloadAdapter() {
        adapter = new GroupDetailListAdapter(context, groupParticipantList, groupRequestResultResultArrayItem);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    public void exitFromGroup(final String userid) {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startExitFromGroup(userid, token);
            }
        });
    }

    private void startExitFromGroup(String userid, String token) {

        final GroupRequest groupRequest = new GroupRequest();
        groupRequest.setRequestType(EXIT_GROUP);
        groupRequest.setUserid(userid);
        groupRequest.setGroupid(groupId);

        GroupResultProcess groupResultProcess = new GroupResultProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                UserGroups.removeGroupFromList(groupRequestResultResultArrayItem);
                SearchFragment.reloadAdapter();
                finish();
            }

            @Override
            public void onFailure(Exception e) {

            }


            @Override
            public void onTaskContinue() {

            }
        }, groupRequest, token);

        groupResultProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void startChooseImageProc() {
        DialogBoxUtil.photoChosenDialogBox(DisplayGroupDetailActivity.this, getResources().
                getString(R.string.CHOOSE_GROUP_PHOTO), photoExistOnImgv, new PhotoChosenCallback() {
            @Override
            public void onGallerySelected() {
                startGalleryProcess();
            }

            @Override
            public void onCameraSelected() {
                startCameraProcess();
            }

            @Override
            public void onPhotoRemoved() {
                updateGroupToNeoJ("");
            }
        });
    }

    public void startCameraProcess() {

        if (!CommonUtils.checkCameraHardware(this)) {
            CommonUtils.showToast(DisplayGroupDetailActivity.this, getResources().getString(R.string.deviceHasNoCamera));
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
                photoSelectUtil = new PhotoSelectUtil(DisplayGroupDetailActivity.this, data, CAMERA_TEXT);
                groupPictureImgV.setImageBitmap(photoSelectUtil.getBitmap());
                updateGroup();
            } else if (requestCode == permissionModule.getImageGalleryPermission()) {
                photoSelectUtil = new PhotoSelectUtil(DisplayGroupDetailActivity.this, data, GALLERY_TEXT);
                groupPictureImgV.setImageBitmap(photoSelectUtil.getBitmap());
                updateGroup();
            } else
                CommonUtils.showToastLong(DisplayGroupDetailActivity.this, getResources().getString(R.string.technicalError) + requestCode);
        } else
            CommonUtils.showToastLong(DisplayGroupDetailActivity.this, getResources().getString(R.string.technicalError) + requestCode + resultCode);
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

    public void updateGroup() {

        mProgressDialog.setMessage(getResources().getString(R.string.groupPhotoChanging));
        dialogShow();

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startUpdateGroup(token);
            }
        });


    }

    private void startUpdateGroup(String token) {

        SignedUrlGetProcess signedUrlGetProcess = new SignedUrlGetProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                final BucketUploadResponse commonS3BucketResult = (BucketUploadResponse) object;

                Log.i("Info", "  >>commonS3BucketResult.getFileExtention():" + commonS3BucketResult.getImages().get(0).getExtension());
                Log.i("Info", "  >>commonS3BucketResult.getSignedUrl()    :" + commonS3BucketResult.getImages().get(0).getUploadUrl());
                Log.i("Info", "  >>commonS3BucketResult.getDownloadUrl()  :" + commonS3BucketResult.getImages().get(0).getDownloadUrl());
                Log.i("Info", "  >>commonS3BucketResult.getError()        :" + commonS3BucketResult.getError().getMessage());

                UploadImageToS3 uploadImageToS3 = new UploadImageToS3(new OnEventListener() {
                    @Override
                    public void onSuccess(Object object) {
                        HttpURLConnection urlConnection = (HttpURLConnection) object;

                        try {
                            // TODO: 30.08.2018 - Grup fotosu guncellendi, S3 den silme akisi nasil olacak...
                            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
                                updateGroupToNeoJ(commonS3BucketResult.getImages().get(0).getDownloadUrl());
                            else {
                                InputStream is = urlConnection.getErrorStream();
                                CommonUtils.showToastLong(context, getResources().getString(R.string.error) + is.toString());
                            }
                        } catch (IOException e) {
                            CommonUtils.showToastLong(context, getResources().getString(R.string.error) + e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        CommonUtils.showToastLong(context, getResources().getString(R.string.error) + e.getMessage());
                    }

                    @Override
                    public void onTaskContinue() {

                    }
                }, photoSelectUtil.getBitmap(), commonS3BucketResult.getImages().get(0).getUploadUrl());

                uploadImageToS3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.showToastLong(context, getResources().getString(R.string.error) + e.getMessage());
            }

            @Override
            public void onTaskContinue() {

            }
        }, 1, 0, token);

        signedUrlGetProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void dialogShow() {
        if (!mProgressDialog.isShowing()) mProgressDialog.show();
    }

    public void dialogDismiss() {
        if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
    }

    public void updateGroupToNeoJ(final String photoNewUrl) {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startUpdateGroupToNeoJ(photoNewUrl, token);
            }
        });
    }

    private void startUpdateGroupToNeoJ(final String photoNewUrl, String token) {

        // TODO: 5.10.2018 - grup fotosu silindiginde S3 den silme akisi yok...

        final GroupRequest groupRequest = new GroupRequest();

        final GroupRequestResultResultArrayItem groupRequestResultResultArrayItem = UserGroups.getGroupWithId(groupId);

        groupRequest.setRequestType(UPDATE_GROUP_INFO);
        groupRequest.setGroupid(groupId);
        groupRequest.setGroupName(groupRequestResultResultArrayItem.getName());
        groupRequest.setUserid(AccountHolderInfo.getUserID());
        groupRequest.setGroupPhotoUrl(photoNewUrl);

        GroupResultProcess groupResultProcess = new GroupResultProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                UserGroups.changeGroupPicture(groupId, photoNewUrl);
                setGroupImage(photoNewUrl);
                SearchFragment.reloadAdapter();
                dialogDismiss();
            }

            @Override
            public void onFailure(Exception e) {
                dialogDismiss();
                CommonUtils.showToast(DisplayGroupDetailActivity.this, getResources().getString(R.string.error) + e.getMessage());
            }

            @Override
            public void onTaskContinue() {

            }
        }, groupRequest, token);

        groupResultProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }
}
