package com.uren.catchu.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.ApiGatewayFunctions.GroupResultProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.GroupPackage.DisplayGroupDetailActivity;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.SelectGroupActivity;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.SelectedGroupList;

import java.util.ArrayList;
import java.util.List;

import catchu.model.GroupRequest;
import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;

import static com.uren.catchu.Constants.NumericConstants.GROUP_NAME_MAX_LENGTH;
import static com.uren.catchu.Constants.StringConstants.EXIT_GROUP;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_GROUP_ID;

public class UserGroupsListAdapter extends RecyclerView.Adapter<UserGroupsListAdapter.MyViewHolder> implements Filterable {

    View view;
    LayoutInflater layoutInflater;
    Context context;
    GroupRequestResult groupRequestResult;
    GroupRequestResult orgGroupRequestResult;
    Activity activity;
    String pendingActivityName;
    GroupRequestResultResultArrayItem seledtedGroup;
    int beforeSelectedPosition = -1;
    GradientDrawable groupPhotoShape;
    GradientDrawable adminButtonShape;

    private static final int SHOW_GROUP_DETAIL = 0;
    private static final int EXIT_FROM_GROUP = 1;

    public UserGroupsListAdapter(Context context, GroupRequestResult groupRequestResult) {
        layoutInflater = LayoutInflater.from(context);
        this.groupRequestResult = groupRequestResult;
        this.orgGroupRequestResult = groupRequestResult;
        this.context = context;
        activity = (Activity) context;
        pendingActivityName = context.getClass().getSimpleName();
        adminButtonShape = ShapeUtil.getShape(context.getResources().getColor(R.color.White, null),
                context.getResources().getColor(R.color.MediumSeaGreen, null), GradientDrawable.RECTANGLE, 15, 2);
    }

    @Override
    public UserGroupsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.group_vert_list_item, parent, false);
        UserGroupsListAdapter.MyViewHolder holder = new UserGroupsListAdapter.MyViewHolder(view);
        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView groupnameTextView;
        TextView shortGroupNameTv;
        ImageView groupPicImgView;
        Button adminDisplayButton;
        LinearLayout groupSelectMainLinLay;
        RadioButton selectGroupRb;
        GroupRequestResultResultArrayItem groupRequestResultResultArrayItem;
        int position = 0;

        public MyViewHolder(final View itemView) {
            super(itemView);

            groupPicImgView = view.findViewById(R.id.groupPicImgView);
            groupnameTextView = view.findViewById(R.id.groupnameTextView);
            adminDisplayButton = view.findViewById(R.id.adminDisplayButton);
            groupSelectMainLinLay = view.findViewById(R.id.groupSelectMainLinLay);
            selectGroupRb = view.findViewById(R.id.selectGroupRb);
            shortGroupNameTv = view.findViewById(R.id.shortGroupNameTv);
            adminDisplayButton.setBackground(adminButtonShape);

            if (pendingActivityName.equals(SelectGroupActivity.class.getSimpleName()))
                selectGroupRb.setVisibility(View.VISIBLE);

            groupSelectMainLinLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pendingActivityName.equals(SelectGroupActivity.class.getSimpleName())) {
                        selectGroupRb.setChecked(true);
                        manageSelectedItem();
                    }
                }
            });

            groupSelectMainLinLay.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showGroupDetail();
                    return false;
                }
            });

            selectGroupRb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manageSelectedItem();
                }
            });
        }

        public void manageSelectedItem() {
            seledtedGroup = groupRequestResultResultArrayItem;

            notifyItemChanged(position);

            if (beforeSelectedPosition > -1)
                notifyItemChanged(beforeSelectedPosition);

            beforeSelectedPosition = position;
            List<GroupRequestResultResultArrayItem> itemList = new ArrayList<>();
            itemList.add(groupRequestResultResultArrayItem);
            SelectedGroupList.getInstance().setGroupRequestResultList(itemList);
        }

        private void showGroupDetail() {

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
            adapter.add(context.getResources().getString(R.string.groupInformation));
            adapter.add(context.getResources().getString(R.string.exitFromGroup));

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(groupRequestResultResultArrayItem.getName());

            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    if (item == SHOW_GROUP_DETAIL) {

                        Intent intent = new Intent(context, DisplayGroupDetailActivity.class);
                        intent.putExtra(PUTEXTRA_GROUP_ID, groupRequestResultResultArrayItem.getGroupid());
                        context.startActivity(intent);

                    } else if (item == EXIT_FROM_GROUP) {

                        exitFromGroup();

                    } else {
                        CommonUtils.showToast(context, context.getResources().getString(R.string.error) +
                                context.getResources().getString(R.string.technicalError));
                    }
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }

        public void exitFromGroup() {

            AccountHolderInfo.getToken(new TokenCallback() {
                @Override
                public void onTokenTaken(String token) {
                    startExitFromGroupProcess(token);
                }
            });
        }

        private void startExitFromGroupProcess(String token) {

            final GroupRequest groupRequest = new GroupRequest();
            groupRequest.setRequestType(EXIT_GROUP);
            groupRequest.setUserid(AccountHolderInfo.getUserID());
            groupRequest.setGroupid(groupRequestResultResultArrayItem.getGroupid());

            GroupResultProcess groupResultProcess = new GroupResultProcess(new OnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    groupRequestResult.getResultArray().remove(groupRequestResultResultArrayItem);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                }

                @Override
                public void onFailure(Exception e) {

                }

                @Override
                public void onTaskContinue() {

                }
            }, groupRequest, token);

            groupResultProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        public void setData(GroupRequestResultResultArrayItem groupRequestResultResultArrayItem, int position) {
            this.groupRequestResultResultArrayItem = groupRequestResultResultArrayItem;
            this.position = position;
            setGroupName();
            setGroupPhoto();
            setAdminButtonValues();
            setRadioButtonValues();
        }

        public void setGroupName() {
            if (groupRequestResultResultArrayItem.getName() != null && !groupRequestResultResultArrayItem.getName().trim().isEmpty()) {
                if (groupRequestResultResultArrayItem.getName().trim().length() > GROUP_NAME_MAX_LENGTH)
                    this.groupnameTextView.setText(groupRequestResultResultArrayItem.getName().trim().substring(0, GROUP_NAME_MAX_LENGTH) + "...");
                else
                    this.groupnameTextView.setText(groupRequestResultResultArrayItem.getName());
            }
        }

        public void setGroupPhoto() {
            if (groupRequestResultResultArrayItem.getGroupPhotoUrl() != null && !groupRequestResultResultArrayItem.getGroupPhotoUrl().trim().isEmpty()) {
                shortGroupNameTv.setVisibility(View.GONE);
                groupPicImgView.setBackground(null);
                Glide.with(context)
                        .load(groupRequestResultResultArrayItem.getGroupPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(groupPicImgView);
            } else {
                if (groupRequestResultResultArrayItem.getName() != null && !groupRequestResultResultArrayItem.getName().trim().isEmpty()) {
                    shortGroupNameTv.setVisibility(View.VISIBLE);
                    shortGroupNameTv.setText(getShortGroupName());
                    groupPhotoShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                            0, GradientDrawable.OVAL, 50, 0);
                    groupPicImgView.setBackground(groupPhotoShape);
                } else {
                    shortGroupNameTv.setVisibility(View.GONE);
                    groupPhotoShape = ShapeUtil.getShape(context.getResources().getColor(R.color.SteelBlue, null),
                            0, GradientDrawable.OVAL, 50, 0);
                    groupPicImgView.setBackground(groupPhotoShape);
                    Glide.with(context)
                            .load(context.getResources().getIdentifier("groups_icon_500", "drawable", context.getPackageName()))
                            .apply(RequestOptions.circleCropTransform())
                            .into(groupPicImgView);
                }
            }
        }

        public String getShortGroupName() {
            String returnValue = "";
            String[] seperatedName = groupRequestResultResultArrayItem.getName().trim().split(" ");
            for (String word : seperatedName) {
                if (returnValue.length() < 5)
                    returnValue = returnValue + word.substring(0, 1).toUpperCase();
            }
            return returnValue;
        }

        public void setRadioButtonValues() {
            if (seledtedGroup != null && groupRequestResultResultArrayItem != null) {
                if (seledtedGroup.getGroupid().equals(groupRequestResultResultArrayItem.getGroupid()))
                    selectGroupRb.setChecked(true);
                else
                    selectGroupRb.setChecked(false);
            }
        }

        public void setAdminButtonValues() {
            if (groupRequestResultResultArrayItem.getGroupAdmin().equals(AccountHolderInfo.getUserID())) {
                adminDisplayButton.setText(context.getResources().getString(R.string.adminText));
                adminDisplayButton.setVisibility(View.VISIBLE);
            } else
                adminDisplayButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(UserGroupsListAdapter.MyViewHolder holder, int position) {

        GroupRequestResultResultArrayItem groupRequestResultResultArrayItem = groupRequestResult.getResultArray().get(position);
        holder.setData(groupRequestResultResultArrayItem, position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return groupRequestResult.getResultArray().size();
    }

    public void updateAdapter(String searchText) {
        getFilter().filter(searchText);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String searchString = charSequence.toString();

                if (searchString.trim().isEmpty())
                    groupRequestResult = orgGroupRequestResult;
                else {
                    GroupRequestResult tempGroupRequestResult = new GroupRequestResult();
                    List<GroupRequestResultResultArrayItem> listItem = new ArrayList<>();
                    tempGroupRequestResult.setResultArray(listItem);

                    for (GroupRequestResultResultArrayItem item : orgGroupRequestResult.getResultArray()) {
                        if (item.getName().toLowerCase().contains(searchString.toLowerCase()))
                            tempGroupRequestResult.getResultArray().add(item);
                    }

                    groupRequestResult = tempGroupRequestResult;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = groupRequestResult;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                groupRequestResult = (GroupRequestResult) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}