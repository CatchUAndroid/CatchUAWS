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
import com.uren.catchu.GeneralUtils.BitmapConversion;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Adapters.MessageWithGroupAdapter;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.SelectedFriendList;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import catchu.model.BucketUploadResponse;
import catchu.model.GroupRequest;
import catchu.model.GroupRequestGroupParticipantArrayItem;
import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.CREATE_GROUP;

public class SaveGroupProcess {

    Context context;
    PhotoSelectUtil photoSelectUtil;
    ProgressDialog mProgressDialog;
    List<GroupRequestGroupParticipantArrayItem> participantArrayItems;
    GroupRequest groupRequest;
    String groupName;
    CompleteCallback completeCallback;
    Bitmap uploadBitmap = null;

    public SaveGroupProcess(Context context, PhotoSelectUtil photoSelectUtil, String groupName, CompleteCallback completeCallback) {
        try {
            this.context = context;
            this.photoSelectUtil = photoSelectUtil;
            this.groupName = groupName;
            this.completeCallback = completeCallback;
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(context.getResources().getString(R.string.groupIsCreating));
            dialogShow();
            setUploadBitmap();

            if (uploadBitmap != null)
                saveGroupImageToS3();
            else
                processSaveGroup(" ");
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void setUploadBitmap() {
        if (photoSelectUtil != null && photoSelectUtil.getBitmap() != null)
            uploadBitmap = BitmapConversion.getResizedBitmap2(context, photoSelectUtil);
    }

    public void dialogShow() {
        if (!mProgressDialog.isShowing()) mProgressDialog.show();
    }

    public void dialogDismiss() {
        if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
    }

    public void saveGroupImageToS3() {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startSaveGroupImageToS3(token);
            }
        });
    }

    private void startSaveGroupImageToS3(String token) {

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
                                    processSaveGroup(commonS3BucketResult.getImages().get(0).getDownloadUrl());
                                } else {
                                    InputStream is = urlConnection.getErrorStream();
                                    ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                                            new Object() {
                                            }.getClass().getEnclosingMethod().getName(), is.toString());
                                    CommonUtils.showToastShort(context, is.toString());
                                    completeCallback.onFailed(new Exception(is.toString()));
                                }
                            } catch (IOException e) {
                                dialogDismiss();
                                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                                        new Object() {
                                        }.getClass().getEnclosingMethod().getName(), e.getMessage());
                                CommonUtils.showToastShort(context, context.getResources().getString(R.string.error) + e.getMessage());
                                completeCallback.onFailed(e);
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            dialogDismiss();
                            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                                    new Object() {
                                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
                            CommonUtils.showToastShort(context, context.getResources().getString(R.string.error) + e.getMessage());
                            completeCallback.onFailed(e);
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
                    completeCallback.onFailed(e);
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

    public void processSaveGroup(String downloadUrl) {

        fillGroupParticipants();
        fillGroupDetail(downloadUrl);
        saveGroupToNeoJ();
    }

    private void fillGroupParticipants() {

        try {
            participantArrayItems = new ArrayList<>();

            for (UserProfileProperties userProfileProperties : SelectedFriendList.getInstance().getSelectedFriendList().getResultArray()) {
                GroupRequestGroupParticipantArrayItem group = new GroupRequestGroupParticipantArrayItem();
                group.setParticipantUserid(userProfileProperties.getUserid());
                participantArrayItems.add(group);
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    private void fillGroupDetail(String downloadUrl) {

        try {
            groupRequest = new GroupRequest();
            groupRequest.setUserid(AccountHolderInfo.getUserID());
            groupRequest.setGroupName(groupName);
            groupRequest.setRequestType(CREATE_GROUP);
            groupRequest.setGroupParticipantArray(participantArrayItems);
            groupRequest.setGroupPhotoUrl(downloadUrl);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveGroupToNeoJ() {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startSaveGroupToNeoJ(token);
            }
        });
    }

    private void startSaveGroupToNeoJ(String token) {

        try {
            GroupResultProcess groupResultProcess = new GroupResultProcess(new OnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    dialogDismiss();
                    GroupRequestResult groupRequestResult = (GroupRequestResult) object;
                    addGroupToUsersGroup(groupRequestResult);
                }

                @Override
                public void onFailure(Exception e) {
                    dialogDismiss();
                    ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                            new Object() {
                            }.getClass().getEnclosingMethod().getName(), e.getMessage());
                    CommonUtils.showToastShort(context, context.getResources().getString(R.string.error) + e.getMessage());
                    completeCallback.onFailed(e);
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

    public void addGroupToUsersGroup(GroupRequestResult groupRequestResult) {
        try {
            GroupRequestResultResultArrayItem groupRequestResultResultArrayItem = new GroupRequestResultResultArrayItem();
            groupRequestResultResultArrayItem.setGroupAdmin(groupRequestResult.getResultArray().get(0).getGroupAdmin());
            groupRequestResultResultArrayItem.setGroupid(groupRequestResult.getResultArray().get(0).getGroupid());
            groupRequestResultResultArrayItem.setGroupPhotoUrl(groupRequestResult.getResultArray().get(0).getGroupPhotoUrl());
            groupRequestResultResultArrayItem.setName(groupRequestResult.getResultArray().get(0).getName());
            groupRequestResultResultArrayItem.setCreateAt(groupRequestResult.getResultArray().get(0).getCreateAt());
            completeCallback.onComplete(groupRequestResultResultArrayItem);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }
}
