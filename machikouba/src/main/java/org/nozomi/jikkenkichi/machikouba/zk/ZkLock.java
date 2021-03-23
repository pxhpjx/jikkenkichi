package org.nozomi.jikkenkichi.machikouba.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.zookeeper.CreateMode;
import org.nozomi.jikkenkichi.machikouba.pojo.BizException;
import org.nozomi.jikkenkichi.machikouba.pojo.LocalConfig;
import org.nozomi.jikkenkichi.machikouba.util.DebugTool;
import org.nozomi.jikkenkichi.machikouba.util.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * simple distributed lock
 */
@Component
public class ZkLock {
    private static ConcurrentHashMap<String, Long> REENTRANT_RECORD = new ConcurrentHashMap<>();

    @Autowired
    LocalConfig localConfig;
    @Autowired
    CuratorFramework zkClient;

    /**
     * try to run with a fair ReentrantLock
     * no operation for exception but release lock
     *
     * @param r
     * @param key
     */
    public void runWithFairReentrantLock(Runnable r, String key) {
        Long currentThreadId = Thread.currentThread().getId();
        boolean hold = currentThreadId.equals(REENTRANT_RECORD.get(key));
        //check holding only,no counter
        if (hold) {
            r.run();
            return;
        }
        String node = lock(key);
        if (node == null) {
            throw new BizException(String.format("Key:[%s] get zk lock failed", key));
        }
        REENTRANT_RECORD.put(key, currentThreadId);
        try {
            r.run();
        } finally {
            //no special operation if release fail in demo
            release(node);
            REENTRANT_RECORD.remove(key);
        }
    }

    /**
     * create a EPHEMERAL_SEQUENTIAL node, wait for a lock
     *
     * @param lockKey
     * @return
     */
    String lock(String lockKey) {
        try {
            String basePath = String.format("%s/%s", ZkCommon.ZK_LOCK_PREFIX, lockKey);
            ZkCommon.checkPath(zkClient, basePath);
            String node = zkClient.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(basePath + "/");
            DebugTool.print(String.format("Thread id %s create node %s", Thread.currentThread().getId(), node));

            CountDownLatch cd = new CountDownLatch(1);
            watch(basePath, node, cd);
            cd.await();
            return node;
        } catch (Exception e) {
            DebugTool.recordAndThrow(e);
        }
        return null;
    }

    void release(String node) {
        try {
            zkClient.delete().forPath(node);
            DebugTool.print(String.format("Thread id %s remove node %s", Thread.currentThread().getId(), node));
        } catch (Exception e) {
            DebugTool.recordAndSkip(e);
        }
    }

    /**
     * wait for lock
     * only watch the previous node to avoid useless notice
     *
     * @param basePath
     * @param node
     * @param cd
     */
    void watch(String basePath, String node, CountDownLatch cd) {
        String preNode = basePath + "/" + NumberUtils.add(node.substring(node.lastIndexOf("/") + 1), -1);
        NodeCache watch = new NodeCache(zkClient, preNode);
        NodeCacheListener l = () -> {
            if (checkLast(basePath, node)) {
                cd.countDown();
                watch.close();
            }
        };
        watch.getListenable().addListener(l);

        try {
            watch.start();
            //quick force check
            l.nodeChanged();
        } catch (Exception e) {
            DebugTool.recordAndThrow(e);
        }
    }

    boolean checkLast(String basePath, String node) throws Exception {
        return node.equals(basePath + "/" + Collections.min(zkClient.getChildren().forPath(basePath)));
    }

}
