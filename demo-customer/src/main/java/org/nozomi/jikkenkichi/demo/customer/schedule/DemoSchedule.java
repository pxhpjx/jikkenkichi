package org.nozomi.jikkenkichi.demo.customer.schedule;

import com.alibaba.fastjson.JSON;
import org.nozomi.jikkenkichi.demo.contract.pojo.ExampleRequest;
import org.nozomi.jikkenkichi.demo.contract.pojo.ExampleResponse;
import org.nozomi.jikkenkichi.demo.customer.remote.DemoContractClient;
import org.nozomi.jikkenkichi.machikouba.util.DebugTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

//test entrance
@Component
public class DemoSchedule {

    @Autowired
    DemoContractClient demoContractClient;

    @Scheduled(fixedDelay = 3000)
    void testDiscovery() {
        ExampleRequest req = new ExampleRequest();
        req.setStr(this.getClass().toString());
        req.setNum(new Random(10000).nextLong());

        DebugTool.print("invoke reportCurrentTime");
        ExampleResponse resp = demoContractClient.reportCurrentTime();
        DebugTool.print("invoke reportCurrentTime with result " + JSON.toJSONString(resp));

        DebugTool.print("invoke makeRequestDouble with param " + JSON.toJSONString(req));
        resp = demoContractClient.makeRequestDouble(req);
        DebugTool.print("invoke makeRequestDouble with result " + JSON.toJSONString(resp));
    }

    @Scheduled(fixedDelay = 1000)
    void testCircuitBreak() {
        //demoContractClient.reportCurrentTime();
        demoContractClient.dummy();
    }


}
