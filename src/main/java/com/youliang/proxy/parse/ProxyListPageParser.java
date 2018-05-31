package com.youliang.proxy.parse;

import com.youliang.proxy.bean.Proxy;

import java.util.List;

public interface ProxyListPageParser extends Parser{
    /**
     * 是否只要匿名代理
     */
    static final boolean anonymousFlag = true;
    List<Proxy> parse(String content);
}
