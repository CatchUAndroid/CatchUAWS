package com.uren.catchu.ApiGatewayFunctions;

import android.os.AsyncTask;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;

import catchu.model.BaseResponse;
import catchu.model.BucketUploadResponse;
import catchu.model.Report;

import static com.uren.catchu.Constants.NumericConstants.RESPONSE_OK;

public class ReportProblemProcess extends AsyncTask<Void, Void, BaseResponse> {

    OnEventListener<BaseResponse> mCallBack;
    Exception mException;
    String token;
    String userid;
    Report report;
    String relatedId;

    public ReportProblemProcess(OnEventListener callback, String userid, String token, Report report, String relatedId) {
        this.mCallBack = callback;
        this.token = token;
        this.userid = userid;
        this.report = report;
        this.relatedId = relatedId;
    }

    @Override
    protected BaseResponse doInBackground(Void... voids) {
        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {
            BaseResponse baseResponse = instance.client.commonReportPost(userid, token, report, relatedId);

            if (baseResponse.getError().getCode().intValue() == RESPONSE_OK) {
                return baseResponse;
            } else {
                return null;
            }

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