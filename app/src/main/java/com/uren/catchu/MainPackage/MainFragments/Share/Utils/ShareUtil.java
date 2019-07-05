package com.uren.catchu.MainPackage.MainFragments.Share.Utils;

import android.os.AsyncTask;
import android.view.View;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.SignedUrlDeleteProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.GifDialogBox;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.YesNoDialogBoxCallback;
import com.uren.catchu.Interfaces.ServiceCompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostHelper;
import com.uren.catchu.MainPackage.MainFragments.Share.Models.ShareItems;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import static com.uren.catchu.Constants.NumericConstants.SHARE_TRY_COUNT;

public class ShareUtil {

    ShareItems shareItems;
    PermissionModule permissionModule;

    /*final static String ACTION = "NotifyServiceAction";
    final static int RQS_STOP_SERVICE = 1;
    NotifyServiceReceiver notifyServiceReceiver;*/

    public ShareUtil(ShareItems shareItems) {
        this.shareItems = shareItems;
        this.permissionModule = new PermissionModule(NextActivity.thisActivity);
    }

    /*@Override
    public void onCreate() {
        notifyServiceReceiver = new NotifyServiceReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(notifyServiceReceiver, intentFilter);
        super.onCreate();
    }*/

    /*@Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        shareItems = (ShareItems)  intent.getExtras().get("ShareItems");
        permissionModule = new PermissionModule(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(notifyServiceReceiver, intentFilter);
        startToShare();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        this.unregisterReceiver(notifyServiceReceiver);
        super.onDestroy();
    }*/

    /*public class NotifyServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {

            int rqs = arg1.getIntExtra("RQS", 0);
            if (rqs == RQS_STOP_SERVICE) {
                stopSelf();
            }
        }
    }*/

    public void startToShare() {
        try {

            //Start progress in FeedFragment
            PostHelper.InitFeed.getFeedFragment().startProgressBar();

            int tryCount = shareItems.getShareTryCount();
            shareItems.setShareTryCount(tryCount + 1);

            new SharePostProcess(NextActivity.thisActivity, shareItems, new ServiceCompleteCallback() {
                @Override
                public void onSuccess() {
                    showShareSuccessView();
                    ShareDeleteProcess.deleteSharedVideo(null, permissionModule, shareItems);
                    ShareDeleteProcess.deleteSharedPhoto(null, permissionModule, shareItems);

                    //Stop progressbar in FeedFragment
                    PostHelper.InitFeed.getFeedFragment().stopProgressBar();
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
                                            ShareDeleteProcess.deleteSharedVideo(null, permissionModule, shareItems);
                                            ShareDeleteProcess.deleteSharedPhoto(null, permissionModule, shareItems);
                                            deleteUploadedItems();
                                        }
                                    });
                        }
                    } else {
                        CommonUtils.showToastShort(NextActivity.thisActivity,
                                NextActivity.thisActivity.getResources().getString(R.string.SHARE_IS_UNSUCCESSFUL));
                        ShareDeleteProcess.deleteSharedVideo(null, permissionModule, shareItems);
                        ShareDeleteProcess.deleteSharedPhoto(null, permissionModule, shareItems);
                        deleteUploadedItems();
                    }

                    //Stop progressbar in FeedFragment
                    PostHelper.InitFeed.getFeedFragment().stopProgressBar();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            PostHelper.InitFeed.getFeedFragment().stopProgressBar();
        }
    }

    public void showShareSuccessView() {
        if (NextActivity.thisActivity != null) {
            new GifDialogBox.Builder(NextActivity.thisActivity)
                    .setMessage(NextActivity.thisActivity.getResources().getString(R.string.SHARE_IS_SUCCESSFUL))
                    .setGifResource(R.drawable.gif16)
                    .setNegativeBtnVisibility(View.GONE)
                    .setPositiveBtnVisibility(View.GONE)
                    .setTitleVisibility(View.GONE)
                    .setDurationTime(3000)
                    .isCancellable(true)
                    .build();

            PostHelper.FeedRefresh.feedRefreshStart();
        }
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

            @Override
            public void onTokenFail(String message) {
            }
        });
    }
}
