package com.uren.catchu.ApiGatewayFunctions;


import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;

import catchu.model.BaseResponse;
import catchu.model.BucketUploadResponse;

import static com.uren.catchu.Constants.NumericConstants.RESPONSE_OK;
import static com.uren.catchu.Constants.StringConstants.JPG_TYPE;
import static com.uren.catchu.Constants.StringConstants.MP4_TYPE;

public class SignedUrlDeleteProcess extends AsyncTask<Void, Void, BaseResponse> {

    OnEventListener<BaseResponse> mCallBack;
    Exception mException;
    String token;
    String userid;
    BucketUploadResponse bucketUploadResponse ;

    public SignedUrlDeleteProcess(OnEventListener callback,String userid, String token, BucketUploadResponse bucketUploadResponse) {
        this.mCallBack = callback;
        this.token = token;
        this.userid = userid;
        this.bucketUploadResponse = bucketUploadResponse;
    }

    @Override
    protected BaseResponse doInBackground(Void... voids) {
        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            BaseResponse baseResponse = instance.client.commonSignedurlDelete(userid, token, bucketUploadResponse);

            if (baseResponse.getError().getCode().intValue() == RESPONSE_OK)
                return baseResponse;
            else
                return null;

        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
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
    protected void onPostExecute(BaseResponse baseResponse) {
        super.onPostExecute(baseResponse);

        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(baseResponse);
            } else {
                mCallBack.onFailure(mException);
            }
        }
    }
}
