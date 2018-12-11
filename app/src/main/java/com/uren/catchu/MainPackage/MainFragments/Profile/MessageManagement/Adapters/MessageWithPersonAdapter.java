package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ProgressDialogUtil.ProgressDialogUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ListItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.MessageDeleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.MessageBox;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import catchu.model.FollowInfoListResponse;
import catchu.model.FriendRequestList;
import catchu.model.User;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.CHAR_AMPERSAND;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_FOLLOWING;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_NONE;
import static com.uren.catchu.Constants.StringConstants.FRIEND_ACCEPT_REQUEST;

public class MessageWithPersonAdapter extends RecyclerView.Adapter<MessageWithPersonAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<MessageBox> messageBoxArrayList;
    boolean deleteActivated;
    MessageDeleteCallback messageDeleteCallback;
    TextView deleteMsgCntTv;

    public MessageWithPersonAdapter(Context context, ArrayList<MessageBox> messageBoxArrayList,
                                    MessageDeleteCallback messageDeleteCallback, TextView deleteMsgCntTv) {
        this.context = context;
        this.messageBoxArrayList = messageBoxArrayList;
        this.messageDeleteCallback = messageDeleteCallback;
        this.deleteMsgCntTv = deleteMsgCntTv;
    }

    @Override
    public MessageWithPersonAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);
        return new MessageWithPersonAdapter.MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView messageTv;
        TextView createAtTv;
        CardView messageCardview;
        RelativeLayout mainRelLayout;
        int position;
        MessageBox messageBox;

        public MyViewHolder(View view) {
            super(view);

            messageTv = view.findViewById(R.id.messageTv);
            createAtTv = view.findViewById(R.id.createAtTv);
            messageCardview = view.findViewById(R.id.messageCardview);
            mainRelLayout = view.findViewById(R.id.mainRelLayout);

            messageCardview.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    deleteActivated = true;
                    messageDeleteCallback.OnDeleteActivated(deleteActivated);
                    return false;
                }
            });

            messageCardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (deleteActivated) {
                        if (messageBox.isSelectedForDelete()) {
                            messageBox.setSelectedForDelete(false);
                            setSelectedDeleteValues();
                        } else {
                            messageBox.setSelectedForDelete(true);
                            setSelectedDeleteValues();
                        }

                        checkDeletedMessages();
                    }
                }
            });
        }

        public void setData(MessageBox messageBox, int position) {
            this.messageBox = messageBox;
            this.position = position;
            setMessageDetails();
            setCardViewPosition();
            setSelectedDeleteValues();
        }

        public void setMessageDetails() {
            if (messageBox != null) {
                if (messageBox.getMessageText() != null)
                    messageTv.setText(messageBox.getMessageText());

                if (messageBox.getDate() != 0) {
                    Date date = new Date(messageBox.getDate());
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                    format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
                    String formatted = format.format(date);
                    System.out.println("formatted:" + formatted);
                    createAtTv.setText(formatted.substring(11, 16));
                }
            }
        }

        public void setCardViewPosition() {
            if (messageBox.getSenderUser().getUserid().equals(AccountHolderInfo.getUserID())) {
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
        }

        public void setSelectedDeleteValues(){
            if(messageBox.isSelectedForDelete())
                mainRelLayout.setBackgroundColor(context.getResources().getColor(R.color.transparentBlack, null));
            else
                mainRelLayout.setBackgroundColor(context.getResources().getColor(R.color.White, null));
        }

        public void checkDeletedMessages(){
            int deleteCount = 0;
            for(MessageBox messageBox : messageBoxArrayList){
                if(messageBox.isSelectedForDelete()){
                    deleteCount ++;
                }
            }

            if(deleteCount == 0) {
                deleteActivated = false;
                messageDeleteCallback.OnDeleteActivated(deleteActivated);
                deleteMsgCntTv.setText("");
            }else {
                deleteMsgCntTv.setText(Integer.toString(deleteCount));
            }
        }
    }

    public void setDeleteActivated(boolean value){
        deleteActivated = value;
    }

    @Override
    public void onBindViewHolder(final MessageWithPersonAdapter.MyViewHolder holder, final int position) {
        MessageBox messageBox = messageBoxArrayList.get(position);
        holder.setData(messageBox, position);
    }

    @Override
    public int getItemCount() {
        if (messageBoxArrayList != null && messageBoxArrayList.size() > 0)
            return messageBoxArrayList.size();
        else
            return 0;
    }


}