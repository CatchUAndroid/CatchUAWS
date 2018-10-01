package com.uren.catchu.GroupPackage.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.ImageCache.ImageLoader;
import com.uren.catchu.GroupPackage.DisplayGroupDetailActivity;
import com.uren.catchu.MainPackage.MainFragments.SearchTab.SearchFragment;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.UserGroups;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import catchu.model.GroupRequest;
import catchu.model.GroupRequestGroupParticipantArrayItem;
import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;
import catchu.model.UserProfile;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.CHANGE_GROUP_ADMIN;
import static com.uren.catchu.Constants.StringConstants.EXIT_GROUP;
import static com.uren.catchu.Constants.StringConstants.displayRounded;
import static com.uren.catchu.Constants.StringConstants.friendsCacheDirectory;

public class GroupDetailListAdapter extends RecyclerView.Adapter<GroupDetailListAdapter.MyViewHolder> {

    public ImageLoader imageLoader;
    View view;
    LinearLayout specialListLinearLayout;
    LayoutInflater layoutInflater;
    List<UserProfileProperties> groupParticipantList;
    GroupRequestResultResultArrayItem groupRequestResultResultArrayItem;

    Context context;
    Activity activity;
    private ItemClickListener mClickListener;

    public static final int CODE_DISPLAY_PROFILE = 0;
    public static final int CODE_REMOVE_FROM_GROUP = 1;
    public static final int CODE_CHANGE_AS_ADMIN = 2;


    TextView textview;
    CardView addFriendCardView;

    int groupAdminPosition = 0;

    public GroupDetailListAdapter(Context context, List<UserProfileProperties> groupParticipantList, GroupRequestResultResultArrayItem groupRequestResultResultArrayItem) {
        layoutInflater = LayoutInflater.from(context);
        initVaribles();
        this.groupParticipantList.addAll(groupParticipantList);
        this.groupRequestResultResultArrayItem = groupRequestResultResultArrayItem;
        //Collections.sort(groupParticipantList, new CustomComparator());
        this.context = context;
        activity = (Activity) context;
        imageLoader = new ImageLoader(context.getApplicationContext(), friendsCacheDirectory);
        Log.i("", "   ");
        Log.i("", "   ");
        Log.i("", "   ");
    }

    public void initVaribles(){
        this.groupParticipantList = new ArrayList<UserProfileProperties>();
        this.groupRequestResultResultArrayItem = new GroupRequestResultResultArrayItem();
    }

    /*public class CustomComparator implements Comparator<UserProfileProperties> {
        @Override
        public int compare(UserProfileProperties o1, UserProfileProperties o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }*/

    @Override
    public GroupDetailListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.group_detail_list, parent, false);
        GroupDetailListAdapter.MyViewHolder holder = new GroupDetailListAdapter.MyViewHolder(view);

        textview = (TextView) activity.findViewById(R.id.personCntTv);
        textview.setText(Integer.toString(groupParticipantList.size()));

        addFriendCardView = activity.findViewById(R.id.addFriendCardView);

        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nameSurname;
        TextView username;
        UserProfileProperties userProfile;
        Button adminDisplayBtn;
        ImageView specialProfileImgView;

        int position = 0;

        @Override
        public void onClick(View v) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }

        public MyViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            specialProfileImgView = (ImageView) view.findViewById(R.id.specialPictureImgView);
            nameSurname = (TextView) view.findViewById(R.id.specialNameTextView);
            username = view.findViewById(R.id.usernameTextView);
            specialListLinearLayout = (LinearLayout) view.findViewById(R.id.specialListLinearLayout);
            adminDisplayBtn = (Button) view.findViewById(R.id.adminDisplayBtn);

            specialListLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!AccountHolderInfo.getUserID().equals(userProfile.getUserid())) {
                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);

                        adapter.add(context.getResources().getString(R.string.viewTheProfile));

                        if(AccountHolderInfo.getUserID().equals(groupRequestResultResultArrayItem.getGroupAdmin()))
                            adapter.add(context.getResources().getString(R.string.removeFromGroup));

                        if(AccountHolderInfo.getUserID().equals(groupRequestResultResultArrayItem.getGroupAdmin()))
                            adapter.add(context.getResources().getString(R.string.changeAsAdmin));

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(userProfile.getName());

                        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {

                                if (item == CODE_DISPLAY_PROFILE){

                                    Toast.makeText(context, "View profile clicked", Toast.LENGTH_SHORT).show();
                                }
                                else if (item == CODE_REMOVE_FROM_GROUP) {
                                    exitFromGroup(userProfile.getUserid(), position);
                                }
                                else if(item == CODE_CHANGE_AS_ADMIN){
                                    changeAdministrator(userProfile.getUserid());
                                    Toast.makeText(context, "Change as admin clicked", Toast.LENGTH_SHORT).show();
                                }
                                else {
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

        public void setData(UserProfileProperties userProfile, int position) {

            //kullanici adi soyadi bilgisi yazilacak
            if (AccountHolderInfo.getUserID().equals(userProfile.getUserid()))
                this.nameSurname.setText(context.getResources().getString(R.string.youText));
            else
                this.nameSurname.setText(userProfile.getName());

            this.username.setText(userProfile.getUsername());

            //Admin grup box degeri eklenecek
            if (groupRequestResultResultArrayItem.getGroupAdmin().equals(userProfile.getUserid())) {
                adminDisplayBtn.setVisibility(View.VISIBLE);
                groupAdminPosition = position;
                Log.i("Info", "groupAdminPosition:" + groupAdminPosition);
            }
            else
                adminDisplayBtn.setVisibility(View.GONE);

            this.position = position;
            this.userProfile = userProfile;
            imageLoader.DisplayImage(userProfile.getProfilePhotoUrl(), specialProfileImgView, displayRounded);
        }

        public void exitFromGroup(final String userid, final int position){

            AccountHolderInfo.getToken(new TokenCallback() {
                @Override
                public void onTokenTaken(String token) {
                    startExitFromGroupProcess(userid, token);
                }
            });


        }

        private void startExitFromGroupProcess(String userid, String token) {

            final GroupRequest groupRequest = new GroupRequest();
            groupRequest.setRequestType(EXIT_GROUP);
            groupRequest.setUserid(userid);
            groupRequest.setGroupid(groupRequestResultResultArrayItem.getGroupid());

            GroupResultProcess groupResultProcess = new GroupResultProcess(new OnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    groupParticipantList.remove(position);
                    DisplayGroupDetailActivity.groupParticipantList.clear();
                    DisplayGroupDetailActivity.groupParticipantList.addAll(groupParticipantList);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                    textview.setText(Integer.toString(getItemCount()));
                }

                @Override
                public void onFailure(Exception e) {
                    CommonUtils.showToast(context, context.getResources().getString(R.string.error) +
                            e.getMessage());
                }

                @Override
                public void onTaskContinue() {

                }
            }, groupRequest, token);

            groupResultProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }

        public void changeAdministrator(final String userid){

            AccountHolderInfo.getToken(new TokenCallback() {
                @Override
                public void onTokenTaken(String token) {
                    startChangeAdministrator(userid, token);
                }
            });

        }

        private void startChangeAdministrator(final String userid, String token) {

            final GroupRequest groupRequest = new GroupRequest();

            List<GroupRequestGroupParticipantArrayItem> list = new ArrayList<GroupRequestGroupParticipantArrayItem>();
            GroupRequestGroupParticipantArrayItem groupRequestGroupParticipantArrayItem = new GroupRequestGroupParticipantArrayItem();
            groupRequestGroupParticipantArrayItem.setParticipantUserid(userid);
            list.add(groupRequestGroupParticipantArrayItem);

            groupRequest.setRequestType(CHANGE_GROUP_ADMIN);
            groupRequest.setUserid(AccountHolderInfo.getUserID());
            groupRequest.setGroupParticipantArray(list);
            groupRequest.setGroupid(groupRequestResultResultArrayItem.getGroupid());


            GroupResultProcess groupResultProcess = new GroupResultProcess(new OnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    addFriendCardView.setVisibility(View.GONE);
                    groupRequestResultResultArrayItem.setGroupAdmin(userid);
                    UserGroups.changeGroupAdmin(groupRequestResultResultArrayItem.getGroupid(), userid);
                    notifyItemChanged(position);
                    notifyItemChanged(groupAdminPosition);
                    SearchFragment.reloadAdapter();
                }

                @Override
                public void onFailure(Exception e) {
                    CommonUtils.showToast(context, context.getResources().getString(R.string.error) +
                            e.getMessage());
                }

                @Override
                public void onTaskContinue() {

                }
            }, groupRequest, token);

            groupResultProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }
    }

    @Override
    public void onBindViewHolder(GroupDetailListAdapter.MyViewHolder holder, int position) {
        UserProfileProperties selectedFriend = groupParticipantList.get(position);
        holder.setData(selectedFriend, position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return groupParticipantList.size();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public String getItem(int id) {
        return groupParticipantList.get(id).getName();
    }
}