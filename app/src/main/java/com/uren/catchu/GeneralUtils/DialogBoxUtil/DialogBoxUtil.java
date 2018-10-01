package com.uren.catchu.GeneralUtils.DialogBoxUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Window;
import android.widget.ArrayAdapter;

import com.uren.catchu.R;

import static com.uren.catchu.Constants.NumericConstants.CODE_CAMERA_POSITION;
import static com.uren.catchu.Constants.NumericConstants.CODE_GALLERY_POSITION;

public class DialogBoxUtil {

    public static void photoChosenDialogBox(Context context, String title, final PhotoChosenCallback photoChosenCallback) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
        adapter.add("  " + context.getResources().getString(R.string.openGallery));
        adapter.add("  " + context.getResources().getString(R.string.openCamera));
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null)
            builder.setTitle(title);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == CODE_GALLERY_POSITION)
                    photoChosenCallback.onGallerySelected();
                else if (item == CODE_CAMERA_POSITION)
                    photoChosenCallback.onCameraSelected();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showInfoDialogBox(Context context, String message, String title, final InfoDialogBoxCallback infoDialogBoxCallback){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setIcon(context.getResources().getDrawable(R.drawable.info_icon_512, null));

        if (title != null)
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

    public static void showSuccessDialogBox(Context context, String message, String title, final InfoDialogBoxCallback infoDialogBoxCallback){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setIcon(R.drawable.success_icon_480);

        if (title != null)
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

        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        builder.setIcon(R.drawable.warning_icon40);
        builder.setMessage(message);

        if (title != null)
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

    public static void showErrorDialog(Context context, String errMessage, final InfoDialogBoxCallback infoDialogBoxCallback){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle(context.getResources().getString(R.string.errorUpper));
        builder.setIcon(R.drawable.toast_error_icon);
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
}
