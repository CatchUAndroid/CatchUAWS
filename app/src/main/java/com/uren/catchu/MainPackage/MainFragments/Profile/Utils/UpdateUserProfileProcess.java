package com.uren.catchu.MainPackage.MainFragments.Profile.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.SignedUrlGetProcess;
import com.uren.catchu.ApiGatewayFunctions.UpdateUserProfile;
import com.uren.catchu.ApiGatewayFunctions.UploadImageToS3;
import com.uren.catchu.GeneralUtils.ProgressDialogUtil.ProgressDialogUtil;
import com.uren.catchu.Interfaces.ServiceCompleteCallback;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import catchu.model.BucketUploadResponse;
import catchu.model.UserProfile;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.USER_PROFILE_UPDATE;

public class UpdateUserProfileProcess {

    Context context;
    ServiceCompleteCallback serviceCompleteCallback;
    boolean profilPicChanged;
    UserProfileProperties userProfileProperties;
    Bitmap bitmap;
    ProgressDialogUtil progressDialogUtil;
    boolean dialogShowed;

    public UpdateUserProfileProcess(Context context, ServiceCompleteCallback serviceCompleteCallback, boolean profilPicChanged,
                                    UserProfileProperties userProfileProperties, Bitmap bitmap){
        this.context = context;
        this.serviceCompleteCallback = serviceCompleteCallback;
        this.profilPicChanged = profilPicChanged;
        this.userProfileProperties = userProfileProperties;
        this.bitmap = bitmap;
        progressDialogUtil = new ProgressDialogUtil(context, context.getResources().getString(R.string.UPDATING), false);

        if(this.profilPicChanged && bitmap != null)
            uploadMediaToS3();
        else
            updateUserProfile();
    }

    public void uploadMediaToS3(){

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                if(!dialogShowed){
                    progressDialogUtil.dialogShow();
                    dialogShowed = true;
                }
                startUploadMediaToS3(token);
            }
        });

    }

    private void startUploadMediaToS3(String token) {

        SignedUrlGetProcess signedUrlGetProcess = new SignedUrlGetProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                final BucketUploadResponse commonS3BucketResult = (BucketUploadResponse) object;

                UploadImageToS3 uploadImageToS3 = new UploadImageToS3(new OnEventListener() {
                    @Override
                    public void onSuccess(Object object) {
                        HttpURLConnection urlConnection = (HttpURLConnection) object;

                        try {
                            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                userProfileProperties.setProfilePhotoUrl(commonS3BucketResult.getImages().get(0).getDownloadUrl());
                                updateUserProfile();

                            } else {
                                InputStream is = urlConnection.getErrorStream();
                                serviceCompleteCallback.onFailed(new Exception(is.toString()));
                            }
                        } catch (IOException e) {
                            progressDialogUtil.dialogDismiss();
                            serviceCompleteCallback.onFailed(e);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        progressDialogUtil.dialogDismiss();
                        serviceCompleteCallback.onFailed(e);
                    }

                    @Override
                    public void onTaskContinue() {
                    }
                }, bitmap, commonS3BucketResult.getImages().get(0).getUploadUrl());

                uploadImageToS3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onFailure(Exception e) {
                progressDialogUtil.dialogDismiss();
                serviceCompleteCallback.onFailed(e);
            }

            @Override
            public void onTaskContinue() {
            }
        }, 1, 0, token);

        signedUrlGetProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void updateUserProfile(){

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                if(!dialogShowed){
                    progressDialogUtil.dialogShow();
                    dialogShowed = true;
                }
                startUpdateUserProfile(token);
            }
        });
    }

    private void startUpdateUserProfile(String token) {

        UserProfile tempUser = new UserProfile();
        tempUser.setUserInfo(userProfileProperties);
        tempUser.setRequestType(USER_PROFILE_UPDATE);

        UpdateUserProfile updateUserProfile = new UpdateUserProfile(context, new OnEventListener<UserProfile>() {
            @Override
            public void onSuccess(UserProfile userProfile) {
                if (userProfile != null)
                    updateAccountHolderInfo(userProfile);
                progressDialogUtil.dialogDismiss();
                serviceCompleteCallback.onSuccess();
            }

            @Override
            public void onFailure(Exception e) {
                progressDialogUtil.dialogDismiss();
                serviceCompleteCallback.onFailed(e);
            }

            @Override
            public void onTaskContinue() {
            }
        }, tempUser, token);

        updateUserProfile.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void updateAccountHolderInfo(UserProfile up) {
        AccountHolderInfo.getInstance().getUser().setUserInfo(up.getUserInfo());
        /*AccountHolderInfo.getInstance().getUser().getUserInfo().setName(up.getUserInfo().getName());
        AccountHolderInfo.getInstance().getUser().getUserInfo().setUsername(up.getUserInfo().getUsername());
        AccountHolderInfo.getInstance().getUser().getUserInfo().setWebsite(up.getUserInfo().getWebsite());
        AccountHolderInfo.getInstance().getUser().getUserInfo().setBirthday(up.getUserInfo().getBirthday());
        AccountHolderInfo.getInstance().getUser().getUserInfo().setEmail(up.getUserInfo().getEmail());
        AccountHolderInfo.getInstance().getUser().getUserInfo().setPhone(up.getUserInfo().getPhone());
        AccountHolderInfo.getInstance().getUser().getUserInfo().setGender(up.getUserInfo().getGender());
        AccountHolderInfo.getInstance().getUser().getUserInfo().setProfilePhotoUrl(up.getUserInfo().getProfilePhotoUrl());
        AccountHolderInfo.getInstance().getUser().getUserInfo().setPhone(up.getUserInfo().getPhone());*/
    }
}
