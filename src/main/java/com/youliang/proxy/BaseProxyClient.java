package com.youliang.proxy;

import com.youliang.proxy.bean.Page;
import com.youliang.proxy.util.HttpProxyUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class BaseProxyClient {
    private static final Logger log = LoggerFactory.getLogger(BaseProxyClient.class);

    /**
     * 获取page
     */
    public Page getWebPage(HttpRequestBase getRequest) throws IOException {
        CloseableHttpResponse response = null;
        response = HttpProxyUtil.getResponse(getRequest);
        Page page = new Page();
        page.setStatusCode(response.getStatusLine().getStatusCode());
        page.setHtml(EntityUtils.toString(response.getEntity()));
        page.setUrl(getRequest.getURI().toString());
        return page;
    }

    public Page getWebPage(String url) throws IOException {
       return getWebPage(url, "utf-8");
    }

    public Page getWebPage(String url, String charset) throws IOException {
        Page page = new Page();
        CloseableHttpResponse response = null;
        response = HttpProxyUtil.getResponse(url);
        page.setStatusCode(response.getStatusLine().getStatusCode());
        page.setUrl(url);
        try {
            if(page.getStatusCode() == 200) {
            page.setHtml(EntityUtils.toString(response.getEntity(), charset));
            }
        } catch (IOException e) {
            log.error("getWebPage happend a exception: {}", e);
            //e.printStackTrace();
        }finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return page;
    }

}
