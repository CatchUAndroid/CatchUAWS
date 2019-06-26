package com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
/*import androidx.recyclerview.widget.RecyclerView;*/
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
import com.uren.catchu.GeneralUtils.ApiModelsProcess.UserGroupsProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.SelectedFriendList;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;

import static com.uren.catchu.Constants.NumericConstants.GROUP_NAME_MAX_LENGTH;
import static com.uren.catchu.Constants.StringConstants.GROUP_OP_CHOOSE_TYPE;
import static com.uren.catchu.Constants.StringConstants.GROUP_OP_VIEW_TYPE;

public class UserGroupsListAdapter extends RecyclerView.Adapter<UserGroupsListAdapter.UserGroupsListHolder> implements Filterable {

    private View view;
    private LayoutInflater layoutInflater;
    private Context context;
    private GroupRequestResult groupRequestResult;
    private GroupRequestResult orgGroupRequestResult;
    private String operationType;
    private GroupRequestResultResultArrayItem seledtedGroup;
    private ReturnCallback returnCallback;
    private ReturnCallback searchResultCallback;
    private ItemClickListener itemClickListener;

    private int beforeSelectedPosition = -1;
    private static final int SHOW_GROUP_DETAIL = 0;

    public UserGroupsListAdapter(Context context, GroupRequestResult groupRequestResult, ReturnCallback returnCallback,
                                 ItemClickListener itemClickListener, String operationType) {
        layoutInflater = LayoutInflater.from(context);
        this.groupRequestResult = groupRequestResult;
        this.orgGroupRequestResult = groupRequestResult;
        this.returnCallback = returnCallback;
        this.itemClickListener = itemClickListener;
        this.context = context;
        this.operationType = operationType;
    }

    @Override
    public UserGroupsListAdapter.UserGroupsListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.group_vert_list_item, parent, false);
        UserGroupsListAdapter.UserGroupsListHolder holder = new UserGroupsListAdapter.UserGroupsListHolder(view);
        return holder;
    }

    class UserGroupsListHolder extends RecyclerView.ViewHolder {

        TextView groupnameTextView;
        TextView shortGroupNameTv;
        ImageView groupPicImgView;
        Button adminDisplayButton;
        LinearLayout groupSelectMainLinLay;
        ImageView tickImgv;
        GroupRequestResultResultArrayItem groupRequestResultResultArrayItem;
        int position = 0;

        public UserGroupsListHolder(final View itemView) {
            super(itemView);

            groupPicImgView = view.findViewById(R.id.groupPicImgView);
            groupnameTextView = view.findViewById(R.id.groupnameTextView);
            adminDisplayButton = view.findViewById(R.id.adminDisplayButton);
            groupSelectMainLinLay = view.findViewById(R.id.groupSelectMainLinLay);
            tickImgv = view.findViewById(R.id.tickImgv);
            shortGroupNameTv = view.findViewById(R.id.shortGroupNameTv);
            setShapes();

            groupSelectMainLinLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (operationType.equals(GROUP_OP_CHOOSE_TYPE)) {
                        manageSelectedItem();
                    } else if (operationType.equals(GROUP_OP_VIEW_TYPE)) {
                        itemClickListener.onClick(groupRequestResultResultArrayItem, SHOW_GROUP_DETAIL);
                    }
                }
            });
        }

        private void setShapes() {
            tickImgv.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.DarkTurquoise, null),
                    context.getResources().getColor(R.color.White, null), GradientDrawable.OVAL, 50, 3));
            adminDisplayButton.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.White, null),
                    context.getResources().getColor(R.color.MediumSeaGreen, null), GradientDrawable.RECTANGLE, 15, 2));
            groupPicImgView.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                    0, GradientDrawable.OVAL, 50, 0));
        }

        public void manageSelectedItem() {
            seledtedGroup = groupRequestResultResultArrayItem;
            notifyItemChanged(position);

            if (beforeSelectedPosition > -1)
                notifyItemChanged(beforeSelectedPosition);

            beforeSelectedPosition = position;
            returnCallback.onReturn(groupRequestResultResultArrayItem);
        }

        public void setData(GroupRequestResultResultArrayItem groupRequestResultResultArrayItem, int position) {
            this.groupRequestResultResultArrayItem = groupRequestResultResultArrayItem;
            this.position = position;
            setGroupName();
            setGroupPhoto();
            setAdminButtonValues();
            updateTickImgv();
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
                Glide.with(context)
                        .load(groupRequestResultResultArrayItem.getGroupPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(groupPicImgView);
            } else {
                if (groupRequestResultResultArrayItem.getName() != null && !groupRequestResultResultArrayItem.getName().trim().isEmpty()) {
                    shortGroupNameTv.setVisibility(View.VISIBLE);
                    shortGroupNameTv.setText(getShortGroupName());
                    groupPicImgView.setImageDrawable(null);
                } else {
                    shortGroupNameTv.setVisibility(View.GONE);
                    Glide.with(context)
                            .load(R.drawable.groups_icon_500)
                            .apply(RequestOptions.circleCropTransform())
                            .into(groupPicImgView);
                }
            }
        }

        public String getShortGroupName() {
            String returnValue = "";
            String[] seperatedName = groupRequestResultResultArrayItem.getName().trim().split(" ");
            for (String word : seperatedName) {
                if (returnValue.length() < 3)
                    returnValue = returnValue + word.substring(0, 1).toUpperCase();
            }
            return returnValue;
        }

        public void updateTickImgv() {
            if (seledtedGroup != null && groupRequestResultResultArrayItem != null) {
                if (seledtedGroup.getGroupid().equals(groupRequestResultResultArrayItem.getGroupid()))
                    tickImgv.setVisibility(View.VISIBLE);
                else
                    tickImgv.setVisibility(View.GONE);
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
    public void onBindViewHolder(UserGroupsListAdapter.UserGroupsListHolder holder, int position) {

        GroupRequestResultResultArrayItem groupRequestResultResultArrayItem = groupRequestResult.getResultArray().get(position);
        holder.setData(groupRequestResultResultArrayItem, position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (groupRequestResult != null && groupRequestResult.getResultArray() != null)
            return groupRequestResult.getResultArray().size();
        else
            return 0;
    }

    public void updateAdapter(String searchText, ReturnCallback searchResultCallback) {
        this.searchResultCallback = searchResultCallback;
        getFilter().filter(searchText);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
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

                filterResults.values = groupRequestResult;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                groupRequestResult = (GroupRequestResult) filterResults.values;
                notifyDataSetChanged();

                if (groupRequestResult != null && groupRequestResult.getResultArray() != null && groupRequestResult.getResultArray().size() > 0)
                    searchResultCallback.onReturn(groupRequestResult.getResultArray().size());
                else
                    searchResultCallback.onReturn(0);
            }
        };
    }
}