package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
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

import com.uren.catchu.Adapters.SpecialSelectTabAdapter;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;

import butterknife.ButterKnife;

public class ExplorePeopleFragment extends BaseFragment{

    Context context;
    View view;
    ViewPager viewPager;
    TabLayout tabLayout;
    int selectedProperty;
    SpecialSelectTabAdapter adapter;
    Toolbar mToolBar;

    FacebookFriendsFragment facebookFriendsFragment;
    ContactFriendsFragment contactFriendsFragment;

    private EditText editTextSearch;
    private ImageView imgCancelSearch;
    private String searchText = "";
    private String tempSearchText = "";
    private RelativeLayout rl;
    private RelativeLayout r2;
    private Boolean refreshSearch = true;

    PermissionModule permissionModule;

    private static final int TAB_FACEBOOK = 0;
    private static final int TAB_CONTACTS = 1;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initializeItems() {
        overwriteToolbar();

        viewPager = view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        selectedProperty = TAB_FACEBOOK;
        addListeners();
    }

    private void setupViewPager(final ViewPager viewPager) {
        facebookFriendsFragment = new FacebookFriendsFragment();
        contactFriendsFragment = new ContactFriendsFragment();

        adapter = new SpecialSelectTabAdapter(getChildFragmentManager());
        adapter.addFragment(facebookFriendsFragment, getResources().getString(R.string.FACEBOOK));
        adapter.addFragment(contactFriendsFragment, getResources().getString(R.string.CONTACTS));
        viewPager.setAdapter(adapter);
    }

    private void addListeners() {

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {

                    case TAB_FACEBOOK:
                        rl.setVisibility(View.VISIBLE);
                        r2.setVisibility(View.GONE);
                        selectedProperty = TAB_FACEBOOK;
                        break;

                    case TAB_CONTACTS:
                        rl.setVisibility(View.VISIBLE);
                        r2.setVisibility(View.GONE);
                        selectedProperty = TAB_CONTACTS;
                        break;

                    default:
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

        imgCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshSearch = true;
                editTextSearch.getText().clear();
                imgCancelSearch.setVisibility(View.GONE);

            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                tempSearchText = editTextSearch.getText().toString();

                if (tempSearchText.matches("")) {
                    if (!refreshSearch) return;
                    //searchForPersons("A");
                    searchText = tempSearchText;
                    return;
                }

                if (!tempSearchText.matches(searchText)) {
                    //searchForPersons(tempSearchText);
                    imgCancelSearch.setVisibility(View.VISIBLE);
                    searchText = tempSearchText;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    /*public void searchForPersons(String searchText) {
        adapter.updateFragment(personFragmentTab, personFragment);
        personFragment.getSearchResult(userid, searchText);
    }

    public static void reloadAdapter() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }*/


}
