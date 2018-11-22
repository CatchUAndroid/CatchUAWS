package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople.Adapters;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople.Interfaces.ContactFriendSelectCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople.Models.ContactFriendModel;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.List;

import catchu.model.FriendRequestList;
import catchu.model.RelationProperties;
import catchu.model.User;
import catchu.model.UserListResponse;

import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_FOLLOWING;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_NONE;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_PENDING;
import static com.uren.catchu.Constants.StringConstants.FRIEND_CREATE_FOLLOW_DIRECTLY;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_FOLLOW;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_PENDING_FOLLOW_REQUEST;
import static com.uren.catchu.Constants.StringConstants.FRIEND_FOLLOW_REQUEST;
import static com.uren.catchu.Constants.StringConstants.PROVIDER_TYPE_PHONE;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> implements Filterable {


    View view;
    LayoutInflater layoutInflater;
    Context context;
    GradientDrawable imageShape;
    GradientDrawable buttonShape;
    ReturnCallback returnCallback;
    List<ContactFriendModel> contactFriendModelList;
    List<ContactFriendModel> orgContactFriendModelList;
    ContactFriendSelectCallback contactFriendSelectCallback;

    public ContactsAdapter(Context context, List<ContactFriendModel> contactFriendModelList, ContactFriendSelectCallback contactFriendSelectCallback) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.contactFriendModelList = contactFriendModelList;
        this.orgContactFriendModelList = contactFriendModelList;
        this.contactFriendSelectCallback = contactFriendSelectCallback;
        imageShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                0, GradientDrawable.OVAL, 50, 0);
    }

    @NonNull
    @Override
    public ContactsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view = layoutInflater.inflate(R.layout.person_vert_list_item, viewGroup, false);
        final ContactsAdapter.MyViewHolder holder = new ContactsAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.MyViewHolder myViewHolder, int position) {
        ContactFriendModel contactFriendModel = contactFriendModelList.get(position);
        myViewHolder.setData(contactFriendModel, position);
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView usernameTextView;
        TextView shortenTextView;
        TextView phoneNumTextView;
        ImageView profilePicImgView;
        CardView personRootCardView;
        ContactFriendModel contactFriendModel;
        Button statuDisplayBtn;
        String requestedUserid;

        int position = 0;

        public MyViewHolder(final View itemView) {
            super(itemView);

            profilePicImgView = view.findViewById(R.id.profilePicImgView);
            usernameTextView = view.findViewById(R.id.usernameTextView);
            nameTextView = view.findViewById(R.id.nameTextView);
            phoneNumTextView = view.findViewById(R.id.phoneNumTextView);
            statuDisplayBtn = view.findViewById(R.id.statuDisplayBtn);
            shortenTextView = view.findViewById(R.id.shortenTextView);
            personRootCardView = view.findViewById(R.id.personRootCardView);
            usernameTextView.setVisibility(View.GONE);
            phoneNumTextView.setVisibility(View.VISIBLE);
            profilePicImgView.setBackground(imageShape);
            statuDisplayBtn.setBackground(buttonShape);

            statuDisplayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    statuDisplayBtn.setEnabled(false);
                    statuDisplayBtn.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_click));

                    if (contactFriendModel != null) {
                        if (contactFriendModel.getUser() != null && contactFriendModel.getUser().getUserid() != null &&
                                !contactFriendModel.getUser().getUserid().isEmpty()) {
                            checkFriendRelation();
                        } else if (contactFriendModel.getContact() != null) {
                            contactFriendSelectCallback.contactSelected(contactFriendModel.getContact());
                        }
                    }
                }
            });

            personRootCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (contactFriendModel != null) {
                        if (contactFriendModel.getUser() != null && contactFriendModel.getUser().getUserid() != null &&
                                !contactFriendModel.getUser().getUserid().isEmpty()) {
                            contactFriendSelectCallback.appUserSelected(contactFriendModel.getUser());
                        }
                    }
                }
            });
        }

        public void checkFriendRelation() {

            if (contactFriendModel.getUser().getFollowStatus() != null) {
                if (contactFriendModel.getUser().getFollowStatus().equals(FOLLOW_STATUS_FOLLOWING))
                    processFriendRequest(FRIEND_DELETE_FOLLOW);
                else if (contactFriendModel.getUser().getFollowStatus().equals(FOLLOW_STATUS_PENDING))
                    processFriendRequest(FRIEND_DELETE_PENDING_FOLLOW_REQUEST);
                else {
                    if (contactFriendModel.getUser().getIsPrivateAccount() != null && contactFriendModel.getUser().getIsPrivateAccount())
                        processFriendRequest(FRIEND_FOLLOW_REQUEST);
                    else
                        processFriendRequest(FRIEND_CREATE_FOLLOW_DIRECTLY);
                }
            }
        }

        public void processFriendRequest(final String requestType) {

            AccountHolderFollowProcess.friendFollowRequest(requestType, AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid(), requestedUserid,
                    new CompleteCallback() {
                        @Override
                        public void onComplete(Object object) {
                            RelationProperties relationProperties = ((FriendRequestList) object).getUpdatedUserRelationInfo();

                            if (relationProperties.getFriendRelation())
                                contactFriendModel.getUser().setFollowStatus(FOLLOW_STATUS_FOLLOWING);
                            else if (relationProperties.getPendingFriendRequest())
                                contactFriendModel.getUser().setFollowStatus(FOLLOW_STATUS_PENDING);
                            else
                                contactFriendModel.getUser().setFollowStatus(FOLLOW_STATUS_NONE);

                            contactFriendModelList.remove(position);
                            contactFriendModelList.add(position, contactFriendModel);

                            UserDataUtil.updateFollowButton(context, relationProperties.getFriendRelation(), relationProperties.getPendingFriendRequest(), statuDisplayBtn, true);
                            AccountHolderInfo.getInstance().updateAccountHolderFollowCnt(requestType);
                            statuDisplayBtn.setEnabled(true);
                        }

                        @Override
                        public void onFailed(Exception e) {
                            statuDisplayBtn.setEnabled(true);
                            DialogBoxUtil.showErrorDialog(context, context.getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                                @Override
                                public void okClick() {
                                }
                            });
                        }
                    });
        }

        public void setData(ContactFriendModel contactFriendModel, int position) {
            this.position = position;
            this.contactFriendModel = contactFriendModel;
            setRequestedUserid();
            setPhoneNum();
            setNameAndProfilePicture();
            setDisplayButton();
        }

        public void setRequestedUserid() {
            if (contactFriendModel != null) {
                if (contactFriendModel.getUser() != null && contactFriendModel.getUser().getUserid() != null &&
                        !contactFriendModel.getUser().getUserid().isEmpty()) {
                    this.requestedUserid = contactFriendModel.getUser().getUserid();
                }
            }
        }

        public void setPhoneNum() {
            if (contactFriendModel.getUser() != null && contactFriendModel.getUser().getUserid() != null) {
                if (contactFriendModel.getUser().getProvider() != null && contactFriendModel.getUser().getProvider().getProviderid() != null &&
                        contactFriendModel.getUser().getProvider().getProviderType() != null) {
                    if (contactFriendModel.getUser().getProvider().getProviderType().equals(PROVIDER_TYPE_PHONE)) {
                        this.phoneNumTextView.setText(contactFriendModel.getUser().getProvider().getProviderid());
                    }
                }
            } else if (contactFriendModel.getContact() != null &&
                    contactFriendModel.getContact().getPhoneNumber() != null && !contactFriendModel.getContact().getPhoneNumber().isEmpty())
                this.phoneNumTextView.setText(contactFriendModel.getContact().getPhoneNumber());
        }

        public void setNameAndProfilePicture() {
            if (contactFriendModel != null) {
                if (contactFriendModel.getUser() != null && contactFriendModel.getUser().getUserid() != null &&
                        !contactFriendModel.getUser().getUserid().isEmpty()) {
                    UserDataUtil.setName(contactFriendModel.getUser().getName(), nameTextView);
                    UserDataUtil.setProfilePicture(context, contactFriendModel.getUser().getProfilePhotoUrl(), contactFriendModel.getUser().getName(), shortenTextView, profilePicImgView);
                } else if (contactFriendModel.getContact() != null) {
                    UserDataUtil.setName(contactFriendModel.getContact().getName(), nameTextView);
                    UserDataUtil.setProfilePicture(context, null, contactFriendModel.getContact().getName(), shortenTextView, profilePicImgView);
                }
            }
        }

        public void setDisplayButton() {
            if (contactFriendModel.getUser() != null && contactFriendModel.getUser().getUserid() != null) {
                if (contactFriendModel.getUser().getUserid().equals(AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid()))
                    statuDisplayBtn.setVisibility(View.GONE);
                else {
                    statuDisplayBtn.setVisibility(View.VISIBLE);
                    UserDataUtil.updateFollowButton2(context, contactFriendModel.getUser().getFollowStatus(), statuDisplayBtn, false);
                }
            } else if (contactFriendModel.getContact() != null) {
                UserDataUtil.updateInviteButton(context, statuDisplayBtn, false);
            }
        }
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String searchString = charSequence.toString();

                if (searchString.trim().isEmpty())
                    contactFriendModelList = orgContactFriendModelList;
                else {
                    List<ContactFriendModel> tempList = new ArrayList<>();

                    for (ContactFriendModel contactFriendModel : orgContactFriendModelList) {
                        if (contactFriendModel != null) {
                            if (contactFriendModel.getContact() != null && contactFriendModel.getContact().getName() != null &&
                                    contactFriendModel.getContact().getName().toLowerCase().contains(searchString.toLowerCase())) {
                                tempList.add(contactFriendModel);
                            } else if (contactFriendModel.getUser() != null && contactFriendModel.getUser().getName() != null &&
                                    contactFriendModel.getUser().getName().toLowerCase().contains(searchString.toLowerCase())) {
                                tempList.add(contactFriendModel);
                            }
                        }
                    }
                    contactFriendModelList = tempList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactFriendModelList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactFriendModelList = (List<ContactFriendModel>) filterResults.values;
                notifyDataSetChanged();

                if (contactFriendModelList != null && contactFriendModelList.size() > 0)
                    returnCallback.onReturn(contactFriendModelList.size());
                else
                    returnCallback.onReturn(0);
            }
        };
    }

    public void updateAdapter(String searchText, ReturnCallback returnCallback) {
        this.returnCallback = returnCallback;
        getFilter().filter(searchText);
    }

    @Override
    public int getItemCount() {
        return contactFriendModelList.size();
    }
}