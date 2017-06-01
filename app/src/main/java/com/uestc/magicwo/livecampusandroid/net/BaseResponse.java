package com.uestc.magicwo.livecampusandroid.net;

import java.io.Serializable;

/**
 * 最外面一层泛型
 */
public class BaseResponse<T> implements Serializable {
    private static final long serialVersionUID = -8791367038185462865L;
    private String error;
    private T value;
    private boolean success;


    public String getError() {
        return error;
    }


    public boolean isSuccess() {
        return success;
    }

    public T getValue() {
        return value;
    }

    public void setError(String error) {
        this.error = error;
    }


    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setValue(T value) {
        this.value = value;
    }
}