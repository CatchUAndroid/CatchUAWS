package com.uren.catchu.MainPackage.MainFragments.SearchTab;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uren.catchu.Adapters.SpecialSelectTabAdapter;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.GroupPackage.SelectFriendToGroupActivity;
import com.uren.catchu.MainPackage.Interfaces.IOnBackPressed;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.SearchTab.SubFragments.GroupFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.MainPackage.MainFragments.SearchTab.SubFragments.PersonFragment;
import com.uren.catchu.Singleton.AccountHolderFollowers;
import com.uren.catchu.Singleton.AccountHolderInfo;

import butterknife.ButterKnife;
import catchu.model.FriendList;
import catchu.model.SearchResult;
import catchu.model.UserProfile;

import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_ACTIVITY_NAME;
import static com.uren.catchu.Constants.StringConstants.propGroups;
import static com.uren.catchu.Constants.StringConstants.propPersons;
import static com.uren.catchu.Constants.StringConstants.verticalShown;


public class SearchFragment extends BaseFragment {

    private Context context;

    View view;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    public String selectedProperty;
    static SpecialSelectTabAdapter adapter;

    private final int personFragmentTab = 0;
    private final int groupFragmentTab = 1;

    String userid;
    Toolbar mToolBar;

    //nurullah - person ve group fragmentları tek bir tane yaratılacak şekilde düzenlendi
    private PersonFragment personFragment;
    private GroupFragment groupFragment;

    private EditText editTextSearch;
    private ImageView imgCancelSearch;
    private String searchText = "";
    private String tempSearchText = "";
    private RelativeLayout rl;
    private RelativeLayout r2;
    private TextView txtAddGroup;
    private Boolean refreshSearch = true;

    PermissionModule permissionModule;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        if (view == null) {

            view = inflater.inflate(R.layout.fragment_search, container, false);
            ButterKnife.bind(this, view);

            context = getActivity();
            initializeItems();
        } else {
            refreshSearch = false;
        }

        permissionModule = new PermissionModule(context);
        permissionModule.checkWriteExternalStoragePermission();

        return view;
    }

    private void initializeItems() {
        viewPager = view.findViewById(R.id.viewpager);
        tabLayout = view.findViewById(R.id.tabs);
        overwriteToolbar();

        if (AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid() != null) {
            userid = AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid();
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
        } else
            getProfileDetail();

        selectedProperty = propPersons;
        addListeners();
    }

    private void getProfileDetail() {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetProfileDetail(token);
            }
        });
    }

    private void startGetProfileDetail(String token) {

        UserDetail loadUserDetail = new UserDetail(getContext(), new OnEventListener<UserProfile>() {
            @Override
            public void onSuccess(UserProfile up) {
                UserProfile userProfile = new UserProfile();
                userid = userProfile.getUserInfo().getUserid();
                setupViewPager(viewPager);
                tabLayout.setupWithViewPager(viewPager);
            }

            @Override
            public void onFailure(Exception e) {
                DialogBoxUtil.showErrorDialog(context, context.getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                    @Override
                    public void okClick() {
                    }
                });
            }

            @Override
            public void onTaskContinue() {

            }
        }, AccountHolderInfo.getUserID(), token);

        loadUserDetail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setupViewPager(final ViewPager viewPager) {
        personFragment = new PersonFragment(userid, verticalShown, "A", context);
        groupFragment = new GroupFragment(context, userid);

        adapter = new SpecialSelectTabAdapter(getChildFragmentManager());
        adapter.addFragment(personFragment, getResources().getString(R.string.friends));
        adapter.addFragment(groupFragment, getResources().getString(R.string.groups));
        viewPager.setAdapter(adapter);
    }

    private void addListeners() {

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {

                    case personFragmentTab:
                        selectedProperty = propPersons;
                        rl.setVisibility(View.VISIBLE);
                        r2.setVisibility(View.GONE);
                        break;
                    case groupFragmentTab:
                        rl.setVisibility(View.GONE);
                        r2.setVisibility(View.VISIBLE);
                        selectedProperty = propGroups;
                        break;
                    default:
                        Log.i("Info", "Tablayout unknown");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void overwriteToolbar() {

        mToolBar = view.findViewById(R.id.toolbar);
        editTextSearch = view.findViewById(R.id.editTextSearch);
        imgCancelSearch = view.findViewById(R.id.imgCancelSearch);
        rl = view.findViewById(R.id.rl);
        r2 = view.findViewById(R.id.r2);
        txtAddGroup = view.findViewById(R.id.txtAddGroup);

        imgCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshSearch = true;
                editTextSearch.getText().clear();
                imgCancelSearch.setVisibility(View.GONE);

            }
        });

        txtAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewGroup();
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                tempSearchText = editTextSearch.getText().toString();

                // TODO : search text boş olduğunda recyler view ler boş olmalı mı? şimdilik A'lar geliyor.
                if (tempSearchText.matches("")) {
                    if (!refreshSearch) return;
                    searchForPersons("A");
                    searchText = tempSearchText;
                    return;
                }

                if (!tempSearchText.matches(searchText)) {
                    searchForPersons(tempSearchText);
                    imgCancelSearch.setVisibility(View.VISIBLE);
                    searchText = tempSearchText;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void addNewGroup() {
        AccountHolderFollowers.getInstance(new CompleteCallback() {
            @Override
            public void onComplete(Object object) {
                FriendList friendList = (FriendList) object;
                if (friendList.getResultArray().size() == 0)
                    CommonUtils.showToast(context, context.getResources().getString(R.string.addFriendFirst));
                else {
                    Intent intent = new Intent(context, SelectFriendToGroupActivity.class);
                    intent.putExtra(PUTEXTRA_ACTIVITY_NAME, NextActivity.class.getSimpleName());
                    startActivity(intent);
                }
            }

            @Override
            public void onFailed(Exception e) {
                DialogBoxUtil.showErrorDialog(context, context.getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                    @Override
                    public void okClick() {
                    }
                });
            }
        });
    }

    public void searchForPersons(String searchText) {
        adapter.updateFragment(personFragmentTab, personFragment);
        personFragment.getSearchResult(userid, searchText);
    }

    public static void reloadAdapter() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
