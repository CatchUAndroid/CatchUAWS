package com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
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
import com.uren.catchu.GeneralUtils.ApiModelsProcess.UserGroupsProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.SelectedFriendList;

import java.util.ArrayList;
import java.util.List;

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
        try {
            layoutInflater = LayoutInflater.from(context);
            this.groupRequestResult = groupRequestResult;
            this.orgGroupRequestResult = groupRequestResult;
            this.returnCallback = returnCallback;
            this.itemClickListener = itemClickListener;
            this.context = context;
            this.operationType = operationType;
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
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

            try {
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

            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
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
            try {
                seledtedGroup = groupRequestResultResultArrayItem;
                notifyItemChanged(position);

                if (beforeSelectedPosition > -1)
                    notifyItemChanged(beforeSelectedPosition);

                beforeSelectedPosition = position;
                returnCallback.onReturn(groupRequestResultResultArrayItem);
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        public void setData(GroupRequestResultResultArrayItem groupRequestResultResultArrayItem, int position) {
            try {
                this.groupRequestResultResultArrayItem = groupRequestResultResultArrayItem;
                this.position = position;
                setGroupName();
                setGroupPhoto();
                setAdminButtonValues();
                updateTickImgv();
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        public void setGroupName() {
            try {
                if (groupRequestResultResultArrayItem.getName() != null && !groupRequestResultResultArrayItem.getName().trim().isEmpty()) {
                    if (groupRequestResultResultArrayItem.getName().trim().length() > GROUP_NAME_MAX_LENGTH)
                        this.groupnameTextView.setText(groupRequestResultResultArrayItem.getName().trim().substring(0, GROUP_NAME_MAX_LENGTH) + "...");
                    else
                        this.groupnameTextView.setText(groupRequestResultResultArrayItem.getName());
                }
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        public void setGroupPhoto() {
            try {
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
                                .load(context.getResources().getIdentifier("groups_icon_500", "drawable", context.getPackageName()))
                                .apply(RequestOptions.circleCropTransform())
                                .into(groupPicImgView);
                    }
                }
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        public String getShortGroupName() {
            String returnValue = "";
            try {
                String[] seperatedName = groupRequestResultResultArrayItem.getName().trim().split(" ");
                for (String word : seperatedName) {
                    if (returnValue.length() < 3)
                        returnValue = returnValue + word.substring(0, 1).toUpperCase();
                }
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
            return returnValue;
        }

        public void updateTickImgv() {
            try {
                if (seledtedGroup != null && groupRequestResultResultArrayItem != null) {
                    if (seledtedGroup.getGroupid().equals(groupRequestResultResultArrayItem.getGroupid()))
                        tickImgv.setVisibility(View.VISIBLE);
                    else
                        tickImgv.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        public void setAdminButtonValues() {
            try {
                if (groupRequestResultResultArrayItem.getGroupAdmin().equals(AccountHolderInfo.getUserID())) {
                    adminDisplayButton.setText(context.getResources().getString(R.string.adminText));
                    adminDisplayButton.setVisibility(View.VISIBLE);
                } else
                    adminDisplayButton.setVisibility(View.GONE);
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
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
        int size = 0;
        try {
            size = groupRequestResult.getResultArray().size();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return size;
    }

    public void updateAdapter(String searchText, ReturnCallback searchResultCallback) {
        try {
            this.searchResultCallback = searchResultCallback;
            getFilter().filter(searchText);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                try {
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
                } catch (Exception e) {
                    ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                            new Object() {
                            }.getClass().getEnclosingMethod().getName(), e.toString());
                    e.printStackTrace();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                try {
                    groupRequestResult = (GroupRequestResult) filterResults.values;
                    notifyDataSetChanged();

                    if (groupRequestResult != null && groupRequestResult.getResultArray() != null && groupRequestResult.getResultArray().size() > 0)
                        searchResultCallback.onReturn(groupRequestResult.getResultArray().size());
                    else
                        searchResultCallback.onReturn(0);
                } catch (Exception e) {
                    ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                            new Object() {
                            }.getClass().getEnclosingMethod().getName(), e.toString());
                    e.printStackTrace();
                }
            }
        };
    }
}