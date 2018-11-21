package com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses;

import catchu.model.Post;

public class SingletonSinglePost {

    private static SingletonSinglePost ourInstance ;
    private static Post post;

    public static SingletonSinglePost getInstance() {
        if (ourInstance == null) {
            ourInstance = new SingletonSinglePost();
        }
        return ourInstance;
    }

    public SingletonSinglePost() {
        post = new Post();
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        SingletonSinglePost.post = post;
    }

    public static synchronized void reset(){
        ourInstance = null;
    }

}
