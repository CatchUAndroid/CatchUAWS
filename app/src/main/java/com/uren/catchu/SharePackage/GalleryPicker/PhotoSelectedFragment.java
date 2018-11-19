package com.uren.catchu.SharePackage.GalleryPicker;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.GeneralUtils.BitmapConversion;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.GalleryPicker.Interfaces.TextCompleteCallback;
import com.uren.catchu.SharePackage.GalleryPicker.Interfaces.TrashDragDropCallback;
import com.uren.catchu.Singleton.Share.ShareItems;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;

@SuppressLint("ValidFragment")
public class PhotoSelectedFragment extends BaseFragment {

    @BindView(R.id.selectedImageView)
    ImageView selectedImageView;
    @BindView(R.id.addTextImgv)
    ImageView addTextImgv;
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

    View mView;
    PhotoSelectUtil photoSelectUtil;
    ReturnCallback returnCallback;
    View trashLayout = null;

    public PhotoSelectedFragment(PhotoSelectUtil photoSelectUtil, ReturnCallback returnCallback) {
        this.photoSelectUtil = photoSelectUtil;
        this.returnCallback = returnCallback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(mView == null) {
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
        finishButton.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.transparentBlack, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 15, 2));
        addTextImgv.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.transparentBlack, null),
                0, GradientDrawable.OVAL, 50, 0));
    }

    private void addListeners() {
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkTextIsAddedOrNot();
                returnCallback.onReturn(photoSelectUtil);
                getActivity().onBackPressed();
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
        photoRelLayout.addView(trashLayout);
        trashImgv.setOnDragListener(new TrashDragListener(new TrashDragDropCallback() {
            @Override
            public void onDropped() {
                addPropRelLayout.setVisibility(View.VISIBLE);
                if (trashLayout != null) {
                    photoRelLayout.removeView(trashLayout);
                    setDefaultTextView();
                    seekbar.setVisibility(View.GONE);
                    trashLayout = null;
                }
            }
        }));
    }

    public void checkTextIsAddedOrNot() {
        if (textView != null && !textView.getText().toString().trim().isEmpty()) {
            Bitmap bitmap = BitmapConversion.getScreenShot(photoRelLayout);
            photoSelectUtil.setScreeanShotBitmap(bitmap);
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
        CommonUtils.setImageScaleType(photoSelectUtil, selectedImageView);

        if(photoSelectUtil != null){
            if(photoSelectUtil.getScreeanShotBitmap() != null)
                Glide.with(getContext())
                        .load(photoSelectUtil.getScreeanShotBitmap())
                        .into(selectedImageView);
            else if(photoSelectUtil.getMediaUri() != null)
                Glide.with(getContext())
                        .load(photoSelectUtil.getMediaUri())
                        .into(selectedImageView);
        }
    }

    private void startTextEditFragment() {
        seekbar.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);

        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new TextEditFragment(textView, photoSelectUtil, new TextCompleteCallback() {
                @Override
                public void textCompleted(View view) {
                    EditText returnEditText = (EditText) view;
                    if (returnEditText != null) {
                        if (!returnEditText.getText().toString().trim().isEmpty()) {
                            textView.setTextColor(returnEditText.getCurrentTextColor());
                            textView.setText(returnEditText.getText().toString());
                            textView.setVisibility(View.VISIBLE);
                            seekbar.setVisibility(View.VISIBLE);
                        } else
                            seekbar.setVisibility(View.GONE);
                    }
                }
            }));
        }
    }

}
