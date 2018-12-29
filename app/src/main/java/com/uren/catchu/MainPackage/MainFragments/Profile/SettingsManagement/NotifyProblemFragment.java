package com.uren.catchu.MainPackage.MainFragments.Profile.SettingsManagement;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.FragmentControllers.FragNavController;
import com.uren.catchu.GeneralUtils.BitmapConversion;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.PhotoChosenForReportCallback;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.GeneralUtils.IntentUtil.IntentSelectUtil;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SettingsManagement.Models.ProblemNotifyModel;
import com.uren.catchu.MainPackage.MainFragments.Profile.SettingsManagement.ReportProblem.SaveReportProblemProcess;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;

public class NotifyProblemFragment extends BaseFragment {

    View mView;

    @BindView(R.id.commonToolbarTickImgv)
    ImageView commonToolbarTickImgv;
    @BindView(R.id.commonToolbarbackImgv)
    ClickableImageView commonToolbarbackImgv;
    @BindView(R.id.toolbarTitleTv)
    TextView toolbarTitleTv;
    @BindView(R.id.noteTextEditText)
    EditText noteTextEditText;

    @BindView(R.id.addPhotoImgv1)
    ImageView addPhotoImgv1;
    @BindView(R.id.addPhotoImgv2)
    ImageView addPhotoImgv2;
    @BindView(R.id.addPhotoImgv3)
    ImageView addPhotoImgv3;
    @BindView(R.id.addPhotoImgv4)
    ImageView addPhotoImgv4;

    @BindView(R.id.imgDelete1)
    ImageView imgDelete1;
    @BindView(R.id.imgDelete2)
    ImageView imgDelete2;
    @BindView(R.id.imgDelete3)
    ImageView imgDelete3;
    @BindView(R.id.imgDelete4)
    ImageView imgDelete4;

    List<ProblemNotifyModel> problemListBox;
    List<PhotoSelectUtil> photoSelectUtilList = new ArrayList<>();
    PermissionModule permissionModule;

    ImageView chosenImgv = null;

    private static final int CODE_GALLERY_REQUEST = 665;

    public NotifyProblemFragment() {

    }

    @Override
    public void onStart() {
        NextActivity.bottomTabLayout.setVisibility(View.GONE);
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        try {
            if (mView == null) {
                mView = inflater.inflate(R.layout.fragment_notify_problem, container, false);
                ButterKnife.bind(this, mView);
                initVariables();
                addListeners();
                setShapes();
                initProblemList();
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_RIGHT_TO_LEFT;
    }

    private void initVariables() {
        try {
            toolbarTitleTv.setText(getResources().getString(R.string.REPORT_PROBLEM_OR_COMMENT));
            permissionModule = new PermissionModule(getContext());
            NextActivity.notifyProblemFragment = this;
            commonToolbarTickImgv.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void setShapes() {
        try {
            GradientDrawable shape = ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                    getResources().getColor(R.color.Gray, null), GradientDrawable.RECTANGLE, 15, 2);
            addPhotoImgv1.setBackground(shape);
            addPhotoImgv2.setBackground(shape);
            addPhotoImgv3.setBackground(shape);
            addPhotoImgv4.setBackground(shape);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void addListeners() {
        try {
            commonToolbarbackImgv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });

            commonToolbarTickImgv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (noteTextEditText != null && noteTextEditText.getText() != null &&
                            noteTextEditText.getText().toString().isEmpty()) {
                        CommonUtils.showToastShort(getContext(), getResources().getString(R.string.CAN_YOU_SPECIFY_THE_PROBLEM));
                        return;
                    }
                    saveReport();
                }
            });

            addPhotoImgv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chosenImgv = addPhotoImgv1;
                    managePhotoChosen();
                }
            });

            addPhotoImgv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chosenImgv = addPhotoImgv2;
                    managePhotoChosen();
                }
            });

            addPhotoImgv3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chosenImgv = addPhotoImgv3;
                    managePhotoChosen();
                }
            });

            addPhotoImgv4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chosenImgv = addPhotoImgv4;
                    managePhotoChosen();
                }
            });

            imgDelete1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chosenImgv = addPhotoImgv1;
                    removePhoto();
                }
            });

            imgDelete2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chosenImgv = addPhotoImgv2;
                    removePhoto();
                }
            });

            imgDelete3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chosenImgv = addPhotoImgv3;
                    removePhoto();
                }
            });

            imgDelete4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chosenImgv = addPhotoImgv4;
                    removePhoto();
                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void initProblemList() {
        try {
            problemListBox = new ArrayList<>();

            ProblemNotifyModel p1 = new ProblemNotifyModel();
            p1.setImageView(addPhotoImgv1);
            p1.setDeleteImgv(imgDelete1);
            problemListBox.add(p1);
            setViewPadding(addPhotoImgv1, p1);

            ProblemNotifyModel p2 = new ProblemNotifyModel();
            p2.setImageView(addPhotoImgv2);
            p2.setDeleteImgv(imgDelete2);
            problemListBox.add(p2);
            setViewPadding(addPhotoImgv2, p2);

            ProblemNotifyModel p3 = new ProblemNotifyModel();
            p3.setImageView(addPhotoImgv3);
            p3.setDeleteImgv(imgDelete3);
            problemListBox.add(p3);
            setViewPadding(addPhotoImgv3, p3);

            ProblemNotifyModel p4 = new ProblemNotifyModel();
            p4.setImageView(addPhotoImgv4);
            p4.setDeleteImgv(imgDelete4);
            problemListBox.add(p4);
            setViewPadding(addPhotoImgv4, p4);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void setViewPadding(ImageView view, ProblemNotifyModel problemNotifyModel) {
        try {
            problemNotifyModel.getImageView().setPadding(70, 70, 70, 70);
            problemNotifyModel.getImageView().setColorFilter(getActivity().getResources().getColor(R.color.Gray, null), PorterDuff.Mode.SRC_IN);
            problemNotifyModel.getImageView().setScaleType(ImageView.ScaleType.FIT_CENTER);
            problemNotifyModel.getDeleteImgv().setVisibility(View.GONE);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void clearViewPadding(ProblemNotifyModel problemNotifyModel) {
        try {
            problemNotifyModel.getImageView().setPadding(0, 0, 0, 0);
            problemNotifyModel.getImageView().setColorFilter(null);
            problemNotifyModel.getImageView().setScaleType(ImageView.ScaleType.FIT_XY);
            problemNotifyModel.getDeleteImgv().setVisibility(View.VISIBLE);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void managePhotoChosen() {
        try {
            for (final ProblemNotifyModel problemNotifyModel : problemListBox) {

                if (problemNotifyModel.getImageView() == chosenImgv) {
                    if (problemNotifyModel.getPhotoSelectUtil() != null) {

                        if (mFragmentNavigation != null) {
                            mFragmentNavigation.pushFragment(new MarkProblemFragment(problemNotifyModel.getPhotoSelectUtil(), new ReturnCallback() {
                                        @Override
                                        public void onReturn(Object object) {
                                            PhotoSelectUtil util = (PhotoSelectUtil) object;
                                            problemNotifyModel.setPhotoSelectUtil(util);
                                            setPhotoSelectUtil(util);
                                        }
                                    }),
                                    ANIMATE_RIGHT_TO_LEFT);
                        }
                        return;
                    }
                }
            }

            startPhotoChosen();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void startPhotoChosen() {
        DialogBoxUtil.photoChosenForProblemReportDialogBox(getContext(), null, new PhotoChosenForReportCallback() {
            @Override
            public void onGallerySelected() {
                startGalleryProcess();
            }

            @Override
            public void onScreenShot() {
                screenShotStart();
            }
        });
    }

    private void screenShotStart() {
        try {
            getActivity().onBackPressed();

            NextActivity.screenShotMainLayout.setVisibility(View.VISIBLE);

            NextActivity.screenShotApproveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bitmap bitmap = BitmapConversion.getScreenShot(NextActivity.profilePageMainLayout);
                    PhotoSelectUtil photoSelectUtil = new PhotoSelectUtil();
                    photoSelectUtil.setBitmap(bitmap);
                    setPhotoSelectUtil(photoSelectUtil);
                    returnNotifyFragment();
                }
            });

            NextActivity.screenShotCancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    returnNotifyFragment();
                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void returnNotifyFragment() {
        try {
            NextActivity.screenShotMainLayout.setVisibility(View.GONE);
            NextActivity.screenShotApproveBtn.setOnClickListener(null);
            NextActivity.screenShotCancelBtn.setOnClickListener(null);

            if (getContext() != null)
                NextActivity.switchAndUpdateTabSelection(FragNavController.TAB3);
            else {
                NextActivity.switchAndUpdateTabSelection(FragNavController.TAB3);
                if (mFragmentNavigation != null) {
                    mFragmentNavigation.pushFragment(NextActivity.notifyProblemFragment, ANIMATE_RIGHT_TO_LEFT);
                }
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void removePhoto() {
        try {
            for (ProblemNotifyModel problemNotifyModel : problemListBox) {
                if (problemNotifyModel.getImageView() == chosenImgv) {
                    problemNotifyModel.setPhotoSelectUtil(null);
                    setViewPadding(chosenImgv, problemNotifyModel);
                    Glide.with(NextActivity.thisActivity)
                            .load(getResources().getIdentifier("icon_add_white", "mipmap", getActivity().getPackageName()))
                            .apply(RequestOptions.centerInsideTransform())
                            .into(chosenImgv);
                    break;
                }
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    private void startGalleryProcess() {
        try {
            if (permissionModule.checkWriteExternalStoragePermission())
                startActivityForResult(Intent.createChooser(IntentSelectUtil.getGalleryIntent(),
                        getResources().getString(R.string.selectPicture)), CODE_GALLERY_REQUEST);
            else
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        permissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        try {
            if (requestCode == permissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(Intent.createChooser(IntentSelectUtil.getGalleryIntent(),
                            getResources().getString(R.string.selectPicture)), CODE_GALLERY_REQUEST);
                }
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == CODE_GALLERY_REQUEST) {
                    PhotoSelectUtil photoSelectUtil = new PhotoSelectUtil(getActivity(), data, GALLERY_TEXT);
                    setPhotoSelectUtil(photoSelectUtil);
                }
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void setPhotoSelectUtil(PhotoSelectUtil photoSelectUtil) {
        try {
            for (ProblemNotifyModel problemNotifyModel : problemListBox) {
                if (problemNotifyModel.getImageView() == chosenImgv) {
                    problemNotifyModel.setPhotoSelectUtil(photoSelectUtil);
                    clearViewPadding(problemNotifyModel);

                    if (photoSelectUtil.getBitmap() != null)
                        Glide.with(NextActivity.thisActivity)
                                .load(photoSelectUtil.getBitmap())
                                .apply(RequestOptions.fitCenterTransform())
                                .into(chosenImgv);
                    break;
                }
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveReport() {
        try {
            setFinalReportBox();
            DialogBoxUtil.showInfoDialogWithLimitedTime(getContext(), null,
                    getResources().getString(R.string.THANKS_FOR_FEEDBACK), 3000, new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {
                            getActivity().onBackPressed();
                            new SaveReportProblemProcess(photoSelectUtilList,
                                    noteTextEditText.getText().toString(),
                                    AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid(),
                                    new CompleteCallback() {
                                        @Override
                                        public void onComplete(Object object) {

                                        }

                                        @Override
                                        public void onFailed(Exception e) {

                                        }
                                    });
                        }
                    });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    private void setFinalReportBox() {
        try {
            for (ProblemNotifyModel problemNotifyModel : problemListBox) {
                if (problemNotifyModel != null && problemNotifyModel.getPhotoSelectUtil() != null &&
                        problemNotifyModel.getPhotoSelectUtil().getBitmap() != null) {
                    photoSelectUtilList.add(problemNotifyModel.getPhotoSelectUtil());
                }
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }


}