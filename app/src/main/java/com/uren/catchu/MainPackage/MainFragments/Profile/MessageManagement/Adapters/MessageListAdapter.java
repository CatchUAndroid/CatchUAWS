package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Adapters;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.MessageBox;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.MessageListBox;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople.Models.ContactFriendModel;
import com.uren.catchu.R;

import java.util.ArrayList;
import java.util.List;

import catchu.model.UserProfileProperties;

public class MessageListAdapter extends RecyclerView.Adapter implements Filterable {

    private Context context;
    private ArrayList<MessageListBox> messageBoxArrayList;
    private ArrayList<MessageListBox> orgMessageBoxArrayList;
    ItemClickListener itemClickListener;

    public static final int VIEW_ITEM = 1;
    public static final int VIEW_PROG = 0;

    public MessageListAdapter(Context context, ArrayList<MessageListBox> messageBoxArrayList,
                              ItemClickListener itemClickListener) {
        this.context = context;
        this.messageBoxArrayList = messageBoxArrayList;
        this.orgMessageBoxArrayList = messageBoxArrayList;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return messageBoxArrayList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_list_item, parent, false);

            viewHolder = new MessageListAdapter.MessageListHolder(itemView);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar_item, parent, false);

            viewHolder = new MessageListAdapter.ProgressViewHolder(v);
        }
        return viewHolder;
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBarLoading);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MessageListAdapter.MessageListHolder) {
            MessageListBox messageListBox = messageBoxArrayList.get(position);
            ((MessageListAdapter.MessageListHolder) holder).setData(messageListBox, position);
        } else {
            ((MessageListAdapter.ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public class MessageListHolder extends RecyclerView.ViewHolder {

        ImageView profilePicImgView;
        TextView shortUserNameTv;
        TextView profileNameTv;
        TextView messageTextTv;
        TextView messageDateTv;
        RelativeLayout messageRelLayout;
        int position;
        MessageListBox messageListBox;

        public MessageListHolder(View view) {
            super(view);

            profilePicImgView = view.findViewById(R.id.profilePicImgView);
            shortUserNameTv = view.findViewById(R.id.shortUserNameTv);
            profileNameTv = view.findViewById(R.id.profileNameTv);
            messageTextTv = view.findViewById(R.id.messageTextTv);
            messageDateTv = view.findViewById(R.id.messageDateTv);
            messageRelLayout = view.findViewById(R.id.messageRelLayout);

            messageRelLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onClick(messageListBox, position);
                }
            });
        }

        public void setData(MessageListBox messageListBox, int position) {
            this.messageListBox = messageListBox;
            this.position = position;
            setUserInfo();
            setMessageText();
            setMessageDate();
        }

        private void setUserInfo() {
            if (messageListBox != null && messageListBox.getUserProfileProperties() != null) {

                if (messageListBox.getUserProfileProperties().getName() != null &&
                        !messageListBox.getUserProfileProperties().getName().isEmpty())
                    UserDataUtil.setName(messageListBox.getUserProfileProperties().getName(), profileNameTv);
                else if (messageListBox.getUserProfileProperties().getUsername() != null &&
                        !messageListBox.getUserProfileProperties().getUsername().isEmpty())
                    UserDataUtil.setUsername(messageListBox.getUserProfileProperties().getUsername(), profileNameTv);

                UserDataUtil.setProfilePicture(context, messageListBox.getUserProfileProperties().getProfilePhotoUrl(),
                        messageListBox.getUserProfileProperties().getName(),
                        messageListBox.getUserProfileProperties().getUsername(), shortUserNameTv, profilePicImgView);
            }
        }

        private void setMessageDate() {
            if (messageListBox != null && messageListBox.getDate() != 0)
                messageDateTv.setText(CommonUtils.getMessageTime(context, messageListBox.getDate()));
        }

        private void setMessageText() {
            if (messageListBox.getMessageText() != null)
                messageTextTv.setText(messageListBox.getMessageText());

            if (messageListBox.isIamReceipt() && !messageListBox.isSeen()) {
                messageTextTv.setTypeface(messageTextTv.getTypeface(), Typeface.BOLD);
                messageTextTv.setTextColor(context.getResources().getColor(R.color.Red, null));
            } else {
                messageTextTv.setTypeface(messageTextTv.getTypeface(), Typeface.NORMAL);
                messageTextTv.setTextColor(context.getResources().getColor(R.color.DarkGray, null));
            }
        }
    }

    public void addProgressLoading() {
        messageBoxArrayList.add(null);
        notifyItemInserted(messageBoxArrayList.size() - 1);
    }

    public void removeProgressLoading() {
        messageBoxArrayList.remove(messageBoxArrayList.size() - 1);
        notifyItemRemoved(messageBoxArrayList.size());
    }

    public boolean isShowingProgressLoading() {
        if (getItemViewType(messageBoxArrayList.size() - 1) == VIEW_PROG)
            return true;
        else
            return false;
    }

    @Override
    public int getItemCount() {
        if (messageBoxArrayList != null && messageBoxArrayList.size() > 0)
            return messageBoxArrayList.size();
        else
            return 0;
    }

    public void updateAdapter(String searchText) {
        getFilter().filter(searchText);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                FilterResults filterResults = null;

                String searchString = charSequence.toString();

                if (searchString.trim().isEmpty())
                    messageBoxArrayList = orgMessageBoxArrayList;
                else {
                    ArrayList<MessageListBox> tempList = new ArrayList<>();

                    for (MessageListBox messageListBox : orgMessageBoxArrayList) {
                        if (messageListBox != null) {

                            if (messageListBox.getMessageText() != null &&
                                    messageListBox.getMessageText().toLowerCase().contains(searchString.toLowerCase())) {
                                tempList.add(messageListBox);
                            } else if (messageListBox.getUserProfileProperties() != null) {

                                UserProfileProperties userProfileProperties = messageListBox.getUserProfileProperties();

                                if (userProfileProperties.getName() != null &&
                                        userProfileProperties.getName().toLowerCase().contains(searchString.toLowerCase())) {
                                    tempList.add(messageListBox);
                                } else if (userProfileProperties.getUsername() != null &&
                                        userProfileProperties.getUsername().toLowerCase().contains(searchString.toLowerCase())) {
                                    tempList.add(messageListBox);
                                }
                            }
                        }
                    }
                    messageBoxArrayList = tempList;
                }

                filterResults = new FilterResults();
                filterResults.values = messageBoxArrayList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                messageBoxArrayList = (ArrayList<MessageListBox>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}