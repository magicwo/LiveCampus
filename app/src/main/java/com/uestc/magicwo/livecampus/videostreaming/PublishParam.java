package com.uestc.magicwo.livecampus.videostreaming;

import java.io.Serializable;

/**
 * Created by Magicwo on 2017/6/13.
 */

public class PublishParam implements Serializable {
    public String pushUrl = null;
    public long bitrate = 0;
    public long fps = 0;
    public String definition = "SD"; // HD: 高清   SD: 标清    LD: 流畅
    public boolean zoomed = false;
    public boolean useFilter = false;
    public boolean faceBeauty = false;
    public boolean graffitiOn = false;
    public boolean qosEnable = true;
    public boolean flashEnable = false;
    public boolean watermark = false;
}