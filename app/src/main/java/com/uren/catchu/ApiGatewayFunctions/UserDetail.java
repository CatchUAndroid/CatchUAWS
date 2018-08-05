package com.uren.catchu.ApiGatewayFunctions;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import catchu.model.UserProfile;

public class UserDetail extends AsyncTask<Void, Void, UserProfile> {

    private OnEventListener<UserProfile> mCallBack;
    private Context mContext;
    public Exception mException;

    public UserDetail(Context context, OnEventListener callback) {
        mCallBack = callback;
        mContext = context;
    }


    @Override
    protected UserProfile doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        //ApiClientFactory factory = new ApiClientFactory();
        //CatchUMobileAPIClient client = factory.build(CatchUMobileAPIClient.class);

        try {

            UserProfile userProfile = instance.client.usersGet("us-east-1:4af861e4-1cb6-4218-87e7-523c84bbfa96");
            //SearchResult searchResult = client.searchGet("us-east-1:4af861e4-1cb6-4218-87e7-523c84bbfa96", "e");
            return userProfile;
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
    protected void onPostExecute(UserProfile userProfile) {
        super.onPostExecute(userProfile);

        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(userProfile);
            } else {
                mCallBack.onFailure(mException);
            }
        }

    }
}
