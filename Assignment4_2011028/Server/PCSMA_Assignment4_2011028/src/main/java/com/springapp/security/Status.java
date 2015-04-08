package com.springapp.security;

/**
 * Created by Apoorv Singh on 4/7/2015.
 */
public class Status {
    private Integer code;
    private String message;

    public Status(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
