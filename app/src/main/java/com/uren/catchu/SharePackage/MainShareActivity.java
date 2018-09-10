package com.uren.catchu.SharePackage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.uren.catchu.Adapters.SpecialSelectTabAdapter;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.GalleryPicker.GalleryPickerFrag;
import com.uren.catchu.SharePackage.TextPicker.TextPickerFrag;
import com.uren.catchu.SharePackage.Utils.CheckShareItems;
import com.uren.catchu.SharePackage.VideoPicker.VideoPickerFrag;
import com.uren.catchu.Singleton.ShareItems;

public class MainShareActivity extends FragmentActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    AppBarLayout appBarLayout;
    FloatingActionButton nextButton;
    PermissionModule permissionModule;

    GalleryPickerFrag galleryPickerFrag;
    TextPickerFrag textPickerFrag;
    VideoPickerFrag videoPickerFrag;

    //Tab constants
    private static final int TAB_TEXT = 0;
    private static final int TAB_PHOTO = 1;
    private static final int TAB_VIDEO = 2;

    private int tabSelectedPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_share);

        permissionModule = new PermissionModule(MainShareActivity.this);

        initVariables();
        addListeners();
        checkWriteStoragePermission();
    }

    private void initVariables() {
        tabLayout = findViewById(R.id.htab_tabs);
        viewPager = findViewById(R.id.htab_viewpager);
        appBarLayout = findViewById(R.id.htab_appbar);
        nextButton = findViewById(R.id.nextButton);
        ShareItems.setInstance(null);
        ShareItems.getInstance();
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

                tabSelectedPosition = tab.getPosition();

                switch (tab.getPosition()) {
                    case TAB_VIDEO:
                        hideKeyBoard();
                        //videoPickerFrag.openCamera();
                        break;
                    case TAB_PHOTO:
                        hideKeyBoard();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckShareItems checkShareItems = new CheckShareItems(MainShareActivity.this);
                if (checkShareItems.shareIsPossible()) {
                    startActivity(new Intent(MainShareActivity.this, ShareDetailActivity.class));
                } else
                    CommonUtils.showToast(MainShareActivity.this, checkShareItems.getErrMessage());
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

        adapter.addFragment(textPickerFrag, getResources().getString(R.string.text));
        adapter.addFragment(galleryPickerFrag, getResources().getString(R.string.photo));
        adapter.addFragment(videoPickerFrag, getResources().getString(R.string.video));

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
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
