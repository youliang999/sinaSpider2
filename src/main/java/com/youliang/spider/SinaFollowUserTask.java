package com.youliang.spider;

import com.google.gson.Gson;
import com.youliang.proxy.bean.Page;
import com.youliang.sina.bean.SpiderQueue;
import org.apache.http.client.methods.HttpRequestBase;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SinaFollowUserTask extends AbstactPageTask {
    private static final Logger log = LoggerFactory.getLogger(SinaFollowUserTask.class);


    public SinaFollowUserTask(String url, boolean proxyFlag) {
        super(url, proxyFlag);
    }

    public SinaFollowUserTask(HttpRequestBase request, boolean proxyFlag) {
        super(request, proxyFlag);
    }

    @Override
    public void run() {
        while (true) {
            log.info("=================》》》 开始抓取关注用户。。。");
            log.info("spiderQueue: {}", new Gson().toJson(spiderQueues));
            SpiderQueue takeQueue;
            do {
                takeQueue = spiderQueues.poll();
            } while (takeQueue == null);
            super.url = takeQueue.getFollowUrl();
            super.run();
        }

    }

    @Override
    protected boolean handle(Page page) {
        log.info("handle follow page: {}", new Gson().toJson(page));
        if(StringUtil.isBlank(page.getHtml())) {
            log.warn("handle page html is blank");
        }

        return false;
    }

    @Override
    protected void retry(Object o) {

    }
}
