package com.uestc.magicwo.livecampus.net;

import java.io.Serializable;

/**

 */
public class SimpleResponse implements Serializable {

    private static final long serialVersionUID = 1930182908642706965L;
    public int code;

    public BaseResponse toBaseResponse() {
        BaseResponse baseResponse = new BaseResponse();
//        baseResponse.error = error;
        baseResponse.setCode(code);
//        BaseResponse.success = success;
        return baseResponse;
    }
}