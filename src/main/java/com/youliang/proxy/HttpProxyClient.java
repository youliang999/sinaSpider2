package com.youliang.proxy;

import com.youliang.config.Constants;
import com.youliang.config.SpiderConfig;
import com.youliang.proxy.bean.Proxy;
import com.youliang.sina.monitor.ThreadPoolMonitor;
import com.youliang.util.MySpiderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HttpProxyClient extends BaseProxyClient{
    private static final Logger log = LoggerFactory.getLogger(HttpProxyClient.class);
    private static volatile HttpProxyClient instance;

    public static HttpProxyClient getInstance() {
        if(instance == null) {
            synchronized (HttpProxyClient.class) {
                if(instance == null) {
                    instance = new HttpProxyClient();
                }
            }
        }
        return instance;
    }
    /**
     * 代理测试线程池
     */
    private ThreadPoolExecutor proxyTestThreadPool;

    /**
     * 代理网站下载线程池
     */
    private ThreadPoolExecutor proxyDownloadThreadPool;

    private HttpProxyClient() {
        initThreadPool();
        initProxy();
    }

    /**
     * 初始化线程池
     */
    private void initThreadPool() {
       proxyTestThreadPool = new ThreadPoolExecutor(10, 10, 0l, TimeUnit.MILLISECONDS,
               new LinkedBlockingQueue<Runnable>(10000),
               new ThreadPoolExecutor.DiscardPolicy());
       proxyDownloadThreadPool = new ThreadPoolExecutor(10, 10, 0l, TimeUnit.MILLISECONDS,
               new LinkedBlockingQueue<Runnable>(),
               new ThreadPoolExecutor.DiscardPolicy());
        new Thread(new ThreadPoolMonitor(proxyTestThreadPool, "代理测试线程池")).start();
        new Thread(new ThreadPoolMonitor(proxyDownloadThreadPool, "代理网站下载线程池")).start();
    }

    private void initProxy() {
        Proxy[] proxy$ = null;
        try {
            log.info("proxyPath: {}", SpiderConfig.proxyPath);
            proxy$ = (Proxy[])MySpiderUtil.deserializeObject(SpiderConfig.proxyPath);
            int useableProxyCount = 0;
            for(Proxy proxy : proxy$) {
                if (proxy == null) {
                    continue;
                }
                log.info("Proxy: ip:{} , port:{}", proxy.getIp(), proxy.getPort());
                proxy.setTimeInterval(Constants.TIME_INTERVAL);
                proxy.setFailureTimes(0);
                proxy.setSuccessfulTimes(0);
                long start = System.currentTimeMillis();
                //离上次成功小于一小时
                if (start - proxy.getLastSuccessfulTime() < 1000 * 60 * 60) {
                    ProxyPool.proxyQueue.add(proxy);
                    ProxyPool.proxySet.add(proxy);
                    useableProxyCount++;
                }
            }
            log.info("反序列化proxy成功，" + proxy$.length + "个代理,可用代理" + useableProxyCount + "个");
        } catch (Exception e) {
            log.warn("反序列化失败!");
            e.printStackTrace();
        }

    }

    /**
     * 抓取proxy
     */
    public void startCrawl() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    for(String url : ProxyPool.proxyMap.keySet()) {
                        /**
                         * 首次直接不通过代理下载代理
                         */
                        proxyDownloadThreadPool.execute(new ProxyPageTask(url, false));
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(1000*60*60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        new Thread(new ProxySerializeTask()).start();
    }

    public ThreadPoolExecutor getProxyDownloadThreadPool() {
        return proxyDownloadThreadPool;
    }

    public ThreadPoolExecutor getProxyTestThreadPool() {
        return proxyTestThreadPool;
    }
}
