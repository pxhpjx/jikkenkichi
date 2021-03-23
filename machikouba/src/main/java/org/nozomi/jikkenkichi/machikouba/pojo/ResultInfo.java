package org.nozomi.jikkenkichi.machikouba.pojo;

import com.alibaba.fastjson.JSON;

public class ResultInfo<T> {
    public static int DEFAULT_SUC_CODE = 0;

    private Integer code;
    private String msg;
    private T data;

    public ResultInfo(Integer c, String m, T d) {
        code = c;
        msg = m;
        data = d;
    }

    public ResultInfo(T d) {
        code = DEFAULT_SUC_CODE;
        data = d;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
