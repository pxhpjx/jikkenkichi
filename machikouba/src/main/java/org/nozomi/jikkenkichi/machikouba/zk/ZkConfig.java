package org.nozomi.jikkenkichi.machikouba.zk;

import com.alibaba.fastjson.JSON;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.nozomi.jikkenkichi.machikouba.pojo.LocalConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;

/**
 * simple distributed config center
 */
@Component
public class ZkConfig {
    private static ConcurrentHashMap<String, String> CONFIG = new ConcurrentHashMap<>();

    @Autowired
    LocalConfig localConfig;
    @Autowired
    CuratorFramework zkClient;
    @Autowired
    ZkOnline zkOnline;

    @Bean
    Object initZkConfig() throws Exception {
        PathChildrenCache watch = new PathChildrenCache(zkClient,
                String.format("%s/%s", ZkCommon.ZK_CONFIG_PREFIX, localConfig.getAppName()), true);
        watch.getListenable().addListener((curatorFramework, treeCacheEvent) -> {
            if (treeCacheEvent.getType() == PathChildrenCacheEvent.Type.CONNECTION_RECONNECTED) {
                zkOnline.onlineReport();
                return;
            }

            if (treeCacheEvent.getData() == null) {
                return;
            }
            int idx = treeCacheEvent.getData().getPath().lastIndexOf("/");
            String key = treeCacheEvent.getData().getPath().substring(idx + 1);
            if (treeCacheEvent.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED
                    || treeCacheEvent.getType() == PathChildrenCacheEvent.Type.CHILD_UPDATED) {
                byte[] v = treeCacheEvent.getData().getData();
                CONFIG.put(key, v == null ? "" : new String(v, Charset.forName(localConfig.getCharset())));
                return;
            }
            if (treeCacheEvent.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                CONFIG.remove(key);
                return;
            }
        });

        try {
            watch.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        zkOnline.onlineReport();
        return null;
    }

    /**
     * get config[string] by a key
     * or get all[json] if no key
     *
     * @param key
     * @return
     */
    public String getConfig(String key) {
        if (StringUtils.hasText(key)) {
            return CONFIG.get(key);
        }
        return JSON.toJSONString(CONFIG);
    }


}
