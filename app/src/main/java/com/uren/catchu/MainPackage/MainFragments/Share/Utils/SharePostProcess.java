package com.uren.catchu.MainPackage.MainFragments.Share.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.PostRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.SignedUrlGetProcess;
import com.uren.catchu.ApiGatewayFunctions.UploadImageToS3;
import com.uren.catchu.ApiGatewayFunctions.UploadVideoToS3;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.Interfaces.ServiceCompleteCallback;
import com.uren.catchu.R;
import com.uren.catchu.MainPackage.MainFragments.Share.Models.ImageShareItemBox;
import com.uren.catchu.MainPackage.MainFragments.Share.Models.VideoShareItemBox;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.SelectedFriendList;
import com.uren.catchu.Singleton.Share.ShareItems;

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
import static com.uren.catchu.Constants.StringConstants.SHARE_TYPE_CUSTOM;
import static com.uren.catchu.Constants.StringConstants.VIDEO_TYPE;

public class SharePostProcess {

    Context context;
    ServiceCompleteCallback serviceCompleteCallback;
    int imageCount = 0;
    int videoCount = 0;
    int totalMediaCount = 0;
    PostRequest postRequest;

    public SharePostProcess(Context context, ServiceCompleteCallback serviceCompleteCallback) {
        this.context = context;
        this.serviceCompleteCallback = serviceCompleteCallback;
        getImageAndVideoCount();

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
                ShareItems.getInstance().setBucketUploadResponse(commonS3BucketResult);

                int counter = 0;
                for (final ImageShareItemBox imageShareItemBox : ShareItems.getInstance().getImageShareItemBoxes()) {
                    BucketUpload bucketUpload = commonS3BucketResult.getImages().get(counter);
                    uploadImages(bucketUpload, imageShareItemBox);
                    counter++;
                }

                counter = 0;
                for (final VideoShareItemBox videoShareItemBox : ShareItems.getInstance().getVideoShareItemBoxes()) {
                    BucketUpload bucketUpload = commonS3BucketResult.getVideos().get(counter);
                    uploadVideos(bucketUpload, videoShareItemBox);
                    uploadThumbnailImage(bucketUpload, videoShareItemBox);
                    counter++;
                }
            }

            @Override
            public void onFailure(Exception e) {
                //dialogDismiss();
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

    public void uploadImages(final BucketUpload bucketUpload, final ImageShareItemBox imageShareItemBox) {
        Bitmap photoBitmap = null;

        if (imageShareItemBox != null && imageShareItemBox.getPhotoSelectUtil() != null) {
            if (imageShareItemBox.getPhotoSelectUtil().getResizedBitmap() != null)
                photoBitmap = imageShareItemBox.getPhotoSelectUtil().getResizedBitmap();
            else if (imageShareItemBox.getPhotoSelectUtil().getScreeanShotBitmap() != null)
                photoBitmap = imageShareItemBox.getPhotoSelectUtil().getScreeanShotBitmap();
            else if (imageShareItemBox.getPhotoSelectUtil().getBitmap() != null)
                photoBitmap = imageShareItemBox.getPhotoSelectUtil().getBitmap();
        }

        if (photoBitmap == null)
            return;

        UploadImageToS3 uploadImageToS3 = new UploadImageToS3(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                HttpURLConnection urlConnection = (HttpURLConnection) object;

                try {
                    if (urlConnection != null) {
                        if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            imageShareItemBox.setUploaded(true);
                            Media media = new Media();
                            media.setExtension(bucketUpload.getExtension());
                            media.setType(IMAGE_TYPE);
                            media.setThumbnail(bucketUpload.getThumbnailUrl());
                            media.setUrl(bucketUpload.getDownloadUrl());
                            ShareItems.getInstance().getPost().getAttachments().add(media);
                            checkAllItemsUploaded();
                        } else {
                            imageShareItemBox.setUploaded(false);
                            InputStream is = urlConnection.getErrorStream();
                            serviceCompleteCallback.onFailed(new Exception(is.toString()));
                        }
                    } else {
                        imageShareItemBox.setUploaded(false);
                        serviceCompleteCallback.onFailed(new Exception(""));
                    }
                } catch (IOException e) {
                    imageShareItemBox.setUploaded(false);
                    serviceCompleteCallback.onFailed(e);
                }
            }

            @Override
            public void onFailure(Exception e) {
                imageShareItemBox.setUploaded(false);
                serviceCompleteCallback.onFailed(e);
            }

            @Override
            public void onTaskContinue() {
            }
        }, photoBitmap, bucketUpload.getUploadUrl());
        uploadImageToS3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void uploadVideos(final BucketUpload bucketUpload, final VideoShareItemBox videoShareItemBox) {
        UploadVideoToS3 uploadVideoToS3 = new UploadVideoToS3(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                HttpURLConnection urlConnection = (HttpURLConnection) object;

                try {
                    if (urlConnection != null) {
                        if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            videoShareItemBox.setVideoUploaded(true);
                            Media media = new Media();
                            media.setExtension(bucketUpload.getExtension());
                            media.setType(VIDEO_TYPE);
                            media.setThumbnail(bucketUpload.getThumbnailUrl());
                            media.setUrl(bucketUpload.getDownloadUrl());
                            ShareItems.getInstance().getPost().getAttachments().add(media);
                            checkAllItemsUploaded();
                        } else {
                            videoShareItemBox.setVideoUploaded(false);
                            InputStream is = urlConnection.getErrorStream();
                            serviceCompleteCallback.onFailed(new Exception(is.toString()));
                        }
                    } else {
                        videoShareItemBox.setVideoUploaded(false);
                        serviceCompleteCallback.onFailed(new Exception(""));
                    }
                } catch (IOException e) {
                    videoShareItemBox.setVideoUploaded(false);
                    serviceCompleteCallback.onFailed(e);
                }
            }

            @Override
            public void onFailure(Exception e) {
                videoShareItemBox.setVideoUploaded(false);
                serviceCompleteCallback.onFailed(e);
            }

            @Override
            public void onTaskContinue() {

            }
        }, bucketUpload.getUploadUrl(), videoShareItemBox.getVideoSelectUtil().getVideoUri());
        uploadVideoToS3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void uploadThumbnailImage(final BucketUpload bucketUpload, final VideoShareItemBox videoShareItemBox) {
        UploadImageToS3 uploadImageToS3 = new UploadImageToS3(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                HttpURLConnection urlConnection = (HttpURLConnection) object;
                try {
                    if (urlConnection != null) {
                        if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                            videoShareItemBox.setThumbnailImgUploaded(false);
                            InputStream is = urlConnection.getErrorStream();
                            serviceCompleteCallback.onFailed(new Exception(is.toString()));
                        } else {
                            videoShareItemBox.setThumbnailImgUploaded(true);
                            checkAllItemsUploaded();
                        }
                    } else {
                        videoShareItemBox.setThumbnailImgUploaded(false);
                        serviceCompleteCallback.onFailed(new Exception(""));
                    }
                } catch (IOException e) {
                    videoShareItemBox.setThumbnailImgUploaded(false);
                    serviceCompleteCallback.onFailed(e);
                }
            }

            @Override
            public void onFailure(Exception e) {
                videoShareItemBox.setThumbnailImgUploaded(false);
                serviceCompleteCallback.onFailed(e);
            }

            @Override
            public void onTaskContinue() {
            }
        }, videoShareItemBox.getVideoSelectUtil().getVideoBitmap(), bucketUpload.getThumbnailUploadUrl());
        uploadImageToS3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void checkAllItemsUploaded() {
        if (ShareItems.getInstance() != null && ShareItems.getInstance().getImageShareItemBoxes() != null && ShareItems.getInstance().getImageShareItemBoxes().size() > 0) {
            for (ImageShareItemBox imageShareItemBox : ShareItems.getInstance().getImageShareItemBoxes()) {
                if (!imageShareItemBox.isUploaded())
                    return;
            }
        }

        if (ShareItems.getInstance() != null && ShareItems.getInstance().getVideoShareItemBoxes() != null && ShareItems.getInstance().getVideoShareItemBoxes().size() > 0) {
            for (VideoShareItemBox videoShareItemBox : ShareItems.getInstance().getVideoShareItemBoxes()) {

                if (!videoShareItemBox.isVideoUploaded())
                    return;

                if (!videoShareItemBox.isThumbnailImgUploaded())
                    return;
            }
        }
        saveShareItemsToNeoJ();
    }

    private void saveShareItemsToNeoJ() {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startSaveShareItemsToNeoJ(token);
            }
        });
    }

    private List<User> getParticipantList() {
        List<User> userList = new ArrayList<>();
        if (ShareItems.getInstance().getSelectedShareType() == SHARE_TYPE_CUSTOM) {
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

    private void startSaveShareItemsToNeoJ(String token) {
        postRequest = new PostRequest();
        ShareItems.getInstance().getPost().setPrivacyType(ShareItems.getInstance().getSelectedShareType());
        ShareItems.getInstance().getPost().setAllowList(getParticipantList());
        ShareItems.getInstance().getPost().setGroupid(ShareItems.getInstance().getSelectedGroup().getGroupid());
        setShareItemUser();
        postRequest.setPost(ShareItems.getInstance().getPost());

        PostRequestProcess postRequestProcess = new PostRequestProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                if (object != null)
                    serviceCompleteCallback.onSuccess();
                else
                    serviceCompleteCallback.onFailed(new Exception(context.getResources().getString(R.string.serverError)));
            }

            @Override
            public void onFailure(Exception e) {
                serviceCompleteCallback.onFailed(e);
            }

            @Override
            public void onTaskContinue() {

            }
        }, postRequest, token);
        postRequestProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setShareItemUser() {
        User user = new User();
        user.setUsername(AccountHolderInfo.getInstance().getUser().getUserInfo().getUsername());
        user.setUserid(AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid());
        user.setProfilePhotoUrl(AccountHolderInfo.getInstance().getUser().getUserInfo().getProfilePhotoUrl());
        ShareItems.getInstance().getPost().setUser(user);
    }
}
