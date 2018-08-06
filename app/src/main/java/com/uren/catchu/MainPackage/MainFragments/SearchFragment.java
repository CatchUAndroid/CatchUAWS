package com.uren.catchu.MainPackage.MainFragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;

import butterknife.ButterKnife;
import catchu.CatchUMobileAPIClient;
import catchu.model.SearchResult;


public class SearchFragment extends BaseFragment{


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        ButterKnife.bind(this, view);

        ( (NextActivity)getActivity()).updateToolbarTitle("Search");


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getFriends();

    }

    @SuppressLint("StaticFieldLeak")
    private void getFriends() {

        new AsyncTask<Void, Void, Void>(){

            ApiClientFactory factory = new ApiClientFactory();
            CatchUMobileAPIClient client = factory.build(CatchUMobileAPIClient.class);


            @Override
            protected Void doInBackground(Void... voids) {


                SearchResult searchResult = client.searchGet("us-east-1:4af861e4-1cb6-4218-87e7-523c84bbfa96", "e");
                Log.i("resultArray ", searchResult.getResultArray().get(0).toString());

                return null;


            }
        }.execute();


    }


}
