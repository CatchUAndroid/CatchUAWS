package com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.JavaClasses;

import java.util.ArrayList;
import java.util.List;

import catchu.model.Post;

public class SingletonPostList {

    private static SingletonPostList ourInstance ;
    private static List<Post> postList;

    public static SingletonPostList getInstance() {
        if (ourInstance == null) {
            ourInstance = new SingletonPostList();
        }
        return ourInstance;
    }

    public SingletonPostList() {
        postList = new ArrayList<Post>();

    }

    public List<Post> getPostList() {
        return postList;
    }

    public void setPostList(List<Post> postList) {
        SingletonPostList.postList = postList;
    }

    public void addPostList(List<Post> postList) {
        SingletonPostList.postList.addAll(postList);
    }

    public void clearPostList() {
        SingletonPostList.postList.clear();
    }

    public static synchronized void reset(){
        ourInstance = null;
    }

}
