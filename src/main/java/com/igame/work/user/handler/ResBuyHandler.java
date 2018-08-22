package com.igame.work.user.handler;






import com.igame.work.monster.MonsterDataManager;
import net.sf.json.JSONObject;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.work.monster.data.ExchangedataTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.core.log.GoldLog;
import com.igame.dto.RetVO;
import com.igame.work.quest.service.QuestService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class ResBuyHandler extends BaseHandler{
	

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
		int extype = jsonObject.getInt("extype");
		int buyCount = 0;		
		if(extype == 1){
			buyCount = player.getPhBuyCount();
		}else if(extype == 2){
			buyCount = player.getTongAdd().getTongBuyCount();
		}else if(extype == 3){
			buyCount = player.getXinBuyCount();
		}else if(extype == 4){
			buyCount = player.getGoldBuyCount();
		}
		int type = buyCount + 1;
		int ret = 0;
		ExchangedataTemplate et = MonsterDataManager.ExchangeData.getTemplate(extype+"_"+type);
		if(et == null){
			ret = ErrorCode.ERROR;
		}else{
			if(player.getDiamond() < et.getGem()){
				ret = ErrorCode.DIAMOND_NOT_ENOUGH;
				
			}else{
				if(extype == 1 && player.getPhBuyCount() >= 5 || extype == 2 && player.getTongAdd().getTongBuyCount()>=3 || extype == 3 && player.getXinBuyCount() >= 5 || extype == 4 && player.getGoldBuyCount() >= 5 ){
					ret = ErrorCode.BUY_COUNT_NOT_ENOUGH;
				}else{
					if(extype == 1){
						ResourceService.ins().addPhysica(player, et.getExchange_value());
						ResourceService.ins().addDiamond(player, 0-et.getGem());
						player.setPhBuyCount(player.getPhBuyCount() +1);
						buyCount = player.getPhBuyCount();
						QuestService.processTask(player, 13, 1);
					}else if(extype == 2){
						ResourceService.ins().addTongRes(player, et.getExchange_value());
						ResourceService.ins().addDiamond(player, 0-et.getGem());
						player.getTongAdd().setTongBuyCount(player.getTongAdd().getTongBuyCount() +1);
						buyCount = player.getTongAdd().getTongBuyCount();
					}else if(extype == 3){
						ResourceService.ins().addXing(player, et.getExchange_value());
						ResourceService.ins().addDiamond(player, 0-et.getGem());
						player.setXinBuyCount(player.getXinBuyCount() +1);
						buyCount = player.getXinBuyCount();
					}else if(extype == 4){
						ResourceService.ins().addGold(player, et.getExchange_value());
						ResourceService.ins().addDiamond(player, 0-et.getGem());
						player.setGoldBuyCount(player.getGoldBuyCount() +1);
						buyCount = player.getGoldBuyCount();
					}
					GoldLog.info(player.getSeverId(), player.getUserId(), player.getPlayerId(), GoldLog.BUYRES, "#extype:" + extype+"#type:"+type+"#count:"+et.getExchange_value());
				}
			}
		}

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("extype", extype);
		vo.addData("buyCount", buyCount);

		send(MProtrol.toStringProtrol(MProtrol.BUG_RES), vo, user);
	}

	
}
