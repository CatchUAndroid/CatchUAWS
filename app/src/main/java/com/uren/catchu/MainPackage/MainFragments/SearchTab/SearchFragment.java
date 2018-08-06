package com.uren.catchu.MainPackage.MainFragments.SearchTab;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.uren.catchu.Adapters.SpecialSelectTabAdapter;
import com.uren.catchu.GeneralUtils.PermissionModule;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.MainPackage.MainFragments.SearchTab.SubFragments.PersonFragment;

import butterknife.ButterKnife;
import catchu.model.SearchResult;

import static com.uren.catchu.Constants.StringConstants.verticalShown;


public class SearchFragment extends BaseFragment {

    private Context context;

    View view;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public static String selectedProperty;

    private boolean queryTextChanged = false;

    String userid = "us-east-1:4af861e4-1cb6-4218-87e7-523c84bbfa96";

    //Toolbar mToolBar;

    public static final String propFriends = "Friends";
    public static final String propPersons = "Persons";
    public static final String propOnlyMe = "OnlyMe";
    public static final String propGroups = "Groups";

    SearchResult searchResult;
    SpecialSelectTabAdapter adapter;

    private static final int personFragmentTab = 0;
    private static final int groupFragmentTab = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);

        initializeItems();

        context = getActivity();

        PermissionModule permissionModule = new PermissionModule(context);
        permissionModule.checkPermissions();

        return view;
    }

    private void initializeItems() {

        overwriteToolbar();

        viewPager = view.findViewById(R.id.viewpager);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        selectedProperty = propPersons;
    }

    private void overwriteToolbar() {

        ((NextActivity) getActivity()).getSupportActionBar().hide();

        Toolbar mToolBar = (Toolbar) view.findViewById(R.id.toolbarLayout);
        mToolBar.setTitle(getResources().getString(R.string.search));
        mToolBar.setNavigationIcon(R.drawable.back_arrow);
        mToolBar.setBackgroundColor(getResources().getColor(R.color.background, null));
        mToolBar.setTitleTextColor(getResources().getColor(R.color.background_white, null));
        mToolBar.setSubtitleTextColor(getResources().getColor(R.color.background_white, null));

        ((NextActivity)getActivity()).setSupportActionBar(mToolBar);
        ((NextActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        menuInflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.item_search);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(!newText.isEmpty()) {

                    if(!queryTextChanged){
                        adapter = new SpecialSelectTabAdapter(getFragmentManager());
                        //adapter.addFragment(new GroupFragment(FirebaseGetAccountHolder.getUserID(), context, null), "Gonderiler");
                        adapter.addFragment(new PersonFragment(userid, verticalShown, newText, context),"Kisiler");
                        viewPager.setAdapter(adapter);
                        queryTextChanged = true;
                    }else {
                        PersonFragment personFragment = new PersonFragment(userid, verticalShown, newText, context);
                        adapter.updateFragment(personFragmentTab, personFragment);
                        reloadAdapter();
                    }
                }

                return false;
            }
        });

        super.onCreateOptionsMenu(menu,menuInflater);
    }

    public void reloadAdapter(){
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //getSearchResult(userid, defSpace);
    }
}
