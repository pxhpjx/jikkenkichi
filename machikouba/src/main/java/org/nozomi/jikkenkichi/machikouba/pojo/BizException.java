package org.nozomi.jikkenkichi.machikouba.pojo;

public class BizException extends RuntimeException {
    public BizException() {
    }

    public BizException(String msg) {
        this.msg = msg;
    }

    public BizException(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }

    public BizException(Throwable t) {
        super(t);
    }


    private String msg;
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
