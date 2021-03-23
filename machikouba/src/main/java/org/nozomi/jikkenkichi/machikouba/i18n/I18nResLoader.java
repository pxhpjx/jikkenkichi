package org.nozomi.jikkenkichi.machikouba.i18n;

import com.alibaba.fastjson.JSON;
import org.nozomi.jikkenkichi.machikouba.pojo.I18nResJson;
import org.nozomi.jikkenkichi.machikouba.util.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * load local file resources
 */
public class I18nResLoader {
    private static Map<String, String> I18N_RES_MAP;

    static {
        I18N_RES_MAP = new HashMap<>();

        File root = new File(I18nResLoader.class.getResource("/").getPath());
        for (File resItem : root.listFiles()) {
            if (!resItem.getPath().endsWith("i18n")) {
                continue;
            }
            if (resItem.listFiles() == null) {
                break;
            }
            for (File lanDir : resItem.listFiles()) {
                String lang = lanDir.getName();
                if (lanDir.listFiles() == null) {
                    continue;
                }
                for (File i18nFile : lanDir.listFiles()) {
                    String json = FileUtils.readFileContent(i18nFile, "UTF-8");
                    I18nResJson i18nJson = JSON.parseObject(json, I18nResJson.class);
                    for (Map.Entry<String, String> entry : i18nJson.getResources().entrySet()) {
                        I18N_RES_MAP.put(formatI18nKey(lang, i18nJson.getEnumName(), entry.getKey()), entry.getValue());
                    }
                }
            }
            break;
        }
    }

    public static String getMessageRes(String lan, String enumName, String code) {
        String msg = loadFromDb(lan, enumName, code);
        if (msg != null) {
            return msg;
        }
        return I18N_RES_MAP.get(formatI18nKey(lan, enumName, code));
    }

    public static String getLocalMessageRes(String key) {
        return I18N_RES_MAP.get(key);
    }

    public static String formatI18nKey(String lan, String enumName, String code) {
        return String.format("%s:%s:%s", lan, enumName, code);
    }

    public static String loadFromDb(String lan, String enumName, String code) {
        return loadFromDb("currentAppName", lan, enumName, code);
    }

    //do nothing in demo
    private static String loadFromDb(String appName, String lan, String enumName, String code) {
        return null;
    }

}
