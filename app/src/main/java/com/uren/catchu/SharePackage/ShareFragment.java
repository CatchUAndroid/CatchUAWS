package com.uren.catchu.SharePackage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.uren.catchu.Adapters.SpecialSelectTabAdapter;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.GalleryPicker.GalleryPickerFrag;
import com.uren.catchu.SharePackage.Utils.CheckShareItems;
import com.uren.catchu.SharePackage.VideoPicker.Utils.VideoFileListForDelete;
import com.uren.catchu.SharePackage.VideoPicker.fragment.VideoPickerFrag;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.Share.ShareItems;

import butterknife.ButterKnife;
import catchu.model.User;

public class ShareFragment extends BaseFragment {

    Context context;
    View view;

    TabLayout tabLayout;
    ViewPager viewPager;
    PermissionModule permissionModule;
    ImageView cancelImgv;
    ImageView nextImgv;

    GalleryPickerFrag galleryPickerFrag;
    VideoPickerFrag videoPickerFrag;

    //Tab constants
    private static final int TAB_PHOTO = 0;
    private static final int TAB_VIDEO = 1;

    private int tabSelectedPosition = 0;
    boolean tabsCreated = false;

    private int[] tabIcons = {
            R.drawable.tab_gallery,
            R.drawable.tab_video
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(view == null) {
            view = inflater.inflate(R.layout.fragment_main_share, container, false);
            ButterKnife.bind(this, view);
            context = getActivity();
            initVariables();
            addListeners();
            checkWriteStoragePermission();
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        /*initVariables();
        addListeners();
        checkWriteStoragePermission();*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == permissionModule.getWriteExternalStoragePermissionCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpPager();
            } else
                setUpPager();
        }
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(0).getIcon().setColorFilter(getActivity().getResources().getColor(R.color.Orange, null), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(getActivity().getResources().getColor(R.color.White, null), PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onStart() {
        NextActivity.bottomTabLayout.setVisibility(View.GONE);
        super.onStart();
    }

    private void initVariables() {
        tabLayout = view.findViewById(R.id.htab_tabs);
        viewPager = view.findViewById(R.id.htab_viewpager);
        cancelImgv = view.findViewById(R.id.cancelImgv);
        nextImgv = view.findViewById(R.id.nextImgv);
        permissionModule = new PermissionModule(getActivity());
        ShareItems.setInstance(null);
        VideoFileListForDelete.setInstance(null);
    }

    private void addListeners() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabsCreated)
                    tab.getIcon().setColorFilter(getActivity().getResources().getColor(R.color.Orange, null), PorterDuff.Mode.SRC_IN);

                tabSelectedPosition = tab.getPosition();

                switch (tab.getPosition()) {
                    case TAB_VIDEO:
                        videoPickerFrag.setFlashModeOff();
                        break;
                    case TAB_PHOTO:
                        videoPickerFrag.setFlashModeOff();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tabsCreated)
                    tab.getIcon().setColorFilter(getActivity().getResources().getColor(R.color.White, null), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        nextImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckShareItems checkShareItems = new CheckShareItems(getActivity());
                if (checkShareItems.shareIsPossible()) {
                    setShareItemUser();
                    VideoFileListForDelete.getInstance().deleteAllFile();
                    galleryPickerFrag.checkTextIsAddedOrNot();
                    startActivity(new Intent(getActivity(), ShareDetailActivity.class));
                } else
                    DialogBoxUtil.showInfoDialogBox(getActivity(), checkShareItems.getErrMessage(), null, new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {

                        }
                    });
            }
        });

        cancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setShareItemUser() {
        User user = new User();
        user.setUsername(AccountHolderInfo.getInstance().getUser().getUserInfo().getUsername());
        user.setUserid(AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid());
        user.setProfilePhotoUrl(AccountHolderInfo.getInstance().getUser().getUserInfo().getProfilePhotoUrl());
        ShareItems.getInstance().getPost().setUser(user);
    }

    public void checkWriteStoragePermission() {
        if (!permissionModule.checkWriteExternalStoragePermission())
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    permissionModule.getWriteExternalStoragePermissionCode());
        else
            setUpPager();
    }

    private void setUpPager() {
        SpecialSelectTabAdapter adapter = new SpecialSelectTabAdapter(getActivity().getSupportFragmentManager());

        galleryPickerFrag = new GalleryPickerFrag();
        videoPickerFrag = new VideoPickerFrag();

        adapter.addFragment(galleryPickerFrag, "");
        adapter.addFragment(videoPickerFrag, "");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        tabsCreated = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (tabSelectedPosition == TAB_PHOTO) {
            if (galleryPickerFrag.gridListAdapter != null) {
                galleryPickerFrag.gridListAdapter.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

}
