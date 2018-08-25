package com.uren.catchu.GroupPackage.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uren.catchu.ApiGatewayFunctions.GroupResultProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.ImageCache.ImageLoader;
import com.uren.catchu.GroupPackage.DisplayGroupDetailActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import catchu.model.GroupRequest;
import catchu.model.GroupRequestResult;
import catchu.model.UserProfile;

import static com.uren.catchu.Constants.StringConstants.EXIT_GROUP;
import static com.uren.catchu.Constants.StringConstants.displayRounded;
import static com.uren.catchu.Constants.StringConstants.friendsCacheDirectory;

public class GroupDetailListAdapter extends RecyclerView.Adapter<GroupDetailListAdapter.MyViewHolder> {

    public ImageLoader imageLoader;
    View view;
    private ImageView specialProfileImgView;
    LinearLayout specialListLinearLayout;
    LayoutInflater layoutInflater;
    List<UserProfile> groupParticipantList;
    GroupRequestResult groupRequestResult;

    Context context;
    Activity activity;

    public static final int CODE_REMOVE_FROM_GROUP = 0;
    public static final int CODE_DISPLAY_PROFILE = 1;

    TextView textview;

    public GroupDetailListAdapter(Context context, List<UserProfile> groupParticipantList, GroupRequestResult groupRequestResult) {
        layoutInflater = LayoutInflater.from(context);
        this.groupParticipantList.addAll(groupParticipantList);
        this.groupRequestResult.setResultArray(groupRequestResult.getResultArray());
        Collections.sort(groupParticipantList, new CustomComparator());
        this.context = context;
        activity = (Activity) context;
        imageLoader = new ImageLoader(context.getApplicationContext(), friendsCacheDirectory);
    }

    public class CustomComparator implements Comparator<UserProfile> {
        @Override
        public int compare(UserProfile o1, UserProfile o2) {
            return o1.getUserInfo().getName().compareToIgnoreCase(o2.getUserInfo().getName());
        }
    }

    @Override
    public GroupDetailListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.group_detail_list, parent, false);
        GroupDetailListAdapter.MyViewHolder holder = new GroupDetailListAdapter.MyViewHolder(view);

        textview = (TextView) activity.findViewById(R.id.personCntTv);
        textview.setText(Integer.toString(groupParticipantList.size()));

        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView userNameSurname;
        UserProfile userProfile;
        Button adminDisplayBtn;

        int position = 0;

        public MyViewHolder(View itemView) {
            super(itemView);

            specialProfileImgView = (ImageView) view.findViewById(R.id.specialPictureImgView);
            userNameSurname = (TextView) view.findViewById(R.id.specialNameTextView);
            specialListLinearLayout = (LinearLayout) view.findViewById(R.id.specialListLinearLayout);
            adminDisplayBtn = (Button) view.findViewById(R.id.adminDisplayBtn);

            //final TextView textview = (TextView) activity.findViewById(R.id.personCntTv);
            //textview.setText(Integer.toString(data.size()));

            specialListLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!AccountHolderInfo.getUserID().equals(userProfile.getUserInfo().getUserid())) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);

                        adapter.add(context.getResources().getString(R.string.removeFromGroup));
                        adapter.add(context.getResources().getString(R.string.viewTheProfile));

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);

                        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {

                                if (item == CODE_REMOVE_FROM_GROUP) {
                                    exitFromGroup(userProfile.getUserInfo().getUserid(), position);



                                    //if (context instanceof DisplayGroupDetailActivity) {
                                    //    ((DisplayGroupDetailActivity) context).setGroupFriendList(data);
                                   // }

                                } else if (item == CODE_DISPLAY_PROFILE) {


                                } else {
                                    CommonUtils.showToast(context, context.getResources().getString(R.string.error) +
                                            context.getResources().getString(R.string.technicalError));
                                }
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            });
        }

        public void setData(UserProfile userProfile, int position) {

            //kullanici adi soyadi bilgisi yazilacak
            if (AccountHolderInfo.getUserID().equals(userProfile.getUserInfo().getUserid()))
                this.userNameSurname.setText(context.getResources().getString(R.string.youText));
            else
                this.userNameSurname.setText(userProfile.getUserInfo().getName());

            //Admin grup box degeri eklenecek
            if (groupRequestResult.getResultArray().get(0).getGroupAdmin().equals(userProfile.getUserInfo().getUserid()))
                adminDisplayBtn.setVisibility(View.VISIBLE);
            else
                adminDisplayBtn.setVisibility(View.GONE);

            this.position = position;
            this.userProfile = userProfile;
            imageLoader.DisplayImage(userProfile.getUserInfo().getProfilePhotoUrl(), specialProfileImgView, displayRounded);
        }

        public void exitFromGroup(String userid, final int position){

            final GroupRequest groupRequest = new GroupRequest();
            groupRequest.setRequestType(EXIT_GROUP);
            groupRequest.setUserid(userid);
            groupRequest.setGroupid(groupRequestResult.getResultArray().get(0).getGroupid());

            GroupResultProcess groupResultProcess = new GroupResultProcess(new OnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    groupParticipantList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                    textview.setText(Integer.toString(getItemCount()));
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

    @Override
    public void onBindViewHolder(GroupDetailListAdapter.MyViewHolder holder, int position) {
        UserProfile selectedFriend = groupParticipantList.get(position);
        holder.setData(selectedFriend, position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return groupParticipantList.size();
    }
}