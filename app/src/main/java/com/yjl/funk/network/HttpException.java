package com.yjl.funk.network;

/**
 * 自定义错误信息，统一处理返回处理
 * Created by WZG on 2016/7/16.
 */
public class HttpException extends RuntimeException {

    public static final int NO_DATA = 0x2;
    private String msg;
    private int resultCode;

    public HttpException(int resultCode) {
        this(getApiExceptionMessage(resultCode));
    }
    public HttpException(String detailMessage) {
        super(detailMessage);
    }
    public HttpException(Throwable throwable){super(throwable);}
    public HttpException(int resultCode, String msg){
        super(msg);
        this.msg = msg;
        this.resultCode = resultCode;
    }
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }
    /**
     * 转换错误数据
     *
     * @param code
     * @return
     */
    private static String getApiExceptionMessage(int code) {
        String message = "";
        switch (code) {
            case NO_DATA:
                message = "无数据";
                break;
            default:
                message = "error";
                break;

        }
        return message;
    }
}

