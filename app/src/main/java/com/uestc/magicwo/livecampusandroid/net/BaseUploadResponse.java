package com.uestc.magicwo.livecampusandroid.net;

import java.io.Serializable;

/**
 * 最外面一层泛型
 */
public class BaseUploadResponse<T> implements Serializable {
    private static final long serialVersionUID = 660687785815809534L;
    private T param;
    private boolean isSuccess;

    public T getParam() {
        return param;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}