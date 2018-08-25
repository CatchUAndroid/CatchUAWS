package com.uren.catchu.GroupPackage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uren.catchu.Adapters.SpecialSelectTabAdapter;
import com.uren.catchu.ApiGatewayFunctions.GroupResultProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.ImageCache.ImageLoader;
import com.uren.catchu.GroupPackage.Fragments.GroupDetailFragment;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.List;

import catchu.model.GroupRequest;
import catchu.model.GroupRequestResult;
import catchu.model.UserProfile;

import static com.uren.catchu.Constants.StringConstants.EXIT_GROUP;
import static com.uren.catchu.Constants.StringConstants.GET_GROUP_PARTICIPANT_LIST;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_GROUP_ID;
import static com.uren.catchu.Constants.StringConstants.displayRectangle;
import static com.uren.catchu.Constants.StringConstants.groupsCacheDirectory;

public class DisplayGroupDetailActivity extends AppCompatActivity {


    ImageView groupPictureImgV;
    ImageLoader imageLoader;
    TextView personCntTv;
    CollapsingToolbarLayout collapsingToolbarLayout;

    SpecialSelectTabAdapter adapter;

    ViewPager viewPager;
    String groupId;

    CardView addFriendCardView;
    CardView deleteGroupCardView;

    List<UserProfile> groupParticipantList;
    GroupRequestResult groupRequestResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_group_detail);

        groupParticipantList = new ArrayList<UserProfile>();

        imageLoader = new ImageLoader(this, groupsCacheDirectory);

        Intent i = getIntent();
        groupId = (String) i.getSerializableExtra(PUTEXTRA_GROUP_ID);

        Log.i("Info", "  >>groupId:" + groupId);

        setGUIVariables();
        getGroupParticipants();
        addListeners();
    }

    public void setGUIVariables() {
        groupPictureImgV = (ImageView) findViewById(R.id.groupPictureImgv);
        personCntTv = (TextView) findViewById(R.id.personCntTv);
        addFriendCardView = (CardView) findViewById(R.id.addFriendCardView);
        deleteGroupCardView = (CardView) findViewById(R.id.deleteGroupCardView);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
    }

    public void getGroupParticipants() {

        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setGroupid(this.groupId);
        groupRequest.setRequestType(GET_GROUP_PARTICIPANT_LIST);

        GroupResultProcess groupResultProcess = new GroupResultProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                groupRequestResult = (GroupRequestResult) object;
                groupParticipantList.addAll(groupRequestResult.getResultArrayParticipantList());
                setParticipantCount();
                setCardViewVisibility();
                setGroupTitle();
                setGroupImage();
                setupViewPager();
            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.showToast(DisplayGroupDetailActivity.this, getResources().getString(R.string.error) + e.getMessage());
            }

            @Override
            public void onTaskContinue() {

            }
        }, groupRequest);

        groupResultProcess.execute();
    }

    public void setParticipantCount() {
        personCntTv.setText(Integer.toString(groupParticipantList.size()));
    }

    public void setCardViewVisibility(){
        if (AccountHolderInfo.getUserID().equals(groupRequestResult.getResultArray().get(0).getGroupAdmin()))
            addFriendCardView.setVisibility(View.VISIBLE);
    }

    public void setGroupTitle(){
        collapsingToolbarLayout.setTitle(groupRequestResult.getResultArray().get(0).getName());
    }

    public void setGroupImage(){
        imageLoader.DisplayImage(groupRequestResult.getResultArray().get(0).getGroupPhotoUrl(), groupPictureImgV, displayRectangle);
    }

    public void addListeners() {

        /*addFriendCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddNewFriendActivity.class);
                intent.putExtra(comeFromPage, this.getClass().getSimpleName());
                intent.putExtra(groupConstant, group);
                startActivity(intent);
            }
        });*/

        deleteGroupCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showYesNoDialog(null, getResources().getString(R.string.areYouSureExitFromGroup));
            }
        });

    }


    private void setupViewPager() {
        adapter = new SpecialSelectTabAdapter(this.getSupportFragmentManager());
        adapter.addFragment(new GroupDetailFragment(groupParticipantList, groupRequestResult), " ");
        viewPager.setAdapter(adapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        reloadAdapter();
    }

    public void reloadAdapter() {

        if(groupParticipantList != null && groupRequestResult != null) {
            GroupDetailFragment groupDetailFragment = new GroupDetailFragment(groupParticipantList, groupRequestResult);
            //adapter.addFragment(new GroupDetailFragment(groupParticipantList, groupRequestResult), " ");
            adapter.updateFragment(0, groupDetailFragment);
            //viewPager.setAdapter(adapter);
        }
    }

    public void showYesNoDialog(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(DisplayGroupDetailActivity.this, android.R.style.Theme_Material_Dialog_Alert);

        builder.setIcon(R.drawable.warning_icon40);
        builder.setMessage(message);

        if (title != null)
            builder.setTitle(title);

        builder.setPositiveButton(getResources().getString(R.string.upperYes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                exitFromGroup(AccountHolderInfo.getUserID());
                finish();
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.upperNo), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void exitFromGroup(String userid){

        final GroupRequest groupRequest = new GroupRequest();
        groupRequest.setRequestType(EXIT_GROUP);
        groupRequest.setUserid(userid);
        groupRequest.setGroupid(groupId);

        GroupResultProcess groupResultProcess = new GroupResultProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                // TODO: 15.08.2018 - Singleton class kullanacaksak eger burada da ekleme olacaktir
                imageLoader.removeImageViewFromMap(groupRequestResult.getResultArray().get(0).getGroupPhotoUrl());
            }

            @Override
            public void onFailure(Exception e) {

            }

            @Override
            public void onTaskContinue() {

            }
        }, groupRequest);
        groupResultProcess.execute();
    }
}
