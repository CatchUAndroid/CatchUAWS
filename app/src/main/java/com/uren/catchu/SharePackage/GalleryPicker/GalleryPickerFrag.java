package com.uren.catchu.SharePackage.GalleryPicker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.PhotoChosenCallback;
import com.uren.catchu.GeneralUtils.IntentUtil.IntentSelectUtil;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.GalleryPicker.Interfaces.PhotoSelectCallback;
import com.uren.catchu.SharePackage.Models.ImageShareItemBox;
import com.uren.catchu.Singleton.Share.ShareItems;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.uren.catchu.Constants.NumericConstants.CODE_CAMERA_POSITION;
import static com.uren.catchu.Constants.NumericConstants.CODE_GALLERY_POSITION;
import static com.uren.catchu.Constants.StringConstants.CAMERA_TEXT;
import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;

public class GalleryPickerFrag extends BaseFragment {
    @BindView(R.id.specialRecyclerView)
    RecyclerView specialRecyclerView;
    @BindView(R.id.photoMainLayout)
    RelativeLayout photoMainLayout;
    @BindView(R.id.selectImageView)
    ImageView selectImageView;
    @BindView(R.id.cancelImageView)
    ImageView cancelImageView;

    View mView;
    ArrayList<File> mFiles;
    GridLayoutManager gridLayoutManager;
    PhotoSelectUtil photoUtil;

    private static final String EXTENSION_JPG = ".jpg";
    private static final String EXTENSION_JPEG = ".jpeg";
    private static final String EXTENSION_PNG = ".png";

    private static final int MARGING_GRID = 2;
    private static final int maxImageCount = 46;
    private static final long maxFileByte = 2500000;
    private static final int spanCount = 4;

    public GalleryGridListAdapter gridListAdapter;
    PermissionModule permissionModule;

    public GalleryPickerFrag() {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initLayoutView(inflater, container);
        return mView;
    }

    public void initLayoutView(LayoutInflater inflater, ViewGroup container){
        if (mView == null) {
            mView = inflater.inflate(R.layout.gallery_picker_layout, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            getData();
            addListeners();
            setShapes();
        }
    }

    public void setShapes(){
        cancelImageView.setBackground(ShapeUtil.getShape(getActivity().getResources().getColor(R.color.transparentBlack, null),
                0, GradientDrawable.OVAL, 50, 0));
    }

    public void initVariables() {
        permissionModule = new PermissionModule(getActivity());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void addListeners() {

        cancelImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelImageView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                ShareItems.getInstance().clearImageShareItemBox();
                photoMainLayout.setVisibility(View.GONE);
                specialRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    public void getData() {
        if (!permissionModule.checkWriteExternalStoragePermission())
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    permissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE);
        else
            setGridListAdapter();
    }

    public void setGridListAdapter() {
        fetchMedia();
        if (getContext() != null) {
            gridListAdapter = new GalleryGridListAdapter(getContext(), mFiles, new PhotoSelectCallback() {
                @Override
                public void onSelect(PhotoSelectUtil photoSelectUtil) {
                    photoUtil = photoSelectUtil;
                    startPhotoSelectedFragment();
                }
            }, new PhotoChosenCallback() {
                @Override
                public void onGallerySelected() {
                    checkGalleryProcess();
                }

                @Override
                public void onCameraSelected() {
                    checkCameraProcess();
                }

                @Override
                public void onPhotoRemoved() {

                }
            });

            specialRecyclerView.setAdapter(gridListAdapter);
            gridLayoutManager = new GridLayoutManager(getContext(), spanCount);
            specialRecyclerView.addItemDecoration(addItemDecoration());
            specialRecyclerView.setLayoutManager(gridLayoutManager);
        }
    }

    public void startPhotoSelectedFragment(){
        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new PhotoSelectedFragment(photoUtil, new ReturnCallback() {
                @Override
                public void onReturn(Object object) {
                    photoUtil = (PhotoSelectUtil) object;
                    photoSelected();
                    fillImageShareItemBox();
                }
            }));
        }
    }

    public void photoSelected() {
        CommonUtils.setImageScaleType(photoUtil, selectImageView);

        if (photoUtil.getScreeanShotBitmap() != null) {
            Glide.with(getContext()).load(photoUtil.getScreeanShotBitmap()).into(selectImageView);
        } else
            Glide.with(getContext()).load(photoUtil.getMediaUri()).into(selectImageView);

        photoMainLayout.setVisibility(View.VISIBLE);
        specialRecyclerView.setVisibility(View.GONE);
    }

    private void checkGalleryProcess() {
        if (permissionModule.checkWriteExternalStoragePermission())
            startActivityForResult(Intent.createChooser(IntentSelectUtil.getGalleryIntent(),
                    getContext().getResources().getString(R.string.selectPicture)), permissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE);
        else
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    permissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE);
    }

    private void checkCameraProcess() {
        if (!CommonUtils.checkCameraHardware(getContext())) {
            CommonUtils.showToast(getContext(), getContext().getResources().getString(R.string.deviceHasNoCamera));
            return;
        }

        if (permissionModule.checkCameraPermission())
            startActivityForResult(IntentSelectUtil.getCameraIntent(), permissionModule.PERMISSION_CAMERA);
        else
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    permissionModule.PERMISSION_CAMERA);
    }

    private RecyclerView.ItemDecoration addItemDecoration() {
        return new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = MARGING_GRID;
                outRect.right = MARGING_GRID;
                outRect.bottom = MARGING_GRID;
                if (parent.getChildLayoutPosition(view) >= 0 && parent.getChildLayoutPosition(view) <= 3) {
                    outRect.top = MARGING_GRID;
                }
            }
        };
    }

    private void fetchMedia() {
        mFiles = new ArrayList<>();

        File dirDcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        parseDir(dirDcim);

        if (mFiles.size() < maxImageCount) {
            File dirDownloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            parseDir(dirDownloads);
        }

        if (mFiles.size() < maxImageCount) {
            File dirPictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            parseDir(dirPictures);
        }
    }

    private void parseDir(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            parseFileList(files);
        }
    }

    private void parseFileList(File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                if (!file.getName().toLowerCase().startsWith(".")) {
                    parseDir(file);
                }
            } else {
                if (file.getName().toLowerCase().endsWith(EXTENSION_JPG)
                        || file.getName().toLowerCase().endsWith(EXTENSION_JPEG)
                        || file.getName().toLowerCase().endsWith(EXTENSION_PNG)) {

                    if (mFiles.size() < maxImageCount) {
                        if (file.length() < maxFileByte)
                            mFiles.add(file);
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == permissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (gridListAdapter != null) {
                    if (gridListAdapter.selectedPosition == CODE_GALLERY_POSITION)
                        startActivityForResult(Intent.createChooser(IntentSelectUtil.getGalleryIntent(),
                                getContext().getResources().getString(R.string.selectPicture)), permissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE);
                    else if (gridListAdapter.selectedPosition == CODE_CAMERA_POSITION)
                        startActivityForResult(IntentSelectUtil.getCameraIntent(), permissionModule.PERMISSION_CAMERA);
                } else
                    setGridListAdapter();
            }
        } else if (requestCode == permissionModule.PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(IntentSelectUtil.getCameraIntent(), permissionModule.PERMISSION_CAMERA);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == permissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE) {
                photoUtil = new PhotoSelectUtil(getContext(), data, GALLERY_TEXT);
                startPhotoSelectedFragment();
            } else if (requestCode == permissionModule.PERMISSION_CAMERA) {
                photoUtil = new PhotoSelectUtil(getContext(), data, CAMERA_TEXT);
                startPhotoSelectedFragment();
            }
        }
    }

    public void fillImageShareItemBox() {
        ImageShareItemBox imageShareItemBox = new ImageShareItemBox(photoUtil);
        ShareItems.getInstance().clearImageShareItemBox();
        ShareItems.getInstance().addImageShareItemBox(imageShareItemBox);
    }

    public void updateAfterShare(){
        if(mView != null){
            photoMainLayout.setVisibility(View.GONE);
            specialRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
