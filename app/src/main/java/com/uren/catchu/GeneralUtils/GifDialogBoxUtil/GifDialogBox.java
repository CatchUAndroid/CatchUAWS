package com.uren.catchu.GeneralUtils.GifDialogBoxUtil;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.R;

import pl.droidsonroids.gif.GifImageView;

public class GifDialogBox {
    private String title;
    private String message;
    private String positiveBtnText;
    private String negativeBtnText;
    private int pBtnColor;
    private int nBtnColor;
    private int nBtnVisibleType;
    private Activity activity;
    private GifDialogListener pListener;
    private GifDialogListener nListener;
    private boolean cancel;
    int gifImageResource;

    private GifDialogBox(GifDialogBox.Builder builder) {
        this.title = builder.title;
        this.message = builder.message;
        this.activity = builder.activity;
        this.pListener = builder.pListener;
        this.nListener = builder.nListener;
        this.pBtnColor = builder.pBtnColor;
        this.nBtnColor = builder.nBtnColor;
        this. nBtnVisibleType = builder.nBtnVisibleType;
        this.positiveBtnText = builder.positiveBtnText;
        this.negativeBtnText = builder.negativeBtnText;
        this.gifImageResource = builder.gifImageResource;
        this.cancel = builder.cancel;
    }

    public static class Builder {
        private String title;
        private String message;
        private String positiveBtnText;
        private String negativeBtnText;
        private int pBtnColor;
        private int nBtnColor;
        private int nBtnVisibleType;
        private Activity activity;
        private GifDialogListener pListener;
        private GifDialogListener nListener;
        private boolean cancel;
        int gifImageResource;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public GifDialogBox.Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public GifDialogBox.Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public GifDialogBox.Builder setPositiveBtnText(String positiveBtnText) {
            this.positiveBtnText = positiveBtnText;
            return this;
        }

        public GifDialogBox.Builder setPositiveBtnBackground(int pBtnColor) {
            this.pBtnColor = pBtnColor;
            return this;
        }

        public GifDialogBox.Builder setNegativeBtnText(String negativeBtnText) {
            this.negativeBtnText = negativeBtnText;
            return this;
        }

        public GifDialogBox.Builder setNegativeBtnBackground(int nBtnColor) {
            this.nBtnColor = nBtnColor;
            return this;
        }

        public GifDialogBox.Builder setNegativeBtnVisibility(int visibleType) {
            this.nBtnVisibleType = visibleType;
            return this;
        }

        public GifDialogBox.Builder OnPositiveClicked(GifDialogListener pListener) {
            this.pListener = pListener;
            return this;
        }

        public GifDialogBox.Builder OnNegativeClicked(GifDialogListener nListener) {
            this.nListener = nListener;
            return this;
        }

        public GifDialogBox.Builder isCancellable(boolean cancel) {
            this.cancel = cancel;
            return this;
        }

        public GifDialogBox.Builder setGifResource(int gifImageResource) {
            this.gifImageResource = gifImageResource;
            return this;
        }

        public GifDialogBox build() {
            final Dialog dialog = new Dialog(this.activity);
            dialog.requestWindowFeature(1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setCancelable(this.cancel);
            dialog.setContentView(R.layout.layout_gif_dialog_box);
            TextView title1 = (TextView)dialog.findViewById(R.id.titleTv);
            TextView message1 = (TextView)dialog.findViewById(R.id.messageTv);
            Button nBtn = (Button)dialog.findViewById(R.id.cancelBtn);
            Button pBtn = (Button)dialog.findViewById(R.id.okBtn);
            GifImageView gifImageView = (GifImageView)dialog.findViewById(R.id.gifImgv);
            RelativeLayout mainLayout = (RelativeLayout) dialog.findViewById(R.id.mainLayout);
            mainLayout.setBackground(ShapeUtil.getShape(this.activity.getResources().getColor(R.color.White, null),
                    0, GradientDrawable.RECTANGLE, 20, 0));
            gifImageView.setImageResource(this.gifImageResource);
            nBtn.setVisibility(nBtnVisibleType);
            title1.setText(this.title);
            message1.setText(this.message);

            if (this.positiveBtnText != null) {
                pBtn.setText(this.positiveBtnText);
            }

            if (this.negativeBtnText != null) {
                nBtn.setText(this.negativeBtnText);
            }

            if (this.pBtnColor != 0) {
                pBtn.setBackground(ShapeUtil.getShape(pBtnColor, 0, GradientDrawable.RECTANGLE, 20, 0));
            }

            if (this.nBtnColor != 0) {
                nBtn.setBackground(ShapeUtil.getShape(nBtnColor, 0, GradientDrawable.RECTANGLE, 20, 0));
            }

            if (this.pListener != null) {
                pBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        GifDialogBox.Builder.this.pListener.OnClick();
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
                        GifDialogBox.Builder.this.nListener.OnClick();
                        dialog.dismiss();
                    }
                });
            }

            dialog.show();
            return new GifDialogBox(this);
        }
    }
}
