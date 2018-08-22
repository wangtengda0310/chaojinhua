package com.igame.work.chat.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * @author xym
 *
 * 禁止陌生人私聊
 */
public class BanStrangersHandler extends BaseHandler{

    @Override
    public void handleClientRequest(User user, ISFSObject params) {

		RetVO vo = new RetVO();
		if(reviceMessage(user,params,vo)){
			return;
		}

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
        if(player == null){
            this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
            return;
        }

        int state = jsonObject.getInt("state");
        vo.addData("state", state);

        //校验参数
        if (state != 0 && state != 1){
            sendError(ErrorCode.PARAMS_INVALID,MProtrol.toStringProtrol(MProtrol.MESSAGE_BAN_STRANGERS),vo,user);
            return;
        }

        //校验状态
        int isBanStrangers = player.getIsBanStrangers();
        if (state == isBanStrangers){
            sendError(ErrorCode.ERROR,MProtrol.toStringProtrol(MProtrol.MESSAGE_BAN_STRANGERS),vo,user);
            return;
        }

        //更新
        player.setIsBanStrangers(state);

        sendSucceed(MProtrol.toStringProtrol(MProtrol.MESSAGE_BAN_STRANGERS),vo,user);
    }
}
