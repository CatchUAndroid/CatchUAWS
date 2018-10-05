package com.uren.catchu.GroupPackage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uren.catchu.ApiGatewayFunctions.GroupResultProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.GroupPackage.Interfaces.UpdateGroupCallback;
import com.uren.catchu.GroupPackage.Utils.UpdateGroupProcess;
import com.uren.catchu.MainPackage.MainFragments.SearchTab.SearchFragment;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.UserGroups;

import java.util.List;

import butterknife.BindView;
import catchu.model.GroupRequest;
import catchu.model.GroupRequestResultResultArrayItem;

import static com.uren.catchu.Constants.NumericConstants.GROUP_NAME_MAX_LENGTH;
import static com.uren.catchu.Constants.StringConstants.EXIT_GROUP;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_GROUP_ID;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_GROUP_NAME;
import static com.uren.catchu.Constants.StringConstants.UPDATE_GROUP_INFO;

public class EditGroupNameActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText groupNameEditText;
    TextView textSizeCntTv;
    Button cancelButton;
    Button approveButton;
    RelativeLayout relLayout;

    String groupId;
    String groupName;

    int groupNameSize = 0;

    ProgressBar progressBar;
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
        toolbar = findViewById(R.id.toolbar);
        groupNameEditText = findViewById(R.id.groupNameEditText);
        textSizeCntTv = findViewById(R.id.textSizeCntTv);
        cancelButton = findViewById(R.id.cancelButton);
        approveButton = findViewById(R.id.approveButton);
        relLayout = findViewById(R.id.relLayout);
        progressBar = findViewById(R.id.progressBar);
        toolbar.setTitle(getResources().getString(R.string.giveNewName));
        toolbar.setTitleTextColor(getResources().getColor(R.color.White, null));
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
                hideKeyBoard();
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
                    hideKeyBoard();
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
                progressBar.setVisibility(View.GONE);
                UserGroups.changeGroupItem(groupId, groupItem);
                DisplayGroupDetailActivity.subtitleCollapsingToolbarLayout.setTitle(groupNameEditText.getText().toString());
                SearchFragment.reloadAdapter();
                finish();
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }

    public void hideKeyBoard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
