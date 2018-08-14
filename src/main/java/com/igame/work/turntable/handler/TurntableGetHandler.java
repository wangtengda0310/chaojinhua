package com.igame.work.turntable.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * @author xym
 *
 * 获取玩家幸运大转盘
 */
public class TurntableGetHandler extends BaseHandler{

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

        //校验等级
        if (player.getPlayerLevel() < 15 || player.getTurntable() == null){
            sendError(ErrorCode.LEVEL_NOT,MProtrol.toStringProtrol(MProtrol.LUCKTABLE_GET),vo,user);
            return;
        }

        vo.addData("turntable",player.transTurntableVo());
        sendSucceed(MProtrol.toStringProtrol(MProtrol.LUCKTABLE_GET),vo,user);
    }
}
