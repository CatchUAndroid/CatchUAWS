package com.uren.catchu.MainPackage.MainFragments.Profile.Operations;

import android.content.Context;
import android.widget.Switch;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.twitter.sdk.android.core.TwitterCore;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.YesNoDialogBoxCallback;
import com.uren.catchu.Interfaces.ServiceCompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.Utils.UpdateUserProfileProcess;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import catchu.model.UserProfileProperties;

public class SettingOperation {

    public static void userSignOut() {

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

        
    }

    public static void changeUserPrivacy(final Context context, final Switch privateAccSwitch) {
        privateAccSwitch.setEnabled(false);
        if (AccountHolderInfo.getInstance().getUser().getUserInfo().getIsPrivateAccount().booleanValue() && privateAccSwitch.isChecked()) {
            DialogBoxUtil.showYesNoDialog(context, null, context.getResources().getString(R.string.ASK_FOR_REMOVE_ACCOUNT_PRIVACY), new YesNoDialogBoxCallback() {
                @Override
                public void yesClick() {
                    updateUserPrivacy(false, context, privateAccSwitch);
                    privateAccSwitch.setChecked(false);
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
                    privateAccSwitch.setChecked(true);
                }

                @Override
                public void noClick() {
                    privateAccSwitch.setEnabled(true);
                }
            });
        }
    }

    public static void updateUserPrivacy(boolean privacyValue, Context context, final Switch privateAccSwitch) {
        UserProfileProperties userProfileProperties = AccountHolderInfo.getInstance().getUser().getUserInfo();
        userProfileProperties.setIsPrivateAccount(privacyValue);

        new UpdateUserProfileProcess(context, new ServiceCompleteCallback() {
            @Override
            public void onSuccess() {
                privateAccSwitch.setEnabled(true);
            }

            @Override
            public void onFailed(Exception e) {
                privateAccSwitch.setEnabled(true);
            }
        }, false, userProfileProperties, null);
    }
}
