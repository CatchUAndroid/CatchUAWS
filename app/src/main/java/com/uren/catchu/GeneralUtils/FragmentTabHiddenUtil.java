package com.uren.catchu.GeneralUtils;

import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.FilterFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.PostImageViewFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.PostVideoPlayFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.SinglePostFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.AddGroupFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.EditGroupNameFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SettingsManagement.MarkProblemFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SettingsManagement.NotifyProblemFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ChangePasswordFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.PhoneNumEditFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.UserEditFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.VerifyPhoneNumberFragment;

public class FragmentTabHiddenUtil {

    public static String[] fragmentList = new String[]{
            MarkProblemFragment.class.getName(),
            AddGroupFragment.class.getName(),
            PostVideoPlayFragment.class.getName(),
            PostImageViewFragment.class.getName(),
            SinglePostFragment.class.getName(),
            UserEditFragment.class.getName(),
            PhoneNumEditFragment.class.getName(),
            VerifyPhoneNumberFragment.class.getName(),
            NotifyProblemFragment.class.getName(),
            ChangePasswordFragment.class.getName(),
            EditGroupNameFragment.class.getName(),
            FilterFragment.class.getName()
    };

    public static boolean isFragmentInHiddenList(String fragName) {

        for (String fragmentName : fragmentList) {
            if (fragName.trim().equals(fragmentName.trim()))
                return true;
        }
        return false;
    }
}
