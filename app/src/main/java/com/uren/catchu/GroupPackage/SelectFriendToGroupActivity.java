package com.uren.catchu.GroupPackage;

import android.app.Fragment;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.uren.catchu.Adapters.SpecialSelectTabAdapter;
import com.uren.catchu.GroupPackage.Fragments.FriendFragment;
import com.uren.catchu.MainPackage.MainFragments.SearchTab.SubFragments.PersonFragment;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.SelectedFriendList;

import static com.uren.catchu.Constants.StringConstants.verticalShown;

public class SelectFriendToGroupActivity extends AppCompatActivity {

    Toolbar mToolBar;

    ViewPager viewPager;
    ViewPager viewPagerHorizontal;
    //String comingPageName = null;
    String userid;

    private static SelectedFriendList selectedFriendListInstance;

    SpecialSelectTabAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend_to_group);

        mToolBar = (Toolbar) findViewById(R.id.toolbarLayout);
        mToolBar.setSubtitle(getResources().getString(R.string.addPersonToGroup));
        mToolBar.setNavigationIcon(R.drawable.back_arrow);
        mToolBar.setBackgroundColor(getResources().getColor(R.color.background, null));
        mToolBar.setTitleTextColor(getResources().getColor(R.color.background_white, null));
        mToolBar.setSubtitleTextColor(getResources().getColor(R.color.background_white, null));
        setSupportActionBar(mToolBar);

        userid = AccountHolderInfo.getUserID();

        SelectedFriendList.setInstance(null);

        FloatingActionButton nextFab = (FloatingActionButton) findViewById(R.id.nextFab);
        nextFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkSelectedPerson();
            }
        });

        initUI();
        getFriendSelectionPage();
    }

    private void initUI() {

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPagerHorizontal = (ViewPager) findViewById(R.id.viewpagerHorizontal);
    }

    @Override
    public void onStart(){
        super.onStart();
        reloadAdapter();
    }

    private void reloadAdapter() {

        if(adapter.getItem(0) != null) {
            FriendFragment friendFragment = new FriendFragment(SelectFriendToGroupActivity.this, userid, verticalShown);
            adapter.updateFragment(0, friendFragment);
            adapter.notifyDataSetChanged();
        }
    }

    private void getFriendSelectionPage() {

        adapter = new SpecialSelectTabAdapter(this.getSupportFragmentManager());
        adapter.addFragment(new FriendFragment(SelectFriendToGroupActivity.this, userid, verticalShown)," ");
        viewPager.setAdapter(adapter);
    }

    public void checkSelectedPerson(){

        selectedFriendListInstance = SelectedFriendList.getInstance();

        if (selectedFriendListInstance.getSelectedFriendList().getResultArray().size() == 0) {
            Toast.makeText(this, getResources().getString(R.string.selectLeastOneFriend), Toast.LENGTH_SHORT).show();
            return;
        }

        /*if(comingPageName != null){
            if(comingPageName.equals(DisplayGroupDetail.class.getSimpleName())) {
                addFriendToGroup();
                finish();
            }
        }else
            startActivity(new Intent(this, AddGroupDetailActivity.class));*/

        startActivity(new Intent(this, AddGroupActivity.class));
    }

    private void addFriendToGroup() {

       /* for(Friend friend : selectedFriendListInstance.getSelectedFriendList()) {
            DisplayGroupDetail.addFriendToGroup(friend);
        }

        FirebaseAddFriendToGroupAdapter addFriendToGroupAdapter =
                new FirebaseAddFriendToGroupAdapter(selectedFriendListInstance.getSelectedFriendList(), group.getGroupID());
        addFriendToGroupAdapter.addFriendsToGroup();*/
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
