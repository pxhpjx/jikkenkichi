package org.nozomi.jikkenkichi.machikouba.util;

import java.util.*;

public class FileUtilsTest extends FileUtils {
    public static void main(String[] args) {
        HashSet<String> extensions = new HashSet<>();
        extensions.add("xml");

        List<String> fileNameFilter = new ArrayList<>();
        fileNameFilter.add("pom");

        List<String> fileContentFilter = new ArrayList<>();
        fileContentFilter.add("a");

        List<String> l = fileSeeking("G:\\Github\\jikkenkichi", extensions, fileNameFilter, fileContentFilter, "UTF-8");
        l.size();

        Map<String, String> replaceRule = new HashMap<>();
        replaceRule.put("nozomi", "のぞみ");
        replaceRule.put("のぞみ", "希");

        textReplace(l, "G:\\Github\\ww", replaceRule, "UTF-8", "Shift-JIS");
    }
}
