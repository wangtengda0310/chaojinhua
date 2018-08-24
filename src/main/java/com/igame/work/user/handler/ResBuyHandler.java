package com.igame.work.user.handler;


import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.core.log.GoldLog;
import com.igame.work.monster.MonsterDataManager;
import com.igame.work.monster.data.ExchangedataTemplate;
import com.igame.work.quest.service.QuestService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class ResBuyHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

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
		ExchangedataTemplate et = MonsterDataManager.ExchangeData.getTemplate(extype+"_"+type);
		if(et == null){
			return error(ErrorCode.ERROR);
		}else{
			if(player.getDiamond() < et.getGem()){
				return error(ErrorCode.DIAMOND_NOT_ENOUGH);
				
			}else{
				if(extype == 1 && player.getPhBuyCount() >= 5 || extype == 2 && player.getTongAdd().getTongBuyCount()>=3 || extype == 3 && player.getXinBuyCount() >= 5 || extype == 4 && player.getGoldBuyCount() >= 5 ){
					return error(ErrorCode.BUY_COUNT_NOT_ENOUGH);
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

		vo.addData("extype", extype);
		vo.addData("buyCount", buyCount);

		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.BUG_RES;
	}

}
