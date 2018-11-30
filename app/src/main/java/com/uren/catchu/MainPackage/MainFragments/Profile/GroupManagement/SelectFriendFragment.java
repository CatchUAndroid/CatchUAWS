package com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement;

import android.annotation.SuppressLint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.ApiModelsProcess.UserGroupsProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ProgressDialogUtil.ProgressDialogUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Adapters.SelectFriendAdapter;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.MainPackage.MainFragments.Share.SharePostFragment;
import com.uren.catchu.Singleton.SelectedFriendList;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.Error;
import catchu.model.FriendList;
import catchu.model.GroupRequestGroupParticipantArrayItem;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.NumericConstants.CODE_SELECT_ALL;
import static com.uren.catchu.Constants.NumericConstants.CODE_UNSELECT_ALL;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;

@SuppressLint("ValidFragment")
public class SelectFriendFragment extends BaseFragment {

    View mView;

    @BindView(R.id.friendCountTv)
    TextView friendCountTv;
    @BindView(R.id.nextFab)
    FloatingActionButton nextFab;
    @BindView(R.id.imgCancelSearch)
    ImageView imgCancelSearch;
    @BindView(R.id.editTextSearch)
    EditText editTextSearch;
    @BindView(R.id.selectAllCb)
    CheckBox selectAllCb;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.searchToolbarBackImgv)
    ImageView searchToolbarBackImgv;
    @BindView(R.id.searchToolbarAddItemImgv)
    ImageView searchToolbarAddItemImgv;


    FriendList followerList;
    ProgressDialogUtil progressDialogUtil;
    SelectFriendAdapter adapter;

    String groupId;
    List<UserProfileProperties> groupParticipantList;
    String pendingName;

    ReturnCallback returnCallback;

    public SelectFriendFragment(String groupId, List<UserProfileProperties> groupParticipantList, String pendingName,
                                ReturnCallback returnCallback) {
        this.groupId = groupId;
        this.groupParticipantList = groupParticipantList;
        this.pendingName = pendingName;
        this.returnCallback = returnCallback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_select_friend, container, false);
            ButterKnife.bind(this, mView);
            addListeners();
            setShapes();
            initFollowerList();
            getFriendSelectionPage();
            SelectedFriendList.setInstance(null);
            progressDialogUtil = new ProgressDialogUtil(getContext(), null, false);
            progressDialogUtil.dialogShow();
            searchToolbarAddItemImgv.setVisibility(View.GONE);
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    public void addListeners() {
        searchToolbarBackImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
                getActivity().onBackPressed();
            }
        });

        nextFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextFab.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                checkSelectedPerson();
            }
        });

        imgCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextSearch.setText("");
                imgCancelSearch.setVisibility(View.GONE);
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.toString() != null) {
                    if (!s.toString().trim().isEmpty()) {
                        imgCancelSearch.setVisibility(View.VISIBLE);
                        searchToolbarBackImgv.setVisibility(View.GONE);
                    } else {
                        imgCancelSearch.setVisibility(View.GONE);
                        searchToolbarBackImgv.setVisibility(View.VISIBLE);
                    }


                    if (adapter != null)
                        adapter.updateAdapter(s.toString());
                } else
                    imgCancelSearch.setVisibility(View.GONE);
            }
        });

        selectAllCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter != null) {
                    if (selectAllCb.isChecked())
                        adapter.updateAdapterForSelectAll(CODE_SELECT_ALL);
                    else
                        adapter.updateAdapterForSelectAll(CODE_UNSELECT_ALL);
                }
            }
        });
    }

    public void initFollowerList() {
        followerList = new FriendList();
        followerList.setResultArray(new ArrayList<UserProfileProperties>());
        followerList.setError(new Error());
    }

    private void setShapes() {
        GradientDrawable shape = ShapeUtil.getShape(getResources().getColor(R.color.LightSeaGreen, null),
                0, GradientDrawable.OVAL, 50, 0);
        nextFab.setBackground(shape);
    }

    private void getFriendSelectionPage() {

        AccountHolderFollowProcess.getFollowers(new CompleteCallback() {
            @Override
            public void onComplete(Object object) {
                if (object != null) {
                    followerList = getUserFollowers((FriendList) object);
                    setAdapter();
                }
                progressDialogUtil.dialogDismiss();
            }

            @Override
            public void onFailed(Exception e) {
                progressDialogUtil.dialogDismiss();
                DialogBoxUtil.showErrorDialog(getContext(), getContext().getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                    @Override
                    public void okClick() {
                    }
                });
            }
        });
    }

    public void setAdapter() {
        setFriendCountTextView();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SelectFriendAdapter(getContext(), followerList, new ReturnCallback() {
            @Override
            public void onReturn(Object object) {
                if (followerList.getResultArray().size() == SelectedFriendList.getInstance().getSize())
                    selectAllCb.setChecked(true);
                else
                    selectAllCb.setChecked(false);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    public void setFriendCountTextView() {
        friendCountTv.setText(Integer.toString(followerList.getResultArray().size()));
    }

    public FriendList getUserFollowers(FriendList tempFriendList) {

        if (groupParticipantList == null)
            return tempFriendList;
        else
            return extractGroupParticipants(tempFriendList);
    }

    public FriendList extractGroupParticipants(FriendList friendListTemp) {

        for (UserProfileProperties userProfileProperties1 : groupParticipantList) {

            int index = 0;

            for (UserProfileProperties userProfileProperties : friendListTemp.getResultArray()) {

                if (userProfileProperties.getUserid().equals(userProfileProperties1.getUserid())) {
                    friendListTemp.getResultArray().remove(index);
                    break;
                }
                index = index + 1;
            }
        }
        return friendListTemp;
    }

    public void checkSelectedPerson() {

        if (SelectedFriendList.getInstance().getSelectedFriendList().getResultArray().size() == 0) {
            Toast.makeText(getContext(), getResources().getString(R.string.selectLeastOneFriend), Toast.LENGTH_SHORT).show();
            return;
        }

        if (pendingName != null) {
            if (pendingName.equals(ViewGroupDetailFragment.class.getName())) {
                startAddParticipantToGroup();
            } else if (pendingName.equals(SharePostFragment.class.getName())) {
                getActivity().onBackPressed();
                returnCallback.onReturn(null);
            } else if (pendingName.equals(GroupManagementFragment.class.getName())) {

                if (mFragmentNavigation != null) {
                    mFragmentNavigation.pushFragment(new AddGroupFragment(new CompleteCallback() {
                        @Override
                        public void onComplete(Object object) {
                            getActivity().onBackPressed();
                            returnCallback.onReturn(object);
                        }

                        @Override
                        public void onFailed(Exception e) {

                        }
                    }), ANIMATE_RIGHT_TO_LEFT);
                }
            }
        }
    }

    private void startAddParticipantToGroup() {

        UserGroupsProcess.addParticipantsToGroup(groupId, fillSelectedFriendList(), new CompleteCallback() {
            @Override
            public void onComplete(Object object) {
                returnCallback.onReturn(null);
                getActivity().onBackPressed();
            }

            @Override
            public void onFailed(Exception e) {
                CommonUtils.showCustomToast(getContext(), getResources().getString(R.string.error) +
                        getResources().getString(R.string.SOMETHING_WENT_WRONG));
            }
        });
    }

    public List<GroupRequestGroupParticipantArrayItem> fillSelectedFriendList() {

        List<GroupRequestGroupParticipantArrayItem> selectedFriendList = new ArrayList<>();

        for (UserProfileProperties userProfileProperties : SelectedFriendList.getInstance().getSelectedFriendList().getResultArray()) {
            GroupRequestGroupParticipantArrayItem groupRequestGroupParticipantArrayItem = new GroupRequestGroupParticipantArrayItem();
            groupRequestGroupParticipantArrayItem.setParticipantUserid(userProfileProperties.getUserid());
            selectedFriendList.add(groupRequestGroupParticipantArrayItem);
        }

        return selectedFriendList;
    }
}
