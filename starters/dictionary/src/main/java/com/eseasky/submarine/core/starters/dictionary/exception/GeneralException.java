package com.eseasky.submarine.core.starters.dictionary.exception;

public class GeneralException extends Exception {
    private static final long serialVersionUID = 1L;
    private String code;
    private String msg;

    public GeneralException(String code, String msg) {
        super(msg);
        this.setCode(code);
        this.setMsg(msg);
    }

    public GeneralException(String[] msg) {
        super(msg[1]);
        this.setCode(msg[0]);
        this.setMsg(msg[1]);
    }

    public GeneralException(String[] msg, String extInfo) {
        super(msg[1] + ", 详情：" + extInfo);
        this.setCode(msg[0]);
        this.setMsg(msg[1] + ", 详情：" + extInfo);
    }

    public String[] getExceptionInfo() {
        return new String[]{this.getCode(), this.getMsg()};
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return this.msg;
    }
}

