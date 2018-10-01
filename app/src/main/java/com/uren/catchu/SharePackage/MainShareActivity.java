package com.uren.catchu.SharePackage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.uren.catchu.Adapters.SpecialSelectTabAdapter;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.GalleryPicker.GalleryPickerFrag;
import com.uren.catchu.SharePackage.GalleryPicker.TextEditFragment;
import com.uren.catchu.SharePackage.Utils.CheckShareItems;
import com.uren.catchu.SharePackage.VideoPicker.Utils.VideoFileListForDelete;
import com.uren.catchu.SharePackage.VideoPicker.fragment.VideoPickerFrag;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.Share.ShareItems;

import java.util.List;

import catchu.model.User;

public class MainShareActivity extends FragmentActivity {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_share);
        initVariables();
        addListeners();
        checkWriteStoragePermission();
    }

    private void initVariables() {
        tabLayout = findViewById(R.id.htab_tabs);
        viewPager = findViewById(R.id.htab_viewpager);
        cancelImgv = findViewById(R.id.cancelImgv);
        nextImgv = findViewById(R.id.nextImgv);
        permissionModule = new PermissionModule(MainShareActivity.this);
        ShareItems.setInstance(null);
        ShareItems.getInstance();
    }

    private void setShareItemUser() {
        User user = new User();
        user.setUsername(AccountHolderInfo.getInstance().getUser().getUserInfo().getUsername());
        user.setUserid(AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid());
        user.setProfilePhotoUrl(AccountHolderInfo.getInstance().getUser().getUserInfo().getProfilePhotoUrl());
        ShareItems.getInstance().getPost().setUser(user);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.Orange, null), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.White, null), PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == permissionModule.getWriteExternalStoragePermissionCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpPager();
            } else
                setUpPager();
        }
    }

    private void addListeners() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabsCreated)
                    tab.getIcon().setColorFilter(getResources().getColor(R.color.Orange, null), PorterDuff.Mode.SRC_IN);

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
                    tab.getIcon().setColorFilter(getResources().getColor(R.color.White, null), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        nextImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckShareItems checkShareItems = new CheckShareItems(MainShareActivity.this);
                if (checkShareItems.shareIsPossible()) {
                    setShareItemUser();
                    VideoFileListForDelete.getInstance().deleteAllFile();
                    startActivity(new Intent(MainShareActivity.this, ShareDetailActivity.class));
                } else
                    DialogBoxUtil.showInfoDialogBox(MainShareActivity.this, checkShareItems.getErrMessage(), null, new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {

                        }
                    });
            }
        });

        cancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void checkWriteStoragePermission() {
        if (!permissionModule.checkWriteExternalStoragePermission())
            ActivityCompat.requestPermissions(MainShareActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    permissionModule.getWriteExternalStoragePermissionCode());
        else
            setUpPager();
    }

    private void setUpPager() {
        SpecialSelectTabAdapter adapter = new SpecialSelectTabAdapter(this.getSupportFragmentManager());

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (tabSelectedPosition == TAB_PHOTO) {
            if (galleryPickerFrag.gridListAdapter != null) {
                galleryPickerFrag.gridListAdapter.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onBackPressed() {
        manageVisibleFragments();
        super.onBackPressed();
    }

    public void manageVisibleFragments(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null){
            if(fragments.size() > 0) {
                for (Fragment fragment : fragments) {
                    if (fragment instanceof TextEditFragment) {
                        galleryPickerFrag.textFragBackPressed();
                    }
                }
            }
        }
    }
}