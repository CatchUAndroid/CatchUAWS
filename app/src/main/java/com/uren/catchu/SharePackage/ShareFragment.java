package com.uren.catchu.SharePackage;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.uren.catchu.Adapters.SpecialSelectTabAdapter;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.GalleryPicker.GalleryPickerFrag;
import com.uren.catchu.SharePackage.VideoPicker.Utils.VideoFileListForDelete;
import com.uren.catchu.SharePackage.VideoPicker.fragment.VideoPickerFrag;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.Share.ShareItems;

import butterknife.ButterKnife;
import catchu.model.User;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;

public class ShareFragment extends BaseFragment {

    View view;
    ViewPager viewPager;
    TabLayout tabLayout;
    ImageView cancelImgv;
    ImageView nextImgv;

    SpecialSelectTabAdapter adapter;

    GalleryPickerFrag galleryPickerFrag;
    VideoPickerFrag videoPickerFrag;

    PermissionModule permissionModule;
    boolean tabsCreated = false;

    //Tab constants
    private static final int TAB_PHOTO = 0;
    private static final int TAB_VIDEO = 1;

    private int tabSelectedPosition = 0;
    private int[] tabIcons = {
            R.drawable.tab_gallery,
            R.drawable.tab_video
    };

    public ShareFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        NextActivity.bottomTabLayout.setVisibility(View.GONE);
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        if(view == null) {
            view = inflater.inflate(R.layout.fragment_share, container, false);
            ButterKnife.bind(this, view);
            initializeItems();
            addListeners();
            setupViewPager();
            setShareItemUser();
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initializeItems() {
        viewPager = view.findViewById(R.id.viewpager);
        tabLayout = view.findViewById(R.id.htab_tabs);
        cancelImgv = view.findViewById(R.id.cancelImgv);
        nextImgv = view.findViewById(R.id.nextImgv);
        permissionModule = new PermissionModule(getContext());
        ShareItems.setInstance(null);
        VideoFileListForDelete.setInstance(null);
    }

    private void setShareItemUser() {
        User user = new User();
        user.setUsername(AccountHolderInfo.getInstance().getUser().getUserInfo().getUsername());
        user.setUserid(AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid());
        user.setProfilePhotoUrl(AccountHolderInfo.getInstance().getUser().getUserInfo().getProfilePhotoUrl());
        ShareItems.getInstance().getPost().setUser(user);
    }

    private void setupViewPager() {
        galleryPickerFrag = new GalleryPickerFrag();
        videoPickerFrag = new VideoPickerFrag();

        adapter = new SpecialSelectTabAdapter(getChildFragmentManager());
        adapter.addFragment(galleryPickerFrag, "");
        adapter.addFragment(videoPickerFrag, "");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        tabsCreated = true;
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(0).getIcon().setColorFilter(getActivity().getResources().getColor(R.color.Orange, null), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(getActivity().getResources().getColor(R.color.White, null), PorterDuff.Mode.SRC_IN);
    }

    private void addListeners() {

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab != null) {
                    if (tabsCreated && getContext() != null && tab.getIcon() != null)
                        tab.getIcon().setColorFilter(getContext().getResources().getColor(R.color.Orange, null), PorterDuff.Mode.SRC_IN);

                    tabSelectedPosition = tab.getPosition();

                    switch (tab.getPosition()) {
                        case TAB_VIDEO:
                            if (videoPickerFrag != null)
                                videoPickerFrag.setFlashModeOff();
                            break;
                        case TAB_PHOTO:
                            if (videoPickerFrag != null)
                                videoPickerFrag.setFlashModeOff();
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tabsCreated && getContext() != null && tab.getIcon() != null)
                    tab.getIcon().setColorFilter(getContext().getResources().getColor(R.color.White, null), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        nextImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShareItemUser();
                VideoFileListForDelete.getInstance().deleteAllFile();
                /*galleryPickerFrag.checkTextIsAddedOrNot();*/

                if (mFragmentNavigation != null) {
                    mFragmentNavigation.pushFragment(new ShareDetailFragment(new ReturnCallback() {
                        @Override
                        public void onReturn(Object object) {
                            galleryPickerFrag.updateAfterShare();
                            getActivity().onBackPressed();
                        }
                    }), ANIMATE_RIGHT_TO_LEFT);
                }
            }
        });

        cancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }
}