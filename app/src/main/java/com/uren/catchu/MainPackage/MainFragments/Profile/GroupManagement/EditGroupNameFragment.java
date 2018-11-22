package com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement;

import android.annotation.SuppressLint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.ApiModelsProcess.UserGroupsProcess;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Interfaces.UpdateGroupCallback;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.GroupRequestResultResultArrayItem;

import static com.uren.catchu.Constants.NumericConstants.GROUP_NAME_MAX_LENGTH;

@SuppressLint("ValidFragment")
public class EditGroupNameFragment extends BaseFragment{

    View mView;

    @BindView(R.id.commonToolbarbackImgv)
    ClickableImageView commonToolbarbackImgv;
    @BindView(R.id.toolbarTitleTv)
    TextView toolbarTitleTv;
    @BindView(R.id.groupNameEditText)
    EditText groupNameEditText;
    @BindView(R.id.textSizeCntTv)
    TextView textSizeCntTv;
    @BindView(R.id.cancelButton)
    Button cancelButton;
    @BindView(R.id.approveButton)
    Button approveButton;
    @BindView(R.id.relLayout)
    RelativeLayout relLayout;

    CompleteCallback completeCallback;

    int groupNameSize = 0;
    GradientDrawable buttonShape;
    GroupRequestResultResultArrayItem groupRequestResultResultArrayItem;

    public EditGroupNameFragment(GroupRequestResultResultArrayItem groupRequestResultResultArrayItem, CompleteCallback completeCallback) {
        this.groupRequestResultResultArrayItem = groupRequestResultResultArrayItem;
        this.completeCallback = completeCallback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_edit_group_name, container, false);
            ButterKnife.bind(this, mView);
            addListeners();
            setGroupVariables();
            toolbarTitleTv.setText(getResources().getString(R.string.giveNewName));
            setButtonShapes();
        }
        return mView;
    }

    private void setGroupVariables() {
        groupNameEditText.setText(groupRequestResultResultArrayItem.getName());
        groupNameSize = GROUP_NAME_MAX_LENGTH - groupRequestResultResultArrayItem.getName().length();
        textSizeCntTv.setText(Integer.toString(groupNameSize));
    }

    private void setButtonShapes() {
        buttonShape = ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.Gray, null), GradientDrawable.RECTANGLE, 15, 2);
        cancelButton.setBackground(buttonShape);
        approveButton.setBackground(buttonShape);
    }

    public void addListeners() {
        relLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.hideKeyBoard(getContext());
            }
        });

        commonToolbarbackImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        groupNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                groupNameSize = GROUP_NAME_MAX_LENGTH - s.toString().length();

                if (groupNameSize >= 0)
                    textSizeCntTv.setText(Integer.toString(groupNameSize));
                else
                    textSizeCntTv.setText(Integer.toString(0));
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                getActivity().onBackPressed();
            }
        });

        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.hideKeyBoard(getContext());
                approveButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                if (groupNameEditText.getText() != null && !groupNameEditText.getText().toString().trim().isEmpty())
                    updateGroup();
                else {
                    DialogBoxUtil.showInfoDialogBox(getContext(), getResources().getString(R.string.pleaseWriteGroupName),null, new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {

                        }
                    });
                }
            }
        });
    }

    public void updateGroup() {
        groupRequestResultResultArrayItem.setName(groupNameEditText.getText().toString());

        UserGroupsProcess.updateGroup(getContext(), null, groupRequestResultResultArrayItem,
                new UpdateGroupCallback() {
                    @Override
                    public void onSuccess(GroupRequestResultResultArrayItem groupItem) {
                        completeCallback.onComplete(groupNameEditText.getText().toString());
                        getActivity().onBackPressed();
                    }

                    @Override
                    public void onFailed(Exception e) {
                        completeCallback.onFailed(e);
                    }
                });
    }

}
