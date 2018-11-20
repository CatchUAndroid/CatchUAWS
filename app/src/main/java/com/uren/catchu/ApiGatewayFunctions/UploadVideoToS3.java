package com.uren.catchu.ApiGatewayFunctions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UploadVideoToS3 extends AsyncTask<Void, Void, HttpURLConnection> {

    private OnEventListener<HttpURLConnection> mCallBack;
    public Exception mException;
    public String uploadUrl;
    public String realPath;

    public UploadVideoToS3(OnEventListener callback, String uploadUrl, String realPath) {
        this.mCallBack = callback;
        this.uploadUrl = uploadUrl;
        this.realPath = realPath;
    }

    @Override
    protected HttpURLConnection doInBackground(Void... voids) {

        HttpURLConnection connection = null;
        int bytesAvailable, bufferSize, bytesRead, serverResponseCode;
        int maxBufferSize = 1 * 1024 * 1024;
        byte[] buffer;

        try {
            File sourceFile = new File(realPath);

            if (!sourceFile.isFile())
                return null;

            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(uploadUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "video/mp4");
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // Responses from the server (code and message)
            serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();

            Log.i("Upload file to server", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
            // close streams
            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (MalformedURLException e) {
            mException = e;
            e.printStackTrace();
            Log.e("Upload file to server", "error: " + e.getMessage(), e);
        } catch (Exception e) {
            mException = e;
            e.printStackTrace();
            e.printStackTrace();
        }

        //this block will give the response of upload link
        /*try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection
                    .getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                Log.i("Huzza", "RES Message: " + line);
            }
            rd.close();
        } catch (IOException e) {
            mException = e;
            e.printStackTrace();
            Log.e("Huzza", "error: " + e.getMessage(), e);
        }*/
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