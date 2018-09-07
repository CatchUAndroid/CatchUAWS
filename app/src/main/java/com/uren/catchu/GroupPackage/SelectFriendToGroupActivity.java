package com.uren.catchu.GroupPackage;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.uren.catchu.Adapters.SpecialSelectTabAdapter;
import com.uren.catchu.ApiGatewayFunctions.FriendListRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.GroupResultProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GroupPackage.Adapters.GroupDetailListAdapter;
import com.uren.catchu.GroupPackage.Adapters.SelectFriendAdapter;
import com.uren.catchu.MainPackage.MainFragments.SearchTab.SubFragments.PersonFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.ShareDetailActivity;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.SelectedFriendList;
import com.uren.catchu.Singleton.UserFriends;

import java.util.ArrayList;
import java.util.List;

import catchu.model.FriendList;
import catchu.model.GroupRequest;
import catchu.model.GroupRequestGroupParticipantArrayItem;
import catchu.model.GroupRequestResult;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.ADD_PARTICIPANT_INTO_GROUP;
import static com.uren.catchu.Constants.StringConstants.GET_GROUP_PARTICIPANT_LIST;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_ACTIVITY_NAME;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_GROUP_ID;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_SHARE_FRIEND_COUNT;
import static com.uren.catchu.Constants.StringConstants.verticalShown;

public class SelectFriendToGroupActivity extends AppCompatActivity {

    /*TextView friendCountTv;*/
    FloatingActionButton nextFab;
    ImageView imgCancelSearch;
    EditText editTextSearch;
    //CheckBox selectAllCb;

    public static Activity thisActivity;

    private static SelectedFriendList selectedFriendListInstance;

    public static SelectFriendAdapter adapter;
    String pendingActivityName;
    String groupId;
    FriendList friendList;

    ProgressDialog mProgressDialog;

    public static RecyclerView recyclerView;

    // TODO: 3.09.2018 - Resmi olmayan kullanicilar icin isim soyad bas harf ile resme ekleme yapalim. Uloader gibi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend_to_group);

        thisActivity = this;

        getIntentValues(savedInstanceState);
        initUI();
        getFriendSelectionPage();
        addListeners();
    }

    private void initUI() {

        nextFab = (FloatingActionButton) findViewById(R.id.nextFab);
        recyclerView = findViewById(R.id.recyclerView);
        //friendCountTv = findViewById(R.id.friendCountTv);
        imgCancelSearch = (ImageView) findViewById(R.id.imgCancelSearch);
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        //selectAllCb = findViewById(R.id.selectAllCb);
        SelectedFriendList.setInstance(null);
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

        Intent i = getIntent();
        groupId = (String) i.getSerializableExtra(PUTEXTRA_GROUP_ID);
    }

    public void addListeners() {
        nextFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSelectedPerson();
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
                if (!s.toString().isEmpty())
                    imgCancelSearch.setVisibility(View.VISIBLE);
                else
                    imgCancelSearch.setVisibility(View.GONE);

                adapter.updateAdapter(s.toString());
            }
        });
    }

    /*public void setFriendCountTextView() {
        friendCountTv.setText(Integer.toString(friendList.getResultArray().size()));
    }*/

    private void getFriendSelectionPage() {
        friendList = getUserFriends();
        /*setFriendCountTextView();*/
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SelectFriendAdapter(this, friendList);
        recyclerView.setAdapter(adapter);
    }

    public FriendList getUserFriends() {

        FriendList friendListTemp = UserFriends.getFriendList();

        if (pendingActivityName == null)
            return friendListTemp;
        else if (pendingActivityName.equals(DisplayGroupDetailActivity.class.getSimpleName())) {
            if (DisplayGroupDetailActivity.groupParticipantList == null)
                return friendListTemp;
            else if (DisplayGroupDetailActivity.groupParticipantList.size() == 0)
                return friendListTemp;
            else {
                return extractGroupParticipants(friendListTemp);
            }
        } else if (pendingActivityName.equals(NextActivity.class.getSimpleName())) {
            return friendListTemp;
        } else
            return friendListTemp;
    }

    public FriendList extractGroupParticipants(FriendList friendListTemp) {

        for (UserProfileProperties userProfileProperties1 : DisplayGroupDetailActivity.groupParticipantList) {

            int index = 0;

            for (UserProfileProperties userProfileProperties : friendListTemp.getResultArray()) {

                if (userProfileProperties.getUserid().equals(userProfileProperties1.getUserid())) {
                    friendListTemp.getResultArray().remove(index);
                    break;
                }
                index = index + 1;
            }
        }
        return friendListTemp;
    }

    public void checkSelectedPerson() {

        selectedFriendListInstance = SelectedFriendList.getInstance();

        if (selectedFriendListInstance.getSelectedFriendList().getResultArray().size() == 0) {
            Toast.makeText(this, getResources().getString(R.string.selectLeastOneFriend), Toast.LENGTH_SHORT).show();
            return;
        }

        if (pendingActivityName != null) {
            if (pendingActivityName.equals(DisplayGroupDetailActivity.class.getSimpleName())) {
                addParticipantToGroup();
                finish();
            } else if (pendingActivityName.equals(ShareDetailActivity.class.getSimpleName())) {
                setResultForShareActivity();
                finish();
            } else if (pendingActivityName.equals(NextActivity.class.getSimpleName())) {
                startActivity(new Intent(this, AddGroupActivity.class));
            }
        } else
            CommonUtils.showToastLong(SelectFriendToGroupActivity.this, getResources().getString(R.string.error) +
                    getResources().getString(R.string.technicalError));
    }

    private void setResultForShareActivity() {
        Intent intent = new Intent();
        intent.putExtra(PUTEXTRA_SHARE_FRIEND_COUNT, SelectedFriendList.getInstance().getSize());
        setResult(RESULT_OK, intent);
    }

    private void addParticipantToGroup() {

        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setGroupid(groupId);
        groupRequest.setRequestType(ADD_PARTICIPANT_INTO_GROUP);
        groupRequest.setGroupParticipantArray(fillSelectedFriendList());

        // TODO: 30.08.2018 - ProgressDialog yada progressbar ekleyelim... 
        //mProgressDialog = new ProgressDialog(this);
        //mProgressDialog.setMessage(getResources().getString(R.string.friendsAdding));
        //mProgressDialog.show();

        GroupResultProcess groupResultProcess = new GroupResultProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                //if(mProgressDialog.isShowing()) mProgressDialog.dismiss();
                DisplayGroupDetailActivity.groupParticipantList.addAll(selectedFriendListInstance.getSelectedFriendList().getResultArray());
                DisplayGroupDetailActivity.reloadAdapter();
                DisplayGroupDetailActivity.personCntTv.setText(Integer.toString(DisplayGroupDetailActivity.getParticipantCount()));
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                //if(mProgressDialog.isShowing()) mProgressDialog.dismiss();
                CommonUtils.showToast(SelectFriendToGroupActivity.this, getResources().getString(R.string.error) + e.getMessage());
            }

            @Override
            public void onTaskContinue() {

            }
        }, groupRequest);

        groupResultProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public List<GroupRequestGroupParticipantArrayItem> fillSelectedFriendList() {

        List<GroupRequestGroupParticipantArrayItem> selectedFriendList = new ArrayList<>();

        for (UserProfileProperties userProfileProperties : selectedFriendListInstance.getSelectedFriendList().getResultArray()) {
            GroupRequestGroupParticipantArrayItem groupRequestGroupParticipantArrayItem = new GroupRequestGroupParticipantArrayItem();
            groupRequestGroupParticipantArrayItem.setParticipantUserid(userProfileProperties.getUserid());
            selectedFriendList.add(groupRequestGroupParticipantArrayItem);
        }

        return selectedFriendList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                SelectedFriendList.setInstance(null);
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
        SelectedFriendList.setInstance(null);
        if (pendingActivityName.equals(ShareDetailActivity.class.getSimpleName()))
            setResultForShareActivity();

        super.onBackPressed();
    }
}