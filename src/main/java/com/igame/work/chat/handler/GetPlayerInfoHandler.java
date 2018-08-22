package com.igame.work.chat.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.chat.dto.PlayerInfo;
import com.igame.work.user.dto.Player;
import com.igame.work.user.service.PlayerCacheService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * @author xym
 *
 * 禁止陌生人私聊
 */
public class GetPlayerInfoHandler extends BaseHandler{

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

        long playerId = jsonObject.getLong("playerId");
        vo.addData("playerId", playerId);

        Player playerSession = SessionManager.ins().getSessionByPlayerId(playerId);
        Player playerCache = PlayerCacheService.ins().getPlayerById(playerId);
        if (playerSession == null && playerCache == null){
            sendError(ErrorCode.ERROR,MProtrol.toStringProtrol(MProtrol.MESSAGE_PLAYERINFO),vo,user);
            return;
        }

        if (playerSession != null){
            vo.addData("playerInfo",new PlayerInfo(playerSession));
        }else {
            vo.addData("playerInfo",new PlayerInfo(playerCache));
        }

        sendSucceed(MProtrol.toStringProtrol(MProtrol.MESSAGE_PLAYERINFO),vo,user);
    }
}
