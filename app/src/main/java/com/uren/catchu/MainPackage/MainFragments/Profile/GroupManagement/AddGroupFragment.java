package com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.uren.catchu.GeneralUtils.ApiModelsProcess.UserGroupsProcess;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.PhotoChosenCallback;
import com.uren.catchu.GeneralUtils.IntentUtil.IntentSelectUtil;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Adapters.FriendGridListAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.Utils.ProfileHelper;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.SelectedFriendList;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.uren.catchu.Constants.NumericConstants.GROUP_NAME_MAX_LENGTH;
import static com.uren.catchu.Constants.StringConstants.CAMERA_TEXT;
import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;

@SuppressLint("ValidFragment")
public class AddGroupFragment extends BaseFragment {

    View mView;

    @BindView(R.id.commonToolbarbackImgv)
    ClickableImageView commonToolbarbackImgv;
    @BindView(R.id.toolbarTitleTv)
    TextView toolbarTitleTv;

    @BindView(R.id.saveGroupInfoFab)
    FloatingActionButton saveGroupInfoFab;
    @BindView(R.id.groupNameEditText)
    EditText groupNameEditText;
    @BindView(R.id.groupPictureImgv)
    ImageView groupPictureImgv;
    @BindView(R.id.addGroupDtlRelLayout)
    RelativeLayout addGroupDtlRelLayout;
    @BindView(R.id.participantSize)
    TextView participantSize;
    @BindView(R.id.textSizeCntTv)
    TextView textSizeCntTv;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    PhotoSelectUtil photoSelectUtil;
    int groupNameSize = 0;
    GradientDrawable imageShape;
    boolean groupPhotoExist = false;

    FriendGridListAdapter adapter;
    PermissionModule permissionModule;

    CompleteCallback completeCallback;

    private static final int CODE_GALLERY_REQUEST = 665;
    private static final int CODE_CAMERA_REQUEST = 662;

    public AddGroupFragment(CompleteCallback completeCallback) {
        this.completeCallback = completeCallback;
    }

    @Override
    public void onStart() {
        Objects.requireNonNull(getActivity()).findViewById(R.id.tabMainLayout).setVisibility(View.GONE);
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_add_group, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        toolbarTitleTv.setText(getResources().getString(R.string.addGroupName));
        addListeners();
        setGroupTextSize();
        openPersonSelectionPage();
        permissionModule = new PermissionModule(getContext());
        imageShape = ShapeUtil.getShape(getResources().getColor(R.color.LightGrey, null),
                0, GradientDrawable.OVAL, 50, 0);
        groupPictureImgv.setBackground(imageShape);
        participantSize.setText(Integer.toString(SelectedFriendList.getInstance().getSize()));
    }

    public void addListeners() {

        commonToolbarbackImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        groupPictureImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChooseImageProc();
            }
        });

        saveGroupInfoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.hideKeyBoard(Objects.requireNonNull(getContext()));
                if (groupNameEditText.getText().toString().equals("") || groupNameEditText.getText() == null) {
                    CommonUtils.showToastShort(getContext(), getResources().getString(R.string.pleaseWriteGroupName));
                    return;
                }
                saveGroup();
            }
        });

        addGroupDtlRelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.hideKeyBoard(Objects.requireNonNull(getContext()));
            }
        });

        groupNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                groupNameSize = GROUP_NAME_MAX_LENGTH - s.toString().length();

                if (groupNameSize >= 0)
                    textSizeCntTv.setText(Integer.toString(groupNameSize));
                else
                    textSizeCntTv.setText(Integer.toString(0));
            }
        });
    }

    private void startChooseImageProc() {
        DialogBoxUtil.photoChosenDialogBox(getContext(), getResources().
                getString(R.string.chooseProfilePhoto), groupPhotoExist, new PhotoChosenCallback() {
            @Override
            public void onGallerySelected() {
                startGalleryProcess();
            }

            @Override
            public void onCameraSelected() {
                startCameraProcess();
            }

            @Override
            public void onPhotoRemoved() {
                setGroupPhoto(null);
            }
        });
    }

    private void startGalleryProcess() {
        if (permissionModule.checkWriteExternalStoragePermission())
            startActivityForResult(Intent.createChooser(IntentSelectUtil.getGalleryIntent(),
                    getResources().getString(R.string.selectPicture)), CODE_GALLERY_REQUEST);
        else
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PermissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE);
    }

    public void startCameraProcess() {

        if (!CommonUtils.checkCameraHardware(Objects.requireNonNull(getContext()))) {
            CommonUtils.showToastShort(getContext(), getResources().getString(R.string.deviceHasNoCamera));
            return;
        }

        if (!permissionModule.checkCameraPermission())
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PermissionModule.PERMISSION_CAMERA);
        else
            startActivityForResult(IntentSelectUtil.getCameraIntent(), CODE_CAMERA_REQUEST);
    }

    private void setGroupTextSize() {
        groupNameSize = GROUP_NAME_MAX_LENGTH;
        textSizeCntTv.setText(Integer.toString(GROUP_NAME_MAX_LENGTH));
    }

    private void openPersonSelectionPage() {
        adapter = new FriendGridListAdapter(getContext(), SelectedFriendList.getInstance().getSelectedFriendList(), new ReturnCallback() {
            @Override
            public void onReturn(Object object) {

                int itemCount = (int) object;

                if (itemCount == 0)
                    Objects.requireNonNull(getActivity()).onBackPressed();
                else
                    participantSize.setText(Integer.toString(itemCount));
            }
        });
        recyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    public void saveGroup() {
        UserGroupsProcess.saveGroup(getContext(), photoSelectUtil, groupNameEditText.getText().toString(), new CompleteCallback() {
            @Override
            public void onComplete(final Object object) {
                if (object != null) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ProfileHelper.ProfileRefresh.profileRefreshStart(); // grup eklendikçe profil sayfası güncellenir.
                            completeCallback.onComplete(object);
                            Objects.requireNonNull(getActivity()).onBackPressed();
                        }
                    }, 1000);
                }
            }

            @Override
            public void onFailed(Exception e) {
                DialogBoxUtil.showErrorDialog(getContext(), getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                    @Override
                    public void okClick() {
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == CODE_CAMERA_REQUEST) {
                photoSelectUtil = new PhotoSelectUtil(getContext(), data, CAMERA_TEXT);
                setGroupPhoto(photoSelectUtil.getMediaUri());
            } else if (requestCode == CODE_GALLERY_REQUEST) {
                photoSelectUtil = new PhotoSelectUtil(getContext(), data, GALLERY_TEXT);
                setGroupPhoto(photoSelectUtil.getMediaUri());
            }
        }
    }

    public void setGroupPhoto(Uri groupPhotoUri) {
        if (groupPhotoUri != null && !groupPhotoUri.toString().trim().isEmpty()) {
            groupPhotoExist = true;
            groupPictureImgv.setPadding(0, 0, 0, 0);
            Glide.with(Objects.requireNonNull(getContext()))
                    .load(groupPhotoUri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(groupPictureImgv);
        } else {
            groupPhotoExist = false;
            photoSelectUtil = null;
            int paddingPx = getResources().getDimensionPixelSize(R.dimen.ADD_GROUP_IMGV_SIZE);
            groupPictureImgv.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
            Glide.with(this)
                    .load(R.mipmap.icon_photo_camera)
                    .into(groupPictureImgv);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PermissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(Intent.createChooser(IntentSelectUtil.getGalleryIntent(),
                        getResources().getString(R.string.selectPicture)), CODE_GALLERY_REQUEST);
            }
        } else if (requestCode == PermissionModule.PERMISSION_CAMERA) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(IntentSelectUtil.getCameraIntent(), CODE_CAMERA_REQUEST);
            }
        }
    }
}
