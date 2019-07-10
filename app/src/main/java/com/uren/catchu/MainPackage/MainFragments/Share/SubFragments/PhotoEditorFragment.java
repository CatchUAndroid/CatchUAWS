package com.uren.catchu.MainPackage.MainFragments.Share.SubFragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.GeneralUtils.ProgressDialogUtil.ProgressDialogUtil;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Share.filters.FilterListener;
import com.uren.catchu.MainPackage.MainFragments.Share.filters.FilterViewAdapter;
import com.uren.catchu.MainPackage.MainFragments.Share.tools.EditingToolsAdapter;
import com.uren.catchu.MainPackage.MainFragments.Share.tools.ToolType;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;

import butterknife.ButterKnife;
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.ViewType;

@SuppressLint("ValidFragment")
public class PhotoEditorFragment extends BaseFragment implements OnPhotoEditorListener,
        View.OnClickListener,
        PropertiesBSFragment.Properties,
        EmojiBSFragment.EmojiListener, EditingToolsAdapter.OnItemSelected, FilterListener {

    View mView;
    PhotoSelectUtil thisPhotoSelectUtil;
    ReturnCallback returnCallback;

    private PhotoEditor mPhotoEditor;
    private PhotoEditorView mPhotoEditorView;
    private PropertiesBSFragment mPropertiesBSFragment;
    private EmojiBSFragment mEmojiBSFragment;
    private TextView mTxtCurrentTool;
    private RecyclerView mRvTools, mRvFilters;
    private FilterViewAdapter mFilterViewAdapter = new FilterViewAdapter(this);
    private ConstraintLayout mRootView;
    private ConstraintSet mConstraintSet = new ConstraintSet();
    private boolean mIsFilterVisible;
    private PermissionModule permissionModule;
    ProgressDialogUtil progressDialogUtil;

    public PhotoEditorFragment(PhotoSelectUtil photoSelectUtil, ReturnCallback returnCallback) {
        this.thisPhotoSelectUtil = photoSelectUtil;
        this.returnCallback = returnCallback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_photo_editor, container, false);
            ButterKnife.bind(this, mView);
            initViews();
            initVariables();
            setSelectedPhoto();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initViews() {
        ImageView imgUndo;
        ImageView imgRedo;
        ImageView imgSave;
        ImageView imgClose;

        mPhotoEditorView = mView.findViewById(R.id.photoEditorView);
        mTxtCurrentTool = mView.findViewById(R.id.txtCurrentTool);
        mRvTools = mView.findViewById(R.id.rvConstraintTools);
        mRvFilters = mView.findViewById(R.id.rvFilterView);
        mRootView = mView.findViewById(R.id.rootView);

        imgUndo = mView.findViewById(R.id.imgUndo);
        imgUndo.setOnClickListener(this);

        imgRedo = mView.findViewById(R.id.imgRedo);
        imgRedo.setOnClickListener(this);

        imgSave = mView.findViewById(R.id.imgSave);
        imgSave.setOnClickListener(this);

        imgClose = mView.findViewById(R.id.imgClose);
        imgClose.setOnClickListener(this);
    }

    public void initVariables() {
        permissionModule = new PermissionModule(getContext());
        EditingToolsAdapter mEditingToolsAdapter = new EditingToolsAdapter(this, getContext());

        mPropertiesBSFragment = new PropertiesBSFragment();
        mEmojiBSFragment = new EmojiBSFragment();
        mEmojiBSFragment.setEmojiListener(this);
        mPropertiesBSFragment.setPropertiesChangeListener(this);

        LinearLayoutManager llmTools = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRvTools.setLayoutManager(llmTools);
        mRvTools.setAdapter(mEditingToolsAdapter);

        LinearLayoutManager llmFilters = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRvFilters.setLayoutManager(llmFilters);
        mRvFilters.setAdapter(mFilterViewAdapter);

        mPhotoEditor = new PhotoEditor.Builder(getContext(), mPhotoEditorView)
                .setPinchTextScalable(true) // set flag to make text scalable when pinch
                .build(); // build photo editor sdk

        mPhotoEditor.setOnPhotoEditorListener(this);
        progressDialogUtil = new ProgressDialogUtil(getContext(), null, false);
    }

    public void setSelectedPhoto() {
        if (thisPhotoSelectUtil != null) {
            if (thisPhotoSelectUtil.getScreeanShotBitmap() != null)
                mPhotoEditorView.getSource().setImageBitmap(thisPhotoSelectUtil.getScreeanShotBitmap());

            else if (thisPhotoSelectUtil.getBitmap() != null)
                mPhotoEditorView.getSource().setImageBitmap(thisPhotoSelectUtil.getBitmap());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.imgUndo:
                mPhotoEditor.undo();
                break;

            case R.id.imgRedo:
                mPhotoEditor.redo();
                break;

            case R.id.imgSave:
                checkPermission();
                break;

            case R.id.imgClose:
                removeFilter();
                break;
        }
    }

    public void removeFilter() {
        if (mIsFilterVisible) {
            showFilter(false);
            mTxtCurrentTool.setText(R.string.app_name);
        } else if (!mPhotoEditor.isCacheEmpty()) {
            showSaveDialog();
        }
    }

    private void checkPermission() {

        if (permissionModule.checkWriteExternalStoragePermission()) {
            saveImage();
        } else
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PermissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE);
    }

    @SuppressLint("MissingPermission")
    private void saveImage() {
        progressDialogUtil.dialogShow();

        try {
            mPhotoEditor.saveAsBitmap(new OnSaveBitmap() {
                @Override
                public void onBitmapReady(Bitmap saveBitmap) {
                    progressDialogUtil.dialogDismiss();
                    CommonUtils.snackbarShow(mRootView, getContext(), getResources().getString(R.string.image_saved_successfully), R.color.DodgerBlue);
                    thisPhotoSelectUtil.setScreeanShotBitmap(saveBitmap);
                    returnCallback.onReturn(thisPhotoSelectUtil);
                    getActivity().onBackPressed();
                }

                @Override
                public void onFailure(Exception e) {
                    progressDialogUtil.dialogDismiss();
                    CommonUtils.snackbarShow(mRootView, getContext(), getResources().getString(R.string.failed_to_save_image), R.color.red);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            progressDialogUtil.dialogDismiss();
            CommonUtils.snackbarShow(mRootView, getContext(), e.getMessage(), R.color.red);
        }
    }

    @Override
    public void onEmojiClick(String emojiUnicode) {
        mPhotoEditor.addEmoji(emojiUnicode);
        mTxtCurrentTool.setText(R.string.label_emoji);
    }

    @Override
    public void onColorChanged(int colorCode) {
        mPhotoEditor.setBrushColor(colorCode);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onOpacityChanged(int opacity) {
        mPhotoEditor.setOpacity(opacity);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        mPhotoEditor.setBrushSize(brushSize);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PermissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImage();
            }
        }
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getResources().getString(R.string.sure_exit_not_saving_image));
        builder.setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkPermission();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton(getResources().getString(R.string.discard), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().onBackPressed();
            }
        });
        builder.create().show();

    }

    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {
        mPhotoEditor.setFilterEffect(photoFilter);
    }

    @Override
    public void onToolSelected(ToolType toolType) {
        switch (toolType) {
            case BRUSH:
                mPhotoEditor.setBrushDrawingMode(true);
                mTxtCurrentTool.setText(R.string.label_brush);
                mPropertiesBSFragment.show(getActivity().getSupportFragmentManager(), mPropertiesBSFragment.getTag());
                break;
            case TEXT:
                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(getActivity());
                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                    @Override
                    public void onDone(String inputText, int colorCode) {
                        mPhotoEditor.addText(inputText, colorCode);
                        mTxtCurrentTool.setText(R.string.label_text);
                    }
                });
                break;
            case ERASER:
                mPhotoEditor.brushEraser();
                mTxtCurrentTool.setText(R.string.label_eraser);
                break;
            case FILTER:
                mTxtCurrentTool.setText(R.string.label_filter);
                showFilter(true);
                break;
            case EMOJI:
                mEmojiBSFragment.show(getActivity().getSupportFragmentManager(), mEmojiBSFragment.getTag());
                break;
        }
    }

    void showFilter(boolean isVisible) {
        mIsFilterVisible = isVisible;
        mConstraintSet.clone(mRootView);

        if (isVisible) {
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);

        mConstraintSet.applyTo(mRootView);
    }

    @Override
    public void onEditTextChangeListener(final View rootView, String text, int colorCode) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(getActivity(), text, colorCode);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode) {
                mPhotoEditor.editText(rootView, inputText, colorCode);
                mTxtCurrentTool.setText(R.string.label_text);
            }
        });
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {

    }

    @Override
    public void onRemoveViewListener(int numberOfAddedViews) {

    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {

    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {

    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {

    }
}
