package com.uren.catchu.SharePackage.GalleryPicker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
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
import com.uren.catchu.GeneralUtils.BaseBackPressedListener;
import com.uren.catchu.GeneralUtils.BitmapConversion;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.Interfaces.OnBackPressedListener;
import com.uren.catchu.MainPackage.Interfaces.IOnBackPressed;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.GalleryPicker.Adapters.ColorPaletteAdapter;
import com.uren.catchu.SharePackage.GalleryPicker.Interfaces.PhotoSelectCallback;
import com.uren.catchu.SharePackage.GalleryPicker.Interfaces.TextCompleteCallback;
import com.uren.catchu.SharePackage.GalleryPicker.Interfaces.TrashDragDropCallback;
import com.uren.catchu.SharePackage.MainShareActivity;
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
public class GalleryPickerFrag extends Fragment{
    RecyclerView specialRecyclerView;
    RelativeLayout photoRelLayout;
    RelativeLayout addPropRelLayout;
    RelativeLayout photoMainLayout;
    ImageView selectImageView;
    ImageView cancelImageView;
    ImageView addTextImgv;
    TextView textView;
    SeekBar seekbar;

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
        specialRecyclerView = (RecyclerView) mView.findViewById(R.id.specialRecyclerView);
        photoRelLayout = mView.findViewById(R.id.photoRelLayout);
        selectImageView = mView.findViewById(R.id.selectImageView);
        permissionModule = new PermissionModule(getActivity());
        cancelImageView = mView.findViewById(R.id.cancelImageView);
        addTextImgv = mView.findViewById(R.id.addTextImgv);
        addPropRelLayout = mView.findViewById(R.id.addPropRelLayout);
        photoMainLayout = mView.findViewById(R.id.photoMainLayout);
        textView = mView.findViewById(R.id.textView);
        seekbar = mView.findViewById(R.id.seekbar);
        getData();
        addListeners();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addListeners() {
        addTextImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaultTextView();
                startTextEditFragment();
            }
        });

        cancelImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareItems.getInstance().clearImageShareItemBox();
                photoMainLayout.setVisibility(View.GONE);
                specialRecyclerView.setVisibility(View.VISIBLE);
                setDefaultTextView();
            }
        });

        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(data, shadowBuilder, v, 0);
                if (addPropRelLayout.getVisibility() == View.VISIBLE) {
                    addPropRelLayout.setVisibility(View.GONE);
                    trashDragProcess();
                }
                return false;
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTextEditFragment();
            }
        });

        photoRelLayout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                final TextView draggedView = (TextView) event.getLocalState();

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
                        draggedView.setX((float) x - (draggedView.getWidth() / 2));
                        draggedView.setY((float) y - (draggedView.getHeight() / 2));
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        return true;
                    default:
                        return true;
                }
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (textView != null)
                    textView.setTextSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    private void setDefaultTextView() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        textView.setTextColor(getActivity().getResources().getColor(R.color.White, null));
        textView.setText("");
        textView.setVisibility(View.GONE);
        textView.setLayoutParams(layoutParams);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15.0f);
    }

    private void startTextEditFragment() {
        cancelImageView.setVisibility(View.GONE);
        addTextImgv.setVisibility(View.GONE);
        seekbar.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);

        TextEditFragment nextFrag = new TextEditFragment(textView, new TextCompleteCallback() {
            @Override
            public void textCompleted(View view) {
                cancelImageView.setVisibility(View.VISIBLE);
                addTextImgv.setVisibility(View.VISIBLE);
                EditText returnEditText = (EditText) view;
                if (returnEditText != null) {
                    if (!returnEditText.getText().toString().trim().isEmpty()) {
                        textView.setTextColor(returnEditText.getCurrentTextColor());
                        textView.setText(returnEditText.getText().toString());
                        textView.setVisibility(View.VISIBLE);
                        seekbar.setVisibility(View.VISIBLE);
                    } else
                        seekbar.setVisibility(View.GONE);
                }
            }
        });
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.addPropRelLayout, nextFrag, TextEditFragment.class.getName())
                .addToBackStack(TextEditFragment.class.getName())
                .commit();
    }

    public void closeTrashLayout() {
        if (trashLayout != null) {
            addPropRelLayout.setVisibility(View.VISIBLE);
            photoRelLayout.removeView(trashLayout);
            trashLayout = null;
        }
    }

    private void trashDragProcess() {
        addPropRelLayout.setVisibility(View.GONE);
        trashLayout = getLayoutInflater().inflate(R.layout.text_drag_layout, addPropRelLayout, false);
        ImageView trashImgv = trashLayout.findViewById(R.id.trashImgv);
        photoRelLayout.addView(trashLayout);
        trashImgv.setOnDragListener(new TrashDragListener(new TrashDragDropCallback() {
            @Override
            public void onDropped() {
                addPropRelLayout.setVisibility(View.VISIBLE);
                if (trashLayout != null) {
                    photoRelLayout.removeView(trashLayout);
                    setDefaultTextView();
                    seekbar.setVisibility(View.GONE);
                    trashLayout = null;
                }
            }
        }));
    }

    public void getData() {
        fetchMedia();
        gridListAdapter = new GalleryGridListAdapter(getActivity(), mFiles, GalleryPickerFrag.this, new PhotoSelectCallback() {
            @Override
            public void onSelect(PhotoSelectUtil photoSelectUtil) {
                photoUtil = photoSelectUtil;
                resetPhotoImageView(photoUtil.isPortraitMode());
                Glide.with(getActivity()).load(photoUtil.getMediaUri()).into(selectImageView);
                photoMainLayout.setVisibility(View.VISIBLE);
                specialRecyclerView.setVisibility(View.GONE);
                seekbar.setVisibility(View.GONE);
            }
        });

        specialRecyclerView.setAdapter(gridListAdapter);
        gridLayoutManager = new GridLayoutManager(getActivity(), spanCount);
        specialRecyclerView.addItemDecoration(addItemDecoration());
        specialRecyclerView.setLayoutManager(gridLayoutManager);
    }

    public void resetPhotoImageView(boolean portraitMode) {
        if (portraitMode)
            selectImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        else
            selectImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
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

                if (gridListAdapter.selectedPosition == CODE_GALLERY_POSITION)
                    gridListAdapter.startGalleryProcess();
                else if (gridListAdapter.selectedPosition == CODE_CAMERA_POSITION)
                    gridListAdapter.startCameraProcess();
                else
                    gridListAdapter.startGalleryProcess();
            }
        } else if (requestCode == permissionModule.PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                gridListAdapter.startCameraProcess();
            }
        } else
            CommonUtils.showToast(getActivity(), getActivity().getString(R.string.technicalError) + requestCode);
    }

    public void textFragBackPressed(){
        cancelImageView.setVisibility(View.VISIBLE);
        addTextImgv.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
    }

    public void checkTextIsAddedOrNot(){
        if(textView != null && !textView.getText().toString().trim().isEmpty()){
            Bitmap bitmap = BitmapConversion.getScreenShot(photoRelLayout);
            photoUtil.setScreeanShotBitmap(bitmap);
        }

    }
}
