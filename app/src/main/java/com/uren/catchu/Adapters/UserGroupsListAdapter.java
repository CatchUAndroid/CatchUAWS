package com.uren.catchu.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

import catchu.model.GroupRequest;
import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;

import static com.uren.catchu.Constants.StringConstants.EXIT_GROUP;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_GROUP_ID;
import static com.uren.catchu.Constants.StringConstants.displayRounded;
import static com.uren.catchu.Constants.StringConstants.groupsCacheDirectory;

public class UserGroupsListAdapter extends RecyclerView.Adapter<UserGroupsListAdapter.MyViewHolder>{

    public ImageLoader imageLoader;
    View view;
    LayoutInflater layoutInflater;
    Context context;

    private GroupRequestResult groupRequestResult;
    Activity activity;

    private static final int SHOW_GROUP_DETAIL = 0;
    private static final int EXIT_FROM_GROUP = 1;

    public UserGroupsListAdapter(Context context, GroupRequestResult groupRequestResult) {
        layoutInflater = LayoutInflater.from(context);
        this.groupRequestResult = groupRequestResult;
        this.context = context;
        activity = (Activity) context;
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
        LinearLayout specialListLinearLayout;
        GroupRequestResultResultArrayItem groupRequestResultResultArrayItem;
        int position = 0;

        public MyViewHolder(final View itemView) {
            super(itemView);

            groupPicImgView = (ImageView) view.findViewById(R.id.groupPicImgView);
            groupnameTextView = (TextView) view.findViewById(R.id.groupnameTextView);
            adminDisplayButton = (Button) view.findViewById(R.id.adminDisplayButton);
            specialListLinearLayout = (LinearLayout) view.findViewById(R.id.specialListLinearLayout);

            specialListLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    hideKeyBoard(itemView);
                    ViewGroup viewGroup = (ViewGroup) v;

                    showGroupDetail();

                    return true;
                }
            });
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

        public void exitFromGroup(){

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
            }, groupRequest);
            groupResultProcess.execute();
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


            try {
                if (groupRequestResultResultArrayItem.getGroupAdmin().equals(AccountHolderInfo.getUserID()))
                    adminDisplayButton.setText(context.getResources().getString(R.string.adminText));
                else
                    adminDisplayButton.setVisibility(View.GONE);
            }catch (NullPointerException e){
                adminDisplayButton.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBindViewHolder(UserGroupsListAdapter.MyViewHolder holder, int position) {

        GroupRequestResultResultArrayItem groupRequestResultResultArrayItem = groupRequestResult.getResultArray().get(position);
        holder.setData(groupRequestResultResultArrayItem, position);
    }

    public void hideKeyBoard(View view){

        InputMethodManager inputMethodManager =(InputMethodManager)context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return groupRequestResult.getResultArray().size();
    }
}