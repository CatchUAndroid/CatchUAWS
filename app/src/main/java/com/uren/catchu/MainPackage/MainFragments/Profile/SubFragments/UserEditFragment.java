package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uren.catchu.GeneralUtils.CircleTransform;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.Calendar;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.uren.catchu.Constants.StringConstants.AnimateLeftToRight;
import static com.uren.catchu.Constants.StringConstants.AnimateRightToLeft;

public class UserEditFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;

    @BindView(R.id.toolbarLayout)
    Toolbar mToolBar;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;

    //@BindView(R.id.imgCancel)
    //ClickableImageView imgCancel;
    //@BindView(R.id.imgConfirm)
    //ClickableImageView imgConfirm;

    //@BindView(R.id.rlCoverPicture)
    //RelativeLayout rlCoverPicture;
    @BindView(R.id.rlProfilePicture)
    RelativeLayout rlProfilePicture;

    @BindView(R.id.imgProfile)
    ImageView imgProfile;

    @BindView(R.id.txtCancel)
    TextView txtCancel;
    @BindView(R.id.txtSave)
    TextView txtSave;

    //Fields
    //@BindView(R.id.edtBirthDay)
    EditText edtBirthDay;


    private DatePickerDialog.OnDateSetListener mDateSetListener;

    public UserEditFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.profile_subfragment_user_edit, container, false);
        ButterKnife.bind(this, mView);

        setUpToolbar();

        return mView;
    }

    private void setUpToolbar() {

        //imgCancel.setOnClickListener(this);
        //imgConfirm.setOnClickListener(this);

        txtSave.setOnClickListener(this);
        txtCancel.setOnClickListener(this);

        //rlCoverPicture.setOnClickListener(this);
        rlProfilePicture.setOnClickListener(this);

        edtBirthDay = (EditText) mView.findViewById(R.id.edtBirthDay);
        edtBirthDay.setOnClickListener(this);

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                //Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                edtBirthDay.setText(date);
            }
        };

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //setUpRecyclerView();

        updateUI();

        /*
        Button btn = (Button) view.findViewById(R.id.btnBack);
        Button btnbtnNextFrag = (Button) view.findViewById(R.id.btnNextFrag);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnbtnNextFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mFragmentNavigation != null) {

                    mFragmentNavigation.pushFragment(new UserEditFragment(), AnimateRightToLeft);

                    //mFragmentNavigation.pushFragment(new UserEditFragment());

                }
            }
        });
        */
    }

    private void updateUI() {


        // TODO : Update Cover picture


        //Profile picture
        Picasso.with(getActivity())
                //.load(userProfile.getResultArray().get(0).getProfilePhotoUrl())
                .load(AccountHolderInfo.getInstance().getUser().getUserInfo().getProfilePhotoUrl())
                .transform(new CircleTransform())
                .into(imgProfile);


    }

    @Override
    public void onClick(View v) {

        if (v == txtCancel) {
            editProfileCancelClicked();
        }

        if (v == txtSave) {
            editProfileConfirmClicked();
        }

/*
        if (v == rlCoverPicture) {
            coverPictureClicked();
        }
*/
        if (v == rlProfilePicture) {
            profilePictureClicked();
        }

        if (v == edtBirthDay) {
            birthDayClicked();
        }


    }



    private void birthDayClicked() {


        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                getActivity(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year,month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();



    }

    private void editProfileCancelClicked() {

        ((NextActivity) getActivity()).ANIMATION_TAG = AnimateLeftToRight;
        getActivity().onBackPressed();

    }

    private void editProfileConfirmClicked() {

    }

    private void coverPictureClicked() {


    }

    private void profilePictureClicked() {


    }

}