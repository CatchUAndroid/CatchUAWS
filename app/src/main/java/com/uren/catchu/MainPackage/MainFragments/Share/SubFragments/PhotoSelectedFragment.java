package com.uren.catchu.MainPackage.MainFragments.Share.SubFragments;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.uren.catchu.GeneralUtils.BitmapConversion;
import com.uren.catchu.GeneralUtils.BlurBuilder;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.LoginPackage.LoginActivity;
import com.uren.catchu.LoginPackage.RegisterActivity;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Share.Interfaces.BrushCompleteCallback;
import com.uren.catchu.R;
import com.uren.catchu.MainPackage.MainFragments.Share.Interfaces.TextCompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Share.Interfaces.TrashDragDropCallback;
import com.uren.catchu.MainPackage.MainFragments.Share.Utils.TrashDragListener;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class PhotoSelectedFragment extends BaseFragment {

    @BindView(R.id.selectedImageView)
    ImageView selectedImageView;
    @BindView(R.id.addTextImgv)
    ImageView addTextImgv;
    @BindView(R.id.brushImgv)
    ImageView brushImgv;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.seekbar)
    SeekBar seekbar;
    @BindView(R.id.finishButton)
    Button finishButton;
    @BindView(R.id.photoRelLayout)
    RelativeLayout photoRelLayout;
    @BindView(R.id.addPropRelLayout)
    RelativeLayout addPropRelLayout;
    @BindView(R.id.seekbarLayout)
    FrameLayout seekbarLayout;
    @BindView(R.id.cleanImgv)
    ImageView cleanImgv;

    View mView;
    PhotoSelectUtil thisPhotoSelectUtil;
    ReturnCallback returnCallback;
    View trashLayout = null;

    public PhotoSelectedFragment(PhotoSelectUtil photoSelectUtil, ReturnCallback returnCallback) {
        this.thisPhotoSelectUtil = photoSelectUtil;
        this.returnCallback = returnCallback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_photo_selected, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            addListeners();
            setSelectedPhoto();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    public void initVariables() {
        setShapes();
    }

    private void setShapes() {
        finishButton.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.Black, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 15, 2));
        addTextImgv.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.Black, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 2));
        brushImgv.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.Black, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 2));
        cleanImgv.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.Black, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 2));
        seekbarLayout.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.transparentBlack, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 2));
    }

    private void addListeners() {
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkTextIsAddedOrNot();
                returnCallback.onReturn(thisPhotoSelectUtil);
                getActivity().onBackPressed();
            }
        });

        cleanImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thisPhotoSelectUtil.setScreeanShotBitmap(null);
                setSelectedPhoto();
            }
        });

        addTextImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTextImgv.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                setDefaultTextView();
                startTextEditFragment();
            }
        });

        brushImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                brushImgv.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                startPhotoBrushFragment();
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTextEditFragment();
            }
        });

        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(data, shadowBuilder, v, 0);
                if (addPropRelLayout.getVisibility() == View.VISIBLE) {
                    addPropRelLayout.setVisibility(View.GONE);
                    trashDragProcess();
                }
                return false;
            }
        });

        photoRelLayout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                final TextView draggedView = (TextView) event.getLocalState();

                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        return true;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        Log.i("Info", "photoRelLayout ACTION_DRAG_ENDED");
                        return true;
                    case DragEvent.ACTION_DROP:
                        Log.i("Info", "photoRelLayout ACTION_DROP");
                        closeTrashLayout();
                        draggedView.setX((float) x - (draggedView.getWidth() / 2));
                        draggedView.setY((float) y - (draggedView.getHeight() / 2));
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        return true;
                    default:
                        return true;
                }
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (textView != null)
                    textView.setTextSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void closeTrashLayout() {
        if (trashLayout != null) {
            addPropRelLayout.setVisibility(View.VISIBLE);
            photoRelLayout.removeView(trashLayout);
            trashLayout = null;
        }
    }

    private void trashDragProcess() {
        addPropRelLayout.setVisibility(View.GONE);
        trashLayout = getLayoutInflater().inflate(R.layout.text_drag_layout, addPropRelLayout, false);
        ImageView trashImgv = trashLayout.findViewById(R.id.trashImgv);
        trashImgv.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.transparentBlack, null),
                getResources().getColor(R.color.White, null), GradientDrawable.OVAL, 50, 3));
        photoRelLayout.addView(trashLayout);
        trashImgv.setOnDragListener(new TrashDragListener(new TrashDragDropCallback() {
            @Override
            public void onDropped() {
                addPropRelLayout.setVisibility(View.VISIBLE);
                if (trashLayout != null) {
                    photoRelLayout.removeView(trashLayout);
                    setDefaultTextView();
                    seekbarLayout.setVisibility(View.GONE);
                    trashLayout = null;
                }
            }
        }));
    }

    public void checkTextIsAddedOrNot() {
        if (textView != null && !textView.getText().toString().trim().isEmpty()) {
            Bitmap bitmap = BitmapConversion.getScreenShot(photoRelLayout);
            thisPhotoSelectUtil.setScreeanShotBitmap(bitmap);
        }
    }

    private void setDefaultTextView() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        textView.setTextColor(getActivity().getResources().getColor(R.color.White, null));
        textView.setText("");
        textView.setVisibility(View.GONE);
        textView.setLayoutParams(layoutParams);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15.0f);
    }

    public void setSelectedPhoto() {
        CommonUtils.setImageScaleType(thisPhotoSelectUtil, selectedImageView);

        if (thisPhotoSelectUtil != null) {
            if (thisPhotoSelectUtil.getScreeanShotBitmap() != null)
                Glide.with(getContext())
                        .load(thisPhotoSelectUtil.getScreeanShotBitmap())
                        .into(selectedImageView);
            else if (thisPhotoSelectUtil.getMediaUri() != null)
                Glide.with(getContext())
                        .load(thisPhotoSelectUtil.getMediaUri())
                        .into(selectedImageView);

            if (thisPhotoSelectUtil.getBitmap() != null)
                BitmapConversion.setBlurBitmap(getContext(), photoRelLayout,
                        0, 0.2f, 20.5f, thisPhotoSelectUtil.getBitmap());
        }
    }

    private void startTextEditFragment() {
        seekbarLayout.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);

        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new TextEditFragment(textView, thisPhotoSelectUtil, new TextCompleteCallback() {
                @Override
                public void textCompleted(View view) {
                    EditText returnEditText = (EditText) view;
                    if (returnEditText != null) {
                        if (!returnEditText.getText().toString().trim().isEmpty()) {
                            textView.setTextColor(returnEditText.getCurrentTextColor());
                            textView.setText(returnEditText.getText().toString());
                            textView.setVisibility(View.VISIBLE);
                            seekbarLayout.setVisibility(View.VISIBLE);
                        } else
                            seekbarLayout.setVisibility(View.GONE);
                    }
                }
            }));
        }
    }

    public void startPhotoBrushFragment() {
        PhotoSelectUtil tempUtil = thisPhotoSelectUtil;
        tempUtil.setScreeanShotBitmap(BitmapConversion.getScreenShot(photoRelLayout));
        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new PhotoBrushFragment(tempUtil, new BrushCompleteCallback() {
                @Override
                public void OnBrushCompleted(PhotoSelectUtil photoSelectUtil) {
                    thisPhotoSelectUtil = photoSelectUtil;
                    seekbarLayout.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                    setSelectedPhoto();
                }
            }));
        }
    }

}
