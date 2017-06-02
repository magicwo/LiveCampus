package com.uestc.magicwo.livecampus.net;

import java.io.Serializable;

/**

 */
public class SimpleUploadResponse implements Serializable {


    private static final long serialVersionUID = -181212749885315577L;
    public boolean isSuccess;

    public BaseUploadResponse toBaseUploadResponse() {
        BaseUploadResponse baseUploadResponse = new BaseUploadResponse();
        baseUploadResponse.setSuccess(isSuccess);
        return baseUploadResponse;
    }
}