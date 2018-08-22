package com.igame.work.turntable.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.turntable.service.TurntableService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * @author xym
 *
 * 刷新幸运大转盘
 */
public class TurntableReloadHandler extends BaseHandler{

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
            sendError(ErrorCode.LEVEL_NOT,MProtrol.toStringProtrol(MProtrol.LUCKTABLE_RELOAD),vo,user);
            return;
        }

        //校验钻石
        if (player.getDiamond() < 20){
            sendError(ErrorCode.DIAMOND_NOT_ENOUGH,MProtrol.toStringProtrol(MProtrol.LUCKTABLE_RELOAD),vo,user);
            return;
        }

        //扣除钻石
        ResourceService.ins().addDiamond(player,-20);

        //刷新大转盘
        TurntableService.ins().reloadTurntable(player);

        vo.addData("turntable",player.transTurntableVo());
        sendSucceed(MProtrol.toStringProtrol(MProtrol.LUCKTABLE_RELOAD),vo,user);
    }
}
