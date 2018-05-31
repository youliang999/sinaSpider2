package com.youliang.sina.bean;

import java.io.Serializable;

public class Actionlog implements Serializable {
    private static final long serialVersionUID = 8607706940076212934L;
    private Integer act_code;
    private String cardid;
    private Long oid;
    private String featurecode;
    private String mark;
    private String ext;
    private String uicode;
    private String luicode;
    private String fid;
    private String lfid;
    private String lcardid;
    public Integer getAct_code() {
        return act_code;
    }

    public void setAct_code(Integer act_code) {
        this.act_code = act_code;
    }

    public String getCardid() {
        return cardid;
    }

    public void setCardid(String cardid) {
        this.cardid = cardid;
    }

    public Long getOid() {
        return oid;
    }

    public void setOid(Long oid) {
        this.oid = oid;
    }

    public String getFeaturecode() {
        return featurecode;
    }

    public void setFeaturecode(String featurecode) {
        this.featurecode = featurecode;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getUicode() {
        return uicode;
    }

    public void setUicode(String uicode) {
        this.uicode = uicode;
    }

    public String getLuicode() {
        return luicode;
    }

    public void setLuicode(String luicode) {
        this.luicode = luicode;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getLfid() {
        return lfid;
    }

    public void setLfid(String lfid) {
        this.lfid = lfid;
    }

    public String getLcardid() {
        return lcardid;
    }

    public void setLcardid(String lcardid) {
        this.lcardid = lcardid;
    }
}
