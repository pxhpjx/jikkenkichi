package org.nozomi.jikkenkichi.machikouba.aop;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.nozomi.jikkenkichi.machikouba.util.DebugTool;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AspectFunc {

    /**
     * Automatic recording of inputs and outputs, only for testing
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("@annotation(AutoLog)")
    public Object autoLog(ProceedingJoinPoint pjp) throws Throwable {
        DebugTool.print(String.format("%s exec with param %s", pjp.toString(), JSON.toJSON(pjp.getArgs())));
        long start = System.currentTimeMillis();
        try {
            Object obj = pjp.proceed();
            DebugTool.print(String.format("%s finish in %sms with result %s", pjp.toString(), System.currentTimeMillis() - start, JSON.toJSON(obj)));
            return obj;
        } catch (Throwable t) {
            DebugTool.print(String.format("%s finish in %sms with exception %s", pjp.toString(), System.currentTimeMillis() - start, JSON.toJSON(t)));
            throw t;
        }
    }

    /**
     * Automatic error catching to avoid annoying logs, only for testing
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("@annotation(AutoCatch)")
    public Object autoCatch(ProceedingJoinPoint pjp) throws Throwable {
        try {
            return pjp.proceed();
        } catch (Throwable t) {
            DebugTool.recordAndSkip(t);
        }
        return null;
    }

}
