package com.uren.catchu.ApiGatewayFunctions;

import android.os.AsyncTask;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.CountryListResponse;

import static com.uren.catchu.Constants.NumericConstants.RESPONSE_OK;

public class CountryListProcess extends AsyncTask<Void, Void, CountryListResponse> {

    private OnEventListener<CountryListResponse> mCallBack;
    public Exception mException;
    private String token;
    private String userid;

    public CountryListProcess(OnEventListener callback, String userid, String token) {
        mCallBack = callback;
        this.userid = userid;
        this.token = token;
    }

    @Override
    protected CountryListResponse doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            CountryListResponse rsp = instance.client.commonCountriesGet(userid,token);

            if (rsp.getError().getCode().intValue() == RESPONSE_OK) {
                return rsp;
            } else {
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
    protected void onPostExecute(CountryListResponse countryListResponse) {
        super.onPostExecute(countryListResponse);

        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(countryListResponse);
            } else {
                mCallBack.onFailure(mException);
            }
        }

    }
}