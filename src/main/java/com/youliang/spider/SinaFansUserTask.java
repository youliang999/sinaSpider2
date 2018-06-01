package com.youliang.spider;

import com.google.gson.Gson;
import com.youliang.proxy.bean.Page;
import org.apache.http.client.methods.HttpRequestBase;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SinaFansUserTask extends AbstactPageTask {
    private static final Logger log = LoggerFactory.getLogger(SinaFansUserTask.class);

    public SinaFansUserTask(String url, boolean proxyFlag) {
        super(url, proxyFlag);
    }

    public SinaFansUserTask(HttpRequestBase request, boolean proxyFlag) {
        super(request, proxyFlag);
    }

    @Override
    public void run() {
        log.info("=================》》》 开始抓取粉丝用户。。。");
        log.info("spiderQueue: {}", new Gson().toJson(spiderQueues));
        super.url = spiderQueues.poll().getFansUrl();
        super.run();
    }

    @Override
    protected boolean handle(Page page) {
        log.info("handle fans page: {}", new Gson().toJson(page));
        if(StringUtil.isBlank(page.getHtml())) {
            log.warn("handle page html is blank");
        }
        return false;
    }

    @Override
    protected void retry(Object o) {

    }
}
