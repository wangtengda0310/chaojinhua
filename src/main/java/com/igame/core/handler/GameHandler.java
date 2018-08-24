package com.igame.core.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

public interface GameHandler {
    default RetVO error(int errorCode) {
        RetVO vo = new RetVO();
        vo.setState(1);
        vo.setErrCode(errorCode);
        return vo;
    }

    void warn(String warn,Exception e);
    void sendClient(User user, String cmdName, ISFSObject params);
    default ISFSObject sendClient(String cmdName, RetVO vo, User user) {
        String json = null;

        try {
            json = json(vo);
        } catch (JsonProcessingException e) {
            warn(cmdName+"  Vo2Json error", e);
        }

        ISFSObject res = new SFSObject();
        res.putUtfString("infor", json);
        sendClient(user, cmdName, res);

        return res;
    }

    default EventListener eventListener() {
        return null;
    }
    interface EventListener {
        void handleEvent(Object event);
    }

    /** 写成全局的单例 稍微提高点性能 */
    ObjectMapper mapper = new ObjectMapper();
    /** 把对象转成json */
    default String json(Object vo) throws JsonProcessingException {
        return mapper.writeValueAsString(vo);
    }
}
