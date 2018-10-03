package com.uren.catchu.SharePackage.VideoPicker.Utils;

import com.uren.catchu.Singleton.SelectedGroupList;
import com.uren.catchu.Singleton.Share.ShareItems;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;

public class VideoFileListForDelete {

    static VideoFileListForDelete instance = null;
    static List<File> fileList;

    public static VideoFileListForDelete getInstance(){

        if(instance == null) {
            fileList = new ArrayList<File>();
            instance = new VideoFileListForDelete();
        }
        return instance;
    }

    public static void setInstance(VideoFileListForDelete videoFileListForDelete){
        instance = videoFileListForDelete;
    }

    public void addFileToList(File file){
        fileList.add(file);
    }

    public void deleteAllFile(){
        for(File file: fileList)
            file.delete();
        instance = null;
    }
}
