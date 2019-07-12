package com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostHelper;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.Objects;

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
    //GradientButton btnClearFilter;


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

            initValues();
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
                user.getName(), user.getUsername(), txtProfilePic, imgProfilePic, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        Objects.requireNonNull(getActivity()).findViewById(R.id.tabMainLayout).setVisibility(View.GONE);
        super.onStart();
    }

    private void initValues() {

        //imgCancel.setOnClickListener(this);
        txtCancel.setOnClickListener(this);
        txtApply.setOnClickListener(this);
        btnClearFilter.setOnClickListener(this);
        //btnClearFilter.getButton().setOnClickListener(this);

        btnClearFilter.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.DodgerBlue, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 50, 2));

    }

    private void setSeekbar() {

        suffix = " " + Objects.requireNonNull(getContext()).getResources().getString(R.string.meter);
        radius = FILTERED_FEED_RADIUS;
        seekBar.setMax(10000);
        txtRadius.setText(radius + suffix);
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
                txtRadius.setText(pval + suffix);

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
                txtRadius.setText(pval + suffix);
            }


        });

    }


    @Override
    public void onClick(View v) {

        if (v == txtCancel) {
            //((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
            Objects.requireNonNull(getActivity()).onBackPressed();
        }

        if (v == txtApply) {
            //Save changes
            FILTERED_FEED_RADIUS = radius;
            PostHelper.FeedRefresh.getInstance().feedRefreshStart();
            Objects.requireNonNull(getActivity()).onBackPressed();

        }

        if(v == btnClearFilter){
            radius = DEFAULT_FEED_RADIUS;
            seekBar.setProgress(radius);
        }

        /*if (v == btnClearFilter.getButton()) {
            radius = DEFAULT_FEED_RADIUS;
            seekBar.setProgress(radius);
        }*/

    }


}
