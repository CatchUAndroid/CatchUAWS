package com.uren.catchu.GeneralUtils.DataModelUtil;

import android.view.View;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.ClickableImage.EffectTouchListener;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;

import catchu.model.FriendRequestList;
import catchu.model.GroupRequestResult;
import catchu.model.UserListResponse;

public class MessageDataUtil {

    public static void setWarningMessageVisibility(Object object, TextView warningMsgTv, String message) {

        try {
            if (warningMsgTv != null && message != null && !message.isEmpty())
                warningMsgTv.setText(message);

            if (object instanceof FriendRequestList) {
                FriendRequestList friendRequestList = (FriendRequestList) object;

                if (friendRequestList == null)
                    warningMsgTv.setVisibility(View.VISIBLE);
                else if(friendRequestList.getResultArray() == null)
                    warningMsgTv.setVisibility(View.VISIBLE);
                else if(friendRequestList.getResultArray().size() == 0)
                    warningMsgTv.setVisibility(View.VISIBLE);
                else
                    warningMsgTv.setVisibility(View.GONE);
            }

            else if (object instanceof GroupRequestResult) {
                GroupRequestResult groupRequestResult = (GroupRequestResult) object;

                if (groupRequestResult == null)
                    warningMsgTv.setVisibility(View.VISIBLE);
                else if(groupRequestResult.getResultArray() == null)
                    warningMsgTv.setVisibility(View.VISIBLE);
                else if(groupRequestResult.getResultArray().size() == 0)
                    warningMsgTv.setVisibility(View.VISIBLE);
                else
                    warningMsgTv.setVisibility(View.GONE);
            }

            else if (object instanceof UserListResponse) {
                UserListResponse userListResponse = (UserListResponse) object;

                if (userListResponse == null)
                    warningMsgTv.setVisibility(View.VISIBLE);
                else if(userListResponse.getItems() == null)
                    warningMsgTv.setVisibility(View.VISIBLE);
                else if(userListResponse.getItems().size() == 0)
                    warningMsgTv.setVisibility(View.VISIBLE);
                else
                    warningMsgTv.setVisibility(View.GONE);
            }

            else if(object == null){
                warningMsgTv.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, MessageDataUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }

    }
}
