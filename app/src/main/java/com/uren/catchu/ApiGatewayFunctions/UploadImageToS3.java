package com.uren.catchu.ApiGatewayFunctions;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import catchu.model.CommonS3BucketResult;

public class UploadImageToS3 extends AsyncTask<Void, Void, HttpURLConnection> {

    private OnEventListener<HttpURLConnection> mCallBack;
    public Exception mException;
    public String urlString;
    public Bitmap bitmap;

    public UploadImageToS3(OnEventListener callback, Bitmap bitmap, String urlString) {
        this.mCallBack = callback;
        this.bitmap = bitmap;
        this.urlString = urlString;
    }

    @Override
    protected HttpURLConnection doInBackground(Void... voids) {

        HttpURLConnection connection = null;
        
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            //connection.setRequestProperty("Content-Type", "application/octet-stream");
            connection.setRequestProperty("Content-Type", "image/jpg");
            OutputStream output = connection.getOutputStream();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();

            InputStream input = new ByteArrayInputStream(b);
            byte[] buffer = new byte[4096];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            output.flush();

            InputStream is;

            Log.i("Info", "getResponseCode:" + connection.getResponseCode());

            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                Log.i("Info", "getInputStream error:" + is.toString());
            } else {
                is = connection.getErrorStream();
                Log.i("Info", "getErrorStream error:" + is.toString());
            }
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
