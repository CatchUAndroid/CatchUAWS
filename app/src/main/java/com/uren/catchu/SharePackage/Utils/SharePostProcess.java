package com.uren.catchu.SharePackage.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.PostRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.SignedUrlGetProcess;
import com.uren.catchu.ApiGatewayFunctions.UploadImageToS3;
import com.uren.catchu.ApiGatewayFunctions.UploadVideoToS3;
import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ServiceCompleteCallback;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.Models.ImageShareItemBox;
import com.uren.catchu.SharePackage.Models.VideoShareItemBox;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.SelectedFriendList;
import com.uren.catchu.Singleton.SelectedGroupList;
import com.uren.catchu.Singleton.Share.ShareItems;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import catchu.model.BucketUpload;
import catchu.model.BucketUploadResponse;
import catchu.model.FriendList;
import catchu.model.Media;
import catchu.model.PostRequest;
import catchu.model.User;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.IMAGE_TYPE;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_ALL_FOLLOWERS;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_CUSTOM;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_EVERYONE;
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_GROUP;
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
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startUploadMediaToS3(token);
            }
        });
    }

    private void startUploadMediaToS3(String token) {
        SignedUrlGetProcess signedUrlGetProcess = new SignedUrlGetProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                final BucketUploadResponse commonS3BucketResult = (BucketUploadResponse) object;

                int counter = 0;
                for (ImageShareItemBox imageShareItemBox : ShareItems.getInstance().getImageShareItemBoxes()) {
                    Bitmap photoBitmap;
                    if (imageShareItemBox.getPhotoSelectUtil().getScreeanShotBitmap() != null)
                        photoBitmap = imageShareItemBox.getPhotoSelectUtil().getScreeanShotBitmap();
                    else
                        photoBitmap = imageShareItemBox.getPhotoSelectUtil().getBitmap();

                    BucketUpload bucketUpload = commonS3BucketResult.getImages().get(counter);
                    uploadImages(bucketUpload, photoBitmap);
                    counter++;
                }

                counter = 0;
                for (VideoShareItemBox videoShareItemBox : ShareItems.getInstance().getVideoShareItemBoxes()) {
                    BucketUpload bucketUpload = commonS3BucketResult.getVideos().get(counter);
                    uploadVideos(bucketUpload, videoShareItemBox.getVideoSelectUtil().getVideoUri(),
                            videoShareItemBox.getVideoSelectUtil().getVideoBitmap());
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
        }, imageCount, videoCount, token);
        signedUrlGetProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void uploadImages(final BucketUpload bucketUpload, Bitmap bitmap) {
        UploadImageToS3 uploadImageToS3 = new UploadImageToS3(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                HttpURLConnection urlConnection = (HttpURLConnection) object;
                handleUrlConnectionResult(urlConnection, bucketUpload, IMAGE_TYPE, null);
            }

            @Override
            public void onFailure(Exception e) {
                dialogDismiss();
                CommonUtils.showToastLong(context, context.getResources().getString(R.string.error) + e.getMessage());
                serviceCompleteCallback.onFailed(e);
            }

            @Override
            public void onTaskContinue() {
            }
        }, bitmap, bucketUpload.getUploadUrl());
        uploadImageToS3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void uploadVideos(final BucketUpload bucketUpload, Uri videoUri, final Bitmap videoBitmap) {
        UploadVideoToS3 uploadVideoToS3 = new UploadVideoToS3(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                HttpURLConnection urlConnection = (HttpURLConnection) object;
                handleUrlConnectionResult(urlConnection, bucketUpload, VIDEO_TYPE, videoBitmap);
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
        }, bucketUpload.getUploadUrl(), videoUri);
        uploadVideoToS3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void handleUrlConnectionResult(HttpURLConnection urlConnection, BucketUpload bucketUpload, String mediaType, Bitmap videoBitmap) {
        try {
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Media media = new Media();
                media.setExtension(bucketUpload.getExtension());
                media.setType(mediaType);
                media.setThumbnail(bucketUpload.getThumbnailUrl());
                media.setUrl(bucketUpload.getDownloadUrl());
                ShareItems.getInstance().getPost().getAttachments().add(media);

                if (mediaType.equals(VIDEO_TYPE)) {
                    uploadThumbnailImage(bucketUpload, videoBitmap);
                } else {
                    uploadIndex++;
                    checkAllItemsUploaded();
                }
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

    public void uploadThumbnailImage(final BucketUpload bucketUpload, Bitmap bitmap) {
        UploadImageToS3 uploadImageToS3 = new UploadImageToS3(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                HttpURLConnection urlConnection = (HttpURLConnection) object;
                try {
                    if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        dialogDismiss();
                        InputStream is = urlConnection.getErrorStream();
                        serviceCompleteCallback.onFailed(new Exception(is.toString()));
                    } else {
                        uploadIndex++;
                        checkAllItemsUploaded();
                    }
                } catch (IOException e) {
                    dialogDismiss();
                    CommonUtils.showToastLong(context, context.getResources().getString(R.string.error) + e.getMessage());
                    serviceCompleteCallback.onFailed(e);
                }
            }

            @Override
            public void onFailure(Exception e) {
                dialogDismiss();
                CommonUtils.showToastLong(context, context.getResources().getString(R.string.error) + e.getMessage());
                serviceCompleteCallback.onFailed(e);
            }

            @Override
            public void onTaskContinue() {
            }
        }, bitmap, bucketUpload.getThumbnailUploadUrl());
        uploadImageToS3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void checkAllItemsUploaded() {
        if (uploadIndex == totalMediaCount)
            saveShareItemsToNeoJ();
    }

    private void saveShareItemsToNeoJ() {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                checkPrivacyType(token);
            }
        });
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

    private void checkPrivacyType(final String token) {
        if (selectedItem == ShareDetailActivity.CODE_PUBLIC_SHARED)
            saveToNeo(token, SHARE_TYPE_EVERYONE);
        else if (selectedItem == ShareDetailActivity.CODE_FRIEND_SHARED) {

            AccountHolderFollowProcess.getFollowers(new CompleteCallback() {
                @Override
                public void onComplete(Object object) {
                    if (object != null) {
                        FriendList friendList = (FriendList) object;
                        if (friendList != null && friendList.getResultArray() != null) {
                            if (friendList.getResultArray().size() == SelectedFriendList.getInstance().getSize())
                                saveToNeo(token, SHARE_TYPE_ALL_FOLLOWERS);
                        } else
                            saveToNeo(token, SHARE_TYPE_CUSTOM);
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    serviceCompleteCallback.onFailed(e);
                }
            });
        } else if (selectedItem == ShareDetailActivity.CODE_JUSTME_SHARED)
            saveToNeo(token, SHARE_TYPE_SELF);
        else if (selectedItem == ShareDetailActivity.CODE_GROUP_SHARED)
            saveToNeo(token, SHARE_TYPE_GROUP);
    }

    private void saveToNeo(String token, String postPrivacyType) {
        postRequest = new PostRequest();
        ShareItems.getInstance().getPost().setPrivacyType(postPrivacyType);
        ShareItems.getInstance().getPost().setAllowList(getParticipantList());
        ShareItems.getInstance().getPost().setGroupid(getGroupId());
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
        }, postRequest, token);
        postRequestProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private String getGroupId() {
        if (SelectedGroupList.getInstance() != null && SelectedGroupList.getInstance().getSize() > 0) {
            return SelectedGroupList.getInstance().getGroupRequestResult().getResultArray().get(0).getGroupid();
        } else
            return "";
    }
}
