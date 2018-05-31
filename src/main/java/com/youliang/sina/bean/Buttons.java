package com.youliang.sina.bean;

import java.io.Serializable;

public class Buttons implements Serializable {
    private static final long serialVersionUID = 1481332818127955700L;
    private String type;
    private Integer sub_type;
    private String name;
    private Integer skip_format;
    private Params params;
    private Actionlog actionlog;

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getSub_type() {
        return sub_type;
    }

    public void setSub_type(Integer sub_type) {
        this.sub_type = sub_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSkip_format() {
        return skip_format;
    }

    public void setSkip_format(int skip_format) {
        this.skip_format = skip_format;
    }

    public Actionlog getActionlog() {
        return actionlog;
    }

    public void setActionlog(Actionlog actionlog) {
        this.actionlog = actionlog;
    }
}
