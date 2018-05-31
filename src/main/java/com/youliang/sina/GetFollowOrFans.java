package com.youliang.sina;

import net.sf.json.JSONArray;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.youliang.sina.Config.cookie;

public class GetFollowOrFans {
    private static final Logger log = LoggerFactory.getLogger(GetFollowOrFans.class);
    private static final String URLPREFIX = "https://m.weibo.cn/api/container/getIndex?";
    public static void main(String[] args) {
        //getFollows("https://m.weibo.cn/u/1834253195?uid=1834253195&luicode=10000012&lfid=1005056501995851_-_FOLLOWERS&featurecode=20000320", 1834253195l);
        String res = getFollows("https://m.weibo.cn/u/6516607106?uid=6516607106&luicode=10000011&lfid=231051_-_followers_-_1834253195&featurecode=20000320", 6516607106l);
        log.info("res: {}",res);
        // getFollows("https://m.weibo.cn/u/5803219802?uid=5803219802&luicode=10000011&lfid=231051_-_followers_-_6516607106&featurecode=20000320", 5803219802l);
    }

    public static String getContainId(String url) {
        String uid ="";
        String fix = "";
        String[] args = url.split("\\?");
        if(args == null || args.length < 2) {
            return "";
        }
//        log.info("args: {}", new Gson().toJson(args));
        if(args != null && args.length == 2) {
            String argus = args[1];
//            log.info(" argus: {}", argus);
            String[] params = argus.split("\\&");
            for(String param : params) {
//                log.info(" param: {}", param);
                Pattern p = Pattern.compile("(\\w+)=(\\w+)");
                Matcher m = p.matcher(param);
                if(m.find()) {
                    if("uid".equals(m.group(1))) {
                        uid = m.group(2);
                    }
                    if("lfid".equals(m.group(1))) {
                        fix = m.group(2).substring(0,6);
                    }
                }
            }
        }
        return fix + uid;
    }

    public static String getAjaxUrl(String url, long id){
        String containId = "";
        URL url1 = null;
        try {
            url1 = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URLConnection con = null;
        try {

            con = url1.openConnection();
            con.addRequestProperty("Cookie", cookie);//设置获取的cookie
            con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36");
            con.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        log.info("set-cookie: {}",con.getHeaderField("Set-Cookie"));
        String sCookie = null;

        sCookie = con.getHeaderField("Set-Cookie");

        if (StringUtil.isBlank(sCookie)) {
            return "";
        }
        String[] cookies = sCookie.split("%26");
        if(cookies == null || cookies.length <= 0) {
            return "";
        }
//        log.info("cookie: {}, size:{}", cookies, cookies.length);
        for(String c : cookies) {
            if(c.startsWith("fid")) {
//                log.info("fid: {}", c);
                containId = c.split("%3D")[1];
//                    log.info("containId: {}", containId);

            }
        }

        if(StringUtil.isBlank(containId)) {
            containId = getContainId(url);
        }
        String params = "";
        String[] urlParams = url.split("\\?");
        if(urlParams != null && urlParams.length == 2) {
            params = urlParams[1];
        }
        String retUrl = URLPREFIX + params + "&" + "type=uid&value=" + id+ "&containerid=" +  containId;
//        log.info("====>>>> url: {}", retUrl);
        log.info("retUrl: {}", retUrl);
       return retUrl;
    }

  //  &type=uid&value=1834253195&containerid=1005051834253195


    public static String getFollows(String url, Long id) {
        log.info("getFollows: {}. {}", url, id);
        String ajaxUrl = getAjaxUrl(url, id);
        log.info("getAjaxUrl: {}", ajaxUrl);
        String followUrl = "";
        CloseableHttpClient client  = HttpClients.custom()
                .setDefaultCookieStore(Config.cookieStore).build();
        HttpGet get = new HttpGet(ajaxUrl);
        get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36");
        get.addHeader("Referer", "https://weibo.com/6501995851/follow?from=page_100505&wvr=6&mod=headfollow");
        try {
            HttpResponse httpResponse = client.execute(get);
            followUrl = printResponse(httpResponse);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭流并释放资源
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(StringUtil.isBlank(followUrl)) {
            return "";
        } else {
            return followUrl.replace("followersrecomm", "followers");
        }

    }

    public static String printResponse(HttpResponse httpResponse)
            throws ParseException, IOException {
        String followsUrl = "";
        // 获取响应消息实体
        HttpEntity entity = httpResponse.getEntity();
        // 响应状态
//        System.out.println("status:" + httpResponse.getStatusLine());
//        System.out.println("headers:");
//        HeaderIterator iterator = httpResponse.headerIterator();
//        while (iterator.hasNext()) {
//            System.out.println("\t" + iterator.next());
//        }
        // 判断响应实体是否为空
        if (entity != null) {
            String responseString = EntityUtils.toString(entity);
//            System.out.println("response length:" + responseString.length());
//            System.out.println("response content:"
//                    + unicodeToString(responseString.replace("\r\n", "")));
            JSONArray jsonArr = JSONArray.fromObject("[" + responseString + "]");
            log.info("jsonArr: {}", jsonArr);
            if ((Integer) jsonArr.getJSONObject(0).get("ok") == 1) {
               //log.info("fansUrl: {}", jsonArr.getJSONObject(0).getJSONObject("data").get("fans_scheme"));
//               log.info("followUrl: {}", jsonArr.getJSONObject(0).getJSONObject("data").get("follow_scheme"));
                followsUrl = (String)jsonArr.getJSONObject(0).getJSONObject("data").get("follow_scheme");
            }
        }
        return followsUrl;
    }

        public static String unicodeToString(String str) {

            Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
            Matcher matcher = pattern.matcher(str);
            char ch;
            while (matcher.find()) {
                //group 6728
                String group = matcher.group(2);
                //ch:'木' 26408
                ch = (char) Integer.parseInt(group, 16);
                //group1 \u6728
                String group1 = matcher.group(1);
                str = str.replace(group1, ch + "");
            }
            return str;
        }
 }
