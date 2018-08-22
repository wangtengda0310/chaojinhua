package com.igame.work.turntable.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.gm.service.GMService;
import com.igame.work.item.dto.Item;
import com.igame.work.turntable.service.TurntableService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * @author xym
 *
 * 幸运大转盘十连抽
 */
public class TenLotteryHandler extends BaseHandler{

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
            sendError(ErrorCode.LEVEL_NOT,MProtrol.toStringProtrol(MProtrol.LUCKTABLE_LOTTERY_TEN),vo,user);
            return;
        }

        //校验道具
        Item item = player.getItems().get(200072);
        if (item == null || item.getUsableCount(-1) < 8){
            sendError(ErrorCode.ITEM_NOT_ENOUGH,MProtrol.toStringProtrol(MProtrol.LUCKTABLE_LOTTERY_TEN),vo,user);
            return;
        }

        //扣除道具
        ResourceService.ins().addItem(player,200072,-8,true);

        //抽取奖励
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {

            int site = TurntableService.ins().lottery(player);

            //发放奖励
            String reward = player.getTurntable().getRewards().get(site);
            GMService.processGM(player,reward);

            sb.append(reward).append(";");
        }

        if(sb.length() > 0){
            sb.deleteCharAt(sb.length()-1);
        }

        vo.addData("reward",sb);
        sendSucceed(MProtrol.toStringProtrol(MProtrol.LUCKTABLE_LOTTERY_TEN),vo,user);
    }
}
