package com.uren.catchu.GroupPackage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.MainPackage.MainFragments.SearchTab.SearchFragment;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.UserGroups;

import java.util.List;

import butterknife.BindView;
import catchu.model.GroupRequest;
import catchu.model.GroupRequestResultResultArrayItem;

import static com.uren.catchu.Constants.NumericConstants.groupNameMaxLen;
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
    String befGroupName;

    int groupNameSize = 0;

    ProgressBar progressBar;

    // TODO: 6.09.2018 - Grup listeleme ekraninda maz size ne oluyor kontrol edilecek. Grup admin buyonu kayiyordu.

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
        groupNameSize = groupNameMaxLen - groupName.length();
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
    }

    public void addListeners(){

        relLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard();
            }
        });

        groupNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("Info", "beforeTextChanged s: " + s.toString());
                befGroupName = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("Info", "onTextChanged s:" + s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("Info", "afterTextChanged s:" + s.toString());
                groupNameSize = groupNameMaxLen - s.toString().length();

                if(groupNameSize >= 0)
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
                changeGroupName();
            }
        });


    }

    public void changeGroupName(){

        final GroupRequest groupRequest = new GroupRequest();

        GroupRequestResultResultArrayItem groupRequestResultResultArrayItem = new GroupRequestResultResultArrayItem();
        groupRequestResultResultArrayItem = UserGroups.getGroupWithId(groupId);

        groupRequest.setRequestType(UPDATE_GROUP_INFO);
        groupRequest.setGroupid(groupId);
        groupRequest.setGroupName(groupNameEditText.getText().toString());
        groupRequest.setUserid(AccountHolderInfo.getUserID());
        groupRequest.setGroupPhotoUrl(groupRequestResultResultArrayItem.getGroupPhotoUrl());

        GroupResultProcess groupResultProcess = new GroupResultProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                progressBar.setVisibility(View.GONE);
                UserGroups.changeGroupName(groupId, groupNameEditText.getText().toString());
                DisplayGroupDetailActivity.subtitleCollapsingToolbarLayout.setTitle(groupNameEditText.getText().toString());
                SearchFragment.reloadAdapter();
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                progressBar.setVisibility(View.GONE);
                CommonUtils.showToast( EditGroupNameActivity.this, getResources().getString(R.string.error) + e.getMessage());
            }

            @Override
            public void onTaskContinue() {
                progressBar.setVisibility(View.VISIBLE);
            }
        }, groupRequest);

        groupResultProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void hideKeyBoard() {

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
}
