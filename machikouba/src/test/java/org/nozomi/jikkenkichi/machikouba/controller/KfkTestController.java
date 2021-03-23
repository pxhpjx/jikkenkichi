package org.nozomi.jikkenkichi.machikouba.controller;

import org.nozomi.jikkenkichi.machikouba.fliter.RestMappingController;
import org.nozomi.jikkenkichi.machikouba.kfk.KfkProducer;
import org.nozomi.jikkenkichi.machikouba.pojo.TestSimpleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RestMappingController(path = "/kfk", method = RequestMethod.POST)
public class KfkTestController {
    @Autowired
    KfkProducer kfkProducer;


    @RequestMapping(value = "/send")
    public Object getEnumLocaleRes(@RequestBody TestSimpleRequest req) {
        kfkProducer.send(req.getStr(), req.getNum(), null, req);
        return null;
    }

}
