package com.uren.catchu.ApiGatewayFunctions;

import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;

import catchu.model.BucketUploadResponse;

import static com.uren.catchu.Constants.NumericConstants.RESPONSE_OK;
import static com.uren.catchu.Constants.StringConstants.JPG_TYPE;
import static com.uren.catchu.Constants.StringConstants.MP4_TYPE;

public class SignedUrlGetProcess extends AsyncTask<Void, Void, BucketUploadResponse> {

    private OnEventListener<BucketUploadResponse> mCallBack;
    public Exception mException;
    public String imageExtensionType;
    public String videoExtensionType;
    public String imageCount;
    public String videoCount;
    private String token;

    public SignedUrlGetProcess(OnEventListener callback,int imageCount, int videoCount, String token) {
        this.imageExtensionType = JPG_TYPE;
        this.videoExtensionType = MP4_TYPE;
        this.mCallBack = callback;
        this.imageCount = Integer.toString(imageCount);
        this.videoCount = Integer.toString(videoCount);
        this.token = token;
    }

    @Override
    protected BucketUploadResponse doInBackground(Void... voids) {
        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            BucketUploadResponse commonS3BucketResult = instance.client.commonSignedurlGet(token, imageCount, videoCount, videoExtensionType, imageExtensionType);

            if (commonS3BucketResult.getError().getCode().intValue() == RESPONSE_OK)
                return commonS3BucketResult;
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
