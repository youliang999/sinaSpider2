package com.youliang;

import com.youliang.proxy.HttpProxyClient;
import com.youliang.spider.SinaHttpClient;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        HttpProxyClient.getInstance().startCrawl();
        SinaHttpClient.getInstance().startCrawl();
    }
}
