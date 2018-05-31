package com.youliang.sina.bean;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 7259505994921464778L;
    private Long id;
    private String screen_name;
    private String profile_image_url;
    private String profile_url;
    private Integer statuses_count;
    private boolean verified;
    private Integer verified_type;
    private Integer verified_type_ext;
    private String verified_reason;
    private boolean close_blue_v;
    private String description;
    private String gender;
    private Integer mbtype;
    private Integer urank;
    private Integer mbrank;
    private boolean follow_me;
    private boolean following;
    private Integer followers_count;
    private Integer follow_count;
    private String cover_image_phone;
    private String avatar_hd;
    private boolean like;
    private boolean like_me;
    private String desc1;
    private String desc2;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getStatuses_count() {
        return statuses_count;
    }

    public void setStatuses_count(Integer statuses_count) {
        this.statuses_count = statuses_count;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public Integer getVerified_type() {
        return verified_type;
    }

    public void setVerified_type(Integer verified_type) {
        this.verified_type = verified_type;
    }

    public Integer getVerified_type_ext() {
        return verified_type_ext;
    }

    public void setVerified_type_ext(Integer verified_type_ext) {
        this.verified_type_ext = verified_type_ext;
    }

    public String getVerified_reason() {
        return verified_reason;
    }

    public void setVerified_reason(String verified_reason) {
        this.verified_reason = verified_reason;
    }

    public boolean isClose_blue_v() {
        return close_blue_v;
    }

    public void setClose_blue_v(boolean close_blue_v) {
        this.close_blue_v = close_blue_v;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getMbtype() {
        return mbtype;
    }

    public void setMbtype(Integer mbtype) {
        this.mbtype = mbtype;
    }

    public Integer getUrank() {
        return urank;
    }

    public void setUrank(Integer urank) {
        this.urank = urank;
    }

    public Integer getMbrank() {
        return mbrank;
    }

    public void setMbrank(Integer mbrank) {
        this.mbrank = mbrank;
    }

    public boolean isFollow_me() {
        return follow_me;
    }

    public void setFollow_me(boolean follow_me) {
        this.follow_me = follow_me;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
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

    public String getAvatar_hd() {
        return avatar_hd;
    }

    public void setAvatar_hd(String avatar_hd) {
        this.avatar_hd = avatar_hd;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public boolean isLike_me() {
        return like_me;
    }

    public void setLike_me(boolean like_me) {
        this.like_me = like_me;
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

    public static void main(String[] args) {
        System.out.println("max: "+ Integer.MAX_VALUE);
    }
}
