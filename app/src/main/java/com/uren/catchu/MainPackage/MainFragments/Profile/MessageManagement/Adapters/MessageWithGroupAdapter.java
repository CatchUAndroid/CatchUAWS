package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Adapters;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.MessageDeleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.MessageWithPersonFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.GroupMessageBox;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.MessageBox;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class MessageWithGroupAdapter extends RecyclerView.Adapter<MessageWithGroupAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<GroupMessageBox> messageBoxArrayList;
    boolean deleteActivated;
    MessageDeleteCallback messageDeleteCallback;
    TextView deleteMsgCntTv;

    public MessageWithGroupAdapter(Context context, ArrayList<GroupMessageBox> messageBoxArrayList,
                                   MessageDeleteCallback messageDeleteCallback, TextView deleteMsgCntTv) {
        try {
            this.context = context;
            this.messageBoxArrayList = messageBoxArrayList;
            this.messageDeleteCallback = messageDeleteCallback;
            this.deleteMsgCntTv = deleteMsgCntTv;
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context,MessageWithGroupAdapter.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public MessageWithGroupAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_message_item, parent, false);
        return new MessageWithGroupAdapter.MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView messageTv;
        TextView createAtTv;
        TextView senderNameTv;
        CardView messageCardview;
        RelativeLayout mainRelLayout;
        int position;
        GroupMessageBox groupMessageBox;

        public MyViewHolder(View view) {
            super(view);

            try {
                messageTv = view.findViewById(R.id.messageTv);
                createAtTv = view.findViewById(R.id.createAtTv);
                senderNameTv = view.findViewById(R.id.senderNameTv);
                messageCardview = view.findViewById(R.id.messageCardview);
                mainRelLayout = view.findViewById(R.id.mainRelLayout);

                messageCardview.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (groupMessageBox != null && groupMessageBox.getSenderUser() != null &&
                                groupMessageBox.getSenderUser().getUserid() != null &&
                                groupMessageBox.getSenderUser().getUserid().equals(AccountHolderInfo.getUserID())) {
                            deleteActivated = true;
                            messageDeleteCallback.OnDeleteActivated(deleteActivated);
                        }
                        return false;
                    }
                });

                messageCardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (groupMessageBox != null && groupMessageBox.getSenderUser() != null &&
                                groupMessageBox.getSenderUser().getUserid() != null &&
                                groupMessageBox.getSenderUser().getUserid().equals(AccountHolderInfo.getUserID())) {
                            if (deleteActivated) {
                                if (groupMessageBox.isSelectedForDelete()) {
                                    groupMessageBox.setSelectedForDelete(false);
                                    setSelectedDeleteValues();
                                } else {
                                    groupMessageBox.setSelectedForDelete(true);
                                    setSelectedDeleteValues();
                                }
                                checkDeletedMessages();
                            }
                        }
                    }
                });
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context, MessageWithGroupAdapter.class.getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.getMessage());
                e.printStackTrace();
            }
        }

        public void setData(GroupMessageBox groupMessageBox, int position) {
            try {
                this.groupMessageBox = groupMessageBox;
                this.position = position;
                setMessageDetails();
                setCardViewPosition();
                setSelectedDeleteValues();
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context,MessageWithGroupAdapter.class.getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.getMessage());
                e.printStackTrace();
            }
        }

        public void setMessageDetails() {
            try {
                if (groupMessageBox != null) {
                    if (groupMessageBox.getMessageText() != null)
                        messageTv.setText(groupMessageBox.getMessageText());

                    if (groupMessageBox.getDate() != 0) {
                        Date date = new Date(groupMessageBox.getDate());
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
                        String formatted = format.format(date);
                        System.out.println("formatted:" + formatted);
                        createAtTv.setText(formatted.substring(11, 16));
                    }

                    if (groupMessageBox.getSenderUser() != null && groupMessageBox.getSenderUser().getName() != null) {
                        senderNameTv.setText(groupMessageBox.getSenderUser().getName());
                    }
                }
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context,MessageWithGroupAdapter.class.getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.getMessage());
                e.printStackTrace();
            }
        }

        public void setCardViewPosition() {
            try {
                if (groupMessageBox.getSenderUser().getUserid().equals(AccountHolderInfo.getUserID())) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) messageCardview.getLayoutParams();
                    params.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    messageCardview.setLayoutParams(params);
                    messageCardview.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.PowderBlue, null),
                            0, GradientDrawable.RECTANGLE, 15, 0));
                } else {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) messageCardview.getLayoutParams();
                    params.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    messageCardview.setLayoutParams(params);
                    messageCardview.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.Silver, null),
                            0, GradientDrawable.RECTANGLE, 15, 0));
                }
            } catch (Resources.NotFoundException e) {
                ErrorSaveHelper.writeErrorToDB(context,MessageWithGroupAdapter.class.getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.getMessage());
                e.printStackTrace();
            }
        }

        public void setSelectedDeleteValues() {
            try {
                if (groupMessageBox.isSelectedForDelete())
                    mainRelLayout.setBackgroundColor(context.getResources().getColor(R.color.transparentBlack, null));
                else
                    mainRelLayout.setBackgroundColor(context.getResources().getColor(R.color.White, null));
            } catch (Resources.NotFoundException e) {
                ErrorSaveHelper.writeErrorToDB(context,MessageWithGroupAdapter.class.getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.getMessage());
                e.printStackTrace();
            }
        }

        public void checkDeletedMessages() {
            try {
                int deleteCount = 0;
                for (GroupMessageBox groupMessageBox : messageBoxArrayList) {
                    if (groupMessageBox.isSelectedForDelete()) {
                        deleteCount++;
                    }
                }

                if (deleteCount == 0) {
                    deleteActivated = false;
                    messageDeleteCallback.OnDeleteActivated(deleteActivated);
                    deleteMsgCntTv.setText("");
                } else {
                    deleteMsgCntTv.setText(Integer.toString(deleteCount));
                }
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context,MessageWithGroupAdapter.class.getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void setDeleteActivated(boolean value) {
        deleteActivated = value;
    }

    @Override
    public void onBindViewHolder(final MessageWithGroupAdapter.MyViewHolder holder, final int position) {
        try {
            GroupMessageBox groupMessageBox = messageBoxArrayList.get(position);
            holder.setData(groupMessageBox, position);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context,MessageWithGroupAdapter.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
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
            ErrorSaveHelper.writeErrorToDB(context,MessageWithGroupAdapter.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
        return listSize;
    }
}