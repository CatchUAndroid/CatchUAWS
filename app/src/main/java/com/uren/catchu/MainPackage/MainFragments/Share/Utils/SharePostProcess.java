package com.uren.catchu.MainPackage.MainFragments.Share.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.PostRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.SignedUrlGetProcess;
import com.uren.catchu.ApiGatewayFunctions.UploadImageToS3;
import com.uren.catchu.ApiGatewayFunctions.UploadVideoToS3;
import com.uren.catchu.GeneralUtils.BitmapConversion;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.Interfaces.ServiceCompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Share.Models.ImageShareItemBox;
import com.uren.catchu.MainPackage.MainFragments.Share.Models.ShareItems;
import com.uren.catchu.MainPackage.MainFragments.Share.Models.VideoShareItemBox;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.SelectedFriendList;

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
    ShareItems shareItems;
    SignedUrlGetProcess signedUrlGetProcess = null;
    UploadImageToS3 uploadImageToS3 = null;
    UploadVideoToS3 uploadVideoToS3 = null;
    UploadImageToS3 uploadThumbnailToS3 = null;
    Bitmap photoBitmap = null;

    public SharePostProcess(Context context, ShareItems shareItems, ServiceCompleteCallback serviceCompleteCallback) {
        this.context = context;
        this.shareItems = shareItems;
        this.serviceCompleteCallback = serviceCompleteCallback;
        getImageAndVideoCount();

        if (totalMediaCount == 0)
            saveShareItemsToNeoJ();
        else
            uploadMediasToS3();
    }

    private void getImageAndVideoCount() {
        videoCount = shareItems.getVideoShareItemBoxes().size();
        imageCount = shareItems.getImageShareItemBoxes().size();
        totalMediaCount = imageCount + videoCount;
    }

    private void uploadMediasToS3() {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startUploadMediaToS3(token);
            }

            @Override
            public void onTokenFail(String message) {
            }
        });
    }

    private void startUploadMediaToS3(String token) {

        signedUrlGetProcess = new SignedUrlGetProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                final BucketUploadResponse commonS3BucketResult = (BucketUploadResponse) object;
                shareItems.setBucketUploadResponse(commonS3BucketResult);

                int counter = 0;
                for (final ImageShareItemBox imageShareItemBox : shareItems.getImageShareItemBoxes()) {
                    BucketUpload bucketUpload = commonS3BucketResult.getImages().get(counter);
                    uploadImages(bucketUpload, imageShareItemBox);
                    counter++;
                }

                /*counter = 0;
                for (final VideoShareItemBox videoShareItemBox : shareItems.getVideoShareItemBoxes()) {
                    BucketUpload bucketUpload = commonS3BucketResult.getVideos().get(counter);
                    uploadVideos(bucketUpload, videoShareItemBox);
                    uploadThumbnailImage(bucketUpload, videoShareItemBox);
                    counter++;
                }*/
            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.showToastShort(context, context.getResources().getString(R.string.error) + e.getMessage());
                serviceCompleteCallback.onFailed(e);
                signedUrlGetProcess.cancel(true);
            }

            @Override
            public void onTaskContinue() {

            }
        }, imageCount, videoCount, token);
        signedUrlGetProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void uploadImages(final BucketUpload bucketUpload, final ImageShareItemBox imageShareItemBox) {
        if (imageShareItemBox != null && imageShareItemBox.getPhotoSelectUtil() != null) {

            photoBitmap = BitmapConversion.compressImage(context, imageShareItemBox.getPhotoSelectUtil());

            if (photoBitmap == null)
                photoBitmap = BitmapConversion.getResizedBitmap2(context, imageShareItemBox.getPhotoSelectUtil());
        }

        if (photoBitmap == null) {
            if (imageShareItemBox.getPhotoSelectUtil().getScreeanShotBitmap() != null)
                photoBitmap = imageShareItemBox.getPhotoSelectUtil().getScreeanShotBitmap();
            else if (imageShareItemBox.getPhotoSelectUtil().getBitmap() != null)
                photoBitmap = imageShareItemBox.getPhotoSelectUtil().getBitmap();
        }

        if (photoBitmap == null)
            return;

        uploadImageToS3 = new UploadImageToS3(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                HttpURLConnection urlConnection = (HttpURLConnection) object;

                try {
                    if (urlConnection != null) {
                        if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            recycleImageBitmaps(imageShareItemBox);
                            imageShareItemBox.setUploaded(true);
                            Media media = new Media();
                            media.setExtension(bucketUpload.getExtension());
                            media.setType(IMAGE_TYPE);
                            media.setThumbnail(bucketUpload.getThumbnailUrl());
                            media.setUrl(bucketUpload.getDownloadUrl());
                            shareItems.getPost().getAttachments().add(media);
                            checkAllItemsUploaded();
                        } else {
                            imageShareItemBox.setUploaded(false);
                            InputStream is = urlConnection.getErrorStream();
                            serviceCompleteCallback.onFailed(new Exception(is.toString()));
                            uploadImageToS3.cancel(true);
                        }
                    } else {
                        imageShareItemBox.setUploaded(false);
                        serviceCompleteCallback.onFailed(new Exception(""));
                        uploadImageToS3.cancel(true);
                    }
                } catch (Exception e) {
                    imageShareItemBox.setUploaded(false);
                    serviceCompleteCallback.onFailed(e);
                    uploadImageToS3.cancel(true);
                }
            }

            @Override
            public void onFailure(Exception e) {
                imageShareItemBox.setUploaded(false);
                serviceCompleteCallback.onFailed(e);
                uploadImageToS3.cancel(true);
            }

            @Override
            public void onTaskContinue() {
            }
        }, photoBitmap, bucketUpload.getUploadUrl());
        uploadImageToS3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void recycleImageBitmaps(ImageShareItemBox imageShareItemBox) {
        if (photoBitmap != null)
            photoBitmap.recycle();

        if (imageShareItemBox != null && imageShareItemBox.getPhotoSelectUtil() != null) {
            if (imageShareItemBox.getPhotoSelectUtil().getBitmap() != null &&
                    !imageShareItemBox.getPhotoSelectUtil().getBitmap().isRecycled()) {
                imageShareItemBox.getPhotoSelectUtil().getBitmap().recycle();
                imageShareItemBox.getPhotoSelectUtil().setBitmap(null);
            }

            if (imageShareItemBox.getPhotoSelectUtil().getScreeanShotBitmap() != null &&
                    !imageShareItemBox.getPhotoSelectUtil().getScreeanShotBitmap().isRecycled()) {
                imageShareItemBox.getPhotoSelectUtil().getScreeanShotBitmap().recycle();
                imageShareItemBox.getPhotoSelectUtil().setScreeanShotBitmap(null);
            }
        }
    }

    /*private void uploadVideos(final BucketUpload bucketUpload, final VideoShareItemBox videoShareItemBox) {
        uploadVideoToS3 = new UploadVideoToS3(new OnEventListener() {
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
                            shareItems.getPost().getAttachments().add(media);
                            checkAllItemsUploaded();
                        } else {
                            videoShareItemBox.setVideoUploaded(false);
                            InputStream is = urlConnection.getErrorStream();
                            serviceCompleteCallback.onFailed(new Exception(is.toString()));
                            uploadVideoToS3.cancel(true);
                        }
                    } else {
                        videoShareItemBox.setVideoUploaded(false);
                        serviceCompleteCallback.onFailed(new Exception(""));
                        uploadVideoToS3.cancel(true);
                    }
                } catch (IOException e) {
                    videoShareItemBox.setVideoUploaded(false);
                    serviceCompleteCallback.onFailed(e);
                    uploadVideoToS3.cancel(true);
                }
            }

            @Override
            public void onFailure(Exception e) {
                videoShareItemBox.setVideoUploaded(false);
                serviceCompleteCallback.onFailed(e);
                uploadVideoToS3.cancel(true);
            }

            @Override
            public void onTaskContinue() {

            }
        }, bucketUpload.getUploadUrl(), videoShareItemBox.getVideoSelectUtil().getVideoRealPath());
        uploadVideoToS3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }*/

    /*public void uploadThumbnailImage(final BucketUpload bucketUpload, final VideoShareItemBox videoShareItemBox) {
        uploadThumbnailToS3 = new UploadImageToS3(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                HttpURLConnection urlConnection = (HttpURLConnection) object;
                try {
                    if (urlConnection != null) {
                        if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                            videoShareItemBox.setThumbnailImgUploaded(false);
                            InputStream is = urlConnection.getErrorStream();
                            serviceCompleteCallback.onFailed(new Exception(is.toString()));
                            uploadThumbnailToS3.cancel(true);
                        } else {
                            recycleVideoBitmaps(videoShareItemBox);
                            videoShareItemBox.setThumbnailImgUploaded(true);
                            checkAllItemsUploaded();
                        }
                    } else {
                        videoShareItemBox.setThumbnailImgUploaded(false);
                        serviceCompleteCallback.onFailed(new Exception(""));
                        uploadThumbnailToS3.cancel(true);
                    }
                } catch (IOException e) {
                    videoShareItemBox.setThumbnailImgUploaded(false);
                    serviceCompleteCallback.onFailed(e);
                    uploadThumbnailToS3.cancel(true);
                }
            }

            @Override
            public void onFailure(Exception e) {
                videoShareItemBox.setThumbnailImgUploaded(false);
                serviceCompleteCallback.onFailed(e);
                uploadThumbnailToS3.cancel(true);
            }

            @Override
            public void onTaskContinue() {
            }
        }, videoShareItemBox.getVideoSelectUtil().getVideoBitmap(), bucketUpload.getThumbnailUploadUrl());
        uploadThumbnailToS3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }*/

    /*private void recycleVideoBitmaps(VideoShareItemBox videoShareItemBox) {
        if (videoShareItemBox != null && videoShareItemBox.getVideoSelectUtil() != null &&
                videoShareItemBox.getVideoSelectUtil().getVideoBitmap() != null && !videoShareItemBox.getVideoSelectUtil().getVideoBitmap().isRecycled()) {
            videoShareItemBox.getVideoSelectUtil().getVideoBitmap().recycle();
            videoShareItemBox.getVideoSelectUtil().setVideoBitmap(null);
        }
    }*/

    public void checkAllItemsUploaded() {
        if (shareItems != null && shareItems.getImageShareItemBoxes() != null && shareItems.getImageShareItemBoxes().size() > 0) {
            for (ImageShareItemBox imageShareItemBox : shareItems.getImageShareItemBoxes()) {
                if (!imageShareItemBox.isUploaded())
                    return;
            }
        }

        if (shareItems != null && shareItems.getVideoShareItemBoxes() != null && shareItems.getVideoShareItemBoxes().size() > 0) {
            for (VideoShareItemBox videoShareItemBox : shareItems.getVideoShareItemBoxes()) {

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

            @Override
            public void onTokenFail(String message) {
            }
        });
    }

    private List<User> getParticipantList() {
        List<User> userList = new ArrayList<>();
        if (shareItems.getSelectedShareType() == SHARE_TYPE_CUSTOM) {
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
        shareItems.getPost().setPrivacyType(shareItems.getSelectedShareType());
        shareItems.getPost().setAllowList(getParticipantList());
        shareItems.getPost().setGroupid(shareItems.getSelectedGroup().getGroupid());
        setShareItemUser();
        postRequest.setPost(shareItems.getPost());

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
        shareItems.getPost().setUser(user);
    }
}
