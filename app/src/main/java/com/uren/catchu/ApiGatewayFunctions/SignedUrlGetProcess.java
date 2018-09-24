package com.uren.catchu.ApiGatewayFunctions;

import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import catchu.model.BucketUploadResponse;

import static com.uren.catchu.Constants.StringConstants.JPG_TYPE;
import static com.uren.catchu.Constants.StringConstants.MP4_TYPE;

public class SignedUrlGetProcess extends AsyncTask<Void, Void, BucketUploadResponse> {

    private OnEventListener<BucketUploadResponse> mCallBack;
    public Exception mException;
    public String imageExtensionType;
    public String videoExtensionType;
    public String imageCount;
    public String videoCount;

    public SignedUrlGetProcess(OnEventListener callback,int imageCount, int videoCount) {
        this.imageExtensionType = JPG_TYPE;
        this.videoExtensionType = MP4_TYPE;
        this.mCallBack = callback;
        this.imageCount = Integer.toString(imageCount);
        this.videoCount = Integer.toString(videoCount);
    }

    @Override
    protected BucketUploadResponse doInBackground(Void... voids) {
        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            BucketUploadResponse commonS3BucketResult = instance.client.commonSignedurlGet(imageCount, videoCount, videoExtensionType, imageExtensionType);
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
    protected void onPostExecute(BucketUploadResponse commonS3BucketResult) {
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
