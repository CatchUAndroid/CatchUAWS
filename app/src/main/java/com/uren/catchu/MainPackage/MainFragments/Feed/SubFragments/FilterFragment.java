package com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostHelper;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.UserProfileProperties;
import static com.uren.catchu.Constants.NumericConstants.DEFAULT_FEED_RADIUS;
import static com.uren.catchu.Constants.NumericConstants.FILTERED_FEED_RADIUS;


public class FilterFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;
    int radius;
    String suffix = "";

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.txtCancel)
    TextView txtCancel;

    @BindView(R.id.txtApply)
    TextView txtApply;

    @BindView(R.id.txtRadius)
    TextView txtRadius;

    @BindView(R.id.seekbar)
    SeekBar seekBar;

    @BindView(R.id.imgProfilePic)
    ImageView imgProfilePic;

    @BindView(R.id.txtProfilePic)
    TextView txtProfilePic;

    @BindView(R.id.btnClearFilter)
    Button btnClearFilter;

    /*
    @BindView(R.id.imgCancel)
    ClickableImageView imgCancel;
*/

    public static FilterFragment newInstance() {
        Bundle args = new Bundle();
        FilterFragment fragment = new FilterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.filter_fragment, container, false);
            ButterKnife.bind(this, mView);

            initListeners();
            setProfilePic();
            setSeekbar();
            //getPersonList();
        }

        return mView;
    }

    private void setProfilePic() {
        //profile picture
        UserProfileProperties user = AccountHolderInfo.getInstance().getUser().getUserInfo();
        UserDataUtil.setProfilePicture(getContext(), user.getProfilePhotoUrl(),
                user.getName(), txtProfilePic, imgProfilePic);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void initListeners() {

        //imgCancel.setOnClickListener(this);
        txtCancel.setOnClickListener(this);
        txtApply.setOnClickListener(this);
        btnClearFilter.setOnClickListener(this);

    }

    private void setSeekbar() {

        suffix = " " + getContext().getResources().getString(R.string.meter);
        radius = FILTERED_FEED_RADIUS;
        seekBar.setMax(10000);
        txtRadius.setText(String.valueOf(radius) + suffix);
        seekBar.setProgress(radius);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int pval = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pval = progress;
                if (pval < 100) {
                    pval = 100;
                    seekBar.setProgress(100);
                }
                radius = pval;
                txtRadius.setText(String.valueOf(pval) + suffix);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (pval < 100) {
                    pval = 100;
                    seekBar.setProgress(100);
                }
                radius = pval;
                txtRadius.setText(String.valueOf(pval) + suffix);
            }


        });

    }


    @Override
    public void onClick(View v) {

        if (v == txtCancel) {
            //((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
            getActivity().onBackPressed();
        }

        if (v == txtApply) {
            //Save changes
            FILTERED_FEED_RADIUS = radius;
            PostHelper.FeedRefresh.getInstance().feedRefreshStart();
            getActivity().onBackPressed();

        }

        if (v == btnClearFilter) {
            radius = DEFAULT_FEED_RADIUS;
            seekBar.setProgress(radius);
        }

    }


}
