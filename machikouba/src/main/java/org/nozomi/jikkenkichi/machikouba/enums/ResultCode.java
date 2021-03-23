package org.nozomi.jikkenkichi.machikouba.enums;


import org.nozomi.jikkenkichi.machikouba.fliter.ProcessFilter;
import org.nozomi.jikkenkichi.machikouba.i18n.I18nResLoader;
import org.nozomi.jikkenkichi.machikouba.i18n.I18nResInterface;

public enum ResultCode implements I18nResInterface {
    RESULT1("CODE1", "M1"),
    RESULT2("CODE2", "M2"),
    RESULT3("CODE3", "M3"),
    RESULT4("CODE4", "M4"),


    ;

    ResultCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private String code;
    private String msg;

    @Override
    public String getLocaleRes() {
        String m = I18nResLoader.getMessageRes(ProcessFilter.getRequestInfo().getLocale(), this.getClass().getSimpleName(), code);
        return m != null ? m : msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
