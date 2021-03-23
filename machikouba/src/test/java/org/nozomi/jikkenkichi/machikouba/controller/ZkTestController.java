package org.nozomi.jikkenkichi.machikouba.controller;

import org.nozomi.jikkenkichi.machikouba.fliter.RestMappingController;
import org.nozomi.jikkenkichi.machikouba.util.DebugTool;
import org.nozomi.jikkenkichi.machikouba.zk.ZkConfig;
import org.nozomi.jikkenkichi.machikouba.zk.ZkLock;
import org.nozomi.jikkenkichi.machikouba.zk.ZkOnline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestMappingController(path = "/zk", method = RequestMethod.GET)
public class ZkTestController {

    @Autowired
    ZkConfig zkConfig;
    @Autowired
    ZkLock zkLock;
    @Autowired
    ZkOnline zkOnline;

    @RequestMapping(value = "/get-config")
    public String getConfig(@RequestParam("key") String key) {
      DebugTool.print(zkOnline.getServerAddress("app-test"));  ;
        return zkConfig.getConfig(key);
    }


    @RequestMapping(value = "/fair-lock")
    public String fairLockDemo(@RequestParam("key") String key) {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                zkLock.runWithFairReentrantLock(() -> exec(key, 0), key);
            }).start();
        }
        return "";
    }

    void exec(String key, int times) {
        DebugTool.print(String.format("start %s times %s", Thread.currentThread().getId(), times));
        try {
            Thread.sleep(555);
            if (times < 3) {
                zkLock.runWithFairReentrantLock(() -> exec(key, times + 1), key);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DebugTool.print(String.format("finish %s times %s", Thread.currentThread().getId(), times));
    }

}
