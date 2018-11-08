package com.uren.catchu.GeneralUtils.ProgressDialogUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Message;

import com.uren.catchu.R;

public class ProgressDialogUtil {

    String message;
    Context context;
    boolean cancelableValue;
    ProgressDialog progressDialog;

    public ProgressDialogUtil(Context context, String message, boolean cancelableValue) {
        this.context = context;
        this.message = message;
        this.cancelableValue = cancelableValue;
        setProgressDialog();
    }

    public void setProgressDialog() {
        progressDialog = new ProgressDialog(context);
        if (message != null && !message.trim().isEmpty())
            progressDialog.setMessage(message);
        else
            progressDialog.setMessage(context.getResources().getString(R.string.loading));
        progressDialog.setCancelable(cancelableValue);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    public void dialogShow() {
        if (!progressDialog.isShowing()) progressDialog.show();
    }

    public void dialogDismiss() {
        if (progressDialog.isShowing()) progressDialog.dismiss();
    }
}
