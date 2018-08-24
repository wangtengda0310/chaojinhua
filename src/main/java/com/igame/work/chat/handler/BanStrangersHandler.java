package com.igame.work.chat.handler;

import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * @author xym
 *
 * 禁止陌生人私聊
 */
public class BanStrangersHandler extends ReconnectedHandler {

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        int state = jsonObject.getInt("state");
        vo.addData("state", state);

        //校验参数
        if (state != 0 && state != 1){
            return error(ErrorCode.PARAMS_INVALID);
        }

        //校验状态
        int isBanStrangers = player.getIsBanStrangers();
        if (state == isBanStrangers){
            return error(ErrorCode.ERROR);
        }

        //更新
        player.setIsBanStrangers(state);

        return vo;
    }

    @Override
    protected int protocolId() {
        return MProtrol.MESSAGE_BAN_STRANGERS;
    }

}
