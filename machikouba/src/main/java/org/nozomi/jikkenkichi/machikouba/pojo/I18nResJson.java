package org.nozomi.jikkenkichi.machikouba.pojo;

import java.util.Map;

public class I18nResJson {
    private String enumName;
    private Map<String, String> resources;

    public String getEnumName() {
        return enumName;
    }

    public void setEnumName(String enumName) {
        this.enumName = enumName;
    }

    public Map<String, String> getResources() {
        return resources;
    }

    public void setResources(Map<String, String> resources) {
        this.resources = resources;
    }
}
