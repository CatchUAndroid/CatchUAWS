package com.uren.catchu.GroupPackage.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
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
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.SearchTab.SearchFragment;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.UserGroups;

import java.util.ArrayList;
import java.util.List;

import catchu.model.FollowInfoResultArrayItem;
import catchu.model.GroupRequest;
import catchu.model.GroupRequestGroupParticipantArrayItem;
import catchu.model.GroupRequestResultResultArrayItem;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.CHANGE_GROUP_ADMIN;
import static com.uren.catchu.Constants.StringConstants.EXIT_GROUP;

public class GroupDetailListAdapter extends RecyclerView.Adapter<GroupDetailListAdapter.MyViewHolder> {

    View view;
    LinearLayout specialListLinearLayout;
    LayoutInflater layoutInflater;
    List<UserProfileProperties> groupParticipantList;
    GroupRequestResultResultArrayItem groupRequestResultResultArrayItem;
    ItemClickListener itemClickListener;

    Context context;
    Activity activity;

    public static final int CODE_DISPLAY_PROFILE = 0;
    public static final int CODE_REMOVE_FROM_GROUP = 1;
    public static final int CODE_CHANGE_AS_ADMIN = 2;


    TextView textview;
    CardView addFriendCardView;

    int groupAdminPosition = 0;
    GradientDrawable imageShape;
    GradientDrawable adminButtonShape;

    public GroupDetailListAdapter(Context context, List<UserProfileProperties> groupParticipantList, GroupRequestResultResultArrayItem groupRequestResultResultArrayItem,
                                  ItemClickListener itemClickListener) {
        layoutInflater = LayoutInflater.from(context);
        initVaribles();
        this.groupParticipantList.addAll(groupParticipantList);
        this.groupRequestResultResultArrayItem = groupRequestResultResultArrayItem;
        this.itemClickListener = itemClickListener;
        this.context = context;
        activity = (Activity) context;
        imageShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                0, GradientDrawable.OVAL, 50, 0);
        adminButtonShape = ShapeUtil.getShape(context.getResources().getColor(R.color.White, null),
                context.getResources().getColor(R.color.MediumSeaGreen, null), GradientDrawable.RECTANGLE, 15, 2);
    }

    public void initVaribles() {
        this.groupParticipantList = new ArrayList<UserProfileProperties>();
        this.groupRequestResultResultArrayItem = new GroupRequestResultResultArrayItem();
    }

    @Override
    public GroupDetailListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.group_detail_list, parent, false);
        GroupDetailListAdapter.MyViewHolder holder = new GroupDetailListAdapter.MyViewHolder(view);

        textview = activity.findViewById(R.id.personCntTv);
        textview.setText(Integer.toString(groupParticipantList.size()));

        addFriendCardView = activity.findViewById(R.id.addFriendCardView);

        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nameSurname;
        TextView username;
        TextView shortUsernameTv;
        UserProfileProperties userProfile;
        Button adminDisplayBtn;
        ImageView specialProfileImgView;
        int position = 0;

        public MyViewHolder(View itemView) {
            super(itemView);

            specialProfileImgView = view.findViewById(R.id.specialPictureImgView);
            nameSurname = view.findViewById(R.id.specialNameTextView);
            shortUsernameTv = view.findViewById(R.id.shortUsernameTv);
            username = view.findViewById(R.id.usernameTextView);
            specialListLinearLayout = view.findViewById(R.id.specialListLinearLayout);
            adminDisplayBtn = view.findViewById(R.id.adminDisplayBtn);
            specialProfileImgView.setBackground(imageShape);
            adminDisplayBtn.setBackground(adminButtonShape);

            specialListLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!AccountHolderInfo.getUserID().equals(userProfile.getUserid())) {
                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);

                        adapter.add(context.getResources().getString(R.string.viewTheProfile));

                        if (AccountHolderInfo.getUserID().equals(groupRequestResultResultArrayItem.getGroupAdmin()))
                            adapter.add(context.getResources().getString(R.string.removeFromGroup));

                        if (AccountHolderInfo.getUserID().equals(groupRequestResultResultArrayItem.getGroupAdmin()))
                            adapter.add(context.getResources().getString(R.string.changeAsAdmin));

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(userProfile.getName());

                        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {

                                if (item == CODE_DISPLAY_PROFILE) {

                                    Toast.makeText(context, "View profile clicked", Toast.LENGTH_SHORT).show();
                                    FollowInfoResultArrayItem followInfoResultArrayItem = new FollowInfoResultArrayItem();
                                    followInfoResultArrayItem = getFollowProperties();
                                    itemClickListener.onClick(followInfoResultArrayItem, item);

                                } else if (item == CODE_REMOVE_FROM_GROUP) {
                                    exitFromGroup(userProfile.getUserid());
                                } else if (item == CODE_CHANGE_AS_ADMIN) {
                                    changeAdministrator(userProfile.getUserid());
                                    Toast.makeText(context, "Change as admin clicked", Toast.LENGTH_SHORT).show();
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

        public FollowInfoResultArrayItem getFollowProperties() {
            FollowInfoResultArrayItem followItem = new FollowInfoResultArrayItem();
            followItem.setBirthday(userProfile.getName());
            followItem.setEmail(userProfile.getUsername());
            followItem.setProfilePhotoUrl(userProfile.getProfilePhotoUrl());
            followItem.setUserid(userProfile.getUserid());
            followItem.setIsPrivateAccount(userProfile.getIsPrivateAccount());
            return followItem;
        }

        public void setData(UserProfileProperties userProfile, int position) {
            this.position = position;
            this.userProfile = userProfile;
            setName();
            setUserName();
            setGroupAdmin();
            UserDataUtil.setProfilePicture(context, userProfile.getProfilePhotoUrl(),
                    userProfile.getName(), shortUsernameTv, specialProfileImgView);
        }

        public void setName() {
            if (AccountHolderInfo.getUserID() != null && !AccountHolderInfo.getUserID().trim().isEmpty() &&
                    userProfile.getUserid() != null && !userProfile.getUserid().trim().isEmpty()) {

                if (AccountHolderInfo.getUserID().equals(userProfile.getUserid()))
                    this.nameSurname.setText(context.getResources().getString(R.string.youText));
                else
                    UserDataUtil.setName(userProfile.getName(), nameSurname);
            } else if (userProfile.getName() != null && !userProfile.getName().trim().isEmpty())
                UserDataUtil.setName(userProfile.getName(), nameSurname);
        }

        public void setUserName() {
            if (userProfile.getUsername() != null && !userProfile.getUsername().trim().isEmpty())
                this.username.setText(userProfile.getUsername());
        }

        public void setGroupAdmin() {
            if (groupRequestResultResultArrayItem.getGroupAdmin() != null && !groupRequestResultResultArrayItem.getGroupAdmin().trim().isEmpty() &&
                    userProfile.getUserid() != null && !userProfile.getUserid().trim().isEmpty()) {
                if (groupRequestResultResultArrayItem.getGroupAdmin().equals(userProfile.getUserid())) {
                    adminDisplayBtn.setVisibility(View.VISIBLE);
                    groupAdminPosition = position;
                } else
                    adminDisplayBtn.setVisibility(View.GONE);
            } else
                adminDisplayBtn.setVisibility(View.GONE);
        }

        public void exitFromGroup(final String userid) {

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
                    itemClickListener.onClick(groupParticipantList, CODE_REMOVE_FROM_GROUP);
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

        public void changeAdministrator(final String userid) {

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

}