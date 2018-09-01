package com.uren.catchu.GroupPackage;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.uren.catchu.Adapters.SpecialSelectTabAdapter;
import com.uren.catchu.ApiGatewayFunctions.FriendListRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.GroupResultProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GroupPackage.Adapters.GroupDetailListAdapter;
import com.uren.catchu.GroupPackage.Adapters.SelectFriendAdapter;
import com.uren.catchu.MainPackage.MainFragments.SearchTab.SubFragments.PersonFragment;
import com.uren.catchu.R;
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
import static com.uren.catchu.Constants.StringConstants.verticalShown;

public class SelectFriendToGroupActivity extends AppCompatActivity {

    Toolbar mToolBar;

    FloatingActionButton nextFab;

    public static Activity thisActivity;

    private static SelectedFriendList selectedFriendListInstance;

    public static SelectFriendAdapter adapter;
    String pendingActivityName;
    String groupId;

    ProgressDialog mProgressDialog;

    public static RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend_to_group);

        thisActivity = this;

        mToolBar = (Toolbar) findViewById(R.id.toolbarLayout);
        mToolBar.setSubtitle(getResources().getString(R.string.addPersonToGroup));
        mToolBar.setNavigationIcon(R.drawable.back_arrow);
        mToolBar.setBackgroundColor(getResources().getColor(R.color.background, null));
        mToolBar.setTitleTextColor(getResources().getColor(R.color.background_white, null));
        mToolBar.setSubtitleTextColor(getResources().getColor(R.color.background_white, null));
        setSupportActionBar(mToolBar);

        getIntentValues(savedInstanceState);
        SelectedFriendList.setInstance(null);

        initUI();
        getFriendSelectionPage();
        addListeners();
    }

    private void initUI() {

        nextFab = (FloatingActionButton) findViewById(R.id.nextFab);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void getIntentValues(Bundle savedInstanceState) {

        Log.i("Info", "getIntentValues+++++++++++");

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                pendingActivityName = extras.getString(PUTEXTRA_ACTIVITY_NAME);
            }
        } else {
            pendingActivityName = (String) savedInstanceState.getSerializable(PUTEXTRA_ACTIVITY_NAME);
        }

        Intent i = getIntent();
        groupId = (String) i.getSerializableExtra(PUTEXTRA_GROUP_ID);
    }

    public void addListeners(){

        nextFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSelectedPerson();
            }
        });
    }

    private void getFriendSelectionPage() {

        FriendList friendList = getUserFriends();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SelectFriendAdapter(this, friendList);
        recyclerView.setAdapter(adapter);
    }

    public FriendList getUserFriends(){

        FriendList friendListTemp = UserFriends.getFriendList();

        if(pendingActivityName == null)
            return friendListTemp;
        else if(pendingActivityName.equals(DisplayGroupDetailActivity.class.getSimpleName())){
            if(DisplayGroupDetailActivity.groupParticipantList == null)
                return friendListTemp;
            else if(DisplayGroupDetailActivity.groupParticipantList.size() == 0)
                return friendListTemp;
            else{
                return extractGroupParticipants(friendListTemp);
            }
        }else
            return friendListTemp;
    }

    public FriendList extractGroupParticipants(FriendList friendListTemp){

        for(UserProfileProperties userProfileProperties1 : DisplayGroupDetailActivity.groupParticipantList){

            int index = 0;

            for(UserProfileProperties userProfileProperties : friendListTemp.getResultArray()){

                if(userProfileProperties.getUserid().equals(userProfileProperties1.getUserid())){
                    friendListTemp.getResultArray().remove(index);
                    break;
                }
                index = index + 1;
            }
        }
        return friendListTemp;
    }

    public void checkSelectedPerson(){

        selectedFriendListInstance = SelectedFriendList.getInstance();

        if (selectedFriendListInstance.getSelectedFriendList().getResultArray().size() == 0) {
            Toast.makeText(this, getResources().getString(R.string.selectLeastOneFriend), Toast.LENGTH_SHORT).show();
            return;
        }

        if(pendingActivityName != null){
            if(pendingActivityName.equals(DisplayGroupDetailActivity.class.getSimpleName())) {
                addParticipantToGroup();
                finish();
            }else if(pendingActivityName.equals(AddGroupActivity.class.getSimpleName()))
                startActivity(new Intent(this, AddGroupActivity.class));
        }else
            startActivity(new Intent(this, AddGroupActivity.class));
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

    public List<GroupRequestGroupParticipantArrayItem> fillSelectedFriendList(){

        List<GroupRequestGroupParticipantArrayItem> selectedFriendList = new ArrayList<>();

        for(UserProfileProperties userProfileProperties : selectedFriendListInstance.getSelectedFriendList().getResultArray()){
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
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
