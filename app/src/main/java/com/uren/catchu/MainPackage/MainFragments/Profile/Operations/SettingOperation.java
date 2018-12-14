package com.uren.catchu.MainPackage.MainFragments.Profile.Operations;

import android.content.Context;
import android.content.res.Resources;
import android.widget.Switch;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.twitter.sdk.android.core.TwitterCore;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.YesNoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.Interfaces.ServiceCompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.SingletonPostItem;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.SingletonSinglePost;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.PhoneVerification;
import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.JavaClasses.SingletonPostList;
import com.uren.catchu.MainPackage.MainFragments.Profile.Utils.UpdateUserProfileProcess;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderFacebookFriends;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.GroupListHolder;
import com.uren.catchu.Singleton.SelectedFriendList;

import catchu.model.UserProfileProperties;

public class SettingOperation {

    public static void userSignOut() {

        try {
            //Normal users
            FirebaseAuth firebaseAuth = AccountHolderInfo.getFirebaseAuth();
            firebaseAuth.signOut();

            //Facebook users
            if(LoginManager.getInstance()!=null){
                LoginManager.getInstance().logOut();
            }

            //Twitter users
            if(TwitterCore.getInstance()!=null){
                TwitterCore.getInstance().getSessionManager().clearActiveSession();
            }

            clearSingletonClasses();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null,SettingOperation.class.getSimpleName(),
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

            SingletonPostItem.reset();
            SingletonSinglePost.reset();
            SingletonPostList.reset();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null,SettingOperation.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public static void changeUserPrivacy(final Context context, final Switch privateAccSwitch) {
        try {
            privateAccSwitch.setEnabled(false);
            if (AccountHolderInfo.getInstance().getUser().getUserInfo().getIsPrivateAccount().booleanValue() && privateAccSwitch.isChecked()) {
                DialogBoxUtil.showYesNoDialog(context, null, context.getResources().getString(R.string.ASK_FOR_REMOVE_ACCOUNT_PRIVACY), new YesNoDialogBoxCallback() {
                    @Override
                    public void yesClick() {
                        updateUserPrivacy(false, context, privateAccSwitch);
                    }

                    @Override
                    public void noClick() {
                        privateAccSwitch.setEnabled(true);
                    }
                });
            } else {
                DialogBoxUtil.showYesNoDialog(context, null, context.getResources().getString(R.string.ASK_FOR_MAKE_ACCOUNT_PRIVACY), new YesNoDialogBoxCallback() {
                    @Override
                    public void yesClick() {
                        updateUserPrivacy(true, context, privateAccSwitch);
                    }

                    @Override
                    public void noClick() {
                        privateAccSwitch.setEnabled(true);
                    }
                });
            }
        } catch (Resources.NotFoundException e) {
            ErrorSaveHelper.writeErrorToDB(null,SettingOperation.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
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
        } catch (Resources.NotFoundException e) {
            ErrorSaveHelper.writeErrorToDB(null,SettingOperation.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }
}
