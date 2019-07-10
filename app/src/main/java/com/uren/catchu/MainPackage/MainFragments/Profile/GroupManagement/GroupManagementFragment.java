package com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.ApiModelsProcess.UserGroupsProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.InfoDialogBoxCallback;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.Interfaces.RecyclerViewAdapterCallback;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Adapters.UserGroupsListAdapter;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.FriendList;
import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.GROUP_OP_CHOOSE_TYPE;

@SuppressLint("ValidFragment")
public class  GroupManagementFragment extends BaseFragment {

    View mView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.searchToolbarLayout)
    LinearLayout searchToolbarLayout;
    @BindView(R.id.specialRecyclerView)
    RecyclerView specialRecyclerView;
    @BindView(R.id.warningMsgTv)
    TextView warningMsgTv;
    @BindView(R.id.searchToolbarAddItemImgv)
    ImageView searchToolbarAddItemImgv;
    @BindView(R.id.searchToolbarBackImgv)
    ImageView searchToolbarBackImgv;

    @BindView(R.id.editTextSearch)
    EditText editTextSearch;
    @BindView(R.id.imgCancelSearch)
    ImageView imgCancelSearch;
    @BindView(R.id.searchResultTv)
    TextView searchResultTv;
    @BindView(R.id.nextFab)
    FloatingActionButton nextFab;

    private GroupRequestResultResultArrayItem selectedGroupItem;
    private GroupRequestResult groupRequestResult;
    private String operationType;
    private ReturnCallback returnCallback;
    private UserGroupsListAdapter userGroupsListAdapter;

    private static final int ITEM_CHANGED = 0;
    private static final int ITEM_REMOVED = 1;
    private static final int ITEM_INSERTED = 2;

    public GroupManagementFragment(String operationType, ReturnCallback returnCallback) {
        this.operationType = operationType;
        this.returnCallback = returnCallback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        getActivity().findViewById(R.id.tabMainLayout).setVisibility(View.GONE);
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_group_management, container, false);
            ButterKnife.bind(this, mView);
            addListeners();
            initValues();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    public void initValues() {
        searchToolbarLayout.setVisibility(View.VISIBLE);
        searchToolbarAddItemImgv.setVisibility(View.VISIBLE);
        editTextSearch.setHint(getContext().getResources().getString(R.string.searchGroup));
        warningMsgTv.setText(getContext().getResources().getString(R.string.THERE_IS_NO_GROUP_CREATE_OR_INCLUDE));
        setFloatButtonVisibility();
        getGroups();
    }

    @SuppressLint("RestrictedApi")
    public void setFloatButtonVisibility() {
        if (operationType.equals(GROUP_OP_CHOOSE_TYPE))
            nextFab.setVisibility(View.VISIBLE);
    }

    public void addListeners() {
        searchToolbarBackImgv.setOnClickListener(v -> getActivity().onBackPressed());

        nextFab.setOnClickListener(v -> {
            nextFab.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
            if (operationType.equals(GROUP_OP_CHOOSE_TYPE)) {
                if (selectedGroupItem == null) {
                    CommonUtils.showToastShort(getContext(), getResources().getString(R.string.selectLeastOneGroup));
                    return;
                }
                returnCallback.onReturn(selectedGroupItem);
                getActivity().onBackPressed();
            }
        });

        searchToolbarAddItemImgv.setOnClickListener(v -> {
            CommonUtils.hideKeyBoard(getContext());
            searchToolbarAddItemImgv.setEnabled(false);
            searchToolbarAddItemImgv.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
            addNewGroup();
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
                if (s != null && s.toString() != null && !s.toString().isEmpty()) {
                    imgCancelSearch.setVisibility(View.VISIBLE);
                    searchToolbarBackImgv.setVisibility(View.GONE);
                    searchItemInList(s.toString());
                } else {
                    imgCancelSearch.setVisibility(View.GONE);
                    searchToolbarBackImgv.setVisibility(View.VISIBLE);
                    searchItemInList("");
                }
            }
        });

        imgCancelSearch.setOnClickListener(v -> {
            CommonUtils.hideKeyBoard(getContext());
            editTextSearch.setText("");
            imgCancelSearch.setVisibility(View.GONE);
            searchResultTv.setVisibility(View.GONE);
            setMessageWarning(groupRequestResult);

        });
    }

    public void searchItemInList(final String groupName) {
        if (userGroupsListAdapter != null)
            userGroupsListAdapter.updateAdapter(groupName, object -> {
                int itemSize = (int) object;

                if (!groupName.isEmpty()) {
                    warningMsgTv.setVisibility(View.GONE);
                    if (itemSize == 0)
                        searchResultTv.setVisibility(View.VISIBLE);
                    else
                        searchResultTv.setVisibility(View.GONE);
                } else {
                    setMessageWarning(groupRequestResult);
                    searchResultTv.setVisibility(View.GONE);
                }
            });
    }

    public void getGroups() {

        progressBar.setVisibility(View.VISIBLE);

        UserGroupsProcess.getGroups(AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid(),
                new CompleteCallback() {
                    @Override
                    public void onComplete(Object object) {
                        groupRequestResult = (GroupRequestResult) object;

                        if (getContext() != null) {
                            setMessageWarning(groupRequestResult);
                            setGroupsListAdapter();
                        }
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailed(Exception e) {
                        progressBar.setVisibility(View.GONE);
                        if (getContext() != null) {
                            DialogBoxUtil.showErrorDialog(getContext(), getActivity().getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                                @Override
                                public void okClick() {
                                }
                            });
                        }
                    }
                });
    }

    private void setMessageWarning(GroupRequestResult groupRequestResult) {
        if (groupRequestResult != null && groupRequestResult.getResultArray() != null &&
                groupRequestResult.getResultArray().size() > 0)
            warningMsgTv.setVisibility(View.GONE);
        else
            warningMsgTv.setVisibility(View.VISIBLE);
    }

    private void setGroupsListAdapter() {
        userGroupsListAdapter = new UserGroupsListAdapter(getContext(), groupRequestResult, object -> selectedGroupItem = (GroupRequestResultResultArrayItem) object, new ItemClickListener() {
            @Override
            public void onClick(Object object, final int clickedItem) {
                selectedGroupItem = (GroupRequestResultResultArrayItem) object;
                startViewGroupDetailFragment(clickedItem);
            }
        }, operationType);

        specialRecyclerView.setAdapter(userGroupsListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        specialRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void startViewGroupDetailFragment(final int clickedItem) {
        if (mFragmentNavigation != null)
            mFragmentNavigation.pushFragment(new ViewGroupDetailFragment(selectedGroupItem, new RecyclerViewAdapterCallback() {
                @Override
                public void OnRemoved() {
                    localGroupOperation(ITEM_REMOVED, null);
                    userGroupsListAdapter.notifyItemRemoved(clickedItem);
                    userGroupsListAdapter.notifyItemRangeChanged(clickedItem,
                            groupRequestResult.getResultArray().size());
                    setMessageWarning(groupRequestResult);
                }

                @Override
                public void OnInserted() {

                }

                @Override
                public void OnChanged(Object object1) {
                    selectedGroupItem = (GroupRequestResultResultArrayItem) object1;
                    localGroupOperation(ITEM_CHANGED, null);
                    userGroupsListAdapter.notifyDataSetChanged();
                }
            }), ANIMATE_LEFT_TO_RIGHT);
    }

    public void localGroupOperation(int opType, GroupRequestResultResultArrayItem arrayItem) {
        if (arrayItem == null) {
            int index = 0;
            for (GroupRequestResultResultArrayItem resultArrayItem : groupRequestResult.getResultArray()) {

                if (opType == ITEM_CHANGED) {
                    if (selectedGroupItem.getGroupid().equals(resultArrayItem.getGroupid())) {
                        groupRequestResult.getResultArray().remove(index);
                        groupRequestResult.getResultArray().add(index, selectedGroupItem);
                        break;
                    }
                } else if (opType == ITEM_REMOVED) {
                    if (selectedGroupItem.getGroupid().equals(resultArrayItem.getGroupid())) {
                        groupRequestResult.getResultArray().remove(index);
                        break;
                    }
                }

                index++;
            }
        } else if (opType == ITEM_INSERTED)
            groupRequestResult.getResultArray().add(arrayItem);
    }

    public void addNewGroup() {

        AccountHolderFollowProcess.getFollowers(1, 1, new CompleteCallback() {
            @Override
            public void onComplete(Object object) {
                if (object != null) {
                    FriendList friendList = (FriendList) object;
                    if (friendList != null && friendList.getResultArray() != null && friendList.getResultArray().size() == 0 &&
                            getContext() != null)
                        CommonUtils.showToastShort(getContext(), getContext().getResources().getString(R.string.addFriendFirst));
                    else
                        startSelectFriendFragment();

                }
                searchToolbarAddItemImgv.setEnabled(true);
            }

            @Override
            public void onFailed(Exception e) {
                searchToolbarAddItemImgv.setEnabled(true);
                if (getContext() != null) {
                    DialogBoxUtil.showErrorDialog(getContext(), getContext().getResources().getString(R.string.error) + e.getMessage(), () -> {
                    });
                }
            }
        });
    }

    private void startSelectFriendFragment() {
        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new SelectFriendFragment(null, null, null,
                    GroupManagementFragment.class.getName(), object -> {
                        localGroupOperation(ITEM_INSERTED, (GroupRequestResultResultArrayItem) object);
                        userGroupsListAdapter.notifyDataSetChanged();
                        setMessageWarning(groupRequestResult);
                    }), ANIMATE_RIGHT_TO_LEFT);
        }
    }
}
