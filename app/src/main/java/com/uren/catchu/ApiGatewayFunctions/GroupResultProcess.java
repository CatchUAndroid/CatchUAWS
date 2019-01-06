package com.uren.catchu.ApiGatewayFunctions;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.Singleton.AccountHolderInfo;

import catchu.model.GroupRequest;
import catchu.model.GroupRequestResult;

import static com.uren.catchu.Constants.NumericConstants.RESPONSE_OK;

public class GroupResultProcess extends AsyncTask<Void, Void, GroupRequestResult> {

    private OnEventListener<GroupRequestResult> mCallBack;
    public Exception mException;
    public GroupRequest groupRequest;
    private String token;

    public GroupResultProcess(OnEventListener callback, GroupRequest groupRequest, String token) {
        this.mCallBack = callback;
        this.groupRequest = groupRequest;
        this.token = token;
    }

    @Override
    protected GroupRequestResult doInBackground(Void... voids) {

        SingletonApiClient instance = SingletonApiClient.getInstance();

        try {

            GroupRequestResult groupRequestResult = instance.client.groupsPost(token, groupRequest);

            if (groupRequestResult.getError().getCode().intValue() == RESPONSE_OK)
                return groupRequestResult;
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
    protected void onPostExecute(GroupRequestResult groupRequestResult) {
        super.onPostExecute(groupRequestResult);

        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(groupRequestResult);
            } else {
                mCallBack.onFailure(mException);
            }
        }
    }
}
