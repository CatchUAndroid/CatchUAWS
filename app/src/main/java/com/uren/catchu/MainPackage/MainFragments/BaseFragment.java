package com.uren.catchu.MainPackage.MainFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.uren.catchu.FragmentControllers.FragNavTransactionOptions;


public class BaseFragment extends Fragment {

    public static final String ARGS_INSTANCE = "com.f22labs.instalikefragmenttransaction";

    public FragmentNavigation mFragmentNavigation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentNavigation) {
            mFragmentNavigation = (FragmentNavigation) context;
        }
    }

    public interface FragmentNavigation {
        void pushFragment(Fragment fragment);
        void pushFragment(Fragment fragment, String animationTag);
    }

}
