package com.uren.catchu.MainPackage.MainFragments.Profile.Utils;

import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ProfileRefreshCallback;

import java.util.ArrayList;
import java.util.List;

public class ProfileHelper {

    public static class ProfileRefresh {

        private static ProfileRefresh instance = null;
        private static List<ProfileRefreshCallback> profileRefreshCallbackList;

        public ProfileRefresh() {
            profileRefreshCallbackList = new ArrayList<>();
        }

        public static ProfileRefresh getInstance() {
            if (instance == null)
                instance = new ProfileRefresh();

            return instance;
        }

        public void setProfileRefreshCallback(ProfileRefreshCallback profileRefreshCallback) {
            profileRefreshCallbackList.add(profileRefreshCallback);
        }

        public static void profileRefreshStart() {
            if (instance != null) {
                for (int i = 0; i < profileRefreshCallbackList.size(); i++) {
                    profileRefreshCallbackList.get(i).onProfileRefresh();
                }
            }
        }

    }

}
