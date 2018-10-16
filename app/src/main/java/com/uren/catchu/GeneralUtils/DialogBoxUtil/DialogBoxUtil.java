package com.uren.catchu.GeneralUtils.DialogBoxUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.Window;
import android.widget.ArrayAdapter;

import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.R;

import static com.uren.catchu.Constants.NumericConstants.CODE_CAMERA_POSITION;
import static com.uren.catchu.Constants.NumericConstants.CODE_GALLERY_POSITION;
import static com.uren.catchu.Constants.NumericConstants.CODE_PHOTO_REMOVE;

public class DialogBoxUtil {

    public static void photoChosenDialogBox(Context context, String title, boolean photoExist, final PhotoChosenCallback photoChosenCallback) {
        CommonUtils.hideKeyBoard(context);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
        adapter.add("  " + context.getResources().getString(R.string.openGallery));
        adapter.add("  " + context.getResources().getString(R.string.openCamera));

        if(photoExist)
            adapter.add("  " + context.getResources().getString(R.string.REMOVE_PHOTO));

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null && !title.isEmpty())
            builder.setTitle(title);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == CODE_GALLERY_POSITION)
                    photoChosenCallback.onGallerySelected();
                else if (item == CODE_CAMERA_POSITION)
                    photoChosenCallback.onCameraSelected();
                else if (item == CODE_PHOTO_REMOVE){
                    photoChosenCallback.onPhotoRemoved();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showErrorDialog(Context context, String errMessage, final InfoDialogBoxCallback infoDialogBoxCallback){
        CommonUtils.hideKeyBoard(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.errorUpper));
        builder.setIcon(context.getResources().getDrawable(R.drawable.toast_error_icon, null));
        builder.setMessage(errMessage);

        builder.setNeutralButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                infoDialogBoxCallback.okClick();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showInfoDialogBox(Context context, String message, String title, final InfoDialogBoxCallback infoDialogBoxCallback){
        CommonUtils.hideKeyBoard(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(context.getResources().getDrawable(R.drawable.info_icon_512, null));
        builder.setMessage(message);

        if(title != null && !title.trim().isEmpty())
            builder.setTitle(title);

        builder.setNeutralButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                infoDialogBoxCallback.okClick();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showSuccessDialogBox(Context context, String message, String title, final InfoDialogBoxCallback infoDialogBoxCallback){
        CommonUtils.hideKeyBoard(context);
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setIcon(R.drawable.success_icon_480);

        if (title != null && !title.isEmpty())
            alertDialog.setTitle(title);

        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, context.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        infoDialogBoxCallback.okClick();
                    }
                });
        alertDialog.show();
    }

    public static void showYesNoDialog(Context context, String title, String message, final YesNoDialogBoxCallback yesNoDialogBoxCallback) {
        CommonUtils.hideKeyBoard(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.warning_icon40);
        builder.setMessage(message);

        if (title != null && !title.isEmpty())
            builder.setTitle(title);

        builder.setPositiveButton(context.getResources().getString(R.string.upperYes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                yesNoDialogBoxCallback.yesClick();
            }
        });

        builder.setNegativeButton(context.getResources().getString(R.string.upperNo), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                yesNoDialogBoxCallback.noClick();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showInfoDialogWithLimitedTime(Context context, String title, String message, long timeInMs, final InfoDialogBoxCallback infoDialogBoxCallback){
        CommonUtils.hideKeyBoard(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if(title != null && !title.isEmpty())
            builder.setTitle(title);

        builder.setIcon(R.drawable.success_icon_480);
        builder.setMessage(message);
        final AlertDialog alert = builder.create();
        alert.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alert.dismiss();
                infoDialogBoxCallback.okClick();
            }
        }, timeInMs);
    }
}
