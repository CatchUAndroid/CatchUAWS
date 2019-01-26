package com.uren.catchu.GeneralUtils.DialogBoxUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.uren.catchu.Adapters.CustomListAdapter;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.CustomDialogListener;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.PhotoChosenCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.PhotoChosenForReportCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.PhotoChosenForShareCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.PostSettingsChoosenCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.VideoChosenForShareCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.YesNoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;

import catchu.model.Post;
import catchu.model.User;

import static com.uren.catchu.Constants.NumericConstants.CODE_CAMERA_POSITION;
import static com.uren.catchu.Constants.NumericConstants.CODE_GALLERY_POSITION;
import static com.uren.catchu.Constants.NumericConstants.CODE_PHOTO_EDIT;
import static com.uren.catchu.Constants.NumericConstants.CODE_PHOTO_REMOVE;
import static com.uren.catchu.Constants.NumericConstants.CODE_PLAY_VIDEO;
import static com.uren.catchu.Constants.NumericConstants.CODE_SCREENSHOT_POSITION;
import static com.uren.catchu.Constants.NumericConstants.CODE_VIDEO_REMOVE;
import static com.uren.catchu.Constants.NumericConstants.REQUEST_CODE_ENABLE_LOCATION;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_FOLLOW;

public class DialogBoxUtil {

    public static void photoChosenDialogBox(Context context, String title, boolean photoExist, final PhotoChosenCallback photoChosenCallback) {
        try {
            CommonUtils.hideKeyBoard(context);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
            adapter.add("  " + context.getResources().getString(R.string.openGallery));
            adapter.add("  " + context.getResources().getString(R.string.openCamera));

            if (photoExist)
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
                    else if (item == CODE_PHOTO_REMOVE) {
                        photoChosenCallback.onPhotoRemoved();
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, DialogBoxUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public static void photoChosenForProblemReportDialogBox(Context context, String title, final PhotoChosenForReportCallback photoChosenForReportCallback) {
        try {
            CommonUtils.hideKeyBoard(context);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
            adapter.add("  " + context.getResources().getString(R.string.openGallery));
            adapter.add("  " + context.getResources().getString(R.string.TAKE_SCREENSHOT));

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            if (title != null && !title.isEmpty())
                builder.setTitle(title);

            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (item == CODE_GALLERY_POSITION)
                        photoChosenForReportCallback.onGallerySelected();
                    else if (item == CODE_SCREENSHOT_POSITION)
                        photoChosenForReportCallback.onScreenShot();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, DialogBoxUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public static void photoChosenForShareDialogBox(Context context, boolean photoExist, final PhotoChosenForShareCallback callback) {
        try {
            CommonUtils.hideKeyBoard(context);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
            adapter.add("  " + context.getResources().getString(R.string.openGallery));
            adapter.add("  " + context.getResources().getString(R.string.openCamera));

            if (photoExist) {
                adapter.add("  " + context.getResources().getString(R.string.REMOVE_PHOTO));
                adapter.add("  " + context.getResources().getString(R.string.EDIT));
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (item == CODE_GALLERY_POSITION)
                        callback.onGallerySelected();
                    else if (item == CODE_CAMERA_POSITION)
                        callback.onCameraSelected();
                    else if (item == CODE_PHOTO_REMOVE) {
                        callback.onPhotoRemoved();
                    } else if (item == CODE_PHOTO_EDIT) {
                        callback.onEditted();
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, DialogBoxUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public static void videoChosenForShareDialogBox(Context context, boolean videoExist, final VideoChosenForShareCallback callback) {
        try {
            CommonUtils.hideKeyBoard(context);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
            adapter.add("  " + context.getResources().getString(R.string.openGallery));
            adapter.add("  " + context.getResources().getString(R.string.openCamera));

            if (videoExist) {
                adapter.add("  " + context.getResources().getString(R.string.REMOVE_VIDEO));
                adapter.add("  " + context.getResources().getString(R.string.PLAY));
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (item == CODE_GALLERY_POSITION)
                        callback.onGallerySelected();
                    else if (item == CODE_CAMERA_POSITION)
                        callback.onCameraSelected();
                    else if (item == CODE_VIDEO_REMOVE) {
                        callback.onVideoRemoved();
                    } else if (item == CODE_PLAY_VIDEO) {
                        callback.onPlayed();
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, DialogBoxUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public static void showErrorDialog(Context context, String errMessage, final InfoDialogBoxCallback infoDialogBoxCallback) {
        try {
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
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, DialogBoxUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public static void showInfoDialogBox(Context context, String message, String title, final InfoDialogBoxCallback infoDialogBoxCallback) {
        try {
            CommonUtils.hideKeyBoard(context);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(context.getResources().getDrawable(R.drawable.info_icon_512, null));
            builder.setMessage(message);

            if (title != null && !title.trim().isEmpty())
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
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, DialogBoxUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public static void showSuccessDialogBox(Context context, String message, String title, final InfoDialogBoxCallback infoDialogBoxCallback) {
        try {
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
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, DialogBoxUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public static void showYesNoDialog(Context context, String title, String message, final YesNoDialogBoxCallback yesNoDialogBoxCallback) {
        try {
            CommonUtils.hideKeyBoard(context);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(R.drawable.warning_icon40);
            builder.setMessage(message);
            builder.setCancelable(false);

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
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, DialogBoxUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public static void removeFromFollowingsDialog(Context context, User user, final YesNoDialogBoxCallback yesNoDialogBoxCallback) {
        new CustomDialogBox.Builder((Activity) context)
                .setMessage(context.getResources().getString(R.string.ASKING_STOP_FOLLOWING))
                .setTitle(context.getResources().getString(R.string.cancel_following))
                .setUser(user)
                .setNegativeBtnVisibility(View.VISIBLE)
                .setNegativeBtnText(context.getResources().getString(R.string.upperNo))
                .setNegativeBtnBackground(context.getResources().getColor(R.color.Silver, null))
                .setPositiveBtnVisibility(View.VISIBLE)
                .setPositiveBtnText(context.getResources().getString(R.string.upperYes))
                .setPositiveBtnBackground(context.getResources().getColor(R.color.DodgerBlue, null))
                .setDurationTime(0)
                .isCancellable(true)
                .OnPositiveClicked(new CustomDialogListener() {
                    @Override
                    public void OnClick() {
                        yesNoDialogBoxCallback.yesClick();
                    }
                })
                .OnNegativeClicked(new CustomDialogListener() {
                    @Override
                    public void OnClick() {
                        yesNoDialogBoxCallback.noClick();
                    }
                }).build();
    }

    public static void showInfoDialogWithLimitedTime(Context context, String title, String message, long timeInMs, final InfoDialogBoxCallback infoDialogBoxCallback) {
        try {
            CommonUtils.hideKeyBoard(context);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            if (title != null && !title.isEmpty())
                builder.setTitle(title);

            builder.setIcon(R.drawable.success_icon_480);
            builder.setMessage(message);
            builder.setCancelable(false);
            final AlertDialog alert = builder.create();
            alert.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    alert.dismiss();
                    infoDialogBoxCallback.okClick();
                }
            }, timeInMs);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, DialogBoxUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public static void showSettingsAlert(final Activity act) {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(act);
            alertDialog.setTitle(act.getResources().getString(R.string.gpsSettings));
            alertDialog.setMessage(act.getResources().getString(R.string.gpsSettingMessage));
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton(act.getResources().getString(R.string.settings), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    act.startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CODE_ENABLE_LOCATION);
                }
            });
            alertDialog.show();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(act, DialogBoxUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public static void showDialogWithJustPositiveButton(Context context, String title,
                                                        String message, String buttonDesc, final InfoDialogBoxCallback infoDialogBoxCallback) {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle(title);
            alertDialog.setMessage(message);
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton(buttonDesc, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    infoDialogBoxCallback.okClick();
                }
            });
            alertDialog.show();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, DialogBoxUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public static void postSettingsDialogBox(final Activity activity, final Context context, Post post, final PostSettingsChoosenCallback postSettingsChoosenCallback) {

        try {
            CommonUtils.hideKeyBoard(context);
            final ArrayList<String> myList = getItemList(context, post);

            LayoutInflater inflater = activity.getLayoutInflater();
            View convertView = (View) inflater.inflate(R.layout.post_settings_list_items, null);

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setView(convertView);

            ListView listView = (ListView) convertView.findViewById(R.id.mylistview);

            final AlertDialog alert = alertDialog.create();
            final CustomListAdapter myadapter = new CustomListAdapter(context, R.layout.list_view_item, myList);

            listView.setAdapter(myadapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                    Resources resources = context.getResources();
                    String selectedItem = myList.get(position);

                    if (selectedItem.equals(resources.getString(R.string.report))) {
                        String message = context.getResources().getString(R.string.reportPostMessage);
                        DialogBoxUtil.showYesNoDialog(context, null, message, new YesNoDialogBoxCallback() {
                            @Override
                            public void yesClick() {
                                postSettingsChoosenCallback.onReportSelected();
                            }

                            @Override
                            public void noClick() {
                            }
                        });
                    } else if (selectedItem.equals(resources.getString(R.string.unfollow)) ||
                            selectedItem.equals(resources.getString(R.string.follow))) {
                        postSettingsChoosenCallback.onUnFollowSelected();
                    } else if (selectedItem.equals(resources.getString(R.string.disableComment)) ||
                            selectedItem.equals(resources.getString(R.string.enableComment))) {
                        postSettingsChoosenCallback.onDisableCommentSelected();
                    } else if (selectedItem.equals(resources.getString(R.string.delete))) {
                        String message = context.getResources().getString(R.string.deleteThisPost);
                        DialogBoxUtil.showYesNoDialog(context, null, message, new YesNoDialogBoxCallback() {
                            @Override
                            public void yesClick() {
                                postSettingsChoosenCallback.onDeletePostSelected();
                            }

                            @Override
                            public void noClick() {
                            }
                        });

                    }

                    alert.cancel();
                }
            });

            // show dialog
            alert.show();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, DialogBoxUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }

    }

    private static ArrayList<String> getItemList(Context context, Post post) {

        ArrayList<String> myList = new ArrayList<String>();

        try {
            /**Report*/
            if (!post.getUser().getUserid().equals(AccountHolderInfo.getUserID())) {
                myList.add(context.getResources().getString(R.string.report));
            }

            /**Follow*/
            //todo NT - follow statüsüne göre buton eklenecek. Su anda posttan user follow statüsü gelmiyor
            String followElement = getFollowElement(context, post);
            if (!followElement.equals("notValid")) {
                myList.add(followElement);
            }

            /**Disable comment - if post is mine**/
            if (post.getUser().getUserid().equals(AccountHolderInfo.getUserID())) {
                if (post.getIsCommentAllowed()) {
                    myList.add(context.getResources().getString(R.string.disableComment));
                } else {
                    myList.add(context.getResources().getString(R.string.enableComment));
                }
            }

            /**Delete*/
            if (post.getUser().getUserid().equals(AccountHolderInfo.getUserID())) {
                myList.add(context.getResources().getString(R.string.delete));
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, DialogBoxUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }

        return myList;
    }

    private static String getFollowElement(Context context, Post post) {

        String followElement = "notValid";
        String currentFollowStatus = post.getUser().getFollowStatus();
 /*
 if(currentFollowStatus.equals(FOLLOW_STATUS_FOLLOWING)){
 followElement = context.getResources().getString(R.string.unfollow);
 }else if(currentFollowStatus.equals(FOLLOW_STATUS_PENDING)){
 followElement = "notValid";
 } else if(currentFollowStatus.equals(FOLLOW_STATUS_OWN)){
 followElement = "notValid";
 }else if(currentFollowStatus.equals(FOLLOW_STATUS_NONE)){
 followElement = context.getResources().getString(R.string.follow);
 }
 */
        return followElement;

    }


}
