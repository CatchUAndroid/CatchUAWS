package com.uren.catchu.GeneralUtils.DialogBoxUtil;


import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.DataModelUtil.GroupDataUtil;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.CustomDialogListener;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.GifDialogListener;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.R;

import catchu.model.GroupRequestResultResultArrayItem;
import catchu.model.User;


public class CustomDialogBox {
    private String title;
    private String message;
    private String positiveBtnText;
    private String negativeBtnText;
    private int pBtnColor;
    private int nBtnColor;
    private int pBtnVisibleType;
    private int nBtnVisibleType;
    private Activity activity;
    private CustomDialogListener pListener;
    private CustomDialogListener nListener;
    private boolean cancel;
    private User user;
    private GroupRequestResultResultArrayItem group;
    private long durationTime;

    private CustomDialogBox(CustomDialogBox.Builder builder) {
        this.title = builder.title;
        this.message = builder.message;
        this.activity = builder.activity;
        this.pListener = builder.pListener;
        this.nListener = builder.nListener;
        this.pBtnColor = builder.pBtnColor;
        this.nBtnColor = builder.nBtnColor;
        this.pBtnVisibleType = builder.pBtnVisibleType;
        this.nBtnVisibleType = builder.nBtnVisibleType;
        this.positiveBtnText = builder.positiveBtnText;
        this.negativeBtnText = builder.negativeBtnText;
        this.user = builder.user;
        this.group = builder.group;
        this.cancel = builder.cancel;
        this.durationTime = builder.durationTime;
    }

    public static class Builder {
        private String title;
        private String message;
        private String positiveBtnText;
        private String negativeBtnText;
        private int pBtnColor;
        private int nBtnColor;
        private int pBtnVisibleType;
        private int nBtnVisibleType;
        private Activity activity;
        private CustomDialogListener pListener;
        private CustomDialogListener nListener;
        private boolean cancel;
        private User user;
        private GroupRequestResultResultArrayItem group;
        private long durationTime;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public CustomDialogBox.Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public CustomDialogBox.Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public CustomDialogBox.Builder setPositiveBtnText(String positiveBtnText) {
            this.positiveBtnText = positiveBtnText;
            return this;
        }

        public CustomDialogBox.Builder setPositiveBtnBackground(int pBtnColor) {
            this.pBtnColor = pBtnColor;
            return this;
        }

        public CustomDialogBox.Builder setNegativeBtnText(String negativeBtnText) {
            this.negativeBtnText = negativeBtnText;
            return this;
        }

        public CustomDialogBox.Builder setNegativeBtnBackground(int nBtnColor) {
            this.nBtnColor = nBtnColor;
            return this;
        }

        public CustomDialogBox.Builder setPositiveBtnVisibility(int visibleType) {
            this.pBtnVisibleType = visibleType;
            return this;
        }

        public CustomDialogBox.Builder setNegativeBtnVisibility(int visibleType) {
            this.nBtnVisibleType = visibleType;
            return this;
        }

        public CustomDialogBox.Builder setDurationTime(long durationTime) {
            this.durationTime = durationTime;
            return this;
        }

        public CustomDialogBox.Builder OnPositiveClicked(CustomDialogListener pListener) {
            this.pListener = pListener;
            return this;
        }

        public CustomDialogBox.Builder OnNegativeClicked(CustomDialogListener nListener) {
            this.nListener = nListener;
            return this;
        }

        public CustomDialogBox.Builder isCancellable(boolean cancel) {
            this.cancel = cancel;
            return this;
        }

        public CustomDialogBox.Builder setUser(User user) {
            this.user = user;
            return this;
        }

        public CustomDialogBox.Builder setGroup(GroupRequestResultResultArrayItem group) {
            this.group = group;
            return this;
        }

        public CustomDialogBox build() {
            try {
                final Dialog dialog = new Dialog(this.activity);
                dialog.requestWindowFeature(1);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.setCancelable(this.cancel);
                dialog.setContentView(R.layout.layout_custom_dialog_box);
                TextView title1 = (TextView) dialog.findViewById(R.id.title);
                TextView message1 = (TextView) dialog.findViewById(R.id.message);
                TextView shortUserNameTv = dialog.findViewById(R.id.shortUserNameTv);
                ImageView profilePicImgView = dialog.findViewById(R.id.profilePicImgView);
                TextView usernameTextView = dialog.findViewById(R.id.usernameTextView);
                Button nBtn = (Button) dialog.findViewById(R.id.negativeBtn);
                Button pBtn = (Button) dialog.findViewById(R.id.positiveBtn);
                RelativeLayout relativelayout1 = dialog.findViewById(R.id.relativelayout1);

                nBtn.setVisibility(nBtnVisibleType);
                pBtn.setVisibility(pBtnVisibleType);

                if (message != null && !message.isEmpty())
                    message1.setText(this.message);
                else
                    message1.setVisibility(View.GONE);

                if (title != null && !title.isEmpty())
                    title1.setText(this.title);
                else
                    title1.setVisibility(View.GONE);

                if (user != null) {
                    UserDataUtil.setProfilePicture(this.activity, user.getProfilePhotoUrl(),
                            user.getName(), user.getUsername(), shortUserNameTv, profilePicImgView);
                    UserDataUtil.setNameOrUserName(user.getName(), user.getUsername(), usernameTextView);
                } else if (group != null) {
                    GroupDataUtil.setGroupPicture(this.activity, group.getGroupPhotoUrl(),
                            group.getName(), shortUserNameTv, profilePicImgView);

                    if (group.getName() != null && !group.getName().isEmpty())
                        usernameTextView.setText(group.getName());
                    else
                        usernameTextView.setVisibility(View.GONE);
                } else
                    relativelayout1.setVisibility(View.GONE);

                if (pBtnColor != 0) {
                    GradientDrawable bgShape = (GradientDrawable) pBtn.getBackground();
                    bgShape.setColor(pBtnColor);
                }
                if (nBtnColor != 0) {
                    GradientDrawable bgShape = (GradientDrawable) nBtn.getBackground();
                    bgShape.setColor(nBtnColor);
                }

                if (this.positiveBtnText != null) {
                    pBtn.setText(this.positiveBtnText);
                }

                if (this.negativeBtnText != null) {
                    nBtn.setText(this.negativeBtnText);
                }

                if (this.pListener != null) {
                    pBtn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            Builder.this.pListener.OnClick();
                            dialog.dismiss();
                        }
                    });
                } else {
                    pBtn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }

                if (this.nListener != null) {
                    nBtn.setVisibility(View.VISIBLE);
                    nBtn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            Builder.this.nListener.OnClick();
                            dialog.dismiss();
                        }
                    });
                }

                dialog.show();

                if (this.durationTime > 0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog.isShowing())
                                dialog.dismiss();
                        }
                    }, this.durationTime);
                }
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(this.activity, CustomDialogBox.class.getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.getMessage());
                e.printStackTrace();
            }
            return new CustomDialogBox(this);
        }
    }
}
