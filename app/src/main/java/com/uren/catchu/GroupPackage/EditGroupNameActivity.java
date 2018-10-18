package com.uren.catchu.GroupPackage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.GroupPackage.Interfaces.UpdateGroupCallback;
import com.uren.catchu.GroupPackage.Utils.UpdateGroupProcess;
import com.uren.catchu.MainPackage.MainFragments.SearchTab.SearchFragment;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.SelectedGroupList;
import com.uren.catchu.Singleton.UserGroups;

import catchu.model.GroupRequestResultResultArrayItem;

import static com.uren.catchu.Constants.NumericConstants.GROUP_NAME_MAX_LENGTH;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_GROUP_ID;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_GROUP_NAME;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_SHARE_GROUP_COUNT;

public class EditGroupNameActivity extends AppCompatActivity {

    ImageView backImgv;
    TextView toolbarTitleTv;
    EditText groupNameEditText;
    TextView textSizeCntTv;
    Button cancelButton;
    Button approveButton;
    RelativeLayout relLayout;
    String groupId;
    String groupName;
    int groupNameSize = 0;
    GradientDrawable buttonShape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group_name);

        initVariables();

        Intent i = getIntent();
        groupId = (String) i.getSerializableExtra(PUTEXTRA_GROUP_ID);
        groupName = (String) i.getSerializableExtra(PUTEXTRA_GROUP_NAME);

        setGroupVariables();
        addListeners();
    }

    private void setGroupVariables() {
        groupNameEditText.setText(groupName);
        groupNameSize = GROUP_NAME_MAX_LENGTH - groupName.length();
        textSizeCntTv.setText(Integer.toString(groupNameSize));
    }

    private void initVariables() {
        backImgv = findViewById(R.id.backImgv);
        toolbarTitleTv = findViewById(R.id.toolbarTitleTv);
        groupNameEditText = findViewById(R.id.groupNameEditText);
        textSizeCntTv = findViewById(R.id.textSizeCntTv);
        cancelButton = findViewById(R.id.cancelButton);
        approveButton = findViewById(R.id.approveButton);
        relLayout = findViewById(R.id.relLayout);
        toolbarTitleTv.setText(getResources().getString(R.string.giveNewName));
        setButtonShapes();
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
                CommonUtils.hideKeyBoard(EditGroupNameActivity.this);
            }
        });

        backImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        groupNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("Info", "onTextChanged s:" + s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("Info", "afterTextChanged s:" + s.toString());
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
                finish();
            }
        });

        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupNameEditText.getText() != null && !groupNameEditText.getText().toString().trim().isEmpty())
                    updateGroup();
                else {
                    CommonUtils.hideKeyBoard(EditGroupNameActivity.this);
                    DialogBoxUtil.showInfoDialogBox(EditGroupNameActivity.this, getResources().getString(R.string.pleaseWriteGroupName),null, new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {

                        }
                    });
                }
            }
        });
    }

    public void updateGroup() {
        GroupRequestResultResultArrayItem groupRequestResultResultArrayItem = UserGroups.getGroupWithId(groupId);
        groupRequestResultResultArrayItem.setName(groupNameEditText.getText().toString());

        new UpdateGroupProcess(EditGroupNameActivity.this, null, groupRequestResultResultArrayItem, new UpdateGroupCallback() {
            @Override
            public void onSuccess(GroupRequestResultResultArrayItem groupItem) {
                UserGroups.changeGroupItem(groupId, groupItem);
                setResultForShareActivity();
                SearchFragment.reloadAdapter();
                finish();
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }

    private void setResultForShareActivity() {
        Intent intent = new Intent();
        intent.putExtra(PUTEXTRA_GROUP_NAME, groupNameEditText.getText().toString());
        setResult(RESULT_OK, intent);
    }

}
