package com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses;

import catchu.model.Post;

public class SinglePost {

    private static SinglePost ourInstance ;
    private static Post post;

    public static SinglePost getInstance() {
        if (ourInstance == null) {
            ourInstance = new SinglePost();
        }
        return ourInstance;
    }

    public SinglePost() {
        post = new Post();
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        SinglePost.post = post;
    }


}
