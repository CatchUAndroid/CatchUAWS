package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Adapters;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.MessageListBox;
import com.uren.catchu.R;

import java.util.ArrayList;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageListHolder> {

    private Context context;
    private ArrayList<MessageListBox> messageBoxArrayList;
    ItemClickListener itemClickListener;

    public MessageListAdapter(Context context, ArrayList<MessageListBox> messageBoxArrayList,
                              ItemClickListener itemClickListener) {
        try {
            this.context = context;
            this.messageBoxArrayList = messageBoxArrayList;
            this.itemClickListener = itemClickListener;
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public MessageListAdapter.MessageListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_list_item, parent, false);
        return new MessageListAdapter.MessageListHolder(itemView);
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

            try {
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

            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        public void setData(MessageListBox messageListBox, int position) {
            try {
                this.messageListBox = messageListBox;
                this.position = position;
                setUserInfo();
                setMessageText();
                setMessageDate();
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        private void setUserInfo() {
            try {
                if (messageListBox != null && messageListBox.getUserProfileProperties() != null ) {

                    if(messageListBox.getUserProfileProperties().getName() != null &&
                            !messageListBox.getUserProfileProperties().getName().isEmpty())
                        UserDataUtil.setName(messageListBox.getUserProfileProperties().getName(), profileNameTv);
                    else if(messageListBox.getUserProfileProperties().getUsername() != null &&
                            !messageListBox.getUserProfileProperties().getUsername().isEmpty())
                        UserDataUtil.setUsername(messageListBox.getUserProfileProperties().getUsername(), profileNameTv);

                    UserDataUtil.setProfilePicture(context, messageListBox.getUserProfileProperties().getProfilePhotoUrl(),
                            messageListBox.getUserProfileProperties().getName(),
                            messageListBox.getUserProfileProperties().getUsername(), shortUserNameTv, profilePicImgView);
                }
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        private void setMessageDate() {
            try {
                if(messageListBox != null && messageListBox.getDate() != 0)
                    messageDateTv.setText(CommonUtils.getMessageTime(context, messageListBox.getDate()));
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        private void setMessageText() {
            try {
                if (messageListBox.getMessageText() != null)
                    messageTextTv.setText(messageListBox.getMessageText());

                if (messageListBox.isIamReceipt() && !messageListBox.isSeen()) {
                    messageTextTv.setTypeface(messageTextTv.getTypeface(), Typeface.BOLD);
                    messageTextTv.setTextColor(context.getResources().getColor(R.color.Black, null));
                } else {
                    messageTextTv.setTypeface(messageTextTv.getTypeface(), Typeface.NORMAL);
                    messageTextTv.setTextColor(context.getResources().getColor(R.color.DarkGray, null));
                }
            } catch (Resources.NotFoundException e) {
                ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBindViewHolder(final MessageListAdapter.MessageListHolder holder, final int position) {
        try {
            MessageListBox messageListBox = messageBoxArrayList.get(position);
            holder.setData(messageListBox, position);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        int listSize = 0;
        try {
            if (messageBoxArrayList != null && messageBoxArrayList.size() > 0)
                listSize = messageBoxArrayList.size();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return listSize;
    }
}