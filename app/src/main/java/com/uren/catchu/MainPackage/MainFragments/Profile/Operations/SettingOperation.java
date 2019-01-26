package com.uren.catchu.MainPackage.MainFragments.Profile.Operations;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Switch;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.twitter.sdk.android.core.TwitterCore;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.CustomDialogBox;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.CustomDialogListener;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.Interfaces.ServiceCompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.SingletonSinglePost;
import com.uren.catchu.MainPackage.MainFragments.Profile.OtherProfile.JavaClasses.OtherProfilePostList;
import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.JavaClasses.SingletonPostList;
import com.uren.catchu.MainPackage.MainFragments.Profile.Utils.UpdateUserProfileProcess;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderFacebookFriends;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.GroupListHolder;
import com.uren.catchu.Singleton.SelectedFriendList;

import catchu.model.UserProfileProperties;

public class SettingOperation {

    private static final int CODE_REMOVE_PRIVACY = 0;
    private static final int CODE_MAKE_PRIVACY = 1;

    public static void userSignOut() {

        try {
            //Normal users
            FirebaseAuth firebaseAuth = AccountHolderInfo.getFirebaseAuth();
            firebaseAuth.signOut();

            facebookLogout();
            twitterLogout();
            clearSingletonClasses();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, SettingOperation.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public static void facebookLogout() {
        //Facebook users
        try {
            if (LoginManager.getInstance() != null) {
                LoginManager.getInstance().logOut();
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, SettingOperation.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public static void twitterLogout() {
        //Twitter users
        try {
            if (TwitterCore.getInstance() != null) {
                TwitterCore.getInstance().getSessionManager().clearActiveSession();
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, SettingOperation.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    static void clearSingletonClasses() {

        try {
            AccountHolderInfo.reset();
            AccountHolderFacebookFriends.reset();
            SelectedFriendList.reset();
            GroupListHolder.reset();

            SingletonSinglePost.reset();
            SingletonPostList.reset();
            OtherProfilePostList.reset();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, SettingOperation.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public static void changeUserPrivacy(final Context context, final Switch privateAccSwitch) {
        try {
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
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, SettingOperation.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
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
        try {
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
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, SettingOperation.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }
}
