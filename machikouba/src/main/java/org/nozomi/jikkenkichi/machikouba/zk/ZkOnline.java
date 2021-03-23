package org.nozomi.jikkenkichi.machikouba.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.nozomi.jikkenkichi.machikouba.pojo.BizException;
import org.nozomi.jikkenkichi.machikouba.pojo.LocalConfig;
import org.nozomi.jikkenkichi.machikouba.util.DebugTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * simple service registration and discovery
 */
@Component
public class ZkOnline {
    @Value("${server.port}")
    private String PORT;
    private final static int FAIL_INTERVAL = 3000;
    private static String CURRENT_NODE = null;
    private static ConcurrentHashMap<String, LockObject> LOCK_MAP = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, ArrayList<String>> ADDRESS_MAP = new ConcurrentHashMap<>();

    @Autowired
    LocalConfig localConfig;
    @Autowired
    CuratorFramework zkClient;

    void onlineReport() throws Exception {
        if (CURRENT_NODE != null) {
            Stat stat = zkClient.checkExists().forPath(CURRENT_NODE);
            if (stat != null) {
                return;
            }
        }
        String appPath = String.format("%s/%s", ZkCommon.ZK_ONLINE_PREFIX, localConfig.getAppName());
        try {
            zkClient.create().withMode(CreateMode.PERSISTENT).forPath(appPath);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        String url = String.format("%s:%s", InetAddress.getLocalHost().getHostAddress(), PORT);
        //simple retry in demo
        int i = 0;
        while (i < 3) {
            try {
                CURRENT_NODE = zkClient.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(appPath + "/", url.getBytes());
                DebugTool.printHighlight(String.format("ZkOnline report suc, node [%s] address [%s]", CURRENT_NODE, url));
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(FAIL_INTERVAL);
            i++;
        }
//        System.exit(-1);
    }

    public void initWatch(String appName) {
        //fake CAS instead of lock
        LockObject lock = new LockObject();
        LOCK_MAP.putIfAbsent(appName, lock);
        if (LOCK_MAP.get(appName) != lock) {
            return;
        }

        PathChildrenCache watch = new PathChildrenCache(zkClient,
                String.format("%s/%s", ZkCommon.ZK_ONLINE_PREFIX, appName), true);
        watch.getListenable().addListener((curatorFramework, treeCacheEvent) -> {
            if (treeCacheEvent.getData() == null) {
                return;
            }
            synchronized (LOCK_MAP.get(appName)) {
                if (treeCacheEvent.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED
                        || treeCacheEvent.getType() == PathChildrenCacheEvent.Type.CHILD_UPDATED) {
                    String address = new String(treeCacheEvent.getData().getData());
                    if (ADDRESS_MAP.get(appName) == null) {
                        ADDRESS_MAP.put(appName, new ArrayList<>(Arrays.asList(address)));
                    } else {
                        ADDRESS_MAP.get(appName).add(address);
                    }
                    lock.idx = 0;
                    DebugTool.printHighlight(String.format("[%s] add server [%s]", appName, address));
                    return;
                }
                if (treeCacheEvent.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                    String address = new String(treeCacheEvent.getData().getData());
                    ArrayList<String> list = ADDRESS_MAP.get(appName);
                    if (list == null) {
                        return;
                    }
                    for (int i = 0; i < list.size(); i++) {
                        if (address.equals(list.get(i))) {
                            list.remove(i);
                            lock.idx = 0;
                            DebugTool.printHighlight(String.format("[%s] remove server [%s]", appName, address));
                            return;
                        }
                    }
                    return;
                }
            }
        });

        try {
            watch.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getServerAddress(String appName) {
        if (!LOCK_MAP.containsKey(appName)) {
            //shall call initWatch beforehand.only for special condition
            //if first call use this,it can't get result
            initWatch(appName);
        }
        LockObject lock = LOCK_MAP.get(appName);
        synchronized (lock) {
            ArrayList<String> arr = ADDRESS_MAP.get(appName);
            if (arr == null || arr.isEmpty()) {
                throw new BizException(appName + " can not get server address");
            }
            lock.idx = ++lock.idx % arr.size();
            return arr.get(lock.idx);
        }
    }

    class LockObject {
        int idx = 0;
    }
}
