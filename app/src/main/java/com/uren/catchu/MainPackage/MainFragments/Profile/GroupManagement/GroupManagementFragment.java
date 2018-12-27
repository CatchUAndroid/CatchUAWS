package com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement;


import android.annotation.SuppressLint;
import android.content.res.Resources;
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
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.Interfaces.RecyclerViewAdapterCallback;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Adapters.UserGroupsListAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Utils.UpdateGroupProcess;
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

    @Override
    public void onStart() {
        NextActivity.bottomTabLayout.setVisibility(View.VISIBLE);
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
            if (mView == null) {
                mView = inflater.inflate(R.layout.fragment_group_management, container, false);
                ButterKnife.bind(this, mView);
                addListeners();
                initValues();
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    public void initValues() {
        try {
            searchToolbarLayout.setVisibility(View.VISIBLE);
            searchToolbarAddItemImgv.setVisibility(View.VISIBLE);
            setFloatButtonVisibility();
            getGroups();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void setFloatButtonVisibility() {
        try {
            if (operationType.equals(GROUP_OP_CHOOSE_TYPE))
                nextFab.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void addListeners() {
        try {
            searchToolbarBackImgv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });

            nextFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nextFab.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                    if (operationType.equals(GROUP_OP_CHOOSE_TYPE)) {
                        if (selectedGroupItem == null) {
                            CommonUtils.showCustomToast(getContext(), getResources().getString(R.string.selectLeastOneGroup));
                            return;
                        }
                        returnCallback.onReturn(selectedGroupItem);
                        getActivity().onBackPressed();
                    }
                }
            });

            searchToolbarAddItemImgv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchToolbarAddItemImgv.setEnabled(false);
                    searchToolbarAddItemImgv.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
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
                        if (!s.toString().trim().isEmpty()) {
                            imgCancelSearch.setVisibility(View.VISIBLE);
                            searchToolbarBackImgv.setVisibility(View.GONE);
                        } else {
                            imgCancelSearch.setVisibility(View.GONE);
                            searchToolbarBackImgv.setVisibility(View.VISIBLE);
                        }

                        if (userGroupsListAdapter != null)
                            userGroupsListAdapter.updateAdapter(s.toString(), new ReturnCallback() {
                                @Override
                                public void onReturn(Object object) {
                                    int itemSize = (int) object;

                                    if (warningMsgTv.getVisibility() == View.GONE) {
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
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void getGroups() {

        try {
            progressBar.setVisibility(View.VISIBLE);

            UserGroupsProcess.getGroups(AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid(),
                    new CompleteCallback() {
                        @Override
                        public void onComplete(Object object) {
                            groupRequestResult = (GroupRequestResult) object;

                            if (getContext() != null) {
                                MessageDataUtil.setWarningMessageVisibility(groupRequestResult, warningMsgTv,
                                        getContext().getResources().getString(R.string.THERE_IS_NO_GROUP_CREATE_OR_INCLUDE));

                                userGroupsListAdapter = new UserGroupsListAdapter(getContext(), groupRequestResult, new ReturnCallback() {
                                    @Override
                                    public void onReturn(Object object) {
                                        selectedGroupItem = (GroupRequestResultResultArrayItem) object;
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
                                            }));
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
                            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                                    new Object() {
                                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
                            if (getContext() != null) {
                                DialogBoxUtil.showErrorDialog(getContext(), getActivity().getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                                    @Override
                                    public void okClick() {
                                    }
                                });
                            }
                        }
                    });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void localGroupOperation(int opType, GroupRequestResultResultArrayItem arrayItem) {
        try {
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
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void addNewGroup() {

        try {
            AccountHolderFollowProcess.getFollowers(new CompleteCallback() {
                @Override
                public void onComplete(Object object) {
                    if (object != null) {
                        FriendList friendList = (FriendList) object;
                        if (friendList != null && friendList.getResultArray() != null && friendList.getResultArray().size() == 0 &&
                                getContext() != null)
                            CommonUtils.showCustomToast(getContext(), getContext().getResources().getString(R.string.addFriendFirst));
                        else {
                            if (mFragmentNavigation != null) {
                                mFragmentNavigation.pushFragment(new SelectFriendFragment(null, null,
                                        GroupManagementFragment.class.getName(), new ReturnCallback() {
                                    @Override
                                    public void onReturn(Object object) {
                                        localGroupOperation(ITEM_INSERTED, (GroupRequestResultResultArrayItem) object);
                                        userGroupsListAdapter.notifyDataSetChanged();
                                    }
                                }), ANIMATE_RIGHT_TO_LEFT);
                            }
                        }
                    }
                    searchToolbarAddItemImgv.setEnabled(true);
                }

                @Override
                public void onFailed(Exception e) {
                    searchToolbarAddItemImgv.setEnabled(true);
                    ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                            new Object() {
                            }.getClass().getEnclosingMethod().getName(), e.getMessage());
                    if (getContext() != null) {
                        DialogBoxUtil.showErrorDialog(getContext(), getContext().getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                            @Override
                            public void okClick() {
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }
}
