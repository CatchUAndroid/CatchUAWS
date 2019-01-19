package com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.uren.catchu.ApiGatewayFunctions.GroupResultProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.SignedUrlGetProcess;
import com.uren.catchu.ApiGatewayFunctions.UploadImageToS3;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Interfaces.UpdateGroupCallback;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import catchu.model.BucketUploadResponse;
import catchu.model.GroupRequest;
import catchu.model.GroupRequestResultResultArrayItem;

import static com.uren.catchu.Constants.StringConstants.UPDATE_GROUP_INFO;

public class UpdateGroupProcess {

    Context context;
    PhotoSelectUtil photoSelectUtil;
    ProgressDialog mProgressDialog;
    GroupRequestResultResultArrayItem groupItem;
    UpdateGroupCallback updateGroupCallback;
    Bitmap uploadBitmap = null;

    // TODO: 30.08.2018 - Grup fotosu guncellendi, S3 den silme akisi nasil olacak...
    // TODO: 5.10.2018 - grup fotosu silindiginde S3 den silme akisi yok...

    public UpdateGroupProcess(Context context, PhotoSelectUtil photoSelectUtil, GroupRequestResultResultArrayItem groupItem, UpdateGroupCallback updateGroupCallback) {
        try {
            this.context = context;
            this.photoSelectUtil = photoSelectUtil;
            this.groupItem = groupItem;
            this.updateGroupCallback = updateGroupCallback;
            mProgressDialog = new ProgressDialog(context);
            setUploadBitmap();

            if (uploadBitmap != null) {
                mProgressDialog.setMessage(context.getResources().getString(R.string.groupPhotoChanging));
                dialogShow();
                AccountHolderInfo.getToken(new TokenCallback() {
                    @Override
                    public void onTokenTaken(String token) {
                        startUpdateGroup(token);
                    }
                });
            } else {
                mProgressDialog.setMessage(context.getResources().getString(R.string.UPDATING));
                dialogShow();
                updateGroupToNeoJ();
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void setUploadBitmap() {
        if (photoSelectUtil != null && photoSelectUtil.getBitmap() != null)
            uploadBitmap = photoSelectUtil.getResizedBitmap();
    }

    public void dialogShow() {
        if (!mProgressDialog.isShowing()) mProgressDialog.show();
    }

    public void dialogDismiss() {
        if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
    }

    private void startUpdateGroup(String token) {

        try {
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
                                    groupItem.setGroupPhotoUrl(commonS3BucketResult.getImages().get(0).getDownloadUrl());
                                    updateGroupToNeoJ();
                                } else {
                                    InputStream is = urlConnection.getErrorStream();
                                    ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                                            new Object() {
                                            }.getClass().getEnclosingMethod().getName(), is.toString());
                                    CommonUtils.showToastShort(context, context.getResources().getString(R.string.error) + is.toString());
                                    updateGroupCallback.onFailed(new Exception(is.toString()));
                                }
                            } catch (IOException e) {
                                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                                        new Object() {
                                        }.getClass().getEnclosingMethod().getName(), e.getMessage());
                                CommonUtils.showToastShort(context, context.getResources().getString(R.string.error) + e.getMessage());
                                updateGroupCallback.onFailed(e);
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                                    new Object() {
                                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
                            CommonUtils.showToastShort(context, context.getResources().getString(R.string.error) + e.getMessage());
                            updateGroupCallback.onFailed(e);
                        }

                        @Override
                        public void onTaskContinue() {

                        }
                    }, uploadBitmap, commonS3BucketResult.getImages().get(0).getUploadUrl());

                    uploadImageToS3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

                @Override
                public void onFailure(Exception e) {
                    dialogDismiss();
                    ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                            new Object() {
                            }.getClass().getEnclosingMethod().getName(), e.getMessage());
                    CommonUtils.showToastShort(context, context.getResources().getString(R.string.error) + e.getMessage());
                    updateGroupCallback.onFailed(e);
                }

                @Override
                public void onTaskContinue() {

                }
            }, 1, 0, token);

            signedUrlGetProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateGroupToNeoJ() {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startUpdateGroupToNeoJ(token);
            }
        });
    }

    public void startUpdateGroupToNeoJ(String token) {
        try {
            final GroupRequest groupRequest = new GroupRequest();

            groupRequest.setRequestType(UPDATE_GROUP_INFO);
            groupRequest.setGroupid(groupItem.getGroupid());
            groupRequest.setGroupName(groupItem.getName());
            groupRequest.setUserid(AccountHolderInfo.getUserID());
            groupRequest.setGroupPhotoUrl(groupItem.getGroupPhotoUrl());

            GroupResultProcess groupResultProcess = new GroupResultProcess(new OnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    dialogDismiss();
                    updateGroupCallback.onSuccess(groupItem);
                }

                @Override
                public void onFailure(Exception e) {
                    dialogDismiss();
                    ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                            new Object() {
                            }.getClass().getEnclosingMethod().getName(), e.getMessage());
                    CommonUtils.showToastShort(context, context.getResources().getString(R.string.error) + e.getMessage());
                    updateGroupCallback.onFailed(e);
                }

                @Override
                public void onTaskContinue() {

                }
            }, groupRequest, token);

            groupResultProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }
}
