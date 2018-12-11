package com.uren.catchu.GeneralUtils.ProgressDialogUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;

import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
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
        try {
            progressDialog = new ProgressDialog(context);
            if (message != null && !message.trim().isEmpty())
                progressDialog.setMessage(message);
            else
                progressDialog.setMessage(context.getResources().getString(R.string.loading));
            progressDialog.setCancelable(cancelableValue);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        } catch (Resources.NotFoundException e) {
            ErrorSaveHelper.writeErrorToDB(null, ProgressDialogUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void dialogShow() {
        if (!progressDialog.isShowing()) progressDialog.show();
    }

    public void dialogDismiss() {
        if (progressDialog.isShowing()) progressDialog.dismiss();
    }
}
