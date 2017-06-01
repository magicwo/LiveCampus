package com.uestc.magicwo.livecampusandroid.net;

import java.io.Serializable;

/**

 */
public class SimpleResponse implements Serializable {

    private static final long serialVersionUID = 1930182908642706965L;
    public String error;
    public boolean success;

    public BaseResponse toBaseResponse() {
        BaseResponse baseResponse = new BaseResponse();
//        baseResponse.error = error;
        baseResponse.setError(error);
//        BaseResponse.success = success;
        baseResponse.setSuccess(success);
        return baseResponse;
    }
}