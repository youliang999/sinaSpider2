package com.youliang.sina.monitor;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.util.concurrent.ThreadPoolExecutor;


public class ThreadPoolMonitor implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ThreadPoolExecutor.class);
    private ThreadPoolExecutor executor;
    private String name;

    public ThreadPoolMonitor(ThreadPoolExecutor threadPoolExecutor, String name) {
        this.executor = threadPoolExecutor;
        this.name = name;
    }


    @Override
    public void run() {
        while (true) {
            log.info("[{}]-[monitor]: [{}/{}] Active: {}, Completed: {}, queueSize: {}, Task: {}, isShutdown: {}, isTerminated: {}",
                    this.name,
                    this.executor.getPoolSize(),
                    this.executor.getCorePoolSize(),
                    this.executor.getActiveCount(),
                    this.executor.getCompletedTaskCount(),
                    this.executor.getQueue().size(),
                    this.executor.getTaskCount(),
                    this.executor.isShutdown(),
                    this.executor.isTerminated());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
