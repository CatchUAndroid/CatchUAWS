package com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses;

import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Models.ReceiveMessageBox;

import java.util.ArrayList;

public class ReceivingMessageUtil {

    private static ReceivingMessageUtil instance = null;
    private static ArrayList<ReceiveMessageBox> receiveMessageBoxes;

    public static ReceivingMessageUtil getInstance() {

        if (instance == null) {
            receiveMessageBoxes = new ArrayList<>();
            instance = new ReceivingMessageUtil();
        }
        return instance;
    }

    public void setInstance(ReceivingMessageUtil instance) {
        ReceivingMessageUtil.instance = instance;
    }

    public void addRemoteMessageToList(ReceiveMessageBox receiveMessageBox) {
        if (receiveMessageBoxes != null)
            receiveMessageBoxes.add(receiveMessageBox);
    }

    public int getListSize() {
        if (receiveMessageBoxes != null)
            return receiveMessageBoxes.size();
        else return 0;
    }
}
