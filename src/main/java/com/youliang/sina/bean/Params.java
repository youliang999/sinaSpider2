package com.youliang.sina.bean;

import java.io.Serializable;

public class Params implements Serializable {
    private static final long serialVersionUID = -953748144163194505L;
    private Long uid;
    private Integer need_follow;
    private Long trend_ext;
    private Integer trend_type;
    private Long itemid;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Integer getNeed_follow() {
        return need_follow;
    }

    public void setNeed_follow(Integer need_follow) {
        this.need_follow = need_follow;
    }

    public Long getTrend_ext() {
        return trend_ext;
    }

    public void setTrend_ext(Long trend_ext) {
        this.trend_ext = trend_ext;
    }

    public Integer getTrend_type() {
        return trend_type;
    }

    public void setTrend_type(Integer trend_type) {
        this.trend_type = trend_type;
    }

    public Long getItemid() {
        return itemid;
    }

    public void setItemid(Long itemid) {
        this.itemid = itemid;
    }
}
