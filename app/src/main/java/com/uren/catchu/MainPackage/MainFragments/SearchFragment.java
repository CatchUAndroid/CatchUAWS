package com.uren.catchu.MainPackage.MainFragments;

import android.content.Context;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.SearchResultProcess;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;

import butterknife.ButterKnife;
import catchu.CatchUMobileAPIClient;
import catchu.model.SearchResult;
import catchu.model.UserProfile;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.uren.catchu.Constants.StringConstants.defSpace;


public class SearchFragment extends BaseFragment{

    private Context context;
    public ProgressBar progressBar;
    View view;

    String userid = "us-east-1:4af861e4-1cb6-4218-87e7-523c84bbfa96";

    //Toolbar mToolBar;

    SearchResult searchResult;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        setHasOptionsMenu(true);

        view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);

        initializeItems();

        context = getActivity();

        return view;
    }

    private void initializeItems() {

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        overwriteToolbar();
    }

    private void overwriteToolbar() {

        ((NextActivity) getActivity()).getSupportActionBar().hide();

        Toolbar mToolBar = (Toolbar) view.findViewById(R.id.toolbarLayout);
        mToolBar.setTitle("Kisi veya Gruplari Seciniz...");
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

                getSearchResult(userid, newText);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu,menuInflater);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getSearchResult(userid, defSpace);
    }

    public void getSearchResult(String userid, String searchText){

        SearchResultProcess searchResultProcess = new SearchResultProcess(getApplicationContext(), new OnEventListener<SearchResult>() {

            @Override
            public void onSuccess(SearchResult object) {
                Log.i("Info", "SearchResult on success");
                progressBar.setVisibility(View.GONE);
                searchResult = object;
            }

            @Override
            public void onFailure(Exception e) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onTaskContinue() {
                progressBar.setVisibility(View.VISIBLE);
            }
        }, userid, searchText);

        searchResultProcess.execute();
    }


}
