package com.uren.catchu.MainPackage.MainFragments.Share.Utils;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.SignedUrlDeleteProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.YesNoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.VideoUtil.VideoSelectUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.GifDialogBox;
import com.uren.catchu.Interfaces.ServiceCompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Share.Models.ShareItems;
import com.uren.catchu.MainPackage.MainFragments.Share.Models.VideoShareItemBox;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.io.File;

import static com.uren.catchu.Constants.NumericConstants.SHARE_TRY_COUNT;
import static com.uren.catchu.Constants.StringConstants.CAMERA_TEXT;

public class ShareUtil {

    ShareItems shareItems;
    PermissionModule permissionModule;

    public ShareUtil(ShareItems shareItems) {
        this.shareItems = shareItems;
        this.permissionModule = new PermissionModule(NextActivity.thisActivity);
    }

    public void startToShare() {
        int tryCount = shareItems.getShareTryCount();
        shareItems.setShareTryCount(tryCount + 1);

        new SharePostProcess(NextActivity.thisActivity, shareItems, new ServiceCompleteCallback() {
            @Override
            public void onSuccess() {
                showShareSuccessView();
                deleteSharedVideo();
            }

            @Override
            public void onFailed(Exception e) {
                if (shareItems.getShareTryCount() <= SHARE_TRY_COUNT) {
                    if (NextActivity.thisActivity != null && shareItems != null) {
                        DialogBoxUtil.showYesNoDialog(NextActivity.thisActivity, null,
                                NextActivity.thisActivity.getResources().getString(R.string.DEFAULT_POST_ERROR_MESSAGE)
                                , new YesNoDialogBoxCallback() {
                                    @Override
                                    public void yesClick() {
                                        startToShare();
                                    }

                                    @Override
                                    public void noClick() {
                                        deleteSharedVideo();
                                        deleteUploadedItems();
                                    }
                                });
                    }
                } else {
                    CommonUtils.showCustomToast(NextActivity.thisActivity,
                            NextActivity.thisActivity.getResources().getString(R.string.SHARE_IS_UNSUCCESSFUL));
                    deleteSharedVideo();
                    deleteUploadedItems();
                }
            }
        });
    }

    public void deleteSharedVideo() {
        if (permissionModule.checkWriteExternalStoragePermission()) {

            for (VideoShareItemBox videoShareItemBox : shareItems.getVideoShareItemBoxes()) {
                VideoSelectUtil videoSelectUtil = videoShareItemBox.getVideoSelectUtil();

                if (videoSelectUtil != null && videoSelectUtil.getVideoRealPath() != null && !videoSelectUtil.getVideoRealPath().isEmpty()) {
                    if (videoSelectUtil.getSelectType() != null && videoSelectUtil.getSelectType().equals(CAMERA_TEXT)) {
                        File file = new File(videoSelectUtil.getVideoRealPath());
                        file.delete();
                        updateGalleryAfterFileDelete(file);
                    }
                }
            }
        }
    }

    public void showShareSuccessView() {
        if (NextActivity.thisActivity != null) {
            try {
                new GifDialogBox.Builder(NextActivity.thisActivity)
                        .setMessage(NextActivity.thisActivity.getResources().getString(R.string.SHARE_IS_SUCCESSFUL))
                        .setGifResource(R.drawable.gif16)
                        .setNegativeBtnVisibility(View.GONE)
                        .setPositiveBtnVisibility(View.GONE)
                        .setTitleVisibility(View.GONE)
                        .setDurationTime(3000)
                        .isCancellable(true)
                        .build();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateGalleryAfterFileDelete(File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        NextActivity.thisActivity.sendBroadcast(intent);
    }

    public void deleteUploadedItems() {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                if (shareItems != null && shareItems.getBucketUploadResponse() != null) {
                    SignedUrlDeleteProcess signedUrlDeleteProcess = new SignedUrlDeleteProcess(new OnEventListener() {
                        @Override
                        public void onSuccess(Object object) {
                        }

                        @Override
                        public void onFailure(Exception e) {
                        }

                        @Override
                        public void onTaskContinue() {

                        }
                    }, AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid(),
                            token,
                            shareItems.getBucketUploadResponse());
                    signedUrlDeleteProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });
    }
}
