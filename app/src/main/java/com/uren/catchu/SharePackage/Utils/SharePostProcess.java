package com.uren.catchu.SharePackage.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.PostRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.SignedUrlGetProcess;
import com.uren.catchu.ApiGatewayFunctions.UploadImageToS3;
import com.uren.catchu.ApiGatewayFunctions.UploadVideoToS3;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.Interfaces.ServiceCompleteCallback;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.Models.ImageShareItemBox;
import com.uren.catchu.SharePackage.Models.VideoShareItemBox;
import com.uren.catchu.SharePackage.ShareDetailActivity;
import com.uren.catchu.Singleton.SelectedFriendList;
import com.uren.catchu.Singleton.Share.ShareItems;
import com.uren.catchu.Singleton.UserFriends;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import catchu.model.BucketUpload;
import catchu.model.BucketUploadResponse;
import catchu.model.Media;
import catchu.model.PostRequest;
import catchu.model.User;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.IMAGE_TYPE;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_ALL_FOLLOWERS;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_CUSTOM;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_EVERYONE;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_SELF;
import static com.uren.catchu.Constants.StringConstants.VIDEO_TYPE;

public class SharePostProcess {

    int selectedItem;
    Context context;
    ServiceCompleteCallback serviceCompleteCallback;
    ProgressDialog mProgressDialog;
    int imageCount = 0;
    int videoCount = 0;
    int totalMediaCount = 0;
    int uploadIndex = 0;
    PostRequest postRequest;

    public SharePostProcess(Context context, int selectedItem, ServiceCompleteCallback serviceCompleteCallback) {
        this.context = context;
        this.selectedItem = selectedItem;
        this.serviceCompleteCallback = serviceCompleteCallback;
        mProgressDialog = new ProgressDialog(context);
        getImageAndVideoCount();
        mProgressDialog.setMessage(context.getResources().getString(R.string.shareIsProcessing));
        dialogShow();

        if (totalMediaCount == 0)
            saveShareItemsToNeoJ();
        else
            uploadMediasToS3();
    }

    private void getImageAndVideoCount() {
        videoCount = ShareItems.getInstance().getVideoShareItemBoxes().size();
        imageCount = ShareItems.getInstance().getImageShareItemBoxes().size();
        totalMediaCount = imageCount + videoCount;
    }

    public void dialogShow() {
        if (!mProgressDialog.isShowing()) mProgressDialog.show();
    }

    public void dialogDismiss() {
        if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
    }

    private void uploadMediasToS3() {
        SignedUrlGetProcess signedUrlGetProcess = new SignedUrlGetProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                final BucketUploadResponse commonS3BucketResult = (BucketUploadResponse) object;

                int counter = 0;
                for (ImageShareItemBox imageShareItemBox : ShareItems.getInstance().getImageShareItemBoxes()) {
                    uploadImages(commonS3BucketResult.getImages().get(counter).getDownloadUrl(),
                            commonS3BucketResult.getImages().get(counter).getThumbnailUrl(),
                            commonS3BucketResult.getImages().get(counter).getUploadUrl(),
                            commonS3BucketResult.getImages().get(counter).getExtension(),
                            imageShareItemBox.getPhotoSelectUtil().getBitmap());
                    counter++;
                }

                counter = 0;
                for (VideoShareItemBox videoShareItemBox : ShareItems.getInstance().getVideoShareItemBoxes()) {
                    uploadVideos(commonS3BucketResult.getVideos().get(counter).getDownloadUrl(),
                            commonS3BucketResult.getVideos().get(counter).getThumbnailUrl(),
                            commonS3BucketResult.getVideos().get(counter).getUploadUrl(),
                            commonS3BucketResult.getVideos().get(counter).getExtension(),
                            videoShareItemBox.getVideoSelectUtil().getVideoUri());
                    counter++;
                }
            }

            @Override
            public void onFailure(Exception e) {
                dialogDismiss();
                Log.i("Info", "Paylasim Exception yedi2:" + e.getMessage());
                CommonUtils.showToastLong(context, context.getResources().getString(R.string.error) + e.getMessage());
                serviceCompleteCallback.onFailed(e);
            }

            @Override
            public void onTaskContinue() {

            }
        }, imageCount, videoCount);
        signedUrlGetProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void uploadImages(final String downloadUrl, final String thumbnailUrl,
                             String uploadUrl, final String extensionType, Bitmap bitmap) {
        UploadImageToS3 uploadImageToS3 = new UploadImageToS3(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                HttpURLConnection urlConnection = (HttpURLConnection) object;
                handleUrlConnectionResult(urlConnection, extensionType, IMAGE_TYPE, thumbnailUrl, downloadUrl);
            }

            @Override
            public void onFailure(Exception e) {
                dialogDismiss();
                Log.i("Info", "Paylasim Exception yedi3:" + e.getMessage());
                CommonUtils.showToastLong(context, context.getResources().getString(R.string.error) + e.getMessage());
                serviceCompleteCallback.onFailed(e);
            }

            @Override
            public void onTaskContinue() {
            }
        }, bitmap, uploadUrl);
        uploadImageToS3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void uploadVideos(final String downloadUrl, final String thumbnailUrl, String uploadUrl, final String extensionType, Uri videoUri) {
        UploadVideoToS3 uploadVideoToS3 = new UploadVideoToS3(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                HttpURLConnection urlConnection = (HttpURLConnection) object;
                handleUrlConnectionResult(urlConnection, extensionType, VIDEO_TYPE, thumbnailUrl, downloadUrl);
            }

            @Override
            public void onFailure(Exception e) {
                dialogDismiss();
                Log.i("Info", "Paylasim video Exception yedi3:" + e.getMessage());
                CommonUtils.showToastLong(context, context.getResources().getString(R.string.error) + e.getMessage());
                serviceCompleteCallback.onFailed(e);
            }

            @Override
            public void onTaskContinue() {

            }
        }, uploadUrl, videoUri);
        uploadVideoToS3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void handleUrlConnectionResult(HttpURLConnection urlConnection, String extensionType, String mediaType, String thumbnailUrl, String downloadUrl) {
        try {
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Media media = new Media();
                media.setExtension(extensionType);
                media.setType(mediaType);
                media.setThumbnail(thumbnailUrl);
                media.setUrl(downloadUrl);
                ShareItems.getInstance().getPost().getAttachments().add(media);
                uploadIndex++;

                if (uploadIndex == totalMediaCount)
                    saveShareItemsToNeoJ();
            } else {
                dialogDismiss();
                InputStream is = urlConnection.getErrorStream();
                serviceCompleteCallback.onFailed(new Exception(is.toString()));
            }
        } catch (IOException e) {
            dialogDismiss();
            Log.i("Info", "Paylasim Exception yedi4:" + e.getMessage());
            CommonUtils.showToastLong(context, context.getResources().getString(R.string.error) + e.getMessage());
            serviceCompleteCallback.onFailed(e);
        }
    }

    private void saveShareItemsToNeoJ() {
        postRequest = new PostRequest();
        ShareItems.getInstance().getPost().setPrivacyType(getPostPrivacyType());
        ShareItems.getInstance().getPost().setAllowList(getParticipantList());
        postRequest.setPost(ShareItems.getInstance().getPost());

        PostRequestProcess postRequestProcess = new PostRequestProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                Log.i("Info", "Paylasim ok");
                dialogDismiss();
                serviceCompleteCallback.onSuccess();
            }

            @Override
            public void onFailure(Exception e) {
                Log.i("Info", "Paylasim Exception yedi1:" + e.getMessage());
                dialogDismiss();
                serviceCompleteCallback.onFailed(e);
            }

            @Override
            public void onTaskContinue() {

            }
        }, postRequest);
        postRequestProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private List<User> getParticipantList() {
        List<User> userList = new ArrayList<>();
        if (selectedItem == ShareDetailActivity.CODE_FRIEND_SHARED) {
            for (UserProfileProperties userProfileProperties : SelectedFriendList.getInstance().getSelectedFriendList().getResultArray()) {
                User user = new User();
                user.setProfilePhotoUrl(userProfileProperties.getProfilePhotoUrl());
                user.setUserid(userProfileProperties.getUserid());
                user.setUsername(userProfileProperties.getUsername());
                userList.add(user);
            }
        }
        return userList;
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
