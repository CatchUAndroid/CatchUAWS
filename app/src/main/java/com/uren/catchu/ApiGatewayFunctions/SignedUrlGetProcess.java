package com.uren.catchu.ApiGatewayFunctions;

import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.BucketUploadResult;

public class SignedUrlGetProcess extends AsyncTask<Void, Void, BucketUploadResult> {

    private OnEventListener<BucketUploadResult> mCallBack;
    public Exception mException;
    public String extensionType;

    public SignedUrlGetProcess(OnEventListener callback,String extensionType) {
        this.extensionType = extensionType;
        this.mCallBack = callback;
    }

    @Override
    protected BucketUploadResult doInBackground(Void... voids) {
        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            BucketUploadResult commonS3BucketResult = instance.client.commonSignedurlGet("1", "0", "", extensionType);
            return commonS3BucketResult;

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
    protected void onPostExecute(BucketUploadResult commonS3BucketResult) {
        super.onPostExecute(commonS3BucketResult);

        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(commonS3BucketResult);
            } else {
                mCallBack.onFailure(mException);
            }
        }
    }
}
