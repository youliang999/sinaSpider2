package com.youliang.spider;

import com.youliang.sina.monitor.ThreadPoolMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SinaHttpClient extends AbstractHttpClient {
    private static Logger logger = LoggerFactory.getLogger(SinaHttpClient.class);
    private volatile static SinaHttpClient instance;

    private ThreadPoolExecutor detailListThreadPool;

    private ThreadPoolExecutor userFollowThreadPool;

    private ThreadPoolExecutor userFansThreadPool;

    /**
     * 统计用户数量
     */
    public static AtomicInteger parseUserCount = new AtomicInteger(0);
    private static long startTime = System.currentTimeMillis();
    public static volatile boolean isStop = false;
    public static SinaHttpClient getInstance(){
        if (instance == null){
            synchronized (SinaHttpClient.class){
                if (instance == null){
                    instance = new SinaHttpClient();
                }
            }
        }
        return instance;
    }

    private SinaHttpClient() {
    }


    public void startCrawl() {
        initThreadPool();
        detailListThreadPool.execute(new SinaPageTask("https://m.weibo.cn/api/container/getSecond?containerid=1005056501995851_-_FOLLOWERS", false));
        try {
            logger.info("===============================sleep 10s================================");
            Thread.sleep(20000);
            logger.info("===============================   over  ================================");
            userFansThreadPool.execute(new SinaFollowUserTask("", true));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initThreadPool() {
        detailListThreadPool = new ThreadPoolExecutor(10, 10, 0l, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1000), new ThreadPoolExecutor.DiscardPolicy());
        userFollowThreadPool = new ThreadPoolExecutor(10, 10, 0l, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1000), new ThreadPoolExecutor.DiscardPolicy());
        userFansThreadPool = new ThreadPoolExecutor(10, 10, 0l, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1000), new ThreadPoolExecutor.DiscardPolicy());
        new Thread(new ThreadPoolMonitor(detailListThreadPool, "列表页下载线程池")).start();
        new Thread(new ThreadPoolMonitor(userFollowThreadPool, "用户关注下载线程池")).start();
        new Thread(new ThreadPoolMonitor(userFansThreadPool, "用户粉丝下载线程池")).start();
    }


    public ThreadPoolExecutor getDetailListThreadPool() {
        return detailListThreadPool;
    }

    public ThreadPoolExecutor getUserFollowThreadPool() {
        return userFollowThreadPool;
    }

    public ThreadPoolExecutor getUserFansThreadPool() {
        return userFansThreadPool;
    }
}
