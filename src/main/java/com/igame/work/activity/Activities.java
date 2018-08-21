package com.igame.work.activity;

import net.sf.json.JSONObject;

public interface Activities {
    int getType();
    /** 转成 MProtrol.ACTICITY 需要的格式 */
    JSONObject toClientData();
    String loadConfig();
}
