package com.youliang.sina.bean;

import java.io.Serializable;

public class HerFollowUser implements Serializable {
    private static final long serialVersionUID = 788608809580314010L;
    private String itemid;
    private Integer card_type;
    private String desc1;
    private String desc2;
    private String recom_remark;
    private String scheme;
    private AnotherUser user;
    private Integer background_color;
    private Actionlog actionlog;
    private Buttons[] buttons;

    public Actionlog getActionlog() {
        return actionlog;
    }

    public void setActionlog(Actionlog actionlog) {
        this.actionlog = actionlog;
    }

    public Buttons[] getButtons() {
        return buttons;
    }

    public void setButtons(Buttons[] buttons) {
        this.buttons = buttons;
    }

    public Integer getBackground_color() {
        return background_color;
    }

    public void setBackground_color(Integer background_color) {
        this.background_color = background_color;
    }

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public Integer getCard_type() {
        return card_type;
    }

    public void setCard_type(Integer card_type) {
        this.card_type = card_type;
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

    public String getRecom_remark() {
        return recom_remark;
    }

    public void setRecom_remark(String recom_remark) {
        this.recom_remark = recom_remark;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public AnotherUser getUser() {
        return user;
    }

    public void setUser(AnotherUser user) {
        this.user = user;
    }
}
