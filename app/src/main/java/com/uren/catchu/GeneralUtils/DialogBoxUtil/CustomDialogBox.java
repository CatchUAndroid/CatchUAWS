package com.uren.catchu.GeneralUtils.DialogBoxUtil;


import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.opengl.Visibility;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.DataModelUtil.GroupDataUtil;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.CustomDialogListener;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.CustomDialogReturnListener;
import com.uren.catchu.R;

import catchu.model.GroupRequestResultResultArrayItem;
import catchu.model.User;


public class CustomDialogBox {

    private CustomDialogBox(CustomDialogBox.Builder builder) {
        String title = builder.title;
        String message = builder.message;
        Activity activity = builder.activity;
        CustomDialogListener pListener = builder.pListener;
        CustomDialogListener nListener = builder.nListener;
        CustomDialogReturnListener returnListener = builder.returnListener;
        int pBtnColor = builder.pBtnColor;
        int nBtnColor = builder.nBtnColor;
        int pBtnVisibleType = builder.pBtnVisibleType;
        int nBtnVisibleType = builder.nBtnVisibleType;
        String positiveBtnText = builder.positiveBtnText;
        String negativeBtnText = builder.negativeBtnText;
        int editTextVisibleType = builder.editTextVisibleType;
        User user = builder.user;
        GroupRequestResultResultArrayItem group = builder.group;
        boolean cancel = builder.cancel;
        long durationTime = builder.durationTime;
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
        private int editTextVisibleType;
        private Activity activity;
        private CustomDialogListener pListener;
        private CustomDialogListener nListener;
        private CustomDialogReturnListener returnListener;
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

        public CustomDialogBox.Builder setEditTextVisibility(int visibleType) {
            this.editTextVisibleType = visibleType;
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

        public CustomDialogBox.Builder OnReturnListenerSet(CustomDialogReturnListener listener) {
            this.returnListener = listener;
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
            final Dialog dialog = new Dialog(this.activity);
            dialog.requestWindowFeature(1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setCancelable(this.cancel);
            dialog.setContentView(R.layout.layout_custom_dialog_box);
            TextView title1 = dialog.findViewById(R.id.title);
            TextView message1 = dialog.findViewById(R.id.message);
            TextView shortUserNameTv = dialog.findViewById(R.id.shortUserNameTv);
            ImageView profilePicImgView = dialog.findViewById(R.id.profilePicImgView);
            TextView usernameTextView = dialog.findViewById(R.id.usernameTextView);
            Button nBtn = dialog.findViewById(R.id.negativeBtn);
            Button pBtn = dialog.findViewById(R.id.positiveBtn);
            RelativeLayout relativelayout1 = dialog.findViewById(R.id.relativelayout1);
            EditText editText = dialog.findViewById(R.id.editText);
            View buttonsView = dialog.findViewById(R.id.buttonsView);

            nBtn.setVisibility(nBtnVisibleType);
            pBtn.setVisibility(pBtnVisibleType);
            editText.setVisibility(editTextVisibleType);

            if(nBtnVisibleType == View.GONE || pBtnVisibleType == View.GONE)
                buttonsView.setVisibility(View.GONE);

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
                        user.getName(), user.getUsername(), shortUserNameTv, profilePicImgView, false);
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
                pBtn.setOnClickListener(view -> {

                    if (Builder.this.returnListener != null &&
                            editText.getText() != null && editText.getText().toString() != null &&
                            !editText.getText().toString().isEmpty()) {
                        Builder.this.returnListener.OnReturn(editText.getText().toString());
                        dialog.dismiss();
                    } else {
                        Builder.this.pListener.OnClick();
                        dialog.dismiss();
                    }
                });
            } else {
                pBtn.setOnClickListener(view -> dialog.dismiss());
            }

            if (this.nListener != null) {
                nBtn.setVisibility(View.VISIBLE);
                nBtn.setOnClickListener(view -> {
                    Builder.this.nListener.OnClick();
                    dialog.dismiss();
                });
            }

            dialog.show();

            if (this.durationTime > 0) {
                new Handler().postDelayed(() -> {
                    if (dialog.isShowing())
                        dialog.dismiss();
                }, this.durationTime);
            }
            return new CustomDialogBox(this);
        }
    }
}
