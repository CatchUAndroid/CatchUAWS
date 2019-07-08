package com.uren.catchu.MainPackage.MainFragments.Profile.SettingsManagement.ReportProblem;


import android.content.Context;
import android.os.AsyncTask;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.ReportProblemProcess;
import com.uren.catchu.ApiGatewayFunctions.SignedUrlDeleteProcess;
import com.uren.catchu.ApiGatewayFunctions.SignedUrlGetProcess;
import com.uren.catchu.ApiGatewayFunctions.UploadImageToS3;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import catchu.model.BaseResponse;
import catchu.model.BucketUploadResponse;
import catchu.model.Media;
import catchu.model.Report;

import static com.uren.catchu.Constants.StringConstants.IMAGE_TYPE;
import static com.uren.catchu.Constants.StringConstants.REPORT_PLATFORM_ANDROID;
import static com.uren.catchu.Constants.StringConstants.REPORT_PROBLEM_TYPE_BUG;

public class SaveReportProblemProcess {

    Context context;
    CompleteCallback completeCallback;
    String userid;
    List<PhotoSelectUtil> photoSelectUtilList;
    BucketUploadResponse commonS3BucketResult;
    Report report = new Report();
    List<Media> mediaList = new ArrayList<>();
    String message;
    int imageCount = 0;
    int bucketIndex = 0;
    UploadImageToS3 uploadImageToS3 = null;
    SignedUrlGetProcess signedUrlGetProcess = null;
    private String reportType;

    public SaveReportProblemProcess(List<PhotoSelectUtil> photoSelectUtilList,
                                    String message, String userid, String reportType,  CompleteCallback completeCallback) {
        this.context = NextActivity.thisActivity;
        this.photoSelectUtilList = photoSelectUtilList;
        this.message = message;
        this.userid = userid;
        this.reportType = reportType;
        this.completeCallback = completeCallback;

        if (photoSelectUtilList != null)
            imageCount = photoSelectUtilList.size();

        if (imageCount > 0)
            saveReportImagesToS3();
        else
            saveReportProblem();
    }

    public void saveReportImagesToS3() {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startSaveReportImagesToS3(token);
            }

            @Override
            public void onTokenFail(String message) {
            }
        });
    }

    private void startSaveReportImagesToS3(String token) {

        signedUrlGetProcess = new SignedUrlGetProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                commonS3BucketResult = (BucketUploadResponse) object;
                saveImages();
            }

            @Override
            public void onFailure(Exception e) {
                completeCallback.onFailed(e);
                signedUrlGetProcess.cancel(true);
            }

            @Override
            public void onTaskContinue() {

            }
        }, imageCount, 0, token);

        signedUrlGetProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void saveImages() {
        if (bucketIndex < imageCount) {
            uploadImageToS3 = new UploadImageToS3(new OnEventListener() {
                @Override
                public void onSuccess(Object object) {

                    HttpURLConnection urlConnection = (HttpURLConnection) object;

                    try {
                        if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            Media media = new Media();
                            media.setExtension(commonS3BucketResult.getImages().get(bucketIndex).getExtension());
                            media.setType(IMAGE_TYPE);
                            media.setThumbnail(commonS3BucketResult.getImages().get(bucketIndex).getThumbnailUrl());
                            media.setUrl(commonS3BucketResult.getImages().get(bucketIndex).getDownloadUrl());
                            mediaList.add(media);
                            checkTaskCompleted();
                            bucketIndex++;
                            saveImages();
                        } else {
                            InputStream is = urlConnection.getErrorStream();
                            completeCallback.onFailed(new Exception(is.toString()));
                            deleteUploadedItems();
                            uploadImageToS3.cancel(true);
                        }
                    } catch (IOException e) {
                        completeCallback.onFailed(e);
                        deleteUploadedItems();
                        uploadImageToS3.cancel(true);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    completeCallback.onFailed(e);
                    deleteUploadedItems();
                    uploadImageToS3.cancel(true);
                }

                @Override
                public void onTaskContinue() {

                }
            }, photoSelectUtilList.get(bucketIndex).getBitmap(),
                    commonS3BucketResult.getImages().get(bucketIndex).getUploadUrl());
            uploadImageToS3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void checkTaskCompleted() {
        if ((bucketIndex + 1) == imageCount)
            saveReportProblem();
    }

    private void saveReportProblem() {
        report.setType(reportType);
        report.setMessage(message);
        report.setPlatform(REPORT_PLATFORM_ANDROID);

        if (mediaList != null && mediaList.size() > 0)
            report.setAttachments(mediaList);
        report.setIsFixed(false);

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startSaveReportProblem(token);
            }

            @Override
            public void onTokenFail(String message) {
            }
        });
    }

    private void startSaveReportProblem(String token) {

        ReportProblemProcess reportProblemProcess = new ReportProblemProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                BaseResponse baseResponse = (BaseResponse) object;

                if (baseResponse == null) {
                    completeCallback.onFailed(new Exception(context.getResources().getString(R.string.serverError)));
                    deleteUploadedItems();
                } else
                    completeCallback.onComplete(null);
            }

            @Override
            public void onFailure(Exception e) {
                completeCallback.onFailed(e);
                deleteUploadedItems();
            }

            @Override
            public void onTaskContinue() {

            }
        }, userid, token, report, "");
        reportProblemProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void deleteUploadedItems() {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startDeleteUploadedItems(token);
            }

            @Override
            public void onTokenFail(String message) {
            }
        });
    }

    public void startDeleteUploadedItems(String token) {
        if (commonS3BucketResult != null) {
            SignedUrlDeleteProcess signedUrlDeleteProcess = new SignedUrlDeleteProcess(new OnEventListener() {
                @Override
                public void onSuccess(Object object) {

                }

                @Override
                public void onFailure(Exception e) {

                }

                @Override
                public void onTaskContinue() {

                }
            }, userid, token, commonS3BucketResult);
            signedUrlDeleteProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }
}
