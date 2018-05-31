package com.youliang.spider;

import com.youliang.config.Constants;
import com.youliang.proxy.ProxyPool;
import com.youliang.proxy.bean.Direct;
import com.youliang.proxy.bean.Page;
import com.youliang.proxy.bean.Proxy;
import com.youliang.proxy.util.HttpProxyUtil;
import com.youliang.proxy.util.ProxyUtil;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class AbstactPageTask implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(AbstractHttpClient.class);
    protected String url;
    protected HttpRequestBase request;
    protected boolean proxyFlag; //是否通过代理下载
    protected Proxy currentProxy;// 当前线程使用的代理
    protected static SinaHttpClient sinaHttpClient = SinaHttpClient.getInstance();

    public AbstactPageTask(String url, boolean proxyFlag) {
        this.url = url;
        this.proxyFlag = proxyFlag;
    }

    public AbstactPageTask(HttpRequestBase request, boolean proxyFlag) {
        this.request = request;
        this.proxyFlag = proxyFlag;
    }

    @Override
    public void run() {
        long start = 0l;
        HttpGet tmpGet = null;
        Page page = null;
        Object retryObject = null;
        try {
            if(url != null) {
                retryObject = url;
                if(proxyFlag) {
                   tmpGet = new HttpGet(url);
                   currentProxy = ProxyPool.proxyQueue.take();
                   if(!(currentProxy instanceof Direct)) {
                       HttpHost proxy = new HttpHost(currentProxy.getIp(), currentProxy.getPort());
                       tmpGet.setConfig(HttpProxyUtil.getRequestConfigBuilder().setProxy(proxy).build());
                   }
                   start = System.currentTimeMillis();
                   page = sinaHttpClient.getWebPage(tmpGet);
                } else {
                   start = System.currentTimeMillis();
                   page = sinaHttpClient.getWebPage(url);
                }
            } else if(request != null) {
                retryObject = request;
                if(proxyFlag) {
                    currentProxy = ProxyPool.proxyQueue.take();
                    if(!(currentProxy instanceof Direct)) {
                        HttpHost proxy = new HttpHost(currentProxy.getIp(), currentProxy.getPort());
                        request.setConfig(HttpProxyUtil.getRequestConfigBuilder().setProxy(proxy).build());
                    }
                    start = System.currentTimeMillis();
                    page = sinaHttpClient.getWebPage(request);
                } else {
                    start = System.currentTimeMillis();
                    page = sinaHttpClient.getWebPage(request);
                }
            }
            long end = System.currentTimeMillis();
            page.setProxy(currentProxy);
            int status = page.getStatusCode();
            String logStr = Thread.currentThread().getName() + " " + currentProxy +
                    "  executing request " + page.getUrl()  + " response statusCode:" + status +
                    "  request cost time:" + (end - start) + "ms";
            if(status == HttpStatus.SC_OK) {
                currentProxy.setSuccessfulTimes(currentProxy.getSuccessfulTimes() + 1);
                currentProxy.setSuccessfulTotalTime(currentProxy.getSuccessfulTotalTime() + (end - start));
                double aTime = (currentProxy.getSuccessfulTotalTime() + 0.0) / currentProxy.getSuccessfulTimes();
                currentProxy.setSuccessfulAverageTime(aTime);
                currentProxy.setLastSuccessfulTime(System.currentTimeMillis());
                handle(page);
                log.info(logStr);
            } else if(status == 404 || status == 401 ||
                    status == 410){
                log.warn(logStr);
            } else {
                log.error(logStr);
                Thread.sleep(100);
                retry(retryObject);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            if(currentProxy != null){
                /**
                 * 该代理可用，将该代理继续添加到proxyQueue
                 */
                currentProxy.setFailureTimes(currentProxy.getFailureTimes() + 1);
            }
            if(sinaHttpClient.getDetailListThreadPool().isShutdown()) {
                retry(retryObject);
            }
        } finally {
            if (request != null){
                request.releaseConnection();
            }
            if (tmpGet != null){
                tmpGet.releaseConnection();
            }
            if (currentProxy != null && !ProxyUtil.isDiscardProxy(currentProxy)){
                currentProxy.setTimeInterval(Constants.TIME_INTERVAL);
                ProxyPool.proxyQueue.add(currentProxy);
            }
        }

    }

    /**
     * handle
     */
    protected abstract void handle(Page page);

    /**
     * retry
     */
    protected abstract void retry(Object o);
}
