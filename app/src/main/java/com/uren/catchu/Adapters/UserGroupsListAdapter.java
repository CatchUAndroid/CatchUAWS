package com.uren.catchu.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.uren.catchu.ApiGatewayFunctions.GroupResultProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.ImageCache.ImageLoader;
import com.uren.catchu.GroupPackage.DisplayGroupDetailActivity;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.SelectGroupActivity;
import com.uren.catchu.SharePackage.ShareDetailActivity;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.SelectedGroupList;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

import catchu.model.FriendList;
import catchu.model.GroupRequest;
import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.EXIT_GROUP;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_GROUP_ID;
import static com.uren.catchu.Constants.StringConstants.displayRounded;
import static com.uren.catchu.Constants.StringConstants.groupsCacheDirectory;

public class UserGroupsListAdapter extends RecyclerView.Adapter<UserGroupsListAdapter.MyViewHolder> implements Filterable{

    public ImageLoader imageLoader;
    View view;
    LayoutInflater layoutInflater;
    Context context;

    private GroupRequestResult groupRequestResult;
    GroupRequestResult orgGroupRequestResult;
    Activity activity;
    String pendingActivityName;
    GroupRequestResultResultArrayItem seledtedGroup;
    int beforeSelectedPosition = -1;

    private static final int SHOW_GROUP_DETAIL = 0;
    private static final int EXIT_FROM_GROUP = 1;

    public UserGroupsListAdapter(Context context, GroupRequestResult groupRequestResult) {
        layoutInflater = LayoutInflater.from(context);
        this.groupRequestResult = groupRequestResult;
        this.orgGroupRequestResult = groupRequestResult;
        this.context = context;
        activity = (Activity) context;
        pendingActivityName = context.getClass().getSimpleName();
        Log.i("Info", "pendingActivityName:" + pendingActivityName);
        imageLoader = new ImageLoader(context.getApplicationContext(), groupsCacheDirectory);
    }

    @Override
    public UserGroupsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.group_vert_list_item, parent, false);
        UserGroupsListAdapter.MyViewHolder holder = new UserGroupsListAdapter.MyViewHolder(view);
        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView groupnameTextView;
        ImageView groupPicImgView;
        Button adminDisplayButton;
        LinearLayout groupSelectMainLinLay;
        RadioButton selectGroupRb;
        GroupRequestResultResultArrayItem groupRequestResultResultArrayItem;
        int position = 0;

        public MyViewHolder(final View itemView) {
            super(itemView);

            groupPicImgView = (ImageView) view.findViewById(R.id.groupPicImgView);
            groupnameTextView = (TextView) view.findViewById(R.id.groupnameTextView);
            adminDisplayButton = (Button) view.findViewById(R.id.adminDisplayButton);
            groupSelectMainLinLay = (LinearLayout) view.findViewById(R.id.groupSelectMainLinLay);
            selectGroupRb = view.findViewById(R.id.selectGroupRb);

            if(pendingActivityName.equals(SelectGroupActivity.class.getSimpleName()))
                selectGroupRb.setVisibility(View.VISIBLE);

            /*groupSelectMainLinLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    hideKeyBoard(itemView);
                    ViewGroup viewGroup = (ViewGroup) v;

                    showGroupDetail();
                }
            });*/

            groupSelectMainLinLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(pendingActivityName.equals(SelectGroupActivity.class.getSimpleName())) {
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

        public void manageSelectedItem(){
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
                    imageLoader.removeImageViewFromMap(groupRequestResultResultArrayItem.getGroupPhotoUrl());
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
            this.groupnameTextView.setText(groupRequestResultResultArrayItem.getName());
            imageLoader.DisplayImage(groupRequestResultResultArrayItem.getGroupPhotoUrl(),
                    groupPicImgView, displayRounded);

            Log.i("Info", "  >>groupRequestResultResultArrayItem.getGroupAdmin():" + groupRequestResultResultArrayItem.getGroupAdmin());
            Log.i("Info", "  >>AccountHolderInfo.getUserID()                    :" + AccountHolderInfo.getUserID());
            Log.i("Info", "  >>groupRequestResultResultArrayItem.getGroupid()   :" + groupRequestResultResultArrayItem.getGroupid());
            Log.i("Info", "  >>==============================");

            setAdminButtonValues();
            setRadioButtonValues();
        }

        public void setRadioButtonValues(){
            if(seledtedGroup != null && groupRequestResultResultArrayItem != null) {
                if (seledtedGroup.getGroupid().equals(groupRequestResultResultArrayItem.getGroupid()))
                    selectGroupRb.setChecked(true);
                else
                    selectGroupRb.setChecked(false);
            }
        }

        public void setAdminButtonValues(){
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

    /*public void hideKeyBoard(View view) {

        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }*/

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

                if (searchString.isEmpty())
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
                filterResults.values = (GroupRequestResult) groupRequestResult;
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