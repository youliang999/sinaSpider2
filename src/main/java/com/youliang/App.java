package com.youliang;

import com.youliang.proxy.HttpProxyClient;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        HttpProxyClient.getInstance().startCrawl();
    }
}
