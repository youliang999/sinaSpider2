package com.youliang.sina;

import org.apache.http.client.CookieStore;
import org.jsoup.helper.StringUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Config {
    public static CookieStore cookieStore = null;
    public static String cookie = "SCF=Av24-2z2LZIS5jPmv-a8xxgt975vd-GbV9LV_lVQydCgpciSSSRBlMb06wSjVFDXNXi_JRKsnu7smvppZHmB_Bg.; _T_WM=d023b97e83cbfbdc3e32f17acefe3e0e; SUB=_2A252CIABDeRhGeBL61MY-SvEzj2IHXVV8iBJrDV6PUJbkdANLUT6kW1NR3TryHC0oiNcwkx2XsLVHMUayERUdUV4; SUHB=0xnSfrvWZqJkEC; SSOLoginState=1527574609; H5_INDEX=3; H5_INDEX_TITLE=killer147; WEIBOCN_FROM=1110006030; MLOGIN=1; M_WEIBOCN_PARAMS=featurecode%3D20000320%26luicode%3D10000012%26lfid%3D1005056501995851_-_FOLLOWERS";
    public static Map<String, String> cookieMap = parseCookie2Map(cookie);

    public static Map<String, String> parseCookie2Map(String cookie) {
        Map<String, String> cookieMap = new HashMap<>();
        if(StringUtil.isBlank(cookie)) {
            return Collections.EMPTY_MAP;
        }
        String[] cs = cookie.split(";");
        if(cs.length <= 0) {
            return Collections.EMPTY_MAP;
        }
        for (String coo : cs) {
            String[] coos = coo.split(":");
            if(coos != null && coos.length == 2) {
                String name = coos[0];
                String value = coos[1];
                cookieMap.put(name, value);
            }
        }
        return cookieMap;
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
