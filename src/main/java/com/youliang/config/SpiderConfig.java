package com.youliang.config;

import java.io.IOException;
import java.util.Properties;

public class SpiderConfig {

    public static String proxyPath ;
    public static String cookie = "SCF=Av24-2z2LZIS5jPmv-a8xxgt975vd-GbV9LV_lVQydCgpciSSSRBlMb06wSjVFDXNXi_JRKsnu7smvppZHmB_Bg.; _T_WM=d023b97e83cbfbdc3e32f17acefe3e0e; SUB=_2A252CIABDeRhGeBL61MY-SvEzj2IHXVV8iBJrDV6PUJbkdANLUT6kW1NR3TryHC0oiNcwkx2XsLVHMUayERUdUV4; SUHB=0xnSfrvWZqJkEC; SSOLoginState=1527574609; H5_INDEX=3; H5_INDEX_TITLE=killer147; WEIBOCN_FROM=1110006030; MLOGIN=1; M_WEIBOCN_PARAMS=featurecode%3D20000320%26luicode%3D10000012%26lfid%3D1005056501995851_-_FOLLOWERS";
    static {
        Properties p = new Properties();
        try {
            p.load(SpiderConfig.class.getResourceAsStream("/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("proxyPath: "+ p.getProperty("proxyPath"));
        proxyPath = p.getProperty("proxyPath");
    }
}
