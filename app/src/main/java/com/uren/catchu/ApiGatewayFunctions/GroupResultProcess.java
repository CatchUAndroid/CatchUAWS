package com.uren.catchu.ApiGatewayFunctions;

import android.content.Context;
import android.os.AsyncTask;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.GroupRequest;
import catchu.model.SearchResult;

public class GroupResultProcess extends AsyncTask<Void, Void, SearchResult> {

    private OnEventListener<GroupRequest> mCallBack;
    private Context mContext;
    public Exception mException;

    @Override
    protected SearchResult doInBackground(Void... voids) {
        return null;
    }
}
