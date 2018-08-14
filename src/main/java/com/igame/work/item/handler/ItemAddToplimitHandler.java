package com.igame.work.item.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * @author xym
 *
 *  增加背包上限
 *
 */
public class ItemAddToplimitHandler extends BaseHandler {

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

        //校验钻石
        if (player.getDiamond() < 50){
            sendError(ErrorCode.DIAMOND_NOT_ENOUGH,MProtrol.toStringProtrol(MProtrol.ITEM_ADD_TOPLIMIT),vo,user);
            return;
        }

        //增加背包上限
        player.addBagSpace(5);

        //扣除钻石
        ResourceService.ins().addDiamond(player,-50);

        vo.addData("bagSpace",player.getBagSpace());
        sendSucceed(MProtrol.toStringProtrol(MProtrol.ITEM_ADD_TOPLIMIT),vo,user);
    }
}
