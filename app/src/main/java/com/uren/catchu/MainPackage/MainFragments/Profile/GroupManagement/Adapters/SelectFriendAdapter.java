package com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.MainPackage.MainFragments.Profile.GroupManagement.Interfaces.ClickCallback;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.SelectedFriendList;

import java.util.ArrayList;
import java.util.List;

import catchu.model.FriendList;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.NumericConstants.CODE_SELECT_ALL;
import static com.uren.catchu.Constants.NumericConstants.CODE_UNSELECT_ALL;

public class SelectFriendAdapter extends RecyclerView.Adapter<SelectFriendAdapter.SelectFriendHolder> implements Filterable {

    View view;
    LayoutInflater layoutInflater;
    Context context;
    Activity activity;
    FriendList friendList;
    FriendList orginalFriendList;
    RecyclerView horRecyclerView;
    LinearLayoutManager linearLayoutManager;
    boolean horAdapterUpdateChk;
    SelectFriendHorizontalAdapter selectFriendHorizontalAdapter = null;
    GradientDrawable imageShape;
    ReturnCallback returnCallback;

    public SelectFriendAdapter(Context context, FriendList friendList, ReturnCallback returnCallback) {
        try {
            layoutInflater = LayoutInflater.from(context);
            this.context = context;
            this.friendList = friendList;
            orginalFriendList = friendList;
            activity = (Activity) context;
            this.returnCallback = returnCallback;
            horAdapterUpdateChk = false;
            imageShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                    context.getResources().getColor(R.color.Orange, null), GradientDrawable.OVAL, 50, 0);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public SelectFriendAdapter.SelectFriendHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        SelectFriendAdapter.SelectFriendHolder holder = null;
        try {
            view = layoutInflater.inflate(R.layout.friend_vert_list_item, viewGroup, false);
            holder = new SelectFriendHolder(view);
            horRecyclerView = activity.findViewById(R.id.horRecyclerView);
            linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            horRecyclerView.setLayoutManager(linearLayoutManager);
            setHorizontalAdapter();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return holder;
    }

    private void setHorizontalAdapter() {
        try {
            selectFriendHorizontalAdapter = new SelectFriendHorizontalAdapter(context, new ClickCallback() {
                @Override
                public void onItemClick() {

                    SelectFriendAdapter.this.notifyDataSetChanged();

                    if (SelectedFriendList.getInstance().getSize() == 0)
                        horRecyclerView.setVisibility(View.GONE);
                    returnCallback.onReturn(null);
                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SelectFriendAdapter.SelectFriendHolder myViewHolder, int position) {
        UserProfileProperties userProfileProperties = friendList.getResultArray().get(position);
        myViewHolder.setData(userProfileProperties, position);
    }

    class SelectFriendHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView usernameTextView;
        TextView shortUserNameTv;
        ImageView profilePicImgView;
        //RadioButton selectRadioBtn;
        LinearLayout specialListLinearLayout;
        UserProfileProperties selectedFriend;
        int position = 0;

        public SelectFriendHolder(final View itemView) {
            super(itemView);

            try {
                profilePicImgView = view.findViewById(R.id.profilePicImgView);
                nameTextView = view.findViewById(R.id.nameTextView);
                usernameTextView = view.findViewById(R.id.usernameTextView);
                //selectRadioBtn = view.findViewById(R.id.selectRadioBtn);
                specialListLinearLayout = view.findViewById(R.id.specialListLinearLayout);
                shortUserNameTv = view.findViewById(R.id.shortUserNameTv);
                profilePicImgView.setBackground(imageShape);

                /*specialListLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtils.hideKeyBoard(context);
                        if (selectRadioBtn.isChecked()) {
                            selectRadioBtn.setChecked(false);
                            SelectedFriendList.getInstance().removeFriend(selectedFriend);
                            checkHorizontalAdapter();
                            returnCallback.onReturn(null);
                        } else {
                            selectRadioBtn.setChecked(true);
                            SelectedFriendList.getInstance().addFriend(selectedFriend);
                            checkHorizontalAdapter();
                            returnCallback.onReturn(null);
                        }
                    }
                });

                selectRadioBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtils.hideKeyBoard(context);
                        if (selectRadioBtn.isChecked()) {
                            if (!SelectedFriendList.getInstance().isUserInList(selectedFriend.getUserid())) {
                                SelectedFriendList.getInstance().addFriend(selectedFriend);
                                checkHorizontalAdapter();
                                returnCallback.onReturn(null);
                            }
                        } else {
                            if (SelectedFriendList.getInstance().isUserInList(selectedFriend.getUserid())) {
                                SelectedFriendList.getInstance().removeFriend(selectedFriend);
                                checkHorizontalAdapter();
                                returnCallback.onReturn(null);
                            }
                        }
                    }
                });*/
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        public void setData(UserProfileProperties selectedFriend, int position) {
            try {
                this.position = position;
                this.selectedFriend = selectedFriend;
                UserDataUtil.setName(selectedFriend.getName(), nameTextView);
                UserDataUtil.setUsername(selectedFriend.getUsername(), usernameTextView);
                UserDataUtil.setProfilePicture(context, selectedFriend.getProfilePhotoUrl(),
                        selectedFriend.getName(), selectedFriend.getUsername(), shortUserNameTv, profilePicImgView);
                updateRadioButtonValue();
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        public void updateRadioButtonValue() {
            try {
                /*if (SelectedFriendList.getInstance().isUserInList(selectedFriend.getUserid()))
                    selectRadioBtn.setChecked(true);
                else
                    selectRadioBtn.setChecked(false);*/
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        public void checkHorizontalAdapter() {
            try {
                if (horAdapterUpdateChk == false) {
                    horRecyclerView.setVisibility(View.VISIBLE);
                    horRecyclerView.setAdapter(selectFriendHorizontalAdapter);
                    horAdapterUpdateChk = true;
                } else {
                    horRecyclerView.setAdapter(selectFriendHorizontalAdapter);

                    if (SelectedFriendList.getInstance().getSize() == 0) {
                        horRecyclerView.setVisibility(View.GONE);
                    } else if (horRecyclerView.getVisibility() == View.GONE) {
                        horRecyclerView.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }
    }

    public void updateAdapter(String searchText) {
        getFilter().filter(searchText);
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
                        friendList.setResultArray(orginalFriendList.getResultArray());
                    else {
                        FriendList tempFriendList = new FriendList();
                        List<UserProfileProperties> userList = new ArrayList<>();
                        tempFriendList.setResultArray(userList);

                        for (UserProfileProperties userProfileProperties : orginalFriendList.getResultArray()) {
                            if (userProfileProperties.getName() != null &&
                                    userProfileProperties.getName().toLowerCase().contains(searchString.toLowerCase()))
                                tempFriendList.getResultArray().add(userProfileProperties);
                            else if (userProfileProperties.getUsername() != null &&
                                    userProfileProperties.getUsername().toLowerCase().contains(searchString.toLowerCase()))
                                tempFriendList.getResultArray().add(userProfileProperties);
                        }
                        friendList.setResultArray(tempFriendList.getResultArray());
                    }

                    filterResults.values = friendList;
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
                    friendList = (FriendList) filterResults.values;
                    notifyDataSetChanged();
                } catch (Exception e) {
                    ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                            new Object() {
                            }.getClass().getEnclosingMethod().getName(), e.toString());
                    e.printStackTrace();
                }
            }
        };
    }

    public void updateAdapterForSelectAll(int selectType) {
        try {
            if (selectType == CODE_SELECT_ALL) {
                SelectedFriendList.getInstance().clearFriendList();
                SelectedFriendList.getInstance().setSelectedFriendList(orginalFriendList);
                if (horRecyclerView != null)
                    horRecyclerView.setVisibility(View.VISIBLE);
                if (selectFriendHorizontalAdapter != null)
                    selectFriendHorizontalAdapter.notifyDataSetChanged();
                notifyDataSetChanged();
            } else if (selectType == CODE_UNSELECT_ALL) {
                SelectedFriendList.getInstance().clearFriendList();
                if (horRecyclerView != null)
                    horRecyclerView.setVisibility(View.GONE);
                if (selectFriendHorizontalAdapter != null)
                    selectFriendHorizontalAdapter.notifyDataSetChanged();
                notifyDataSetChanged();
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        int size = 0;
        try {
            size = friendList.getResultArray().size();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return size;
    }
}