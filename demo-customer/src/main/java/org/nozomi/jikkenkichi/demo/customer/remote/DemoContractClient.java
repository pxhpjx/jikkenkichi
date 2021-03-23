package org.nozomi.jikkenkichi.demo.customer.remote;

import org.nozomi.jikkenkichi.demo.contract.contracts.DemoContract;
import org.nozomi.jikkenkichi.demo.contract.pojo.ExampleRequest;
import org.nozomi.jikkenkichi.demo.contract.pojo.ExampleResponse;
import org.nozomi.jikkenkichi.machikouba.aop.AutoCatch;
import org.nozomi.jikkenkichi.seiran.SeiranClient;
import org.nozomi.jikkenkichi.seiran.SeiranRequest;

/**
 * Seiran client will auto do web request,DO NOT meed to write any code in method
 * actually, in this framework, the response returned from the remote service is always ResultInfo<T>,
 * but @SeiranRequest will pick ResultInfo.data for you
 */
@SeiranClient(value = "demo-provider", basePath = "/jkkt/demo")
public class DemoContractClient implements DemoContract {
    @Override
    @SeiranRequest("/time")
    public ExampleResponse reportCurrentTime() {
        return null;
    }

    @Override
    @SeiranRequest("/double")
    public ExampleResponse makeRequestDouble(ExampleRequest request) {
        return null;
    }

    //use this to test circuit break
    @AutoCatch
    @Override
    @SeiranRequest("/wrong-path")
    //@SeiranRequest("/dummy")
    public ExampleResponse dummy() {
        return null;
    }
}
