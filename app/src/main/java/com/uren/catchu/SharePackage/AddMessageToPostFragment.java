package com.uren.catchu.SharePackage;

import android.annotation.SuppressLint;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.YesNoDialogBoxCallback;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.Interfaces.ServiceCompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.GalleryPicker.TextEditFragment;
import com.uren.catchu.SharePackage.Utils.CheckShareItems;
import com.uren.catchu.SharePackage.Utils.SharePostProcess;
import com.uren.catchu.Singleton.Share.ShareItems;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.LOCATION_SERVICE;
import static com.uren.catchu.Constants.NumericConstants.SHARE_TRY_COUNT;

@SuppressLint("ValidFragment")
public class AddMessageToPostFragment extends BaseFragment {

    View mView;

    CheckShareItems checkShareItems;
    ReturnCallback returnCallback;

    @BindView(R.id.backImgv)
    ImageView backImgv;
    @BindView(R.id.nextImgv)
    ImageView nextImgv;
    @BindView(R.id.toolbarTitleTv)
    TextView toolbarTitleTv;
    @BindView(R.id.noteTextEditText)
    EditText noteTextEditText;

    public AddMessageToPostFragment(CheckShareItems checkShareItems, ReturnCallback returnCallback) {
        this.checkShareItems = checkShareItems;
        this.returnCallback = returnCallback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.message_to_post_frag_layout, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            addListeners();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    public void initVariables() {
        nextImgv.setVisibility(View.VISIBLE);
        toolbarTitleTv.setText(getResources().getString(R.string.typeToAddText));
    }

    public void addListeners() {
        backImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        nextImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextImgv.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                if (!checkShareItems.shareIsPossible()) {
                    DialogBoxUtil.showInfoDialogBox(getContext(), checkShareItems.getErrMessage(), null, new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {
                        }
                    });
                    return;
                }
                sharePost();
            }
        });

        noteTextEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.toString() != null && !s.toString().trim().isEmpty())
                    ShareItems.getInstance().getPost().setMessage(s.toString());
                else
                    ShareItems.getInstance().getPost().setMessage("");
            }
        });
    }

    public void sharePost() {
        getActivity().onBackPressed();
        returnCallback.onReturn(null);
        startToShare();
    }

    public void startToShare() {
        if(ShareItems.getInstance() != null && ShareItems.getInstance().getShareTryCount() < SHARE_TRY_COUNT) {

            int tryCount = ShareItems.getInstance().getShareTryCount();
            ShareItems.getInstance().setShareTryCount(tryCount + 1);

            new SharePostProcess(NextActivity.thisActivity, new ServiceCompleteCallback() {
                @Override
                public void onSuccess() {
                    ShareItems.setInstance(null);
                }

                @Override
                public void onFailed(Exception e) {

                    if (NextActivity.thisActivity != null && ShareItems.getInstance() != null) {
                        DialogBoxUtil.showYesNoDialog(NextActivity.thisActivity, null,
                                NextActivity.thisActivity.getResources().getString(R.string.DEFAULT_POST_ERROR_MESSAGE)
                                , new YesNoDialogBoxCallback() {
                                    @Override
                                    public void yesClick() {
                                        startToShare();
                                    }

                                    @Override
                                    public void noClick() {
                                        // TODO: 7.11.2018 - postlari silme akisi eklenecek.
                                    }
                                });
                    }
                }
            });
        }
    }
}
