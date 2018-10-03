package com.uren.catchu.ApiGatewayFunctions;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.BaseRequest;
import catchu.model.PostListResponse;

import static com.uren.catchu.Constants.NumericConstants.RESPONSE_OK;

public class PostListResponseProcess extends AsyncTask<Void, Void, PostListResponse> {

    private OnEventListener<PostListResponse> mCallBack;
    private Context mContext;
    public Exception mException;
    public String longitude;
    public String latitude;
    public String radius;
    public BaseRequest baseRequest;
    public String perpage;
    public String page;
    private String token;

    public PostListResponseProcess(Context context, OnEventListener callback,
                                    BaseRequest baseRequest, String longitude, String latitude, String radius, String perpage, String page, String token) {
        mCallBack = callback;
        mContext = context;
        this.longitude = longitude;
        this.latitude = latitude;
        this.radius = radius;
        this.baseRequest = baseRequest;
        this.perpage = perpage;
        this.page = page;
        this.token= token;
    }


    @Override
    protected PostListResponse doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            //todo NT - servislere verilen parametreler düzenlenecek...
            PostListResponse postListResponse = instance.client.postsGeolocationPost(token, baseRequest, longitude, perpage, latitude, radius, page);

            if(postListResponse.getError().getCode().intValue() == RESPONSE_OK){
                return postListResponse;
            }else{
                return null;
            }

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
