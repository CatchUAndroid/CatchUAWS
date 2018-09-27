package com.uren.catchu.SharePackage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uren.catchu.Adapters.SpecialSelectTabAdapter;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.GalleryPicker.GalleryPickerFrag;
import com.uren.catchu.SharePackage.TextPicker.TextPickerFrag;
import com.uren.catchu.SharePackage.Utils.CheckShareItems;
import com.uren.catchu.SharePackage.VideoPicker.Utils.VideoFileListForDelete;
import com.uren.catchu.SharePackage.VideoPicker.fragment.VideoPickerFrag;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.Share.ShareItems;

import catchu.model.User;

public class MainShareActivity extends FragmentActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    AppBarLayout appBarLayout;
    PermissionModule permissionModule;
    TextView cancelTv;
    TextView nextTv;
    public static LinearLayout mainShareMainLayout;

    GalleryPickerFrag galleryPickerFrag;
    TextPickerFrag textPickerFrag;
    VideoPickerFrag videoPickerFrag;

    //Tab constants
    private static final int TAB_TEXT = 0;
    private static final int TAB_PHOTO = 1;
    private static final int TAB_VIDEO = 2;

    private int tabSelectedPosition = 0;
    boolean tabsCreated = false;

    private int[] tabIcons = {
            R.drawable.tab_text,
            R.drawable.tab_gallery,
            R.drawable.tab_video
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_share);

        permissionModule = new PermissionModule(MainShareActivity.this);

        initVariables();
        addListeners();
        checkWriteStoragePermission();
        hideKeyBoard();
    }

    private void initVariables() {
        tabLayout = findViewById(R.id.htab_tabs);
        viewPager = findViewById(R.id.htab_viewpager);
        appBarLayout = findViewById(R.id.htab_appbar);
        cancelTv = findViewById(R.id.cancelTv);
        nextTv = findViewById(R.id.nextTv);
        mainShareMainLayout = findViewById(R.id.mainShareMainLayout);
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
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
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
                    tab.getIcon().setColorFilter(getResources().getColor(R.color.white, null), PorterDuff.Mode.SRC_IN);

                tabSelectedPosition = tab.getPosition();

                switch (tab.getPosition()) {
                    case TAB_VIDEO:
                        hideKeyBoard();
                        videoPickerFrag.setFlashModeOff();
                        break;
                    case TAB_PHOTO:
                        hideKeyBoard();
                        videoPickerFrag.setFlashModeOff();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tabsCreated)
                    tab.getIcon().setColorFilter(getResources().getColor(R.color.black, null), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        nextTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckShareItems checkShareItems = new CheckShareItems(MainShareActivity.this);
                if (checkShareItems.shareIsPossible()) {
                    setShareItemUser();
                    VideoFileListForDelete.getInstance().deleteAllFile();
                    startActivity(new Intent(MainShareActivity.this, ShareDetailActivity.class));
                } else
                    CommonUtils.showToast(MainShareActivity.this, checkShareItems.getErrMessage());
            }
        });

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void hideKeyBoard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
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
        textPickerFrag = new TextPickerFrag();
        videoPickerFrag = new VideoPickerFrag();

        adapter.addFragment(textPickerFrag,"" );
        adapter.addFragment(galleryPickerFrag,"");
        adapter.addFragment(videoPickerFrag,"");

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
}