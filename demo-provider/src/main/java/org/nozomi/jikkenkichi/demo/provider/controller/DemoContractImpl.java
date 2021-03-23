package org.nozomi.jikkenkichi.demo.provider.controller;

import org.nozomi.jikkenkichi.demo.contract.contracts.DemoContract;
import org.nozomi.jikkenkichi.demo.contract.pojo.ExampleRequest;
import org.nozomi.jikkenkichi.demo.contract.pojo.ExampleResponse;
import org.nozomi.jikkenkichi.machikouba.fliter.RestMappingController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

//simple implements only, nothing that needs attention
@RestMappingController(path = "/demo", method = RequestMethod.POST)
public class DemoContractImpl implements DemoContract {
    @Override
    @RequestMapping("/time")
    public ExampleResponse reportCurrentTime() {
        ExampleResponse resp = new ExampleResponse();
        resp.setNum(System.currentTimeMillis());
        resp.setStr(new Date().toString());
        return resp;
    }

    @Override
    @RequestMapping("/double")
    public ExampleResponse makeRequestDouble(@RequestBody ExampleRequest request) {
        ExampleResponse resp = new ExampleResponse();
        resp.setStr(request.getStr() + request.getStr());
        resp.setNum(request.getNum() + request.getNum());
        return resp;
    }

    @Override
    @RequestMapping("/dummy")
    public ExampleResponse dummy() {
        return null;
    }
}
