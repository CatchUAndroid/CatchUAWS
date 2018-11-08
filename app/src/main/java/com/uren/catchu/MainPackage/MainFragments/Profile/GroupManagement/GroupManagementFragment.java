package com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement;


import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.ApiModelsProcess.UserGroupsProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.MessageDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.Interfaces.RecyclerViewAdapterCallback;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Adapters.UserGroupsListAdapter;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.SelectedGroupList;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.FriendList;
import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.GROUP_OP_CHOOSE_TYPE;

@SuppressLint("ValidFragment")
public class GroupManagementFragment extends BaseFragment {

    View mView;

    LinearLayoutManager linearLayoutManager;
    UserGroupsListAdapter userGroupsListAdapter;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.searchToolbarLayout)
    LinearLayout searchToolbarLayout;
    @BindView(R.id.specialRecyclerView)
    RecyclerView specialRecyclerView;
    @BindView(R.id.warningMsgTv)
    TextView warningMsgTv;
    @BindView(R.id.addItemImgv)
    ImageView addItemImgv;

    @BindView(R.id.editTextSearch)
    EditText editTextSearch;
    @BindView(R.id.imgCancelSearch)
    ImageView imgCancelSearch;
    @BindView(R.id.searchImgv)
    ImageView searchImgv;
    @BindView(R.id.searchResultTv)
    TextView searchResultTv;
    @BindView(R.id.nextFab)
    FloatingActionButton nextFab;

    GroupRequestResultResultArrayItem selectedGroupItem;
    GroupRequestResult groupRequestResult;
    String operationType;
    ReturnCallback returnCallback;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(mView == null) {
            mView = inflater.inflate(R.layout.fragment_special_select, container, false);
            ButterKnife.bind(this, mView);
            addListeners();
            initValues();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    public void initValues(){
        SelectedGroupList.setInstance(null);
        searchToolbarLayout.setVisibility(View.VISIBLE);
        addItemImgv.setVisibility(View.VISIBLE);
        setFloatButtonVisibility();
        getGroups();
    }

    public void setFloatButtonVisibility(){
        if(operationType.equals(GROUP_OP_CHOOSE_TYPE))
            nextFab.setVisibility(View.VISIBLE);
    }

    public void addListeners(){
        nextFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextFab.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                if(operationType.equals(GROUP_OP_CHOOSE_TYPE)) {
                    if (SelectedGroupList.getInstance().getSize() == 0) {
                        CommonUtils.showToastLong(getContext(), getResources().getString(R.string.selectLeastOneGroup));
                        return;
                    }
                    returnCallback.onReturn(null);
                    getActivity().onBackPressed();
                }
            }
        });

        addItemImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemImgv.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                addNewGroup();
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
                    if (!s.toString().trim().isEmpty())
                        imgCancelSearch.setVisibility(View.VISIBLE);
                    else
                        imgCancelSearch.setVisibility(View.GONE);

                    if (userGroupsListAdapter != null)
                        userGroupsListAdapter.updateAdapter(s.toString(), new ReturnCallback() {
                            @Override
                            public void onReturn(Object object) {
                                int itemSize = (int) object;

                                if(warningMsgTv.getVisibility() == View.GONE) {
                                    if (itemSize == 0)
                                        searchResultTv.setVisibility(View.VISIBLE);
                                    else
                                        searchResultTv.setVisibility(View.GONE);
                                }
                            }
                        });
                } else
                    imgCancelSearch.setVisibility(View.GONE);
            }
        });

        imgCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgCancelSearch.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                CommonUtils.hideKeyBoard(getContext());
                editTextSearch.setText("");
                imgCancelSearch.setVisibility(View.GONE);
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

                        MessageDataUtil.setWarningMessageVisibility(groupRequestResult, warningMsgTv,
                                getActivity().getResources().getString(R.string.THERE_IS_NO_GROUP_CREATE_OR_INCLUDE));

                        if(getContext() != null) {
                            userGroupsListAdapter = new UserGroupsListAdapter(getContext(), groupRequestResult, new ReturnCallback() {
                                @Override
                                public void onReturn(Object object) {
                                    GroupRequestResult groupRequestResult1 = (GroupRequestResult) object;

                                    MessageDataUtil.setWarningMessageVisibility(groupRequestResult1, warningMsgTv,
                                            getActivity().getResources().getString(R.string.THERE_IS_NO_GROUP_CREATE_OR_INCLUDE));
                                }
                            }, new ItemClickListener() {
                                @Override
                                public void onClick(Object object, final int clickedItem) {
                                    selectedGroupItem = (GroupRequestResultResultArrayItem) object;

                                    if (mFragmentNavigation != null)
                                        mFragmentNavigation.pushFragment(new ViewGroupDetailFragment(selectedGroupItem, new RecyclerViewAdapterCallback() {
                                            @Override
                                            public void OnRemoved() {
                                                localGroupOperation(ITEM_REMOVED, null);
                                                userGroupsListAdapter.notifyItemRemoved(clickedItem);
                                                userGroupsListAdapter.notifyItemRangeChanged(clickedItem,
                                                        groupRequestResult.getResultArray().size());
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
                                        }), ANIMATE_RIGHT_TO_LEFT);
                                }
                            }, operationType);

                            specialRecyclerView.setAdapter(userGroupsListAdapter);
                            linearLayoutManager = new LinearLayoutManager(getContext());
                            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            specialRecyclerView.setLayoutManager(linearLayoutManager);
                        }
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailed(Exception e) {
                        progressBar.setVisibility(View.GONE);
                        DialogBoxUtil.showErrorDialog(getContext(), getActivity().getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                            @Override
                            public void okClick() {
                            }
                        });
                    }
                });
    }

    public void localGroupOperation(int opType, GroupRequestResultResultArrayItem arrayItem){
        if(arrayItem == null) {
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
        }else if(opType == ITEM_INSERTED)
            groupRequestResult.getResultArray().add(arrayItem);
    }

    public void addNewGroup() {

        AccountHolderFollowProcess.getFollowers(new CompleteCallback() {
            @Override
            public void onComplete(Object object) {
                if (object != null) {
                    FriendList friendList = (FriendList) object;
                    if (friendList != null && friendList.getResultArray() != null && friendList.getResultArray().size() == 0)
                        CommonUtils.showToast(getContext(), getContext().getResources().getString(R.string.addFriendFirst));
                    else {
                        if (mFragmentNavigation != null) {
                            mFragmentNavigation.pushFragment(new SelectFriendFragment(null, null,
                                    GroupManagementFragment.class.getName(), new ReturnCallback() {
                                @Override
                                public void onReturn(Object object) {
                                    localGroupOperation(ITEM_INSERTED, (GroupRequestResultResultArrayItem)object);
                                    userGroupsListAdapter.notifyDataSetChanged();
                                }
                            }), ANIMATE_RIGHT_TO_LEFT);
                        }
                    }
                }
            }

            @Override
            public void onFailed(Exception e) {
                DialogBoxUtil.showErrorDialog(getContext(), getContext().getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                    @Override
                    public void okClick() {
                    }
                });
            }
        });
    }
}