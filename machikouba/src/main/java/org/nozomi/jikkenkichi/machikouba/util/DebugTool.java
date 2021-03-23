package org.nozomi.jikkenkichi.machikouba.util;

import org.nozomi.jikkenkichi.machikouba.pojo.BizException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DebugTool {
    public static void recordAndThrow(Throwable t) {
        record(t);
        throw new BizException(t);
    }

    public static void recordAndSkip(Throwable t) {
        record(t);
    }

    private static void record(Throwable t) {
        if (t instanceof BizException) {
            print(((BizException) t).getMsg());
            return;
        }
        print(t.getMessage());
        t.printStackTrace();
    }

    public static void printHighlight(String s) {
        System.out.print(String.format("\033[41m%s %s\033[m\r\n", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()), s));
    }

    public static void print(String s) {
        System.out.print(String.format("%s %s\r\n", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()), s));
    }

}
