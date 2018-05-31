package com.youliang.service.bean;

import java.io.Serializable;
import java.util.Date;

public class SinaSpiderUser implements Serializable {
    private static final long serialVersionUID = 7288193292848686343L;
    private int id;
    private Long sid;
    private String desc1;
    private String desc2;
    private String scheme;
    private String screen_name;
    private String profile_image_url;
    private String profile_url;
    private Integer followers_count;
    private Integer follow_count;
    private String cover_image_phone;
    private Date update_date;
    private Date create_date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    public String getDesc1() {
        return desc1;
    }

    public void setDesc1(String desc1) {
        this.desc1 = desc1;
    }

    public String getDesc2() {
        return desc2;
    }

    public void setDesc2(String desc2) {
        this.desc2 = desc2;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public Integer getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(Integer followers_count) {
        this.followers_count = followers_count;
    }

    public Integer getFollow_count() {
        return follow_count;
    }

    public void setFollow_count(Integer follow_count) {
        this.follow_count = follow_count;
    }

    public String getCover_image_phone() {
        return cover_image_phone;
    }

    public void setCover_image_phone(String cover_image_phone) {
        this.cover_image_phone = cover_image_phone;
    }

    public Date getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(Date update_date) {
        this.update_date = update_date;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    @Override
    public String toString() {
        return "SinaSpiderUser{" +
                "id=" + id +
                ", sid=" + sid +
                ", desc1='" + desc1 + '\'' +
                ", desc2='" + desc2 + '\'' +
                ", scheme='" + scheme + '\'' +
                ", screen_name='" + screen_name + '\'' +
                ", profile_image_url='" + profile_image_url + '\'' +
                ", profile_url='" + profile_url + '\'' +
                ", followers_count='" + followers_count + '\'' +
                ", follow_count='" + follow_count + '\'' +
                ", cover_image_phone='" + cover_image_phone + '\'' +
                ", update_date=" + update_date +
                ", create_date=" + create_date +
                '}';
    }
}
