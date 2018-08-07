package com.uren.catchu.ApiGatewayFunctions;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.CatchUMobileAPIClient;
import catchu.model.SearchResult;

public class SearchResultProcess extends AsyncTask<Void, Void, SearchResult> {

    private OnEventListener<SearchResult> mCallBack;
    private Context mContext;
    public Exception mException;
    public String userid;
    public String searchText;

    public SearchResultProcess(Context context, OnEventListener callback, String userid, String searchText) {
        this.mCallBack = callback;
        this.mContext = context;
        this.userid = userid;
        this.searchText = searchText;
    }

    @Override
    protected SearchResult doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            SearchResult searchResult = instance.client.searchGet(userid, searchText);
            return searchResult;

        } catch (Exception e) {
            mException = e;
            e.printStackTrace();
            Log.e("error ", e.toString());
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (mCallBack != null) {
            mCallBack.onTaskContinue();
        }
    }

    @Override
    protected void onPostExecute(SearchResult searchResult) {
        super.onPostExecute(searchResult);

        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(searchResult);
            } else {
                mCallBack.onFailure(mException);
            }
        }
    }
}
