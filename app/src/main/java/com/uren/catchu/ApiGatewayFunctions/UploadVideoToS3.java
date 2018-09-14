package com.uren.catchu.ApiGatewayFunctions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UploadVideoToS3 extends AsyncTask<Void, Void, HttpURLConnection> {

    private OnEventListener<HttpURLConnection> mCallBack;
    public Exception mException;
    public String uploadUrl;
    public String mediaUrl;

    public UploadVideoToS3(OnEventListener callback, String uploadUrl, String mediaUrl) {
        this.mCallBack = callback;
        this.uploadUrl = uploadUrl;
        this.mediaUrl = mediaUrl;
    }

    @Override
    protected HttpURLConnection doInBackground(Void... voids) {

        HttpURLConnection connection = null;

        try {

        } catch (Exception e) {
            mException = e;
            e.printStackTrace();
            Log.e("error ", e.toString());
        }

        return connection;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (mCallBack != null) {
            mCallBack.onTaskContinue();
        }
    }

    @Override
    protected void onPostExecute(HttpURLConnection httpURLConnection) {
        super.onPostExecute(httpURLConnection);

        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(httpURLConnection);
            } else {
                mCallBack.onFailure(mException);
            }
        }
    }
}