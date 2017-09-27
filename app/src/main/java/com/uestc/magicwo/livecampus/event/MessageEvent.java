package com.uestc.magicwo.livecampus.event;

/**
 * Created by Magicwo on 2017/9/27.
 */

public class MessageEvent {

    private String message;

    public MessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
