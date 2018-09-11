package com.uren.catchu.SharePackage.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.ShareRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.SignedUrlGetProcess;
import com.uren.catchu.ApiGatewayFunctions.UploadImageToS3;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.Interfaces.SharePostCallback;
import com.uren.catchu.SharePackage.ShareDetailActivity;
import com.uren.catchu.Singleton.SelectedFriendList;
import com.uren.catchu.Singleton.ShareItems;
import com.uren.catchu.Singleton.UserFriends;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import catchu.model.BucketUploadResult;
import catchu.model.Share;
import catchu.model.ShareRequest;

import static com.uren.catchu.Constants.StringConstants.JPG_TYPE;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_ALL_FOLLOWERS;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_CUSTOM;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_EVERYONE;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_SELF;

public class SharePostProcess {

    int selectedItem;
    Context context;
    SharePostCallback sharePostCallback;
    ProgressDialog mProgressDialog;

    public SharePostProcess(Context context, int selectedItem, SharePostCallback sharePostCallback){
        this.context = context;
        this.selectedItem = selectedItem;
        this.sharePostCallback = sharePostCallback;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(context.getResources().getString(R.string.shareIsProcessing));
        dialogShow();

        if(ShareItems.getInstance().getPhotoSelectAdapter().getPictureUri() == null)
            saveShareItemsToNeoJ();
        else
            uploadShareImageToS3();
    }

    public void dialogShow() {
        if (!mProgressDialog.isShowing()) mProgressDialog.show();
    }

    public void dialogDismiss() {
        if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
    }

    private void uploadShareImageToS3() {

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
                                ShareItems.getInstance().getShare().setImageUrl(commonS3BucketResult.getImages().get(0).getDownloadUrl());
                                saveShareItemsToNeoJ();
                            } else {
                                dialogDismiss();
                                InputStream is = urlConnection.getErrorStream();
                                CommonUtils.showToast(context, is.toString());
                            }
                        } catch (IOException e) {
                            dialogDismiss();
                            Log.i("Info", "Paylasim Exception yedi4:" + e.getMessage());
                            CommonUtils.showToastLong(context, context.getResources().getString(R.string.error) + e.getMessage());
                            sharePostCallback.onFailed(e);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        dialogDismiss();
                        Log.i("Info", "Paylasim Exception yedi3:" + e.getMessage());
                        CommonUtils.showToastLong(context, context.getResources().getString(R.string.error) + e.getMessage());
                        sharePostCallback.onFailed(e);
                    }

                    @Override
                    public void onTaskContinue() {
                    }
                }, ShareItems.getInstance().getPhotoSelectAdapter().getPhotoBitmap(), commonS3BucketResult.getImages().get(0).getUploadUrl());
                uploadImageToS3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onFailure(Exception e) {
                dialogDismiss();
                Log.i("Info", "Paylasim Exception yedi2:" + e.getMessage());
                CommonUtils.showToastLong(context, context.getResources().getString(R.string.error) + e.getMessage());
                sharePostCallback.onFailed(e);
            }

            @Override
            public void onTaskContinue() {

            }
        }, 1, 0);
        signedUrlGetProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void saveShareItemsToNeoJ() {
        ShareRequest shareRequest = new ShareRequest();
        ShareItems.getInstance().getShare().setPrivacyType(getPostPrivacyType());
        shareRequest.setShare(ShareItems.getInstance().getShare());
        ShareRequestProcess shareRequestProcess = new ShareRequestProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                Log.i("Info", "Paylasim ok");
                dialogDismiss();
                sharePostCallback.onSuccess();
            }

            @Override
            public void onFailure(Exception e) {
                Log.i("Info", "Paylasim Exception yedi1:" + e.getMessage());
                dialogDismiss();
                sharePostCallback.onFailed(e);
            }

            @Override
            public void onTaskContinue() {

            }
        }, shareRequest);
        shareRequestProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private String getPostPrivacyType() {
        if (selectedItem == ShareDetailActivity.CODE_PUBLIC_SHARED)
            return SHARE_TYPE_EVERYONE;
        else if (selectedItem == ShareDetailActivity.CODE_FRIEND_SHARED) {
            if (UserFriends.getFriendList().getResultArray().size() == SelectedFriendList.getInstance().getSize()) {
                return SHARE_TYPE_ALL_FOLLOWERS;
            } else {
                return SHARE_TYPE_CUSTOM;
            }
        } else if (selectedItem == ShareDetailActivity.CODE_JUSTME_SHARED)
            return SHARE_TYPE_SELF;
        return null;
    }
}
