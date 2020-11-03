package com.shangqiao56.tms.rms.common;

import com.shangqiao56.ErrorCode;

public enum RmsRouteErrorCode  implements ErrorCode {

    UNDEFINED(50000,"未定义错误"),
    NOTFOUND_STATION(50001,"网点不存在"),
    WAITING_ROUTE_LOADING(50002,"等待加载时间过长"),
    ;

    private int code;
    private String message;

    RmsRouteErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
