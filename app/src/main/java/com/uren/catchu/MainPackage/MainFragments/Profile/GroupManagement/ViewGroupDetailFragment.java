package com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.SubtitleCollapsingToolbarLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.GeneralUtils.ApiModelsProcess.UserGroupsProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.PhotoChosenCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.YesNoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.GeneralUtils.IntentUtil.IntentSelectUtil;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.GeneralUtils.ProgressDialogUtil.ProgressDialogUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Adapters.GroupDetailListAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Interfaces.UpdateGroupCallback;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.Interfaces.RecyclerViewAdapterCallback;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;

import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.UserInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.MessageWithGroupFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.OtherProfile.OtherProfileFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;
import catchu.model.User;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.CAMERA_TEXT;
import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;
import static com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Adapters.GroupDetailListAdapter.CODE_CHANGE_AS_ADMIN;
import static com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Adapters.GroupDetailListAdapter.CODE_DISPLAY_PROFILE;
import static com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Adapters.GroupDetailListAdapter.CODE_REMOVE_FROM_GROUP;

@SuppressLint("ValidFragment")
public class ViewGroupDetailFragment extends BaseFragment {

    View mView;

    @BindView(R.id.groupPictureImgv)
    ImageView groupPictureImgV;
    @BindView(R.id.editImageView)
    ImageView editImageView;
    @BindView(R.id.groupCoordinatorLayout)
    CoordinatorLayout groupCoordinatorLayout;
    @BindView(R.id.personCntTv)
    TextView personCntTv;
    @BindView(R.id.collapsing_toolbar)
    SubtitleCollapsingToolbarLayout subtitleCollapsingToolbarLayout;
    @BindView(R.id.addFriendCardView)
    CardView addFriendCardView;
    @BindView(R.id.deleteGroupCardView)
    CardView deleteGroupCardView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.addFriendImgv)
    ImageView addFriendImgv;
    @BindView(R.id.sendMessageImgv)
    ImageView sendMessageImgv;

    boolean photoExistOnImgv = false;

    GroupDetailListAdapter adapter;
    List<UserProfileProperties> groupParticipantList;
    GroupRequestResult groupRequestResult;
    GroupRequestResultResultArrayItem groupRequestResultResultArrayItem;

    PermissionModule permissionModule;
    ProgressDialog mProgressDialog;

    PhotoSelectUtil photoSelectUtil;
    ProgressDialogUtil progressDialogUtil;

    RecyclerViewAdapterCallback recyclerViewAdapterCallback;

    public ViewGroupDetailFragment(GroupRequestResultResultArrayItem groupRequestResultResultArrayItem, RecyclerViewAdapterCallback recyclerViewAdapterCallback) {
        this.groupRequestResultResultArrayItem = groupRequestResultResultArrayItem;
        this.recyclerViewAdapterCallback = recyclerViewAdapterCallback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        try {
            if (mView == null) {
                mView = inflater.inflate(R.layout.fragment_view_group_detail, container, false);
                ButterKnife.bind(this, mView);
                setGUIVariables();
                getGroupInformation();
                addListeners();
                setShapes();
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return mView;
    }

    public void setGUIVariables() {
        try {
            permissionModule = new PermissionModule(getActivity());
            mProgressDialog = new ProgressDialog(getActivity());
            groupParticipantList = new ArrayList<>();
            progressDialogUtil = new ProgressDialogUtil(getActivity(), null, true);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void setShapes(){
        try {
            addFriendImgv.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.DodgerBlue, null),
                    0, GradientDrawable.OVAL, 50, 0));
            sendMessageImgv.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.MediumTurquoise, null),
                    0, GradientDrawable.OVAL, 50, 0));
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void getGroupInformation() {
        try {
            if (groupRequestResultResultArrayItem != null) {
                setCardViewVisibility();
                setGroupTitle();
                if (groupRequestResultResultArrayItem.getGroupPhotoUrl() != null)
                    setGroupImage(groupRequestResultResultArrayItem.getGroupPhotoUrl());
                startGetGroupParticipants();
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void startGetGroupParticipants() {

        try {
            progressDialogUtil.dialogShow();
            UserGroupsProcess.getGroupParticipants(groupRequestResultResultArrayItem.getGroupid(), new CompleteCallback() {
                @Override
                public void onComplete(Object object) {
                    groupRequestResult = (GroupRequestResult) object;
                    groupParticipantList = new ArrayList<>();
                    groupParticipantList.addAll(groupRequestResult.getResultArrayParticipantList());
                    setParticipantCount();
                    setupViewRecyclerView();
                    progressDialogUtil.dialogDismiss();
                }

                @Override
                public void onFailed(Exception e) {
                    progressDialogUtil.dialogDismiss();
                    ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                            new Object() {
                            }.getClass().getEnclosingMethod().getName(), e.toString());
                    CommonUtils.showToastShort(getActivity(), getResources().getString(R.string.error) +
                            getResources().getString(R.string.SOMETHING_WENT_WRONG));
                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void setParticipantCount() {
        try {
            personCntTv.setText(Integer.toString(groupParticipantList.size()));
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void setCardViewVisibility() {
        try {
            if (AccountHolderInfo.getUserID().equals(groupRequestResultResultArrayItem.getGroupAdmin()))
                addFriendCardView.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void setGroupTitle() {
        try {
            subtitleCollapsingToolbarLayout.setTitle(groupRequestResultResultArrayItem.getName());
            subtitleCollapsingToolbarLayout.setSubtitle(getToolbarSubtitle());
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public String getToolbarSubtitle() {
        return getResources().getString(R.string.createdAt) + " " +
                groupRequestResultResultArrayItem.getCreateAt();
    }

    public void setGroupImage(String photoUrl) {
        try {
            if (photoUrl != null && !photoUrl.trim().isEmpty()) {
                photoExistOnImgv = true;
                groupPictureImgV.setPadding(0, 0, 0, 0);
                Glide.with(this)
                        .load(photoUrl)
                        .apply(RequestOptions.centerInsideTransform())
                        .into(groupPictureImgV);
            } else {
                photoExistOnImgv = false;
                groupPictureImgV.setPadding(200, 200, 200, 200);
                Glide.with(this)
                        .load(getResources().getIdentifier("groups_icon_500", "drawable", getActivity().getPackageName()))
                        .apply(RequestOptions.centerInsideTransform())
                        .into(groupPictureImgV);
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void addListeners() {

        try {
            sendMessageImgv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessageImgv.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                    mFragmentNavigation.pushFragment(new MessageWithGroupFragment(groupRequestResultResultArrayItem), ANIMATE_LEFT_TO_RIGHT);
                }
            });

            addFriendCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mFragmentNavigation != null) {
                        mFragmentNavigation.pushFragment(new SelectFriendFragment(groupRequestResultResultArrayItem.getGroupid(),
                                groupParticipantList, ViewGroupDetailFragment.class.getName(),
                                new ReturnCallback() {
                                    @Override
                                    public void onReturn(Object object) {
                                        startGetGroupParticipants();
                                    }
                                }), ANIMATE_RIGHT_TO_LEFT);
                    }
                }
            });

            deleteGroupCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogBoxUtil.showYesNoDialog(getActivity(), null, getResources().getString(R.string.areYouSureExitFromGroup), new YesNoDialogBoxCallback() {
                        @Override
                        public void yesClick() {
                            exitFromGroup(AccountHolderInfo.getUserID());
                        }

                        @Override
                        public void noClick() {

                        }
                    });
                }
            });

            editImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editImageView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                    if (mFragmentNavigation != null) {
                        mFragmentNavigation.pushFragment(new EditGroupNameFragment(groupRequestResultResultArrayItem,
                                new CompleteCallback() {
                                    @Override
                                    public void onComplete(Object object) {
                                        if (object != null) {
                                            String edittedGroupName = (String) object;
                                            subtitleCollapsingToolbarLayout.setTitle(edittedGroupName);
                                            recyclerViewAdapterCallback.OnChanged(groupRequestResultResultArrayItem);
                                        }
                                    }

                                    @Override
                                    public void onFailed(Exception e) {
                                        DialogBoxUtil.showErrorDialog(getActivity(), getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                                            @Override
                                            public void okClick() {
                                            }
                                        });
                                    }
                                }), ANIMATE_LEFT_TO_RIGHT);
                    }
                }
            });

            groupPictureImgV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startChooseImageProc();
                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void setupViewRecyclerView() {
        try {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter = new GroupDetailListAdapter(getActivity(), groupParticipantList, groupRequestResultResultArrayItem, new ItemClickListener() {
                @Override
                public void onClick(Object object, int clickedItem) {
                    if (clickedItem == CODE_DISPLAY_PROFILE) {
                        if (mFragmentNavigation != null) {
                            User user = (User) object;
                            UserInfoListItem userInfoListItem = new UserInfoListItem(user);
                            userInfoListItem.setAdapter(adapter);
                            userInfoListItem.setClickedPosition(clickedItem);
                            mFragmentNavigation.pushFragment(new OtherProfileFragment(userInfoListItem), ANIMATE_RIGHT_TO_LEFT);
                        }
                    } else if (clickedItem == CODE_REMOVE_FROM_GROUP) {
                        List<UserProfileProperties> groupParticipantList1 = (List<UserProfileProperties>) object;
                        groupParticipantList.clear();
                        groupParticipantList.addAll(groupParticipantList1);
                    } else if (clickedItem == CODE_CHANGE_AS_ADMIN) {
                        recyclerViewAdapterCallback.OnChanged((GroupRequestResultResultArrayItem) object);
                    }
                }
            });
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        NextActivity.bottomTabLayout.setVisibility(View.GONE);
        super.onStart();
    }

    public void exitFromGroup(final String userid) {

        try {
            UserGroupsProcess.exitFromGroup(userid, groupRequestResultResultArrayItem.getGroupid(), new CompleteCallback() {
                @Override
                public void onComplete(Object object) {
                    recyclerViewAdapterCallback.OnRemoved();
                    getActivity().onBackPressed();
                }

                @Override
                public void onFailed(Exception e) {
                    DialogBoxUtil.showErrorDialog(getActivity(), getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {
                        }
                    });
                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void startChooseImageProc() {
        try {
            DialogBoxUtil.photoChosenDialogBox(getActivity(), getResources().
                    getString(R.string.CHOOSE_GROUP_PHOTO), photoExistOnImgv, new PhotoChosenCallback() {
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
                    groupRequestResultResultArrayItem.setGroupPhotoUrl("");
                    photoSelectUtil = null;
                    updateGroup();
                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void startCameraProcess() {

        try {
            if (!CommonUtils.checkCameraHardware(getActivity())) {
                CommonUtils.showToastShort(getActivity(), getResources().getString(R.string.deviceHasNoCamera));
                return;
            }

            if (!permissionModule.checkWriteExternalStoragePermission())
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE);
            else
                checkCameraPermission();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void checkCameraPermission() {
        try {
            if (!permissionModule.checkCameraPermission())
                requestPermissions(new String[]{Manifest.permission.CAMERA}, permissionModule.PERMISSION_CAMERA);
            else {
                startActivityForResult(IntentSelectUtil.getCameraIntent(), permissionModule.PERMISSION_CAMERA);
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    private void startGalleryProcess() {
        try {
            startActivityForResult(Intent.createChooser(IntentSelectUtil.getGalleryIntent(),
                    getResources().getString(R.string.selectPicture)), permissionModule.getImageGalleryPermission());
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == permissionModule.PERMISSION_CAMERA) {
                    photoSelectUtil = new PhotoSelectUtil(getActivity(), data, CAMERA_TEXT);
                    updateGroup();
                } else if (requestCode == permissionModule.getImageGalleryPermission()) {
                    photoSelectUtil = new PhotoSelectUtil(getActivity(), data, GALLERY_TEXT);
                    updateGroup();
                }
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        try {
            if (requestCode == permissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkCameraPermission();
                }
            } else if (requestCode == permissionModule.PERMISSION_CAMERA) {
                startActivityForResult(IntentSelectUtil.getCameraIntent(), permissionModule.PERMISSION_CAMERA);
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void updateGroup() {

        try {
            UserGroupsProcess.updateGroup(getContext(), photoSelectUtil, groupRequestResultResultArrayItem,
                    new UpdateGroupCallback() {
                        @Override
                        public void onSuccess(GroupRequestResultResultArrayItem groupItem) {
                            if (groupItem != null) {
                                groupRequestResultResultArrayItem = groupItem;
                                setGroupImage(groupRequestResultResultArrayItem.getGroupPhotoUrl());
                                recyclerViewAdapterCallback.OnChanged(groupRequestResultResultArrayItem);
                            }
                        }

                        @Override
                        public void onFailed(Exception e) {
                            DialogBoxUtil.showErrorDialog(getActivity(), getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                                @Override
                                public void okClick() {
                                }
                            });
                        }
                    });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }
}
