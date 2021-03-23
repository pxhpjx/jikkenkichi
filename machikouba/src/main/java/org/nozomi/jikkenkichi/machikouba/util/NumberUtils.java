package org.nozomi.jikkenkichi.machikouba.util;

public class NumberUtils {

    /**
     * operate a string as num,and keep it's length
     * @param num
     * @param l
     * @return
     */
    public static String add(String num, long l) {
        String str = String.valueOf(Long.valueOf(num) + l);
        while (str.length() < num.length()) {
            str = "0" + str;
        }
        return str;
    }
}
