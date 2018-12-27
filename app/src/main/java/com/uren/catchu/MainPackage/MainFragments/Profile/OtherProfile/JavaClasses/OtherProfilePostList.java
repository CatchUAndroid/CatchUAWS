package com.uren.catchu.MainPackage.MainFragments.Profile.OtherProfile.JavaClasses;

import java.util.ArrayList;
import java.util.List;

import catchu.model.Post;

public class OtherProfilePostList {

    private static OtherProfilePostList ourInstance ;
    private static List<Post> postList;

    public static OtherProfilePostList getInstance() {
        if (ourInstance == null) {
            ourInstance = new OtherProfilePostList();
        }
        return ourInstance;
    }

    public OtherProfilePostList() {
        postList = new ArrayList<Post>();

    }

    public List<Post> getPostList() {
        return postList;
    }

    public void setPostList(List<Post> postList) {
        OtherProfilePostList.postList = postList;
    }

    public void addPostList(List<Post> postList) {
        OtherProfilePostList.postList.addAll(postList);
    }

    public void clearPostList() {
        OtherProfilePostList.postList.clear();
    }

    public static synchronized void reset(){
        ourInstance = null;
    }

}
