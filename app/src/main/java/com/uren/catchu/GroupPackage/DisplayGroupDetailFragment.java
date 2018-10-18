package com.uren.catchu.GroupPackage;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.SubtitleCollapsingToolbarLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.ApiGatewayFunctions.GroupResultProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.PhotoChosenCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.YesNoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.IntentUtil.IntentSelectUtil;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.GroupPackage.Adapters.GroupDetailListAdapter;
import com.uren.catchu.GroupPackage.Interfaces.UpdateGroupCallback;
import com.uren.catchu.GroupPackage.Utils.UpdateGroupProcess;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ListItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.FollowInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.OtherProfileFragment;
import com.uren.catchu.MainPackage.MainFragments.SearchTab.SearchFragment;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.ShareDetailActivity;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.SelectedFriendList;
import com.uren.catchu.Singleton.UserGroups;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import catchu.model.FollowInfoResultArrayItem;
import catchu.model.GroupRequest;
import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.CAMERA_TEXT;
import static com.uren.catchu.Constants.StringConstants.EXIT_GROUP;
import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;
import static com.uren.catchu.Constants.StringConstants.GET_GROUP_PARTICIPANT_LIST;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_ACTIVITY_NAME;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_GROUP_ID;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_GROUP_NAME;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_SHARE_FRIEND_COUNT;
import static com.uren.catchu.GroupPackage.Adapters.GroupDetailListAdapter.CODE_DISPLAY_PROFILE;
import static com.uren.catchu.GroupPackage.Adapters.GroupDetailListAdapter.CODE_REMOVE_FROM_GROUP;

public class DisplayGroupDetailFragment extends BaseFragment {

    View mView;
    ImageView groupPictureImgV;
    ImageView editImageView;
    CoordinatorLayout groupCoordinatorLayout;
    TextView personCntTv;
    SubtitleCollapsingToolbarLayout subtitleCollapsingToolbarLayout;
    boolean photoExistOnImgv = false;

    public static final int REQUEST_CODE_GRP_NAME_CHANGED = 414;
    public static final int REQUEST_CODE_SELECT_FRIEND = 415;

    String groupId;

    GroupDetailListAdapter adapter;

    CardView addFriendCardView;
    CardView deleteGroupCardView;

    public static List<UserProfileProperties> groupParticipantList;
    GroupRequestResult groupRequestResult;
    public static GroupRequestResultResultArrayItem groupRequestResultResultArrayItem;

    PermissionModule permissionModule;
    ProgressDialog mProgressDialog;

    ProgressBar progressBar;
    RecyclerView recyclerView;
    PhotoSelectUtil photoSelectUtil;

    public static DisplayGroupDetailFragment newInstance(String groupId) {
        Bundle args = new Bundle();
        args.putSerializable(PUTEXTRA_GROUP_ID, groupId);
        DisplayGroupDetailFragment fragment = new DisplayGroupDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.activity_display_group_detail, container, false);
            ButterKnife.bind(this, mView);

            Bundle args = getArguments();
            if (args != null)
                groupId = (String) args.getSerializable(PUTEXTRA_GROUP_ID);

            setGUIVariables();
            getGroupInformation();
            getGroupParticipants();
            addListeners();
        }
        return mView;
    }

    public void setGUIVariables() {
        groupPictureImgV = mView.findViewById(R.id.groupPictureImgv);
        editImageView = mView.findViewById(R.id.editImageView);
        personCntTv = mView.findViewById(R.id.personCntTv);
        addFriendCardView = mView.findViewById(R.id.addFriendCardView);
        deleteGroupCardView = mView.findViewById(R.id.deleteGroupCardView);
        progressBar = mView.findViewById(R.id.progressBar);
        groupCoordinatorLayout = mView.findViewById(R.id.groupCoordinatorLayout);
        subtitleCollapsingToolbarLayout = mView.findViewById(R.id.collapsing_toolbar);
        permissionModule = new PermissionModule(getActivity());
        mProgressDialog = new ProgressDialog(getActivity());
        recyclerView = mView.findViewById(R.id.recyclerView);
        groupParticipantList = new ArrayList<>();
    }

    private void getGroupInformation() {

        UserGroups.getInstance(new CompleteCallback() {
            @Override
            public void onComplete(Object object) {
                groupRequestResultResultArrayItem = UserGroups.getGroupWithId(groupId);

                if (groupRequestResultResultArrayItem == null)
                    CommonUtils.showToast(getActivity(),
                            getResources().getString(R.string.error) + getResources().getString(R.string.technicalError));
                else {
                    setCardViewVisibility();
                    setGroupTitle();
                    setGroupImage(groupRequestResultResultArrayItem.getGroupPhotoUrl());
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
    }

    public void getGroupParticipants() {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetGroupParticipants(token);
            }
        });
    }

    private void startGetGroupParticipants(String token) {

        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setGroupid(this.groupId);
        groupRequest.setRequestType(GET_GROUP_PARTICIPANT_LIST);

        GroupResultProcess groupResultProcess = new GroupResultProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                progressBar.setVisibility(View.GONE);
                groupRequestResult = (GroupRequestResult) object;
                groupParticipantList.addAll(groupRequestResult.getResultArrayParticipantList());
                setParticipantCount();
                setupViewRecyclerView();
            }

            @Override
            public void onFailure(Exception e) {
                progressBar.setVisibility(View.GONE);
                CommonUtils.showToast(getActivity(), getResources().getString(R.string.error) + e.getMessage());
            }

            @Override
            public void onTaskContinue() {
                progressBar.setVisibility(View.VISIBLE);
            }
        }, groupRequest, token);

        groupResultProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public void setParticipantCount() {
        personCntTv.setText(Integer.toString(groupParticipantList.size()));
    }

    public int getParticipantCount() {
        return groupParticipantList.size();
    }

    public void setCardViewVisibility() {
        if (AccountHolderInfo.getUserID().equals(groupRequestResultResultArrayItem.getGroupAdmin()))
            addFriendCardView.setVisibility(View.VISIBLE);
    }

    public void setGroupTitle() {
        subtitleCollapsingToolbarLayout.setTitle(groupRequestResultResultArrayItem.getName());
        subtitleCollapsingToolbarLayout.setSubtitle(getToolbarSubtitle());
    }

    public String getToolbarSubtitle() {
        return getResources().getString(R.string.createdAt) + " " +
                groupRequestResultResultArrayItem.getCreateAt();
    }

    public void setGroupImage(String photoUrl) {
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
    }

    public void addListeners() {

        addFriendCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectFriendToGroupActivity.class);
                intent.putExtra(PUTEXTRA_ACTIVITY_NAME, DisplayGroupDetailFragment.class.getSimpleName());
                intent.putExtra(PUTEXTRA_GROUP_ID, groupId);
                startActivityForResult(intent, REQUEST_CODE_SELECT_FRIEND);
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

                Intent intent = new Intent(getActivity(), EditGroupNameActivity.class);
                intent.putExtra(PUTEXTRA_GROUP_ID, groupRequestResultResultArrayItem.getGroupid());
                intent.putExtra(PUTEXTRA_GROUP_NAME, groupRequestResultResultArrayItem.getName());
                startActivityForResult(intent, REQUEST_CODE_GRP_NAME_CHANGED);
            }
        });

        groupPictureImgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChooseImageProc();
            }
        });
    }

    private void setupViewRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateAdapter();
        recyclerView.setAdapter(adapter);
    }

    public void updateAdapter() {
        adapter = new GroupDetailListAdapter(getActivity(), groupParticipantList, groupRequestResultResultArrayItem, new ItemClickListener() {
            @Override
            public void onClick(Object object, int clickedItem) {
                if (clickedItem == CODE_DISPLAY_PROFILE) {
                    if (mFragmentNavigation != null) {
                        FollowInfoResultArrayItem followInfoResultArrayItem = (FollowInfoResultArrayItem) object;
                        FollowInfoListItem followInfoListItem = new FollowInfoListItem(followInfoResultArrayItem);
                        followInfoListItem.setAdapter(adapter);
                        followInfoListItem.setClickedPosition(clickedItem);
                        mFragmentNavigation.pushFragment(OtherProfileFragment.newInstance(followInfoListItem), ANIMATE_RIGHT_TO_LEFT);
                    }
                } else if (clickedItem == CODE_REMOVE_FROM_GROUP) {
                    List<UserProfileProperties> groupParticipantList1 = (List<UserProfileProperties>) object;
                    groupParticipantList.clear();
                    groupParticipantList.addAll(groupParticipantList1);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void reloadAdapter() {

        updateAdapter();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    public void exitFromGroup(final String userid) {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startExitFromGroup(userid, token);
            }
        });
    }

    private void startExitFromGroup(String userid, String token) {

        final GroupRequest groupRequest = new GroupRequest();
        groupRequest.setRequestType(EXIT_GROUP);
        groupRequest.setUserid(userid);
        groupRequest.setGroupid(groupId);

        GroupResultProcess groupResultProcess = new GroupResultProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                UserGroups.removeGroupFromList(groupRequestResultResultArrayItem);
                SearchFragment.reloadAdapter();
                getActivity().onBackPressed();
            }

            @Override
            public void onFailure(Exception e) {
                DialogBoxUtil.showErrorDialog(getActivity(), getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                    @Override
                    public void okClick() {
                    }
                });
            }

            @Override
            public void onTaskContinue() {

            }
        }, groupRequest, token);

        groupResultProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void startChooseImageProc() {
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
    }

    public void startCameraProcess() {

        if (!CommonUtils.checkCameraHardware(getActivity())) {
            CommonUtils.showToast(getActivity(), getResources().getString(R.string.deviceHasNoCamera));
            return;
        }

        if (!permissionModule.checkWriteExternalStoragePermission())
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE);
        else
            checkCameraPermission();
    }

    public void checkCameraPermission() {
        if (!permissionModule.checkCameraPermission())
            requestPermissions(new String[]{Manifest.permission.CAMERA}, permissionModule.PERMISSION_CAMERA);
        else {
            startActivityForResult(IntentSelectUtil.getCameraIntent(), permissionModule.PERMISSION_CAMERA);
        }
    }

    private void startGalleryProcess() {
        startActivityForResult(Intent.createChooser(IntentSelectUtil.getGalleryIntent(),
                getResources().getString(R.string.selectPicture)), permissionModule.getImageGalleryPermission());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == permissionModule.PERMISSION_CAMERA) {
                photoSelectUtil = new PhotoSelectUtil(getActivity(), data, CAMERA_TEXT);
                updateGroup();
            } else if (requestCode == permissionModule.getImageGalleryPermission()) {
                photoSelectUtil = new PhotoSelectUtil(getActivity(), data, GALLERY_TEXT);
                updateGroup();
            } else if (requestCode == REQUEST_CODE_GRP_NAME_CHANGED) {
                String groupName = (String) data.getSerializableExtra(PUTEXTRA_GROUP_NAME);
                subtitleCollapsingToolbarLayout.setTitle(groupName);
            } else if (requestCode == REQUEST_CODE_SELECT_FRIEND) {
                groupParticipantList.addAll(SelectedFriendList.getInstance().getSelectedFriendList().getResultArray());
                reloadAdapter();
                personCntTv.setText(Integer.toString(getParticipantCount()));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == permissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkCameraPermission();
            }
        } else if (requestCode == permissionModule.PERMISSION_CAMERA) {
            startActivityForResult(IntentSelectUtil.getCameraIntent(), permissionModule.PERMISSION_CAMERA);
        } else
            CommonUtils.showToast(getActivity(), getResources().getString(R.string.technicalError) + requestCode);
    }

    public void updateGroup() {
        new UpdateGroupProcess(getActivity(), photoSelectUtil, groupRequestResultResultArrayItem, new UpdateGroupCallback() {
            @Override
            public void onSuccess(final GroupRequestResultResultArrayItem groupItem) {

                UserGroups.getInstance(new CompleteCallback() {
                    @Override
                    public void onComplete(Object object) {
                        UserGroups.changeGroupItem(groupId, groupItem);
                        SearchFragment.reloadAdapter();
                        setGroupImage(groupItem.getGroupPhotoUrl());
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
    }
}
