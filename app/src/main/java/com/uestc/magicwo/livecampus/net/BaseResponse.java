package com.uestc.magicwo.livecampus.net;

import java.io.Serializable;

/**
 * 最外面一层泛型
 */
public class BaseResponse<T> implements Serializable {
    private static final long serialVersionUID = -8791367038185462865L;
    private int code;
    private T ret;
    private String uid;
    private String token;

    public String getUid() {
        return uid;
    }

    public String getToken() {
        return token;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getCode() {
        return code;
    }

    public T getRet() {
        return ret;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setRet(T ret) {
        this.ret = ret;
    }
}