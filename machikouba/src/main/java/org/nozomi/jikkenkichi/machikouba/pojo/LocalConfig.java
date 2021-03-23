package org.nozomi.jikkenkichi.machikouba.pojo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "local.config")
public class LocalConfig {
    private String appName;
    private String charset;
    private String pathPrefix;

    private String zkAddress;

    @Value("${spring.kafka.bootstrap-servers}")
    private String kfkAddress;

    private String kfkGroupId;

    private String seiranClientPath;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getPathPrefix() {
        return pathPrefix;
    }

    public void setPathPrefix(String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

    public String getZkAddress() {
        return zkAddress;
    }

    public void setZkAddress(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    public String getKfkAddress() {
        return kfkAddress;
    }

    public void setKfkAddress(String kfkAddress) {
        this.kfkAddress = kfkAddress;
    }

    public String getKfkGroupId() {
        return kfkGroupId;
    }

    public void setKfkGroupId(String kfkGroupId) {
        this.kfkGroupId = kfkGroupId;
    }

    public String getSeiranClientPath() {
        return seiranClientPath;
    }

    public void setSeiranClientPath(String seiranClientPath) {
        this.seiranClientPath = seiranClientPath;
    }
}
