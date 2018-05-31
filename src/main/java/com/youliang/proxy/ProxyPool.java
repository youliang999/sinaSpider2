package com.youliang.proxy;

import com.youliang.proxy.bean.Direct;
import com.youliang.proxy.bean.Proxy;
import com.youliang.proxy.siteparse.Ip181ProxyListPageParser;
import com.youliang.proxy.siteparse.Ip66ProxyListPageParser;
import com.youliang.proxy.siteparse.MimiipProxyListPageParser;
import com.youliang.proxy.siteparse.XicidailiProxyListPageParser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.youliang.config.Constants.TIME_INTERVAL;

public class ProxyPool {
    /**
     * proxySet读写锁
     */
    public static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    public static final Set<Proxy> proxySet = new HashSet<>();

    /**
     * 代理池延迟队列
     */
    public static final DelayQueue<Proxy> proxyQueue = new DelayQueue<>();
    public static final Map<String, Class> proxyMap = new HashMap<>();

    static {
        int pages = 8;
        for(int i=0; i <= pages; i++) {
            proxyMap.put("http://www.xicidaili.com/wt/" + i + ".html", XicidailiProxyListPageParser.class);
            proxyMap.put("http://www.xicidaili.com/nn/" + i + ".html", XicidailiProxyListPageParser.class);
            proxyMap.put("http://www.xicidaili.com/wn/" + i + ".html", XicidailiProxyListPageParser.class);
            proxyMap.put("http://www.xicidaili.com/nt/" + i + ".html", XicidailiProxyListPageParser.class);
            proxyMap.put("http://www.ip181.com/daili/" + i + ".html", Ip181ProxyListPageParser.class);
            proxyMap.put("http://www.mimiip.com/gngao/" + i, MimiipProxyListPageParser.class);//高匿
            proxyMap.put("http://www.mimiip.com/gnpu/" + i, MimiipProxyListPageParser.class);//普匿
            proxyMap.put("http://www.66ip.cn/" + i + ".html", Ip66ProxyListPageParser.class);
            for(int j = 1; j < 34; j++){
                proxyMap.put("http://www.66ip.cn/areaindex_" + j + "/" + i + ".html", Ip66ProxyListPageParser.class);
            }
        }
        proxyQueue.add(new Direct(TIME_INTERVAL));
    }
}
