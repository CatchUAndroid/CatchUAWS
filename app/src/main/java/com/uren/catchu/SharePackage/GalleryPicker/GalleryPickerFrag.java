package com.uren.catchu.SharePackage.GalleryPicker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;

import java.io.File;
import java.util.ArrayList;

import butterknife.ButterKnife;

import static com.uren.catchu.Constants.NumericConstants.CODE_CAMERA_POSITION;
import static com.uren.catchu.Constants.NumericConstants.CODE_GALLERY_POSITION;


@SuppressLint("ValidFragment")
public class GalleryPickerFrag extends Fragment {

    RecyclerView specialRecyclerView;
    RelativeLayout photoRelLayout;
    ImageView imageView;
    ImageView cancelImageView;

    private View mView;
    private ArrayList<File> mFiles;

    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;

    private static final String EXTENSION_JPG = ".jpg";
    private static final String EXTENSION_JPEG = ".jpeg";
    private static final String EXTENSION_PNG = ".png";

    private static final int MARGING_GRID = 2;
    private static final int maxImageCount = 30;
    private static final long maxFileByte = 1700000;
    private static final int spanCount = 4;

    public GalleryGridListAdapter gridListAdapter;
    PermissionModule permissionModule;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_special_select, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        specialRecyclerView = (RecyclerView) mView.findViewById(R.id.specialRecyclerView);
        photoRelLayout = mView.findViewById(R.id.photoRelLayout);
        imageView = mView.findViewById(R.id.imageView);
        cancelImageView = mView.findViewById(R.id.cancelImageView);
        permissionModule = new PermissionModule(getActivity());
        getData();
    }

    public void getData() {

        fetchMedia();
        gridListAdapter = new GalleryGridListAdapter(getActivity(), mFiles, GalleryPickerFrag.this);
        specialRecyclerView.setAdapter(gridListAdapter);
        gridLayoutManager = new GridLayoutManager(getActivity(), spanCount);
        specialRecyclerView.addItemDecoration(addItemDecoration());
        specialRecyclerView.setLayoutManager(gridLayoutManager);

        // TODO: 6.09.2018 - Recycler view da resimler dikdortgen aciliyor. xml de kullanilan ConstraintLayout cozum olabilir.
    }

    public void initImageView() {
        imageView = mView.findViewById(R.id.imageView);
    }

    private RecyclerView.ItemDecoration addItemDecoration() {
        return new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view,
                                       RecyclerView parent, RecyclerView.State state) {
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
        if (requestCode == permissionModule.getWriteExternalStoragePermissionCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (gridListAdapter.selectedPosition == CODE_GALLERY_POSITION)
                    gridListAdapter.startGalleryProcess();
                else if (gridListAdapter.selectedPosition == CODE_CAMERA_POSITION)
                    gridListAdapter.startCameraProcess();
                else
                    gridListAdapter.startGalleryProcess();
            }
        } else if (requestCode == permissionModule.getCameraPermissionCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                gridListAdapter.startCameraProcess();
            }
        } else
            CommonUtils.showToast(getActivity(), getActivity().getString(R.string.technicalError) + requestCode);
    }
}
