package com.youliang.proxy;

import com.youliang.config.Constants;
import com.youliang.proxy.bean.Page;
import com.youliang.proxy.bean.Proxy;
import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ProxyTestTask implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ProxyTestTask.class);
    private Proxy proxy;

    public ProxyTestTask(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        HttpGet get = new HttpGet(Constants.INDEX_URL);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(Constants.TIMEOUT).
                setConnectTimeout(Constants.TIMEOUT).
                setConnectionRequestTimeout(Constants.TIMEOUT).
                setProxy(new HttpHost(proxy.getIp(), proxy.getPort())).
                setCookieSpec(CookieSpecs.STANDARD).
                build();
        get.setConfig(requestConfig);
        try {
            Page page = TestClient.instance.getWebPage(get);
            String logStr = Thread.currentThread().getName() + " " + proxy.getProxyStr() +
                    "  executing request " + page.getUrl()  + " response statusCode:" + page.getStatusCode() +
                    "  request cost time:" + (System.currentTimeMillis() - start) + "ms";
            if (page == null || page.getStatusCode() != 200){
                //log.warn(logStr);
                return;
            }
            get.releaseConnection();
           // log.info(proxy.toString() + "---------" + page.toString());
            log.info("----------代理可用--------请求耗时:" + (System.currentTimeMillis() - start) + "ms");
            ProxyPool.proxyQueue.add(proxy);
        } catch (IOException e) {
            log.debug("IOException:", e);
        } finally {
            if (get != null){
                get.releaseConnection();
            }
        }
    }
}
