package com.youliang.spider;

import com.youliang.proxy.bean.Page;
import com.youliang.proxy.util.HttpProxyUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AbstractHttpClient {
    private static final Logger log = LoggerFactory.getLogger(AbstractHttpClient.class);

    public Page getWebPage(String url) throws IOException {
        return getWebPage(url, "UTF-8");
    }
    public Page getWebPage(String url, String charset) throws IOException {
        Page page = new Page();
        CloseableHttpResponse response = null;
        response = HttpProxyUtil.getResponse(url);
        page.setStatusCode(response.getStatusLine().getStatusCode());
        page.setUrl(url);
        try {
            if(page.getStatusCode() == 200){
                page.setHtml(EntityUtils.toString(response.getEntity(), charset));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return page;
    }
    public Page getWebPage(HttpRequestBase request) throws IOException {
        CloseableHttpResponse response = null;
        response = HttpProxyUtil.getResponse(request);
        Page page = new Page();
        page.setStatusCode(response.getStatusLine().getStatusCode());
        page.setHtml(EntityUtils.toString(response.getEntity()));
        page.setUrl(request.getURI().toString());
        return page;
    }

}
