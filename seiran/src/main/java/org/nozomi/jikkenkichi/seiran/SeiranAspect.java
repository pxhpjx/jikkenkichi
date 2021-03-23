package org.nozomi.jikkenkichi.seiran;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.nozomi.jikkenkichi.machikouba.pojo.BizException;
import org.nozomi.jikkenkichi.machikouba.pojo.ResultInfo;
import org.nozomi.jikkenkichi.machikouba.util.DebugTool;
import org.nozomi.jikkenkichi.machikouba.zk.ZkOnline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The actual work content of @SeiranRequest
 */
@Aspect
@Component
public class SeiranAspect {
    @Autowired
    ZkOnline zkOnline;
    @Autowired
    SeiranHttpClientPool seiranHttpClientPool;

    /**
     * Actual running content
     * The original content of the method is blocked,
     * and the network request is made directly according to the registered @SeiranRequest information
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("@annotation(SeiranRequest)")
    public Object seiranRequestInvoke(ProceedingJoinPoint pjp) throws Throwable {
        String method = pjp.getSignature().toLongString();
        InvokeInfo invokeInfo = SeiranStatusManager.INVOKE_INFO.get(method);

        if (!SeiranStatusManager.checkCircuitBreak(method)) {
            DebugTool.recordAndThrow(new BizException(invokeInfo.getPath() + " is under circuit break"));
        }

        String requestBody;
        if (pjp.getArgs() == null || pjp.getArgs().length == 0) {
            requestBody = null;
        } else {
            requestBody = JSON.toJSONString(pjp.getArgs()[0]);
        }

        String resp = requestRemote(requestBody, invokeInfo, method);
        return parse(invokeInfo, resp);
    }


    String requestRemote(String requestBody, InvokeInfo iv, String method) {
        String url = String.format("http://%s%s", zkOnline.getServerAddress(iv.getServer()), iv.getPath());
        DebugTool.print(String.format("%s exec with param %s", url, requestBody));
        long start = System.currentTimeMillis();
        try {
            String resp = seiranHttpClientPool.postRequest(url, requestBody);
            DebugTool.print(String.format("%s finish in %sms with result %s", url, System.currentTimeMillis() - start, resp));
            return resp;
        } catch (Exception ex) {
            SeiranStatusManager.failCounter(method);
            DebugTool.print(String.format("%s finish in %sms with exception %s", url, System.currentTimeMillis() - start, ex.getMessage()));
            throw ex;
        }
    }

    Object parse(InvokeInfo iv, String resp) {
        JSONObject jsonObj = JSON.parseObject(resp);
        if (jsonObj.getObject("code", Integer.class) != ResultInfo.DEFAULT_SUC_CODE) {
            throw new BizException(jsonObj.getObject("msg", String.class), jsonObj.getObject("code", Integer.class));
        }
        return jsonObj.getObject("data", iv.getReturnType());
    }


}
