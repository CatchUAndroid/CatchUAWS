package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.GeneralUtils.ProgressDialogUtil.ProgressDialogUtil;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.Adapters.FeedAdapter;
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
import catchu.model.Post;
import catchu.model.User;
import catchu.model.UserProfileProperties;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

import static com.uren.catchu.Constants.StringConstants.CHAR_AMPERSAND;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_FOLLOWING;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_NONE;
import static com.uren.catchu.Constants.StringConstants.FRIEND_ACCEPT_REQUEST;

public class MessageWithPersonAdapter extends RecyclerView.Adapter{

    private Context context;
    private ArrayList<MessageBox> messageBoxArrayList;
    boolean deleteActivated;
    MessageDeleteCallback messageDeleteCallback;
    TextView deleteMsgCntTv;

    public static final int VIEW_ITEM = 1;
    public static final int VIEW_PROG = 0;

    public MessageWithPersonAdapter(Context context, ArrayList<MessageBox> messageBoxArrayList,
                                    MessageDeleteCallback messageDeleteCallback, TextView deleteMsgCntTv) {
        try {
            this.context = context;
            this.messageBoxArrayList = messageBoxArrayList;
            this.messageDeleteCallback = messageDeleteCallback;
            this.deleteMsgCntTv = deleteMsgCntTv;
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, MessageWithPersonAdapter.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return messageBoxArrayList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);
        return new MessageWithPersonAdapter.MyViewHolder(itemView);*/


        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_item, parent, false);

            viewHolder = new MessageWithPersonAdapter.MyViewHolder(itemView);
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
        try {
            /*MessageBox messageBox = messageBoxArrayList.get(position);
            holder.setData(messageBox, position);*/

            if (holder instanceof MessageWithPersonAdapter.MyViewHolder) {
                MessageBox messageBox = messageBoxArrayList.get(position);
                ((MessageWithPersonAdapter.MyViewHolder) holder).setData(messageBox, position);
            } else {
                ((MessageWithPersonAdapter.ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            }

        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context,MessageWithPersonAdapter.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        EmojiconTextView messageTv;
        TextView createAtTv;
        CardView messageCardview;
        RelativeLayout mainRelLayout;
        int position;
        MessageBox messageBox;

        public MyViewHolder(View view) {
            super(view);

            try {
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
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context,MessageWithPersonAdapter.class.getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        public void setData(MessageBox messageBox, int position) {
            try {
                this.messageBox = messageBox;
                this.position = position;
                setMessageDetails();
                setCardViewPosition();
                setSelectedDeleteValues();
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context,MessageWithPersonAdapter.class.getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        public void setMessageDetails() {
            try {
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
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context,MessageWithPersonAdapter.class.getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        public void setCardViewPosition() {
            try {
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
            } catch (Resources.NotFoundException e) {
                ErrorSaveHelper.writeErrorToDB(context,MessageWithPersonAdapter.class.getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        public void setSelectedDeleteValues() {
            try {
                if (messageBox.isSelectedForDelete())
                    mainRelLayout.setBackgroundColor(context.getResources().getColor(R.color.transparentBlack, null));
                else
                    mainRelLayout.setBackgroundColor(context.getResources().getColor(R.color.White, null));
            } catch (Resources.NotFoundException e) {
                ErrorSaveHelper.writeErrorToDB(context,MessageWithPersonAdapter.class.getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }

        public void checkDeletedMessages() {
            try {
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
            } catch (Exception e) {
                ErrorSaveHelper.writeErrorToDB(context,MessageWithPersonAdapter.class.getSimpleName(),
                        new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.toString());
                e.printStackTrace();
            }
        }
    }

    public void setDeleteActivated(boolean value) {
        deleteActivated = value;
    }

    /*@Override
    public void onBindViewHolder(final MessageWithPersonAdapter.MyViewHolder holder, final int position) {
        try {
            MessageBox messageBox = messageBoxArrayList.get(position);
            holder.setData(messageBox, position);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context,MessageWithPersonAdapter.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }*/

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
        try {
            if (messageBoxArrayList != null && messageBoxArrayList.size() > 0)
                listSize = messageBoxArrayList.size();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context,MessageWithPersonAdapter.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
        return listSize;
    }


}