package com.igame.work.chat.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.chat.dto.PlayerInfo;
import com.igame.work.user.dto.Player;
import com.igame.work.user.service.PlayerCacheService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * @author xym
 *
 * 禁止陌生人私聊
 */
public class GetPlayerInfoHandler extends ReconnectedHandler {

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        long playerId = jsonObject.getLong("playerId");
        vo.addData("playerId", playerId);

        Player playerSession = SessionManager.ins().getSessionByPlayerId(playerId);
        Player playerCache = PlayerCacheService.ins().getPlayerById(playerId);
        if (playerSession == null && playerCache == null){
            return error(ErrorCode.ERROR);
        }

        if (playerSession != null){
            vo.addData("playerInfo",new PlayerInfo(playerSession));
        }else {
            vo.addData("playerInfo",new PlayerInfo(playerCache));
        }

        return vo;
    }

    @Override
    protected int protocolId() {
        return MProtrol.MESSAGE_PLAYERINFO;
    }

}
