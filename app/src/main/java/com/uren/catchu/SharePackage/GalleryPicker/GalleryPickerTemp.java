package com.uren.catchu.SharePackage.GalleryPicker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.GalleryPicker.Adapters.ColorPaletteAdapter;
import com.uren.catchu.SharePackage.GalleryPicker.Interfaces.PhotoSelectCallback;
import com.uren.catchu.SharePackage.GalleryPicker.Interfaces.TrashDragDropCallback;
import com.uren.catchu.SharePackage.Utils.ColorSelectCallback;
import com.uren.catchu.SharePackage.VideoPicker.fragment.VideoPickerFrag;
import com.uren.catchu.SharePackage.VideoPicker.fragment.VideoViewFragment;
import com.uren.catchu.Singleton.Share.ShareItems;

import java.io.File;
import java.util.ArrayList;

import butterknife.ButterKnife;

import static com.uren.catchu.Constants.NumericConstants.CODE_CAMERA_POSITION;
import static com.uren.catchu.Constants.NumericConstants.CODE_GALLERY_POSITION;


@SuppressLint("ValidFragment")
public class GalleryPickerTemp extends Fragment {

    RecyclerView specialRecyclerView;
    RelativeLayout photoRelLayout;
    RelativeLayout parentRelLayout;
    RelativeLayout photoSelectedLayout;
    ImageView selectImageView;
    ImageView cancelImageView;
    ImageView addTextImgv;
    ImageView brushImgv;
    ViewPager colorViewPager;
    LinearLayout dotsLayout;
    LinearLayout colorPaletteLayout;
    EditText editText;
    SeekBar seekBar;

    View mView;
    ArrayList<File> mFiles;
    GridLayoutManager gridLayoutManager;

    private static final String EXTENSION_JPG = ".jpg";
    private static final String EXTENSION_JPEG = ".jpeg";
    private static final String EXTENSION_PNG = ".png";

    private static final int MARGING_GRID = 2;
    private static final int maxImageCount = 46;
    private static final long maxFileByte = 2500000;
    private static final int spanCount = 4;

    public GalleryGridListAdapter gridListAdapter;
    PermissionModule permissionModule;
    ColorPaletteAdapter colorPaletteAdapter;
    private GestureDetector gestureDetector;
    View trashLayout = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.gallery_picker_layout, container, false);
            ButterKnife.bind(this, mView);
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        parentRelLayout = mView.findViewById(R.id.parentRelLayout);
        specialRecyclerView = (RecyclerView) mView.findViewById(R.id.specialRecyclerView);
        photoSelectedLayout = mView.findViewById(R.id.photoSelectedLayout);
        photoRelLayout = mView.findViewById(R.id.photoRelLayout);
        selectImageView = mView.findViewById(R.id.selectImageView);
        cancelImageView = mView.findViewById(R.id.cancelImageView);
        colorViewPager = mView.findViewById(R.id.colorViewPager);
        dotsLayout = mView.findViewById(R.id.layoutDots);
        colorPaletteLayout = mView.findViewById(R.id.colorPaletteLayout);
        addTextImgv = mView.findViewById(R.id.addTextImgv);
        brushImgv = mView.findViewById(R.id.brushImgv);
        seekBar = mView.findViewById(R.id.seekbar);
        permissionModule = new PermissionModule(getActivity());
        gestureDetector = new GestureDetector(getActivity(), new SingleTapConfirm());
        colorPalettePrepare();
        getData();
        addListeners();
    }

    public void colorPalettePrepare() {
        colorPaletteAdapter = new ColorPaletteAdapter(getActivity(), new ColorSelectCallback() {
            @Override
            public void onClick(int colorCode) {
                brushImgv.setColorFilter(ContextCompat.getColor(getActivity(), colorCode), android.graphics.PorterDuff.Mode.SRC_IN);
                editText.setTextColor(getActivity().getResources().getColor(colorCode, null));
            }
        });
        colorViewPager.setAdapter(colorPaletteAdapter);
        addBottomDots(0);
    }

    private void addListeners() {
        colorViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        addTextImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //colorPaletteLayout.setVisibility(View.VISIBLE);
                //photoRelLayout.removeView(editText);
                //seekBar.setVisibility(View.VISIBLE);
                //addEditText();

                startTextEditFragment("");



            }
        });

        cancelImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoRelLayout.setVisibility(View.GONE);
                specialRecyclerView.setVisibility(View.VISIBLE);
                colorPaletteLayout.setVisibility(View.GONE);
                ShareItems.getInstance().clearImageShareItemBox();
                photoRelLayout.removeView(editText);
                seekBar.setVisibility(View.GONE);
                hideKeyBoard();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (editText != null)
                    editText.setTextSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void startTextEditFragment(String text) {
        TextEditFragment nextFrag = new TextEditFragment(text);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.parentRelLayout, nextFrag, TextEditFragment.class.getName())
                .addToBackStack(null)
                .commit();
    }


    public void resetPhotoImageView(boolean portraitMode) {
        if (portraitMode)
            selectImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        else
            selectImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void addEditText() {
        editText = new EditText(getActivity());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        editText.setBackgroundColor(getActivity().getResources().getColor(R.color.transparent, null));
        editText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        editText.setTextColor(getActivity().getResources().getColor(R.color.White, null));
        editText.setLayoutParams(layoutParams);
        photoRelLayout.addView(editText);
        focusEditText();

        editText.setOnTouchListener(new View.OnTouchListener() {

            private int CLICK_ACTION_THRESHOLD = 200;
            private float startX;
            private float startY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();

                        ClipData data = ClipData.newPlainText("", "");
                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                        view.startDrag(data, shadowBuilder, view, 0);
                        if (photoSelectedLayout.getVisibility() == View.VISIBLE) {
                            photoSelectedLayout.setVisibility(View.GONE);
                            trashDragProcess();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        float endX = event.getX();
                        float endY = event.getY();
                        if (isAClick(startX, endX, startY, endY)) {
                            closeTrashLayout();
                            focusEditText();
                        }
                        break;
                }
                return false;
            }

            private boolean isAClick(float startX, float endX, float startY, float endY) {
                float differenceX = Math.abs(startX - endX);
                float differenceY = Math.abs(startY - endY);
                return !(differenceX > CLICK_ACTION_THRESHOLD/* =5 */ || differenceY > CLICK_ACTION_THRESHOLD);
            }
        });

        photoRelLayout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                final EditText draggedView = (EditText) event.getLocalState();

                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        return true;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        Log.i("Info", "photoRelLayout ACTION_DRAG_ENDED");
                        return true;
                    case DragEvent.ACTION_DROP:
                        Log.i("Info", "photoRelLayout ACTION_DROP");
                        closeTrashLayout();
                        draggedView.setX((float) x - (draggedView.getWidth()/2));
                        draggedView.setY((float) y - (draggedView.getHeight()/2));
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        return true;
                    default:
                        return true;
                }
            }
        });
    }

    public void closeTrashLayout(){
        if (trashLayout != null) {
            photoRelLayout.removeView(trashLayout);
            photoSelectedLayout.setVisibility(View.VISIBLE);
            trashLayout = null;
        }
    }

    private void trashDragProcess() {
        photoSelectedLayout.setVisibility(View.GONE);
        trashLayout = getLayoutInflater().inflate(R.layout.text_drag_layout, photoSelectedLayout, false);
        ImageView trashImgv = trashLayout.findViewById(R.id.trashImgv);
        photoRelLayout.addView(trashLayout);
        trashImgv.setOnDragListener(new TrashDragListener(new TrashDragDropCallback() {
            @Override
            public void onDropped() {
                photoRelLayout.removeView(editText);
                if (trashLayout != null) {
                    photoRelLayout.removeView(trashLayout);
                    photoSelectedLayout.setVisibility(View.VISIBLE);
                    colorPaletteLayout.setVisibility(View.GONE);
                    seekBar.setVisibility(View.GONE);
                    trashLayout = null;
                }
            }
        }));
    }

    public void hideKeyBoard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void focusEditText() {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    private void addBottomDots(int currentPage) {
        TextView[] dots;
        dots = new TextView[colorPaletteAdapter.getCount()];

        int cActive = getActivity().getResources().getColor(R.color.White, null);
        int cInactive = getActivity().getResources().getColor(R.color.Silver, null);

        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(getActivity());
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(cInactive);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(cActive);
    }

    public void getData() {
        fetchMedia();
        /*gridListAdapter = new GalleryGridListAdapter(getActivity(), mFiles, GalleryPickerTemp.this, new PhotoSelectCallback() {
            @Override
            public void onSelect(Uri uri, boolean portraitMode) {
                resetPhotoImageView(portraitMode);
                Glide.with(getActivity()).load(uri).into(selectImageView);
                specialRecyclerView.setVisibility(View.GONE);
                photoRelLayout.setVisibility(View.VISIBLE);
            }
        });*/

        specialRecyclerView.setAdapter(gridListAdapter);
        gridLayoutManager = new GridLayoutManager(getActivity(), spanCount);
        specialRecyclerView.addItemDecoration(addItemDecoration());
        specialRecyclerView.setLayoutManager(gridLayoutManager);
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

    private class SingleTapConfirm extends SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }
}
