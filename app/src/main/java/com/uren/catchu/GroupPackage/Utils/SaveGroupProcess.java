package com.uren.catchu.GroupPackage.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.GroupResultProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.SignedUrlGetProcess;
import com.uren.catchu.ApiGatewayFunctions.UploadImageToS3;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.PhotoSelectAdapter;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.GroupPackage.Interfaces.SaveGroupCallback;
import com.uren.catchu.MainPackage.MainFragments.SearchTab.SearchFragment;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.SelectedFriendList;
import com.uren.catchu.Singleton.UserGroups;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import catchu.model.BucketUploadResult;
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
    SaveGroupCallback saveGroupCallback;

    public SaveGroupProcess(Context context, PhotoSelectUtil photoSelectUtil, String groupName, SaveGroupCallback saveGroupCallback){
        this.context = context;
        this.photoSelectUtil = photoSelectUtil;
        this.groupName = groupName;
        this.saveGroupCallback = saveGroupCallback;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(context.getResources().getString(R.string.groupIsCreating));
        dialogShow();

        if(photoSelectUtil.getMediaUri() != null)
            saveGroupImageToS3();
        else
            processSaveGroup(" ");
    }

    public void dialogShow() {
        if (!mProgressDialog.isShowing()) mProgressDialog.show();
    }

    public void dialogDismiss() {
        if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
    }

    public void saveGroupImageToS3() {

        SignedUrlGetProcess signedUrlGetProcess = new SignedUrlGetProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                final BucketUploadResult commonS3BucketResult = (BucketUploadResult) object;

                UploadImageToS3 uploadImageToS3 = new UploadImageToS3(new OnEventListener() {
                    @Override
                    public void onSuccess(Object object) {
                        HttpURLConnection urlConnection = (HttpURLConnection) object;

                        try {
                            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                processSaveGroup(commonS3BucketResult.getImages().get(0).getDownloadUrl());
                            } else {
                                InputStream is = urlConnection.getErrorStream();
                                CommonUtils.showToast(context, is.toString());
                            }
                        } catch (IOException e) {
                            dialogDismiss();
                            CommonUtils.showToastLong(context, context.getResources().getString(R.string.error) + e.getMessage());
                            saveGroupCallback.onFailed(e);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        dialogDismiss();
                        CommonUtils.showToastLong(context, context.getResources().getString(R.string.error) + e.getMessage());
                        saveGroupCallback.onFailed(e);
                    }

                    @Override
                    public void onTaskContinue() {

                    }
                }, photoSelectUtil.getBitmap(), commonS3BucketResult.getImages().get(0).getUploadUrl());

                uploadImageToS3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onFailure(Exception e) {
                dialogDismiss();
                CommonUtils.showToastLong(context, context.getResources().getString(R.string.error) + e.getMessage());
                saveGroupCallback.onFailed(e);
            }

            @Override
            public void onTaskContinue() {

            }
        }, 1, 0);

        signedUrlGetProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void processSaveGroup(String downloadUrl) {

        fillGroupParticipants();
        fillGroupDetail(downloadUrl);
        saveGroupToNeoJ();
    }

    private void fillGroupParticipants() {

        participantArrayItems = new ArrayList<GroupRequestGroupParticipantArrayItem>();

        for (UserProfileProperties userProfileProperties : SelectedFriendList.getInstance().getSelectedFriendList().getResultArray()) {
            GroupRequestGroupParticipantArrayItem group = new GroupRequestGroupParticipantArrayItem();
            group.setParticipantUserid(userProfileProperties.getUserid());
            participantArrayItems.add(group);
        }
    }

    private void fillGroupDetail(String downloadUrl) {

        groupRequest = new GroupRequest();
        groupRequest.setUserid(AccountHolderInfo.getUserID());
        groupRequest.setGroupName(groupName);
        groupRequest.setRequestType(CREATE_GROUP);
        groupRequest.setGroupParticipantArray(participantArrayItems);
        groupRequest.setGroupPhotoUrl(downloadUrl);
    }

    public void saveGroupToNeoJ() {
        GroupResultProcess groupResultProcess = new GroupResultProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                GroupRequestResult groupRequestResult = (GroupRequestResult) object;
                addGroupToUsersGroup(groupRequestResult);
                saveGroupCallback.onSuccess();
            }

            @Override
            public void onFailure(Exception e) {
                dialogDismiss();
                CommonUtils.showToast(context, context.getResources().getString(R.string.error) + e.getMessage());
                saveGroupCallback.onFailed(e);
            }

            @Override
            public void onTaskContinue() {
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
}
