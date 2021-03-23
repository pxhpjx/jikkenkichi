package org.nozomi.jikkenkichi.machikouba.zk;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.nozomi.jikkenkichi.machikouba.pojo.LocalConfig;
import org.nozomi.jikkenkichi.machikouba.util.DebugTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * do some init work here
 */
@Configuration
public class ZkCommon {
    static final int BASE_SLEEP_TIME = 1000;
    static final int MAX_RETRIES = 3;

    static final String ZK_ALL_PREFIX = "/nozomi";
    static final String ZK_LOCK_PREFIX = ZK_ALL_PREFIX + "/lock";
    static final String ZK_ONLINE_PREFIX = ZK_ALL_PREFIX + "/online";
    static final String ZK_CONFIG_PREFIX = ZK_ALL_PREFIX + "/config";

    @Autowired
    LocalConfig localConfig;

    @Bean
    CuratorFramework initZkClient() {
        // Retry strategy. Retry N times, and will increase the sleep time between retries
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        CuratorFramework zkClient = null;
        try {
            zkClient = CuratorFrameworkFactory.builder()
                    //can be a list split by ,
                    .connectString(localConfig.getZkAddress())
                    .retryPolicy(retryPolicy)
                    .build();
            zkClient.start();
            return zkClient;
        } finally {
            doSomeWork4Test(zkClient);
        }
    }

    /**
     * create node if not exist
     *
     * @param path
     * @return
     */
    public static boolean checkPath(CuratorFramework zkClient, String path) {
        try {
            Stat stat = zkClient.checkExists().forPath(path);
            if (stat == null) {
                zkClient.create().forPath(path);
                return true;
            }
        } catch (Exception e) {
            DebugTool.recordAndSkip(e);
        }
        return false;
    }


    private void doSomeWork4Test(CuratorFramework client) {
        checkPath(client, ZK_ALL_PREFIX);
        checkPath(client, ZK_LOCK_PREFIX);
        checkPath(client, ZK_ONLINE_PREFIX);
        checkPath(client, ZK_CONFIG_PREFIX);
    }

}
