package com.uren.catchu.ApiGatewayFunctions;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.BaseRequest;
import catchu.model.PostListResponse;

public class PostListResponseProcess extends AsyncTask<Void, Void, PostListResponse> {

    private OnEventListener<PostListResponse> mCallBack;
    private Context mContext;
    public Exception mException;
    public String longitude;
    public String latitude;
    public String radius;
    public BaseRequest baseRequest;

    public PostListResponseProcess(Context context, OnEventListener callback,
                                    BaseRequest baseRequest, String longitude, String latitude, String radius) {
        mCallBack = callback;
        mContext = context;
        this.longitude = longitude;
        this.latitude = latitude;
        this.radius = radius;
        this.baseRequest = baseRequest;
    }


    @Override
    protected PostListResponse doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            PostListResponse postListResponse = instance.client.postsGeolocationPost(baseRequest, longitude,latitude,radius);
            return postListResponse;
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
    protected void onPostExecute(PostListResponse postListResponse) {
        super.onPostExecute(postListResponse);

        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(postListResponse);
            } else {
                mCallBack.onFailure(mException);
            }
        }

    }
}
