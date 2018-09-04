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

import com.uren.catchu.ApiGatewayFunctions.GroupResultProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.SignedUrlGetProcess;
import com.uren.catchu.ApiGatewayFunctions.UploadImageToS3;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.ImageCache.ImageLoader;
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

import catchu.model.CommonS3BucketResult;
import catchu.model.GroupRequest;
import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.EXIT_GROUP;
import static com.uren.catchu.Constants.StringConstants.GET_GROUP_PARTICIPANT_LIST;
import static com.uren.catchu.Constants.StringConstants.JPG_TYPE;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_ACTIVITY_NAME;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_GROUP_ID;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_GROUP_NAME;
import static com.uren.catchu.Constants.StringConstants.UPDATE_GROUP_INFO;
import static com.uren.catchu.Constants.StringConstants.displayRectangle;
import static com.uren.catchu.Constants.StringConstants.groupsCacheDirectory;

public class DisplayGroupDetailActivity extends AppCompatActivity implements GroupDetailListAdapter.ItemClickListener{


    ImageView groupPictureImgV;
    ImageView editImageView;
    ImageLoader imageLoader;
    public static TextView personCntTv;
    public static SubtitleCollapsingToolbarLayout subtitleCollapsingToolbarLayout;

    //public static SpecialSelectTabAdapter adapter;

    //ViewPager viewPager;
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

    public int photoChoosenType;
    PermissionModule permissionModule;

    private Bitmap groupPhotoBitmap = null;
    private Bitmap getGroupPhotoBitmapOrjinal = null;

    private Uri groupPictureUri = null;
    private String imageRealPath;
    private InputStream profileImageStream;

    ProgressDialog mProgressDialog;

    ProgressBar progressBar;
    public static RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_group_detail);

        context = this;

        groupParticipantList = new ArrayList<UserProfileProperties>();

        imageLoader = new ImageLoader(this, groupsCacheDirectory);

        Intent i = getIntent();
        groupId = (String) i.getSerializableExtra(PUTEXTRA_GROUP_ID);

        Log.i("Info", "  >>groupId:" + groupId);

        setGUIVariables();
        getGroupInformation();
        getGroupParticipants();
        addListeners();
    }

    public void setGUIVariables() {
        groupPictureImgV = (ImageView) findViewById(R.id.groupPictureImgv);
        editImageView = (ImageView) findViewById(R.id.editImageView);
        personCntTv = (TextView) findViewById(R.id.personCntTv);
        addFriendCardView = (CardView) findViewById(R.id.addFriendCardView);
        deleteGroupCardView = (CardView) findViewById(R.id.deleteGroupCardView);
        //viewPager = (ViewPager) findViewById(R.id.viewpager);
        progressBar = findViewById(R.id.progressBar);
        subtitleCollapsingToolbarLayout = (SubtitleCollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        permissionModule = new PermissionModule(context);
        mProgressDialog = new ProgressDialog(this);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void getGroupInformation() {

        groupRequestResultResultArrayItem = UserGroups.getGroupWithId(this.groupId);

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
        }, groupRequest);

        groupResultProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setParticipantCount() {
        personCntTv.setText(Integer.toString(groupParticipantList.size()));
    }

    public static int getParticipantCount(){
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
        imageLoader.DisplayImage(photoUrl, groupPictureImgV, displayRectangle);
    }

    public void addListeners() {

        addFriendCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectFriendToGroupActivity.class);
                //Log.i("Info", "this.getClass().getSimpleName():" + this.getClass().getSimpleName());
                //Log.i("Info", "DisplayGroupDetailActivity.class.getSimpleName():" + DisplayGroupDetailActivity.class.getSimpleName());
                intent.putExtra(PUTEXTRA_ACTIVITY_NAME, DisplayGroupDetailActivity.class.getSimpleName());
                intent.putExtra(PUTEXTRA_GROUP_ID, groupId);
                startActivity(intent);
            }
        });

        deleteGroupCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showYesNoDialog(null, getResources().getString(R.string.areYouSureExitFromGroup));
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
                //PhotoSelectUtil photoSelectUtil = new PhotoSelectUtil( context, permissionModule, (Activity) context,
                //        600, 600, groupPictureImgV);
                // TODO: 30.08.2018 -  startChooseImageProc ve sonrasini moduler nasil yapariz bakalim...
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

        // TODO: 30.08.2018 - recycler view update etmenin baska yolu var mi?....
        adapter = new GroupDetailListAdapter(context, groupParticipantList, groupRequestResultResultArrayItem);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    public void showYesNoDialog(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(DisplayGroupDetailActivity.this, android.R.style.Theme_Material_Dialog_Alert);

        builder.setIcon(R.drawable.warning_icon40);
        builder.setMessage(message);

        if (title != null)
            builder.setTitle(title);

        builder.setPositiveButton(getResources().getString(R.string.upperYes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                exitFromGroup(AccountHolderInfo.getUserID());
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.upperNo), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void exitFromGroup(String userid) {

        final GroupRequest groupRequest = new GroupRequest();
        groupRequest.setRequestType(EXIT_GROUP);
        groupRequest.setUserid(userid);
        groupRequest.setGroupid(groupId);

        GroupResultProcess groupResultProcess = new GroupResultProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                UserGroups.removeGroupFromList(groupRequestResultResultArrayItem);
                imageLoader.removeImageViewFromMap(groupRequestResultResultArrayItem.getGroupPhotoUrl());
                SearchFragment.reloadAdapter();
                finish();
            }

            @Override
            public void onFailure(Exception e) {

            }


            @Override
            public void onTaskContinue() {

            }
        }, groupRequest);

        groupResultProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

                if (item == CODE_CAMERA_SELECTED) {

                    photoChoosenType = CODE_CAMERA_SELECTED;
                    startCameraProcess();

                } else if (item == CODE_GALLERY_SELECTED) {

                    photoChoosenType = CODE_GALLERY_SELECTED;
                    startGalleryProcess();

                } else
                    CommonUtils.showToast(DisplayGroupDetailActivity.this, getResources().getString(R.string.technicalError));
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
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

            if (requestCode == permissionModule.getCameraPermissionCode() ||
                    requestCode == permissionModule.getImageGalleryPermission()) {
                manageProfilePicChoosen(data);
                updateGroup();
            } else
                CommonUtils.showToastLong(DisplayGroupDetailActivity.this, getResources().getString(R.string.technicalError) + requestCode);
        } else
            CommonUtils.showToastLong(DisplayGroupDetailActivity.this, getResources().getString(R.string.technicalError) + requestCode + resultCode);
    }

    private void manageProfilePicChoosen(Intent data) {

        Log.i("Info", "manageProfilePicChoosen++++++++++++++++++++++++++++++++");

        if (photoChoosenType == CODE_CAMERA_SELECTED) {

            groupPhotoBitmap = (Bitmap) data.getExtras().get("data");
            getGroupPhotoBitmapOrjinal = groupPhotoBitmap;

        } else if (photoChoosenType == CODE_GALLERY_SELECTED) {

            groupPictureUri = data.getData();
            imageRealPath = UriAdapter.getPathFromGalleryUri(getApplicationContext(), groupPictureUri);
            try {
                profileImageStream = getContentResolver().openInputStream(groupPictureUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            groupPhotoBitmap = BitmapFactory.decodeStream(profileImageStream);
            getGroupPhotoBitmapOrjinal = groupPhotoBitmap;
        }

        //groupPictureImgV.setImageBitmap(getGroupPhotoBitmapOrjinal);
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

        SignedUrlGetProcess signedUrlGetProcess = new SignedUrlGetProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                final CommonS3BucketResult commonS3BucketResult = (CommonS3BucketResult) object;

                Log.i("Info", "  >>commonS3BucketResult.getFileExtention():" + commonS3BucketResult.getFileExtention());
                Log.i("Info", "  >>commonS3BucketResult.getSignedUrl()    :" + commonS3BucketResult.getSignedUrl());
                Log.i("Info", "  >>commonS3BucketResult.getDownloadUrl()  :" + commonS3BucketResult.getDownloadUrl());
                Log.i("Info", "  >>commonS3BucketResult.getError()        :" + commonS3BucketResult.getError().getMessage());

                UploadImageToS3 uploadImageToS3 = new UploadImageToS3(new OnEventListener() {
                    @Override
                    public void onSuccess(Object object) {
                        HttpURLConnection urlConnection = (HttpURLConnection) object;

                        try {
                            // TODO: 30.08.2018 - Grup fotosu guncellendi, S3 den silme akisi nasil olacak...
                            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
                                updateGroupToNeoJ(commonS3BucketResult.getDownloadUrl());
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
                }, getGroupPhotoBitmapOrjinal, commonS3BucketResult.getSignedUrl());

                uploadImageToS3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.showToastLong(context, getResources().getString(R.string.error) + e.getMessage());
            }

            @Override
            public void onTaskContinue() {

            }
        }, JPG_TYPE);

        signedUrlGetProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void dialogShow() {
        if (!mProgressDialog.isShowing()) mProgressDialog.show();
    }

    public void dialogDismiss() {
        if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
    }

    public void updateGroupToNeoJ(final String photoNewUrl) {

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
                imageLoader.removeImageViewFromMap(groupRequestResultResultArrayItem.getGroupPhotoUrl());
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
        }, groupRequest);

        groupResultProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }
}
