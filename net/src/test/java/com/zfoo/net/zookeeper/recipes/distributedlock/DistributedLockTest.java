package com.zfoo.net.zookeeper.recipes.distributedlock;

import com.zfoo.net.zookeeper.ZookeeperConstantTest;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Ignore;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * 使用Curator实现分布式锁功能
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2018-08-03 15:47
 */
@Ignore
public class DistributedLockTest {

    static String lock_path = "/node";
    static CuratorFramework curator = CuratorFrameworkFactory
            .builder()
            .connectString(ZookeeperConstantTest.URL)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

    @Test
    public void test() {
        curator.start();
        final InterProcessMutex lock = new InterProcessMutex(curator, lock_path);
        final CountDownLatch down = new CountDownLatch(1);
        for (int i = 0; i < 30; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        down.await();
                        lock.acquire();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
                    String orderNo = sdf.format(new Date());
                    System.out.println("生成的订单号是 : " + orderNo);

                    try {
                        lock.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        down.countDown();
    }

}
