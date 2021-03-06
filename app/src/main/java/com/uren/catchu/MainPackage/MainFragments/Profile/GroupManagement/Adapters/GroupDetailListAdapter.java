package com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
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

import com.uren.catchu.GeneralUtils.ApiModelsProcess.UserGroupsProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.List;

import catchu.model.GroupRequestResultResultArrayItem;
import catchu.model.User;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.CHAR_AMPERSAND;

public class GroupDetailListAdapter extends RecyclerView.Adapter<GroupDetailListAdapter.GroupDetailListHolder> {

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
        initVariables();
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

    public void initVariables() {
        this.groupParticipantList = new ArrayList<UserProfileProperties>();
        this.groupRequestResultResultArrayItem = new GroupRequestResultResultArrayItem();
    }

    @Override
    public GroupDetailListAdapter.GroupDetailListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        GroupDetailListHolder holder = null;
        view = layoutInflater.inflate(R.layout.group_detail_list, parent, false);
        holder = new GroupDetailListHolder(view);

        textview = activity.findViewById(R.id.personCntTv);
        textview.setText(Integer.toString(groupParticipantList.size()));

        addFriendCardView = activity.findViewById(R.id.addFriendCardView);

        return holder;
    }

    class GroupDetailListHolder extends RecyclerView.ViewHolder {

        TextView profileName;
        TextView profileUserName;
        TextView shortUsernameTv;
        UserProfileProperties userProfile;
        Button adminDisplayBtn;
        ImageView specialProfileImgView;
        int position = 0;

        public GroupDetailListHolder(View itemView) {
            super(itemView);

            specialProfileImgView = view.findViewById(R.id.specialPictureImgView);
            profileName = view.findViewById(R.id.profileName);
            shortUsernameTv = view.findViewById(R.id.shortUsernameTv);
            profileUserName = view.findViewById(R.id.profileUserName);
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

                        builder.setTitle(getAlertDialogTitle());

                        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {

                                if (item == CODE_DISPLAY_PROFILE) {

                                    Toast.makeText(context, "View profile clicked", Toast.LENGTH_SHORT).show();
                                    User user = getFollowProperties();
                                    itemClickListener.onClick(user, CODE_DISPLAY_PROFILE);

                                } else if (item == CODE_REMOVE_FROM_GROUP)
                                    exitFromGroup(userProfile.getUserid());
                                else if (item == CODE_CHANGE_AS_ADMIN)
                                    changeAdministrator(userProfile.getUserid());
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            });
        }

        public String getAlertDialogTitle() {
            String title = "";
            if (userProfile.getName() != null && !userProfile.getName().isEmpty())
                title = userProfile.getName();
            else if (userProfile.getUsername() != null && !userProfile.getUsername().isEmpty())
                title = CHAR_AMPERSAND + userProfile.getUsername();
            return title;
        }

        public User getFollowProperties() {
            User user = null;
            user = new User();
            user.setEmail(userProfile.getUsername());
            user.setProfilePhotoUrl(userProfile.getProfilePhotoUrl());
            user.setUserid(userProfile.getUserid());
            user.setIsPrivateAccount(userProfile.getIsPrivateAccount());
            user.setName(userProfile.getName());
            user.setUsername(userProfile.getUsername());
            user.setProvider(userProfile.getProvider());
            return user;
        }

        public void setData(UserProfileProperties userProfile, int position) {
            this.position = position;
            this.userProfile = userProfile;
            UserDataUtil.setName(userProfile.getName(), profileName);
            UserDataUtil.setUsername(userProfile.getUsername(), profileUserName);
            setGroupAdmin();
            UserDataUtil.setProfilePicture(context, userProfile.getProfilePhotoUrl(),
                    userProfile.getName(), userProfile.getUsername(), shortUsernameTv, specialProfileImgView);
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

            UserGroupsProcess.exitFromGroup(userid, groupRequestResultResultArrayItem.getGroupid(), new CompleteCallback() {
                @Override
                public void onComplete(Object object) {
                    groupParticipantList.remove(position);
                    itemClickListener.onClick(groupParticipantList, CODE_REMOVE_FROM_GROUP);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                    textview.setText(Integer.toString(getItemCount()));
                }

                @Override
                public void onFailed(Exception e) {
                    CommonUtils.showToastShort(context, context.getResources().getString(R.string.error) +
                            context.getResources().getString(R.string.SOMETHING_WENT_WRONG));
                }
            });
        }

        public void changeAdministrator(final String userid) {

            UserGroupsProcess.changeGroupAdmin(userid, AccountHolderInfo.getUserID(), groupRequestResultResultArrayItem.getGroupid(),
                    new CompleteCallback() {
                        @Override
                        public void onComplete(Object object) {
                            addFriendCardView.setVisibility(View.GONE);
                            groupRequestResultResultArrayItem.setGroupAdmin(userid);
                            notifyItemChanged(position);
                            notifyItemChanged(groupAdminPosition);
                            itemClickListener.onClick(groupRequestResultResultArrayItem, CODE_CHANGE_AS_ADMIN);
                        }

                        @Override
                        public void onFailed(Exception e) {
                            CommonUtils.showToastShort(context, context.getResources().getString(R.string.error) +
                                    context.getResources().getString(R.string.SOMETHING_WENT_WRONG));
                        }
                    });
        }
    }

    @Override
    public void onBindViewHolder(GroupDetailListAdapter.GroupDetailListHolder holder, int position) {
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