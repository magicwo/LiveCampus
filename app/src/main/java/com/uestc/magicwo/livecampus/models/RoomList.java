package com.uestc.magicwo.livecampus.models;

import java.util.List;

/**
 * Created by Magicwo on 2017/6/22.
 * 房间列表
 */

public class RoomList {
    private List<RoomBaseInfoResponse> list;

    public List<RoomBaseInfoResponse> getList() {
        return list;
    }

    public void setList(List<RoomBaseInfoResponse> list) {
        this.list = list;
    }
}
