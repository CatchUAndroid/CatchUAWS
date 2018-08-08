package com.uren.catchu.GroupPackage.Fragments;


import android.annotation.SuppressLint;
import android.content.Context;
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
import com.uren.catchu.ApiGatewayFunctions.FriendListRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.SearchResultProcess;
import com.uren.catchu.GroupPackage.Adapters.SelectFriendAdapter;
import com.uren.catchu.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.FriendList;
import catchu.model.SearchResult;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.uren.catchu.Constants.StringConstants.gridShown;
import static com.uren.catchu.Constants.StringConstants.horizontalShown;
import static com.uren.catchu.Constants.StringConstants.verticalShown;

@SuppressLint("ValidFragment")
public class FriendFragment extends Fragment {

    RecyclerView personRecyclerView;

    private View mView;
    String userid;
    String viewType;
    Context context;
    SearchResult searchResult;

    @BindView(R.id.progressBar)
    public ProgressBar progressBar;

    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;
    FriendList friendList;

    //@SuppressLint("ValidFragment")
    public FriendFragment(Context context, String userid, String viewType) {
        this.userid = userid;
        this.viewType = viewType;
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
        getUserFriends(userid);
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    public void getUserFriends(String userid){

        FriendListRequestProcess friendListRequestProcess = new FriendListRequestProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                progressBar.setVisibility(View.GONE);
                friendList = (FriendList) object;
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
        }, userid);
        friendListRequestProcess.execute();
    }

    public void getData(){

        switch (viewType){
            case verticalShown:

                SelectFriendAdapter selectFriendAdapter = null;
                selectFriendAdapter = new SelectFriendAdapter(context, friendList, userid);

                personRecyclerView.setAdapter(selectFriendAdapter);
                linearLayoutManager = new LinearLayoutManager(context);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                personRecyclerView.setLayoutManager(linearLayoutManager);
                break;

            case horizontalShown:
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                personRecyclerView.setLayoutManager(linearLayoutManager);
                break;

            case gridShown:
                gridLayoutManager =new GridLayoutManager(context, 4);
                personRecyclerView.setLayoutManager(gridLayoutManager);
                break;

            default:
                Toast.makeText(context, "Friend Fragment getData teknik hata!!", Toast.LENGTH_SHORT).show();
        }
    }

}
