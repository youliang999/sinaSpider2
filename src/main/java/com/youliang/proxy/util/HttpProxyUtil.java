package com.youliang.proxy.util;

import com.youliang.config.Constants;
import com.youliang.config.SpiderConfig;
import org.apache.http.Consts;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.nio.charset.CodingErrorAction;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Random;

public class HttpProxyUtil {
    private static final Logger log = LoggerFactory.getLogger(HttpProxyUtil.class);
    private static CookieStore cookieStore = new BasicCookieStore();
    private static CloseableHttpClient httpClient;
    private static HttpHost proxy;
    private final static String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36";
    private static final RequestConfig  requestConfig =  RequestConfig.custom().setSocketTimeout(Constants.TIMEOUT).
                                                            setConnectTimeout(Constants.TIMEOUT).
                                                            setConnectionRequestTimeout(Constants.TIMEOUT).
                                                            setCookieSpec(CookieSpecs.STANDARD).
                                                            build();
    static {
        initHttpClient();
        setCookieStore(SpiderConfig.cookie);
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

    private static void initHttpClient(){
        SSLContext sslContext =
                null;
        try {
            sslContext = SSLContexts.custom()
                    .loadTrustMaterial(KeyStore.getInstance(KeyStore.getDefaultType()), new TrustStrategy() {
                        @Override
                        public boolean isTrusted(X509Certificate[] chain, String authType)
                                throws CertificateException {
                            return true;
                        }
                    }).build();
        SSLConnectionSocketFactory sslSFactory =
                new SSLConnectionSocketFactory(sslContext);
        Registry<ConnectionSocketFactory> socketFactoryRegistry =
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.INSTANCE).register("https", sslSFactory)
                        .build();

        PoolingHttpClientConnectionManager connManager =
                new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(Constants.TIMEOUT).setTcpNoDelay(true).build();
        connManager.setDefaultSocketConfig(socketConfig);

        ConnectionConfig connectionConfig =
                ConnectionConfig.custom().setMalformedInputAction(CodingErrorAction.IGNORE)
                        .setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8).build();
        connManager.setDefaultConnectionConfig(connectionConfig);
        connManager.setMaxTotal(500);
        connManager.setDefaultMaxPerRoute(300);
        HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                if (executionCount > 2) {
                    return false;
                }
                if (exception instanceof InterruptedIOException) {
                    return true;
                }
                if (exception instanceof ConnectTimeoutException) {
                    return true;
                }
                if (exception instanceof UnknownHostException) {
                    return true;
                }
                if (exception instanceof SSLException) {
                    return true;
                }
                HttpRequest request = HttpClientContext.adapt(context).getRequest();
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };
        HttpClientBuilder httpClientBuilder =
                HttpClients.custom().setConnectionManager(connManager)
                        .setRetryHandler(retryHandler)
                        .setDefaultCookieStore(new BasicCookieStore()).setUserAgent(userAgent);
        if (proxy != null) {
            httpClientBuilder.setRoutePlanner(new DefaultProxyRoutePlanner(proxy)).build();
        }
        httpClient = httpClientBuilder.build();
        } catch(Exception e) {
            log.error("HttpProxyUtil happend exception: {}", e);
            //e.printStackTrace();
        }
    }

    public static org.apache.http.client.config.RequestConfig.Builder getRequestConfigBuilder(){
        return RequestConfig.custom().setSocketTimeout(Constants.TIMEOUT).
                setConnectTimeout(Constants.TIMEOUT).
                setConnectionRequestTimeout(Constants.TIMEOUT).
                setCookieSpec(CookieSpecs.STANDARD);
    }

    public static void setCookieStore(CookieStore cookieStore) {
        HttpProxyUtil.cookieStore = cookieStore;
    }


    public static CloseableHttpResponse getResponse(String url) throws IOException {
        HttpGet getRequest = new HttpGet(url);
        return getResponse(getRequest);
    }

    public static CloseableHttpResponse getResponse(HttpRequestBase getRequest) throws IOException {
        if(getRequest.getConfig() == null) {
            getRequest.setConfig(requestConfig);
        }
        getRequest.setHeader("User-Agent", Constants.userAgentArray[new Random().nextInt(Constants.userAgentArray.length)]);
        HttpClientContext httpClientContext = HttpClientContext.create();
        httpClientContext.setCookieStore(cookieStore);
        CloseableHttpResponse response = null;
        response = httpClient.execute(getRequest, httpClientContext);
        return response;
    }

}
