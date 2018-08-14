package com.igame.core.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igame.dto.RetVO;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

/**
 * @author xym
 */
public abstract class BaseEventHandler extends BaseServerEventHandler {


    protected void sendError(int errorCode, String cmdName, RetVO vo, User user) {

        vo.setState(1);
        vo.setErrCode(errorCode);

        send(cmdName,vo, user);
    }

    protected void sendSucceed(String cmdName, RetVO vo, User user) {

        vo.setState(0);

        send(cmdName,vo, user);
    }

    private void send(String cmdName, RetVO vo, User user) {
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        ISFSObject res = new SFSObject();

        try {
            json = mapper.writeValueAsString(vo);
        } catch (JsonProcessingException e) {
            this.getLogger().warn(cmdName+"  Vo2Json error", e);
        }

        res.putUtfString("infor", json);
        send(cmdName, res, user);
    }

}
