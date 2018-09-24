package com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses;

/**
 * Created by krupenghetiya on 03/02/17.
 */

public class MyVideoModel {
    private final String image_url;
    private String video_url;
    private final String name;



    public MyVideoModel(String video_url, String image_url, String name) {
        this.video_url = video_url;
        this.image_url = image_url;
        this.name = name;
    }

    public MyVideoModel(String image_url, String name) {
        this.image_url = image_url;
        this.name = name;
    }

    public MyVideoModel(String name) {
        this.image_url=null;
        this.name = name;
    }

    public MyVideoModel() {
        this.video_url = null;
        this.image_url = null;
        this.name = null;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getVideo_url() {
        return video_url;
    }

    public String getName() {
        return name;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }
}
