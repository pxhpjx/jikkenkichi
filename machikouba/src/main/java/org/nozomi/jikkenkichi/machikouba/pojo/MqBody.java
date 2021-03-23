package org.nozomi.jikkenkichi.machikouba.pojo;

import com.alibaba.fastjson.JSON;

import java.util.UUID;

public class MqBody<T> {
    private String uuid;
    private T data;

    public MqBody() {
    }

    public MqBody(T data) {
        uuid = UUID.randomUUID().toString();
        this.data = data;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
