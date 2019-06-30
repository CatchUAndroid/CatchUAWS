package com.uren.catchu.ApiGatewayFunctions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import androidx.core.graphics.BitmapCompat;
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


public class UploadImageToS3 extends AsyncTask<Void, Void, HttpURLConnection> {

    private OnEventListener<HttpURLConnection> mCallBack;
    public Exception mException;
    public String uploadUrl;
    public Bitmap bitmap;

    public UploadImageToS3(OnEventListener callback, Bitmap bitmap, String uploadUrl) {
        this.mCallBack = callback;
        this.bitmap = bitmap;
        this.uploadUrl = uploadUrl;
    }

    @Override
    protected HttpURLConnection doInBackground(Void... voids) {

        HttpURLConnection connection = null;
        
        try {
            URL url = new URL(uploadUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "image/jpg");
            OutputStream output = connection.getOutputStream();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] b = baos.toByteArray();

            InputStream input = new ByteArrayInputStream(b);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            output.flush();

            InputStream is;

            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
            } else {
                is = connection.getErrorStream();
            }
        } catch (Exception e) {
            mException = e;
            e.printStackTrace();
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
