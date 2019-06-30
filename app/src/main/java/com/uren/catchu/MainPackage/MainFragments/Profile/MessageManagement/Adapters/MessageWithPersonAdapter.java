package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.MessageDeleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.MessageBox;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class MessageWithPersonAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<MessageBox> messageBoxArrayList;
    boolean deleteActivated;
    MessageDeleteCallback messageDeleteCallback;
    TextView deleteMsgCntTv;

    public static final int VIEW_ITEM = 1;
    public static final int VIEW_PROG = 0;

    public MessageWithPersonAdapter(Context context, ArrayList<MessageBox> messageBoxArrayList,
                                    MessageDeleteCallback messageDeleteCallback, TextView deleteMsgCntTv) {
        this.context = context;
        this.messageBoxArrayList = messageBoxArrayList;
        this.messageDeleteCallback = messageDeleteCallback;
        this.deleteMsgCntTv = deleteMsgCntTv;
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
                    .inflate(R.layout.message_item, parent, false);

            viewHolder = new MessageWithPersonAdapter.MessageWithPersonHolder(itemView);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar_item, parent, false);

            viewHolder = new ProgressViewHolder(v);
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
        if (holder instanceof MessageWithPersonAdapter.MessageWithPersonHolder) {
            MessageBox messageBox = messageBoxArrayList.get(position);
            ((MessageWithPersonAdapter.MessageWithPersonHolder) holder).setData(messageBox, position);
        } else {
            ((MessageWithPersonAdapter.ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public class MessageWithPersonHolder extends RecyclerView.ViewHolder {

        EmojiconTextView messageTv;
        TextView createAtTv;
        CardView messageCardview;
        View mainRelLayout;
        int position;
        MessageBox messageBox;

        public MessageWithPersonHolder(View view) {
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
            setRelLayoutWidth();
            setSelectedDeleteValues();
        }

        public void setMessageDetails() {
            if (messageBox != null) {
                if (messageBox.getMessageText() != null)
                    messageTv.setText(messageBox.getMessageText());

                if (messageBox.getDate() != 0) {

                    createAtTv.setText(CommonUtils.getMessageTime(context, messageBox.getDate()));
                        /*Date date = new Date(messageBox.getDate());
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
                        String formatted = format.format(date);
                        System.out.println("formatted:" + formatted);
                        createAtTv.setText(formatted.substring(11, 16));*/
                }
            }
        }

        public void setCardViewPosition() {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) messageCardview.getLayoutParams();
            params.width = RelativeLayout.LayoutParams.WRAP_CONTENT;

            if (messageBox.getSenderUser().getUserid().equals(AccountHolderInfo.getUserID())) {
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                messageCardview.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.Gold, null),
                        context.getResources().getColor(R.color.DarkOrange, null), GradientDrawable.RECTANGLE, 20, 2));

            } else {
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                messageCardview.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.LightSkyBlue, null),
                        context.getResources().getColor(R.color.RoyalBlue, null), GradientDrawable.RECTANGLE, 20, 2));
            }

            messageCardview.setLayoutParams(params);
        }

        public void setRelLayoutWidth() {
            RecyclerView.LayoutParams paramsRel = (RecyclerView.LayoutParams) mainRelLayout.getLayoutParams();
            if (messageBox.getSenderUser().getUserid().equals(AccountHolderInfo.getUserID()))
                mainRelLayout.setPadding(120, 0, 0, 0);
            else
                mainRelLayout.setPadding(0, 0, 120, 0);

            mainRelLayout.setLayoutParams(paramsRel);
        }

        public void setSelectedDeleteValues() {
            if (messageBox.isSelectedForDelete())
                mainRelLayout.setBackgroundColor(context.getResources().getColor(R.color.Khaki, null));
            else
                mainRelLayout.setBackgroundColor(context.getResources().getColor(R.color.White, null));
        }

        public void checkDeletedMessages() {
            int deleteCount = 0;
            for (MessageBox messageBox : messageBoxArrayList) {
                if (messageBox.isSelectedForDelete()) {
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
        }
    }

    public void setDeleteActivated(boolean value) {
        deleteActivated = value;
    }

    public void addProgressLoading() {
        messageBoxArrayList.add(0, null);
        notifyItemInserted(0);
    }

    public void removeProgressLoading() {
        messageBoxArrayList.remove(0);
        notifyItemRemoved(0);
    }

    public boolean isShowingProgressLoading() {
        if (getItemViewType(messageBoxArrayList.size() - 1) == VIEW_PROG)
            return true;
        else
            return false;
    }

    @Override
    public int getItemCount() {
        int listSize = 0;
        if (messageBoxArrayList != null && messageBoxArrayList.size() > 0)
            listSize = messageBoxArrayList.size();
        return listSize;
    }
}