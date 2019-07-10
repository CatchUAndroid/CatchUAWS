package com.uren.catchu.MainPackage.MainFragments.Profile.Operations;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Switch;

import androidx.annotation.NonNull;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.twitter.sdk.android.core.TwitterCore;
import com.uren.catchu.ApiGatewayFunctions.EndPointProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.CustomDialogBox;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.CustomDialogListener;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.InfoDialogBoxCallback;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ServiceCompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.SingletonSinglePost;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses.MessageUpdateProcess;
import com.uren.catchu.MainPackage.MainFragments.Profile.OtherProfile.JavaClasses.OtherProfilePostList;
import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.JavaClasses.SingletonPostList;
import com.uren.catchu.MainPackage.MainFragments.Profile.Utils.UpdateUserProfileProcess;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderFacebookFriends;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.GroupListHolder;
import com.uren.catchu.Singleton.SelectedFriendList;

import catchu.model.BaseResponse;
import catchu.model.Endpoint;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.CHAR_H;
import static com.uren.catchu.Constants.StringConstants.ENDPOINT_LOGGED_OUT;
import static com.uren.catchu.Constants.StringConstants.ENDPOINT_PLATFORM_ANDROID;

public class SettingOperation {

    private static final int CODE_REMOVE_PRIVACY = 0;
    private static final int CODE_MAKE_PRIVACY = 1;
    private static FirebaseAuth firebaseAuth;
    static CompleteCallback mCompleteCallback;

    public static void userSignOut(CompleteCallback completeCallback) {

        mCompleteCallback = completeCallback;
        firebaseAuth = FirebaseAuth.getInstance();
        updateDeviceTokenForFCM();
    }

    public static void facebookLogout() {
        //Facebook users
        if (LoginManager.getInstance() != null) {
            LoginManager.getInstance().logOut();
        }
    }

    public static void twitterLogout() {
        //Twitter users
        if (TwitterCore.getInstance() != null) {
            TwitterCore.getInstance().getSessionManager().clearActiveSession();
        }
    }

    public static void changeUserPrivacy(final Context context, final Switch privateAccSwitch) {
        privateAccSwitch.setEnabled(false);
        if (AccountHolderInfo.getInstance().getUser().getUserInfo().getIsPrivateAccount().booleanValue() && privateAccSwitch.isChecked()) {
            showCustomDialogForPrivacy(context,
                    context.getResources().getString(R.string.ASK_FOR_REMOVE_ACCOUNT_PRIVACY),
                    CODE_REMOVE_PRIVACY,
                    privateAccSwitch);
        } else {
            showCustomDialogForPrivacy(context,
                    context.getResources().getString(R.string.ASK_FOR_MAKE_ACCOUNT_PRIVACY),
                    CODE_MAKE_PRIVACY,
                    privateAccSwitch);
        }
    }

    private static void showCustomDialogForPrivacy(final Context context, String message, final int privacyCode, final Switch privateAccSwitch) {
        new CustomDialogBox.Builder((Activity) context)
                .setMessage(message)
                .setNegativeBtnVisibility(View.VISIBLE)
                .setNegativeBtnText(context.getResources().getString(R.string.upperNo))
                .setNegativeBtnBackground(context.getResources().getColor(R.color.Silver, null))
                .setPositiveBtnVisibility(View.VISIBLE)
                .setPositiveBtnText(context.getResources().getString(R.string.upperYes))
                .setPositiveBtnBackground(context.getResources().getColor(R.color.DodgerBlue, null))
                .setDurationTime(0)
                .isCancellable(true)
                .setEditTextVisibility(View.GONE)
                .OnPositiveClicked(new CustomDialogListener() {
                    @Override
                    public void OnClick() {
                        if (privacyCode == CODE_REMOVE_PRIVACY)
                            updateUserPrivacy(false, context, privateAccSwitch);
                        else if (privacyCode == CODE_MAKE_PRIVACY)
                            updateUserPrivacy(true, context, privateAccSwitch);
                    }
                })
                .OnNegativeClicked(new CustomDialogListener() {
                    @Override
                    public void OnClick() {
                        privateAccSwitch.setEnabled(true);
                    }
                }).build();
    }

    public static void updateUserPrivacy(final boolean privacyValue, final Context context, final Switch privateAccSwitch) {
        UserProfileProperties userProfileProperties = AccountHolderInfo.getInstance().getUser().getUserInfo();
        userProfileProperties.setIsPrivateAccount(privacyValue);

        new UpdateUserProfileProcess(context, new ServiceCompleteCallback() {
            @Override
            public void onSuccess() {
                privateAccSwitch.setEnabled(true);
                privateAccSwitch.setChecked(privacyValue);
            }

            @Override
            public void onFailed(Exception e) {
                privateAccSwitch.setEnabled(true);
                DialogBoxUtil.showInfoDialogBox(context, context.getResources().getString(R.string.SOMETHING_WENT_WRONG), null, new InfoDialogBoxCallback() {
                    @Override
                    public void okClick() {

                    }
                });
            }
        }, false, userProfileProperties, null);
    }

    private static void updateDeviceTokenForFCM() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                try {
                    MessageUpdateProcess.updateTokenSigninValue(firebaseAuth.getCurrentUser().getUid(), CHAR_H);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    String deviceToken = instanceIdResult.getToken();
                    startEndPointProcess(deviceToken);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(e -> userLogOut());
    }

    private static void startEndPointProcess(final String deviceToken) {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {

                final Endpoint endpoint = new Endpoint();
                endpoint.setDeviceToken(deviceToken);
                endpoint.setUserid(firebaseAuth.getCurrentUser().getUid());
                endpoint.setPlatformType(ENDPOINT_PLATFORM_ANDROID);
                endpoint.setRequestType(ENDPOINT_LOGGED_OUT);

                EndPointProcess endPointProcess = new EndPointProcess(new OnEventListener<BaseResponse>() {

                    @Override
                    public void onSuccess(BaseResponse baseResponse) {
                        userLogOut();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        userLogOut();
                    }

                    @Override
                    public void onTaskContinue() {

                    }
                }, token, endpoint);

                endPointProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onTokenFail(String message) {
                userLogOut();
            }
        });
    }

    private static void userLogOut() {
        firebaseAuth.signOut();
        facebookLogout();
        twitterLogout();
        clearSingletonClasses();
    }

    static void clearSingletonClasses() {

        AccountHolderInfo.reset();
        AccountHolderFacebookFriends.reset();
        SelectedFriendList.reset();
        GroupListHolder.reset();

        SingletonSinglePost.reset();
        SingletonPostList.reset();
        OtherProfilePostList.reset();
        mCompleteCallback.onComplete(null);
    }
}
