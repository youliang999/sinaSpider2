package com.youliang.spider;

import com.google.gson.Gson;
import com.youliang.proxy.bean.Page;
import com.youliang.sina.GetFollowOrFans;
import com.youliang.sina.bean.AjaxEntity;
import com.youliang.sina.bean.SpiderQueue;
import net.sf.json.JSONArray;
import org.apache.http.client.methods.HttpRequestBase;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SinaPageTask extends AbstactPageTask {
    private static final Logger log = LoggerFactory.getLogger(SinaPageTask.class);
    public SinaPageTask(String url, boolean proxyFlag) {
        super(url, proxyFlag);
    }

    public SinaPageTask(HttpRequestBase request, boolean proxyFlag) {
        super(request, proxyFlag);
    }


    @Override
    protected boolean handle(Page page) {
        log.info("handle page: {}", new Gson().toJson(page));
        if(StringUtil.isBlank(page.getHtml())) {
            log.warn("handle page html is blank");
        }
        JSONArray jsonArr = JSONArray.fromObject("["+page.getHtml()+"]");
        if((Integer)jsonArr.getJSONObject(0).get("ok") == 1) {
            JSONArray cardArray = (JSONArray)jsonArr.getJSONObject(0).getJSONObject("data").get("cards");
            List<AjaxEntity> ajaxEntities = new ArrayList<>();
            ajaxEntities = (List<AjaxEntity>)JSONArray.toCollection(cardArray, AjaxEntity.class);
            for(AjaxEntity ajaxEntity : ajaxEntities) {
                if(ajaxEntity == null) {
                    continue;
                }
                String followUrl = GetFollowOrFans.getFollows(ajaxEntity.getScheme(), ajaxEntity.getUser().getId());
                String ajaxFollowUrl ="";
                String ajaxFansUrl = "";
                if(followUrl.split("\\?") == null || followUrl.split("\\?").length <= 0 ) {
                    ajaxFollowUrl = followUrl;
                    ajaxFansUrl = followUrl;
                } else {
                    ajaxFollowUrl ="https://m.weibo.cn/api/container/getIndex?" +  followUrl.split("\\?")[1];
                    ajaxFansUrl = ajaxFollowUrl.replace("followers", "fans");
                }
                if(!spiderQueues.contains(ajaxEntity.getUser().getId())) {
                    spiderQueues.add(new SpiderQueue(ajaxEntity.getUser().getId(), ajaxFollowUrl, ajaxFansUrl));
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.info("++++++++++++++++size: {}, spider queue: {}", spiderQueues.size(), new Gson().toJson(spiderQueues));
            return true;
        } else {
            log.info("errorMsg: {}", jsonArr.getJSONObject(0).getJSONObject("data").get("msg"));
            log.info("++++++++++++++++size: {}, spider queue: {}", spiderQueues.size(), new Gson().toJson(spiderQueues));
            return false;
        }


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
