package org.nozomi.jikkenkichi.seiran;

import org.nozomi.jikkenkichi.machikouba.util.DebugTool;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Use this to manage various information, status, etc.
 * and some hard-coded parameter values
 */
public class SeiranStatusManager {

    //shall not be changed after init
    //key is Method.toGenericString() for quick finding when invoke
    public static HashMap<String, InvokeInfo> INVOKE_INFO = new HashMap<>();

    //target servers that will be used in this project
    public static HashSet<String> SERVER_LIST = new HashSet<>();

    //key is Method.toGenericString(),short-circuiting by method
    //won't short-circuiting by ip or server in demo
    private static ConcurrentHashMap<String, CircuitBreakInfo> CIRCUIT_BREAK_MAP = new ConcurrentHashMap<>();


    public static boolean checkCircuitBreak(String method) {
        return !CIRCUIT_BREAK_MAP.containsKey(method) || CIRCUIT_BREAK_MAP.get(method).checkAvailable();
    }

    public static void failCounter(String method) {
        CircuitBreakInfo circuitBreakInfo = CIRCUIT_BREAK_MAP.get(method);
        if (circuitBreakInfo == null) {
            CIRCUIT_BREAK_MAP.putIfAbsent(method, new CircuitBreakInfo());
        }
        DebugTool.print(sub4TestLog(method) + " circuit break counter ++");
        if (CIRCUIT_BREAK_MAP.get(method).failCounter()) {
            DebugTool.printHighlight(sub4TestLog(method) + " circuit break!");
        }
    }

    static String sub4TestLog(String method) {
        method = method.substring(0, method.lastIndexOf('('));
        return method.substring(method.lastIndexOf('.') + 1);
    }
}
