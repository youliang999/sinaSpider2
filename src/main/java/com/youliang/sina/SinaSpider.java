package com.youliang.sina;

import com.google.gson.Gson;
import com.youliang.service.SinaUserService;
import com.youliang.service.bean.SinaSpiderUser;
import com.youliang.sina.bean.AjaxEntity;
import com.youliang.sina.bean.HerFollowUser;
import com.youliang.sina.bean.SpiderQueue;
import com.youliang.sina.monitor.QueueMonitor;
import com.youliang.util.ApplicationContextUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.youliang.sina.Config.parseCookie2Map;

public class SinaSpider {
    private static SinaUserService sinaUserService = (SinaUserService) ApplicationContextUtil.getInstance().getBean("sinaUserService");
    private static CookieStore cookieStore = null;
    private static final Logger log = LoggerFactory.getLogger(SinaSpider.class);
    private static String getRegionUrl = "https://m.weibo.cn/api/container/getSecond?containerid=1005056501995851_-_FOLLOWERS";
    private static String cookie = "SCF=Av24-2z2LZIS5jPmv-a8xxgt975vd-GbV9LV_lVQydCgpciSSSRBlMb06wSjVFDXNXi_JRKsnu7smvppZHmB_Bg.; _T_WM=d023b97e83cbfbdc3e32f17acefe3e0e; SUB=_2A252CIABDeRhGeBL61MY-SvEzj2IHXVV8iBJrDV6PUJbkdANLUT6kW1NR3TryHC0oiNcwkx2XsLVHMUayERUdUV4; SUHB=0xnSfrvWZqJkEC; SSOLoginState=1527574609; H5_INDEX=3; H5_INDEX_TITLE=killer147; WEIBOCN_FROM=1110006030; MLOGIN=1; M_WEIBOCN_PARAMS=featurecode%3D20000320%26luicode%3D10000012%26lfid%3D1005056501995851_-_FOLLOWERS";
    private static Map<String, String> cookieMap = parseCookie2Map(cookie);
    // CloseableHttpClient client = HttpClients.createDefault();
    private static CloseableHttpClient client = HttpClients.custom()
            .setDefaultCookieStore(cookieStore).build();
    private static final Map<Long, String> followAndFansMap = new ConcurrentHashMap<>();
    private static final ConcurrentLinkedQueue<SpiderQueue> spiderQueues = new ConcurrentLinkedQueue<>();
    public static void main(String[] args) {
        getFollowMap();
        //saveFollowUser("https://m.weibo.cn/api/container/getIndex?containerid=231051_-_followers_-_6516607106&luicode=10000011&lfid=1005056516607106&featurecode=20000320");
        //saveFollowUser("https://m.weibo.cn/api/container/getIndex?containerid=231051_-_followers_-_6301861772_-_1042015%253AtagCategory_004&featurecode=20000320");
        //saveFansUser("https://m.weibo.cn/api/container/getIndex?containerid=231051_-_fans_-_1863847262&featurecode=20000320");
//        for (String url : followAndFansMap.values()) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    SpiderQueue spiderQueue = spiderQueues.poll();
                    if(!StringUtil.isBlank(spiderQueue.getFollowUrl())){
                        saveFollowUser(spiderQueue.getFollowUrl());
                    }
                    if(!StringUtil.isBlank(spiderQueue.getFansUrl())){
                        saveFansUser(spiderQueue.getFansUrl());
                    }
                }
            }
        }).start();
        new Thread(new QueueMonitor("sinaSpiderQueue", spiderQueues)).start();


//        }

    }

    public static void  getFollowMap() {
        boolean res = false;
        HttpResponse httpResponse = null;
        int i = 1;
        String nextUrl = getRegionUrl;
        do {
            i++;
            HttpGet get = new HttpGet(nextUrl);
            get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36");
            get.addHeader("Referer", "https://weibo.com/6501995851/follow?from=page_100505&wvr=6&mod=headfollow");
            try {
                log.info("nextUrl: {}", nextUrl);
                httpResponse = client.execute(get);
                res = printResponse(httpResponse);
                nextUrl += ("&page=" + i);
            } catch (IOException e) {

                e.printStackTrace();
            }

        } while (res);
       // log.info("followMap: {} ", followAndFansMap);
    }

    public static boolean printResponse(HttpResponse httpResponse)
            throws ParseException, IOException {
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
            JSONArray jsonArr = JSONArray.fromObject("["+responseString+"]");
            if((Integer)jsonArr.getJSONObject(0).get("ok") == 1) {
                //log.info("success.{}", jsonArr.getJSONObject(0).getJSONObject("data").get("cards").toString());
                JSONArray cardArray = (JSONArray)jsonArr.getJSONObject(0).getJSONObject("data").get("cards");
                //log.info("cardArray:{}", cardArray);
                List<AjaxEntity> ajaxEntities = new ArrayList<>();
                ajaxEntities = (List<AjaxEntity>)JSONArray.toCollection(cardArray, AjaxEntity.class);
                //log.info("==>> entities: {}", new Gson().toJson(ajaxEntities));

                for(AjaxEntity ajaxEntity : ajaxEntities) {
                    if(ajaxEntity != null) {
                        String followUrl = GetFollowOrFans.getFollows(ajaxEntity.getScheme(), ajaxEntity.getUser().getId());
                        //log.info("====================followUrl: {}", followUrl);
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
//                        if(!followAndFansMap.containsKey(ajaxEntity.getUser().getId())) {
//                            followAndFansMap.put(ajaxEntity.getUser().getId(), ajaxFollowUrl);
//
//                        }
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                log.info("errorMsg: {}", jsonArr.getJSONObject(0).getJSONObject("data").get("msg"));
                return false;
            }
        }
        return true;
    }

    public static void saveFansUser(String url) {
        String nextUrl = url;
        int loop = 1;
        boolean res = true;
        do {

            log.info("======nextUrl==========: {}", nextUrl);
            HttpGet get = new HttpGet(nextUrl);
            get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36");
            get.addHeader("Referer", "https://weibo.com/6501995851/follow?from=page_100505&wvr=6&mod=headfollow");
            try {
                HttpResponse httpResponse = client.execute(get);
                HttpEntity entity = httpResponse.getEntity();
                // 响应状态
//                System.out.println("status:" + httpResponse.getStatusLine());
//                System.out.println("headers:");
//                HeaderIterator iterator = httpResponse.headerIterator();
//                while (iterator.hasNext()) {
//                    System.out.println("\t" + iterator.next());
//                }
                // 判断响应实体是否为空
                if (entity != null) {
                    String responseString = EntityUtils.toString(entity);
                    //System.out.println("response length:" + responseString.length());
                    //System.out.println("response content:" + unicodeToString(responseString.replace("\r\n", "")));
                    JSONArray jsonArr = JSONArray.fromObject("[" + responseString + "]");
                    if ((Integer) jsonArr.getJSONObject(0).get("ok") == 1) {
                        JSONArray cardArray = (JSONArray) jsonArr.getJSONObject(0).getJSONObject("data").get("cards");
                        log.info("size: {}", cardArray.size());
                        List<HerFollowUser> herFollowUsers = new ArrayList<>();
                        for (int i = 0; i < cardArray.size(); i++) {
                            log.info("===========>>>> content: {}", cardArray.get(i));
                            String title = (String) ((JSONObject) cardArray.get(i)).get("title");
                            if (!StringUtil.isBlank(title) && title.indexOf("全部粉丝") != -1) {
                                JSONArray followsArr = (JSONArray) ((JSONObject) cardArray.get(i)).get("card_group");
                                log.info("===>>>size: {} followsArr: {}", followsArr.size(), followsArr);
                                herFollowUsers = (List<HerFollowUser>) JSONArray.toCollection(followsArr, HerFollowUser.class);
                                log.info("====>>>> get FollowUsers: {}", new Gson().toJson(herFollowUsers));
                            } else if(loop > 1) {
                                JSONArray followsArr = (JSONArray) ((JSONObject) cardArray.get(i)).get("card_group");
                                log.info("===>>> loop size: {} followsArr: {}", followsArr.size(), followsArr);
                                herFollowUsers = (List<HerFollowUser>) JSONArray.toCollection(followsArr, HerFollowUser.class);
                                log.info("====>>>> loop get FollowUsers: {}", new Gson().toJson(herFollowUsers));
                            }
                            saveFollowData(herFollowUsers);
                        }

                    } else {
                        log.info("=============error==============: {}", jsonArr.getJSONObject(0).get("msg"));
                        res = false;
                    }
                }
                loop++;
                nextUrl = url +  ("&since_id=" + loop);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (res);

//        try {
//            client.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static void saveFollowUser(String url) {
        String nextUrl = url;
        int loop = 1;
        boolean res = true;
        do {

            //log.info("======nextUrl==========: {}", nextUrl);
            HttpGet get = new HttpGet(nextUrl);
            get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36");
            get.addHeader("Referer", "https://weibo.com/6501995851/follow?from=page_100505&wvr=6&mod=headfollow");
            try {
                HttpResponse httpResponse = client.execute(get);
                HttpEntity entity = httpResponse.getEntity();
                // 响应状态
//                System.out.println("status:" + httpResponse.getStatusLine());
//                System.out.println("headers:");
//                HeaderIterator iterator = httpResponse.headerIterator();
//                while (iterator.hasNext()) {
//                    System.out.println("\t" + iterator.next());
//                }
                // 判断响应实体是否为空
                if (entity != null) {
                    String responseString = EntityUtils.toString(entity);
                    //System.out.println("response length:" + responseString.length());
                    //System.out.println("response content:" + unicodeToString(responseString.replace("\r\n", "")));
                    JSONArray jsonArr = JSONArray.fromObject("[" + responseString + "]");
                    if ((Integer) jsonArr.getJSONObject(0).get("ok") == 1) {
                        JSONArray cardArray = (JSONArray) jsonArr.getJSONObject(0).getJSONObject("data").get("cards");
//                        log.info("size: {}", cardArray.size());
                        List<HerFollowUser> herFollowUsers = new ArrayList<>();
                        for (int i = 0; i < cardArray.size(); i++) {
                            //log.info("===========>>>> content: {}", cardArray.get(i));
                            String title = (String) ((JSONObject) cardArray.get(i)).get("title");
                            if (!StringUtil.isBlank(title) && title.indexOf("全部关注") != -1) {
                                JSONArray followsArr = (JSONArray) ((JSONObject) cardArray.get(i)).get("card_group");
                                log.info("===>>>size: {} followsArr: {}", followsArr.size(), followsArr);
                                herFollowUsers = (List<HerFollowUser>) JSONArray.toCollection(followsArr, HerFollowUser.class);
                                //log.info("====>>>> get FollowUsers: {}", new Gson().toJson(herFollowUsers));
                            } else if(loop > 1) {
                                JSONArray followsArr = (JSONArray) ((JSONObject) cardArray.get(i)).get("card_group");
                                log.info("===>>> loop size: {} followsArr: {}", followsArr.size(), followsArr);
                                herFollowUsers = (List<HerFollowUser>) JSONArray.toCollection(followsArr, HerFollowUser.class);
                                //log.info("====>>>> loop get FollowUsers: {}", new Gson().toJson(herFollowUsers));
                            }
                            saveFollowData(herFollowUsers);
                        }

                    } else {
                        log.info("=============error==============: {}", jsonArr.getJSONObject(0).get("msg"));
                        res = false;
                    }
                }
                loop++;
                nextUrl = url +  ("&page=" + loop);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (res);

//        try {
//            client.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }


    public static  void saveFollowData(List<HerFollowUser> herFollowUsers) {
        for(HerFollowUser user : herFollowUsers) {
            SinaSpiderUser spiderUser = new SinaSpiderUser();
            spiderUser.setSid(user.getUser().getId());
            spiderUser.setDesc1(killEmoji(user.getDesc1()));
            spiderUser.setDesc2(killEmoji(user.getDesc2()));
            spiderUser.setProfile_image_url(user.getUser().getProfile_image_url());
            spiderUser.setCover_image_phone(user.getUser().getCover_image_phone());
            spiderUser.setProfile_url(user.getUser().getProfile_url());

            spiderUser.setScheme(user.getScheme());
            spiderUser.setScreen_name(user.getUser().getScreen_name());
            spiderUser.setFollow_count(user.getUser().getFollow_count());
            spiderUser.setFollowers_count(user.getUser().getFollowers_count());
            if(!sinaUserService.isExistUser(user.getUser().getId())) {
                sinaUserService.insert(spiderUser);
                log.info("insert a record");
            }
            if(!spiderQueues.contains(user.getUser().getId())) {
                pasrseScheme(user.getScheme(), user.getUser().getId());
            }

        }
    }


    public static void pasrseScheme(String scheme, Long id) {
        if(StringUtil.isBlank(scheme)) {
            return;
        }
        String ajaxUrl = scheme2AjaxUrl(scheme);
        String followUrl = GetFollowOrFans.getFollows(ajaxUrl, id);
        log.info("pasrseScheme : followUrl: {}", followUrl);
        String ajaxFollowUrl ="";
        String ajaxFansUrl = "";
        if(StringUtil.isBlank(followUrl)) {
            log.info("pasrseScheme add skip");
            return;
        }
        if(followUrl.split("\\?") == null || followUrl.split("\\?").length <= 0 ) {
            ajaxFollowUrl = followUrl;
            ajaxFansUrl = followUrl;
        } else {
            ajaxFollowUrl ="https://m.weibo.cn/api/container/getIndex?" + (followUrl.split("\\?").length == 2 ? followUrl.split("\\?")[1] : "");
            ajaxFansUrl = ajaxFollowUrl.replace("followers", "fans");
        }

        spiderQueues.add(new SpiderQueue(id, ajaxFollowUrl, ajaxFansUrl));
        log.info("====>>>> add a record..{}, {}", ajaxFollowUrl, ajaxFansUrl);
    }

    public static String scheme2AjaxUrl(String scheme) {
        return "";
    }


    /**
     * 过滤emoji 或者 其他非文字类型的字符
     *
     * @param source
     * @return
     */
    public static String killEmoji(String source) {
        if (StringUtils.isBlank(source)) {
            return source;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : source.toCharArray()) {
            if ((c == 0x0) || (c == 0x9) || (c == 0xA) || (c == 0xD) || ((c >= 0x20) && (c <= 0xD7FF)) || ((c >= 0xE000) && (c <= 0xFFFD)) || ((c >= 0x10000) && (c <= 0x10FFFF))) {
                sb.append(c);
            }
        }
        return sb.toString();
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

    public static void setCookieStore(String cook) {
        System.out.println("----setCookieStore");
        cookieStore = new BasicCookieStore();
        if(StringUtil.isBlank(cook)) {
            return;
        }
        String[] cs = cook.split(";");
        if(cs.length <= 0) {
            return;
        }
        for (String coo : cs) {
            String[] coos = coo.split(":");
            if(coos != null && coos.length == 2) {
                String name = coos[0];
                String value = coos[1];
                BasicClientCookie cookie = new BasicClientCookie(name,
                        value);
                cookieStore.addCookie(cookie);
            }

        }


    }



}
