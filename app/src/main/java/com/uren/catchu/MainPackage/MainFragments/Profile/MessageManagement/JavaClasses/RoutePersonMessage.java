package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.MessageWithPersonFragment;
import com.uren.catchu.Singleton.AccountHolderInfo;

import catchu.model.User;
import catchu.model.UserProfile;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;

public class RoutePersonMessage {

    Context context;
    String chattedUserId;
    int containerViewId;
    FragmentTransaction fragmentTransaction;

    public RoutePersonMessage(Context context, String chattedUserId,
                              int containerViewId, FragmentTransaction fragmentTransaction) {
        this.context = context;
        this.chattedUserId = chattedUserId;
        this.containerViewId = containerViewId;
        this.fragmentTransaction = fragmentTransaction;
    }

    public void routePersonMessagingFragment() {

        try {
            AccountHolderInfo.getToken(new TokenCallback() {
                @Override
                public void onTokenTaken(String token) {
                    UserDetail loadUserDetail = new UserDetail(new OnEventListener<UserProfile>() {

                        @Override
                        public void onSuccess(UserProfile up) {
                            if (up != null)
                                startMessageWithPersonFragment(fillChattedUser(up));
                        }

                        @Override
                        public void onFailure(Exception e) {
                            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                                    new Object() {
                                    }.getClass().getEnclosingMethod().getName(), e.toString());
                        }

                        @Override
                        public void onTaskContinue() {

                        }
                    }, AccountHolderInfo.getUserID(), AccountHolderInfo.getUserID(), token);

                    loadUserDetail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void startMessageWithPersonFragment(User user) {
        try {
            fragmentTransaction.replace(containerViewId, new MessageWithPersonFragment(user), this.getClass().getSimpleName());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public User fillChattedUser(UserProfile up) {
        User chattedUser = null;
        try {
            UserProfile userProfile = (UserProfile) up;
            UserProfileProperties userProfileProperties = userProfile.getUserInfo();

            chattedUser = new User();
            chattedUser.setEmail(userProfileProperties.getEmail());
            chattedUser.setProfilePhotoUrl(userProfileProperties.getProfilePhotoUrl());
            chattedUser.setUserid(userProfileProperties.getUserid());
            chattedUser.setName(userProfileProperties.getName());
            chattedUser.setUsername(userProfileProperties.getUsername());
            chattedUser.setProvider(userProfileProperties.getProvider());
            chattedUser.setIsPrivateAccount(userProfileProperties.getIsPrivateAccount());
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return chattedUser;
    }
}
