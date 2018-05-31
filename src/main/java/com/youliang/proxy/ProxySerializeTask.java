package com.youliang.proxy;

import com.youliang.config.SpiderConfig;
import com.youliang.proxy.bean.Proxy;
import com.youliang.proxy.util.ProxyUtil;
import com.youliang.util.MySpiderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxySerializeTask implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ProxySerializeTask.class);

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000*60*1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Proxy[] copyProxy$ = null;
            ProxyPool.lock.readLock().lock();
            try {
                copyProxy$ = new Proxy[ProxyPool.proxySet.size()];
                int i = 0;
                for (Proxy p : ProxyPool.proxySet) {
                    if(!ProxyUtil.isDiscardProxy(p)) {
                        copyProxy$[i++] = p;
                    }
                }
            } catch (Exception e) {
                log.error("error happend. {}",e.toString());
            } finally {
                ProxyPool.lock.readLock().unlock();
            }
            MySpiderUtil.serializeObject(copyProxy$, SpiderConfig.proxyPath);
            log.info("成功序列化{}个代理", copyProxy$.length);
        }

    }
}
