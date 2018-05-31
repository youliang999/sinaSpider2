package com.youliang.sina.monitor;

import com.youliang.sina.bean.SpiderQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueMonitor implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(QueueMonitor.class);
    private final String queueName;
    private final ConcurrentLinkedQueue<SpiderQueue> spiderQueues;

    public QueueMonitor(String queueName, ConcurrentLinkedQueue<SpiderQueue> spiderQueues) {
        this.queueName = queueName;
        this.spiderQueues = spiderQueues;
    }

    @Override
    public void run() {
        while (true) {
            log.info("[{}]Monitor: size:{}", queueName, spiderQueues.size());
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
