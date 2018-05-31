package com.youliang.sina.bean;

import java.io.Serializable;

public class SpiderQueue implements Serializable {
    private static final long serialVersionUID = -4092756624859048649L;

    private Long id;
    private String followUrl;
    private String fansUrl;

    public SpiderQueue(Long id, String followUrl, String fansUrl) {
        this.id = id;
        this.followUrl = followUrl;
        this.fansUrl = fansUrl;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFollowUrl() {
        return followUrl;
    }

    public void setFollowUrl(String followUrl) {
        this.followUrl = followUrl;
    }

    public String getFansUrl() {
        return fansUrl;
    }

    public void setFansUrl(String fansUrl) {
        this.fansUrl = fansUrl;
    }
}
