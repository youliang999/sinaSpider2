package com.youliang.spider;

import com.youliang.proxy.bean.Page;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SinaPageTask extends AbstactPageTask {
    private static final Logger log = LoggerFactory.getLogger(SinaPageTask.class);

    public SinaPageTask(String url, boolean proxyFlag) {
        super(url, proxyFlag);
    }

    public SinaPageTask(HttpRequestBase request, boolean proxyFlag) {
        super(request, proxyFlag);
    }


    @Override
    protected void handle(Page page) {

    }

    @Override
    protected void retry(Object o) {
        if(o instanceof String) {
            sinaHttpClient.getDetailListThreadPool().execute(new SinaPageTask(url, true));
        } else if (o instanceof HttpRequestBase) {
            sinaHttpClient.getDetailListThreadPool().execute(new SinaPageTask(request, true));
        }
    }
}
