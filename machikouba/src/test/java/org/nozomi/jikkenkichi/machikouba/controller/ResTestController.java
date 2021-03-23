package org.nozomi.jikkenkichi.machikouba.controller;

import org.nozomi.jikkenkichi.machikouba.enums.ResultCode;
import org.nozomi.jikkenkichi.machikouba.fliter.RestMappingController;
import org.nozomi.jikkenkichi.machikouba.pojo.TestSimpleRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestMappingController(path = "/res", method = RequestMethod.POST)
public class ResTestController {

    @RequestMapping(value = "/i18n-enum")
    public Map getEnumLocaleRes(@RequestBody TestSimpleRequest req) {
        ResultCode rc = ResultCode.valueOf(req.getStr());
        Map result = new HashMap<>();
        result.put("Code", rc.getCode());
        result.put("LocaleRes", rc.getLocaleRes());
        return result;
    }
}
