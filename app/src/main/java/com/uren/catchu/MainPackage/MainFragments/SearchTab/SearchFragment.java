package com.uren.catchu.MainPackage.MainFragments.SearchTab;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uren.catchu.Adapters.SpecialSelectTabAdapter;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GroupPackage.DisplayGroupDetailActivity;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.GroupPackage.SelectFriendToGroupActivity;
import com.uren.catchu.MainPackage.Interfaces.IOnBackPressed;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.SearchTab.SubFragments.GroupFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.MainPackage.MainFragments.SearchTab.SubFragments.PersonFragment;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.UserFriends;

import butterknife.ButterKnife;
import catchu.model.SearchResult;

import static com.uren.catchu.Constants.StringConstants.GET_AUTHENTICATED_USER_GROUP_LIST;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_ACTIVITY_NAME;
import static com.uren.catchu.Constants.StringConstants.propGroups;
import static com.uren.catchu.Constants.StringConstants.propPersons;
import static com.uren.catchu.Constants.StringConstants.verticalShown;


public class SearchFragment extends BaseFragment implements IOnBackPressed {

    private Context context;

    View view;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public static String selectedProperty;

    SearchView searchView;

    SearchResult searchResult;
    static SpecialSelectTabAdapter adapter;

    private static final int personFragmentTab = 0;
    private static final int groupFragmentTab = 1;

    String userid;
    MenuItem searchItem;
    Toolbar mToolBar;

    //nurullah - person ve group fragmentları tek bir tane yaratılacak şekilde düzenlendi
    private PersonFragment personFragment;
    private GroupFragment groupFragment;

    //Search bar degiskenleri
    private EditText editTextSearch;
    private ImageView imgCancelSearch;
    private String searchText = "";
    private String tempSearchText = "";
    private RelativeLayout rl;
    private RelativeLayout r2;
    private TextView txtAddGroup;
    private Boolean refreshSearch = true;

    PermissionModule permissionModule;

    // TODO(BUG) - Tab degistirip tekrar search e geldigimizde Person fragment ve Group Fragment funclar calismiyor

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        userid = AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid();
        overwriteToolbar();

        viewPager = view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        selectedProperty = propPersons;
        addListeners();
    }

    private void setupViewPager(final ViewPager viewPager) {
        personFragment = new PersonFragment(userid, verticalShown, "A", context);
        groupFragment = new GroupFragment(context, userid);

        adapter = new SpecialSelectTabAdapter(getFragmentManager());
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

        mToolBar = (Toolbar) view.findViewById(R.id.toolbar);

        editTextSearch = (EditText) view.findViewById(R.id.editTextSearch);
        imgCancelSearch = (ImageView) view.findViewById(R.id.imgCancelSearch);
        rl = (RelativeLayout) view.findViewById(R.id.rl);
        r2 = (RelativeLayout) view.findViewById(R.id.r2);
        txtAddGroup = (TextView) view.findViewById(R.id.txtAddGroup);

        //Cancel click
        imgCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                refreshSearch = true;
                editTextSearch.getText().clear();
                imgCancelSearch.setVisibility(View.GONE);

            }
        });

        //Add new grup click
        txtAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewGroup();
            }
        });

        //Search text change listener
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
        if (UserFriends.getInstance(AccountHolderInfo.getUserID()).getSize() == 0)
            CommonUtils.showToast(context, context.getResources().getString(R.string.addFriendFirst));
        else {
            Intent intent = new Intent(context, SelectFriendToGroupActivity.class);
            intent.putExtra(PUTEXTRA_ACTIVITY_NAME, NextActivity.class.getSimpleName());
            startActivity(intent);
        }
    }

    public void searchForPersons(String searchText) {
        adapter.updateFragment(personFragmentTab, personFragment);
        personFragment.getSearchResult(userid, searchText);
    }

    public static void reloadAdapter() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
