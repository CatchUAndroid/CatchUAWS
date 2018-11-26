package com.uren.catchu.MainPackage.MainFragments.Share.SubFragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.Share.ShareItems;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;

@SuppressLint("ValidFragment")
public class ShareAdvanceSettingsFragment extends BaseFragment {

    View mView;

    @BindView(R.id.closeCommentsSwitch)
    Switch closeCommentsSwitch;
    @BindView(R.id.toolbarTitleTv)
    TextView toolbarTitleTv;
    @BindView(R.id.commonToolbarbackImgv)
    ClickableImageView commonToolbarbackImgv;

    ShareItems shareItems;
    ReturnCallback returnCallback;

    public ShareAdvanceSettingsFragment(ShareItems shareItems, ReturnCallback returnCallback) {
        this.shareItems = shareItems;
        this.returnCallback = returnCallback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_share_advance_settings, container, false);
        ButterKnife.bind(this, mView);
        initVariables();
        addListeners();
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_RIGHT_TO_LEFT;
    }

    private void initVariables() {
        toolbarTitleTv.setText(getResources().getString(R.string.ADVANCED_SETTINGS));
        if(shareItems.getPost().getIsCommentAllowed())
            closeCommentsSwitch.setChecked(true);
        else
            closeCommentsSwitch.setChecked(false);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void addListeners(){
        commonToolbarbackImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        closeCommentsSwitch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        setAllowedValue();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    public void setAllowedValue(){
        if(closeCommentsSwitch.isChecked()){
            shareItems.getPost().setIsCommentAllowed(false);
        }else {
            shareItems.getPost().setIsCommentAllowed(true);
        }
        returnCallback.onReturn(shareItems);
    }
}