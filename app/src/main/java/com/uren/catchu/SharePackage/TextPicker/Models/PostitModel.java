package com.uren.catchu.SharePackage.TextPicker.Models;

import com.uren.catchu.R;

public class PostitModel {

    static int[] postitList = {
            R.drawable.postit_1,
            R.drawable.postit_2,
            R.drawable.postit_3,
            R.drawable.postit_4,
            R.drawable.postit_5
    };

    public static int[] getPostitList() {
        return postitList;
    }

    public static void setPostitList(int[] postitList) {
        PostitModel.postitList = postitList;
    }

    public static int getPostitCount(){
        return postitList.length;
    }

    public static int getSelectedPostit(int index){
        return postitList[index];
    }
}
