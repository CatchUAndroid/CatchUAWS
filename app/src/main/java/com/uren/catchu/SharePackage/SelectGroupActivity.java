package com.uren.catchu.SharePackage;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.uren.catchu.Adapters.UserGroupsListAdapter;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.SelectedGroupList;
import com.uren.catchu.Singleton.UserGroups;

import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_ACTIVITY_NAME;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_SHARE_GROUP_COUNT;

public class SelectGroupActivity extends AppCompatActivity {
    FloatingActionButton nextFab;
    String pendingActivityName;
    RecyclerView recyclerView;
    ImageView imgCancelSearch;
    EditText editTextSearch;
    UserGroupsListAdapter userGroupsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group);
        initUI();
        getIntentValues(savedInstanceState);
        setUpViewPager();
        addListeners();
    }

    private void initUI() {
        nextFab = findViewById(R.id.nextFab);
        recyclerView = findViewById(R.id.recyclerView);
        imgCancelSearch =  findViewById(R.id.imgCancelSearch);
        editTextSearch = findViewById(R.id.editTextSearch);
        SelectedGroupList.setInstance(null);
        SelectedGroupList.getInstance();
    }

    private void getIntentValues(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                pendingActivityName = extras.getString(PUTEXTRA_ACTIVITY_NAME);
            }
        } else {
            pendingActivityName = (String) savedInstanceState.getSerializable(PUTEXTRA_ACTIVITY_NAME);
        }
    }

    private void setUpViewPager() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userGroupsListAdapter = new UserGroupsListAdapter(SelectGroupActivity.this, UserGroups.getInstance().getGroupRequestResult());
        recyclerView.setAdapter(userGroupsListAdapter);
    }

    public void addListeners(){
        nextFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SelectedGroupList.getInstance().getSize() == 0){
                    CommonUtils.showToastLong(SelectGroupActivity.this, getResources().getString(R.string.selectLeastOneGroup));
                    return;
                }
                setResultForShareActivity();
                finish();
            }
        });

        imgCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextSearch.setText("");
                imgCancelSearch.setVisibility(View.GONE);
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().isEmpty())
                    imgCancelSearch.setVisibility(View.VISIBLE);
                else
                    imgCancelSearch.setVisibility(View.GONE);

                userGroupsListAdapter.updateAdapter(s.toString());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                SelectedGroupList.setInstance(null);
                if (pendingActivityName.equals(ShareDetailActivity.class.getSimpleName()))
                    setResultForShareActivity();

                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        SelectedGroupList.setInstance(null);
        if (pendingActivityName.equals(ShareDetailActivity.class.getSimpleName()))
            setResultForShareActivity();

        super.onBackPressed();
    }

    private void setResultForShareActivity() {
        Intent intent = new Intent();
        intent.putExtra(PUTEXTRA_SHARE_GROUP_COUNT, SelectedGroupList.getInstance().getSize());
        setResult(RESULT_OK, intent);
    }
}
