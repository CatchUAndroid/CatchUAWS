package com.uren.catchu.GeneralUtils;

import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.PostVideoPlayFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.SinglePostFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.AddGroupFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SettingsManagement.MarkProblemFragment;

public class FragmentTabHiddenUtil {

    public static String[] fragmentList = new String[]{
            MarkProblemFragment.class.getName(),
            AddGroupFragment.class.getName(),
            PostVideoPlayFragment.class.getName(),
            SinglePostFragment.class.getName()
    };

    public static boolean isFragmentInHiddenList(String fragName) {

        for (String fragmentName : fragmentList) {
            if (fragName.trim().equals(fragmentName.trim()))
                return true;
        }
        return false;
    }
}
