package com.uren.catchu.MainPackage.MainFragments.SearchTab.SubFragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.uren.catchu.Adapters.UserDetailAdapter;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.SearchResultProcess;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.SearchResult;

import static com.uren.catchu.Constants.StringConstants.gridShown;
import static com.uren.catchu.Constants.StringConstants.horizontalShown;
import static com.uren.catchu.Constants.StringConstants.verticalShown;

@SuppressLint("ValidFragment")
public class PersonFragment extends Fragment {

    RecyclerView personRecyclerView;

    private View mView;
    String userid;
    String viewType;
    String searchText;
    Context context;
    SearchResult searchResult;

    @BindView(R.id.progressBar)
    public ProgressBar progressBar;

    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;

    //@SuppressLint("ValidFragment")
    public PersonFragment(String userid, String viewType, String searchText, Context context) {
        this.userid = userid;
        this.viewType = viewType;
        this.searchText = searchText;
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_special_select, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        personRecyclerView = (RecyclerView) mView.findViewById(R.id.specialRecyclerView);
        getSearchResult(userid, searchText);
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    public void getSearchResult(final String userid, final String searchText){
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetProfileDetail(userid, searchText, token);
            }
        });
    }

    private void startGetProfileDetail(String userid, String searchText, String token) {
        SearchResultProcess searchResultProcess = new SearchResultProcess(context, new OnEventListener<SearchResult>() {

            @Override
            public void onSuccess(SearchResult object) {
                Log.i("Info", "SearchResult on success");
                progressBar.setVisibility(View.GONE);
                searchResult = object;
                getData();
            }

            @Override
            public void onFailure(Exception e) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onTaskContinue() {
                progressBar.setVisibility(View.VISIBLE);
            }
        }, userid, searchText, token);

        searchResultProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void getData(){

        switch (viewType){
            case verticalShown:

                UserDetailAdapter userDetailAdapter = new UserDetailAdapter(context, searchText, searchResult, userid);
                personRecyclerView.setAdapter(userDetailAdapter);
                linearLayoutManager = new LinearLayoutManager(context);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                personRecyclerView.setLayoutManager(linearLayoutManager);
                break;

            case horizontalShown:
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                personRecyclerView.setLayoutManager(linearLayoutManager);
                break;

            case gridShown:
                //FriendGridListAdapter friendGridListAdapter = new FriendGridListAdapter(context, instance.getFriendList());
                //personRecyclerView.setAdapter(friendGridListAdapter);

                gridLayoutManager =new GridLayoutManager(context, 4);
                personRecyclerView.setLayoutManager(gridLayoutManager);
                break;

            default:
                Toast.makeText(context, "Person Fragment getData teknik hata!!", Toast.LENGTH_SHORT).show();
        }
    }

}
