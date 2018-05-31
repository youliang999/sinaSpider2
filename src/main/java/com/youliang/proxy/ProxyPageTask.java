package com.youliang.proxy;

import com.youliang.config.Constants;
import com.youliang.proxy.bean.Direct;
import com.youliang.proxy.bean.Page;
import com.youliang.proxy.bean.Proxy;
import com.youliang.proxy.parse.ProxyListPageParser;
import com.youliang.proxy.siteparse.ProxyListPageParserFactory;
import com.youliang.proxy.util.HttpProxyUtil;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static com.youliang.proxy.ProxyPool.proxyQueue;

public class ProxyPageTask implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ProxyPageTask.class);
    private String url;
    private boolean isProxyDownload;
    private Proxy currentProxy;

    private HttpProxyClient proxyClient = HttpProxyClient.getInstance();

    public ProxyPageTask(String url, boolean isProxyDownload) {
        this.url = url;
        this.isProxyDownload = isProxyDownload;
    }

    /**
     * 代理获取主线程
     */
    @Override
    public void run() {
        long start = System.currentTimeMillis();
        HttpGet tmpGet = null;
        try {
            Page page = null;
            if(isProxyDownload) {
                tmpGet = new HttpGet(url);
                currentProxy = proxyQueue.take();
                if(!(currentProxy instanceof Direct)) {
                    HttpHost proxy = new HttpHost(currentProxy.getIp(), currentProxy.getPort());
                    tmpGet.setConfig(HttpProxyUtil.getRequestConfigBuilder().setProxy(proxy).build());
                }
                page = proxyClient.getWebPage(tmpGet);
            } else {
                page = proxyClient.getWebPage(url);
            }
            page.setProxy(currentProxy);
            int status = page.getStatusCode();
            String logStr = Thread.currentThread().getName() + " " + getProxyStr(currentProxy) +
                    "  executing request " + page.getUrl()  + " response statusCode:" + status +
                    "  request cost time:" + (System.currentTimeMillis() - start) + "ms";

            if(status == HttpStatus.SC_OK) {
                //log.debug(logStr);
                handle(page);
            } else {
                //log.error(logStr);
                Thread.sleep(100);
                retry();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            retry();
        } finally {
            if(currentProxy != null) {
                currentProxy.setTimeInterval(Constants.TIME_INTERVAL);
                proxyQueue.add(currentProxy);
            }
            if( tmpGet != null) {
                tmpGet.releaseConnection();
            }
        }

    }

    private void handle(Page page) {
        if(StringUtil.isBlank(page.getHtml())) {
            log.warn("splider page html is blank");
            return;
        }
        ProxyListPageParser proxyListPageParser = ProxyListPageParserFactory.getProxyListPageParser(ProxyPool.proxyMap.get(url));
        List<Proxy> proxies = proxyListPageParser.parse(page.getHtml());
        for(Proxy proxy : proxies) {
            if(true) {
                ProxyPool.lock.readLock().lock();
                boolean containFlag = ProxyPool.proxySet.contains(proxy);
                ProxyPool.lock.readLock().unlock();
                if(!containFlag) {
                    ProxyPool.lock.writeLock().lock();
                    ProxyPool.proxySet.add(proxy);
                    ProxyPool.lock.writeLock().unlock();
                    proxyClient.getProxyTestThreadPool().execute(new ProxyTestTask(proxy));
                }
            }
        }
    }

    private void retry() {
        proxyClient.getProxyDownloadThreadPool().execute(new ProxyPageTask(url, isProxyDownload));
    }

    private String getProxyStr(Proxy proxy) {
        if(proxy == null) {
            return "";
        }
        return proxy.getIp() + ":" + proxy.getPort();
    }
}
