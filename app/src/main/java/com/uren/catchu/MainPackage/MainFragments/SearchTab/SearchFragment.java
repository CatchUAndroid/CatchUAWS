package com.uren.catchu.MainPackage.MainFragments.SearchTab;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.uren.catchu.Adapters.SpecialSelectTabAdapter;
import com.uren.catchu.GeneralUtils.PermissionModule;
import com.uren.catchu.MainPackage.Interfaces.IOnBackPressed;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.SearchTab.SubFragments.GroupFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.MainPackage.MainFragments.SearchTab.SubFragments.PersonFragment;

import butterknife.ButterKnife;
import catchu.model.SearchResult;

import static com.uren.catchu.Constants.StringConstants.GET_AUTHENTICATED_USER_GROUP_LIST;
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

    String userid = "us-east-1:4af861e4-1cb6-4218-87e7-523c84bbfa96";

    //Toolbar mToolBar;


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

        context = getActivity();
        initializeItems();

        PermissionModule permissionModule = new PermissionModule(context);
        permissionModule.checkPermissions();

        return view;
    }

    private void initializeItems() {

        overwriteToolbar();

        viewPager = view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        selectedProperty = propPersons;

        addListeners();
    }

    private void setupViewPager(final ViewPager viewPager) {

        adapter = new SpecialSelectTabAdapter(getFragmentManager());
        adapter.addFragment(new PersonFragment(userid, verticalShown, "A", context), getResources().getString(R.string.friends));
        adapter.addFragment(new GroupFragment(context, userid, GET_AUTHENTICATED_USER_GROUP_LIST), getResources().getString(R.string.groups));
        viewPager.setAdapter(adapter);
    }

    private void addListeners() {

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {

                    case personFragmentTab:

                        selectedProperty = propPersons;
                        break;


                    case groupFragmentTab:

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

        Toolbar mToolBar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolBar.setTitle(getResources().getString(R.string.search));
        mToolBar.setNavigationIcon(R.drawable.back_arrow);
        mToolBar.setBackgroundColor(getResources().getColor(R.color.background, null));
        mToolBar.setTitleTextColor(getResources().getColor(R.color.background_white, null));
        mToolBar.setSubtitleTextColor(getResources().getColor(R.color.background_white, null));

        ((NextActivity) getActivity()).setSupportActionBar(mToolBar);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        menuInflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        searchView = (android.support.v7.widget.SearchView) searchItem.getActionView();


        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (!newText.isEmpty()) {

                    if (selectedProperty.equals(propPersons))
                        searchForPersons(newText);
                }

                return false;
            }
        });

        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuItem register = menu.findItem(R.id.addNewGroup);
        if(selectedProperty.equals(propPersons))
            register.setVisible(false);
        else
            register.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                break;
            case R.id.logOut:
                break;
            case R.id.addNewGroup:

                /*if(FirebaseGetFriends.getInstance(getFbUserID()).getListSize() == 0){
                    CustomDialogAdapter.showDialogWarning(SpecialSelectActivity.this,
                            "Grup olusturmak için önce arkadas eklemelisiniz.");
                }else {
                    startActivity(new Intent(this, AddNewFriendActivity.class));
                }*/

                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return super.onOptionsItemSelected(item);
    }

    public void searchForPersons(String searchText) {

        PersonFragment personFragment = new PersonFragment(userid, verticalShown, searchText, context);
        adapter.updateFragment(personFragmentTab, personFragment);
        reloadAdapter();
    }

    public void reloadAdapter() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //getSearchResult(userid, defSpace);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
