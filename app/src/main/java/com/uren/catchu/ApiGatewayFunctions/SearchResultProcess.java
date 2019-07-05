package com.uren.catchu.ApiGatewayFunctions;

import android.content.Context;
import android.os.AsyncTask;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.UserListResponse;

import static com.uren.catchu.Constants.NumericConstants.RESPONSE_OK;

public class SearchResultProcess extends AsyncTask<Void, Void, UserListResponse> {

    private OnEventListener<UserListResponse> mCallBack;
    private Context mContext;
    public Exception mException;
    public String userid;
    public String searchText;
    public String perpage;
    public String page;
    private String token;

    public SearchResultProcess(Context context, OnEventListener callback, String userid, String searchText, String perpage, String page, String token) {
        this.mCallBack = callback;
        this.mContext = context;
        this.userid = userid;
        this.searchText = searchText;
        this.page = page;
        this.perpage = perpage;
        this.token = token;
    }

    @Override
    protected UserListResponse doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            UserListResponse userListResponse = instance.client.searchUsersGet(userid, searchText, token, perpage, page);

            if(userListResponse.getError().getCode().intValue() == RESPONSE_OK){
                return userListResponse;
            }else{
                return null;
            }

        } catch (Exception e) {
            mException = e;
            e.printStackTrace();
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
    protected void onPostExecute(UserListResponse searchResult) {
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
