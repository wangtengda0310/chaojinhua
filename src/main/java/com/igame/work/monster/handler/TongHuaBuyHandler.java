package com.igame.work.monster.handler;





import java.util.List;

import net.sf.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.ExchangedataTemplate;
import com.igame.core.data.template.TangSuoTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.checkpoint.dto.TangSuoDto;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TongHuaBuyHandler extends BaseHandler{
	

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
		int type = jsonObject.getInt("type");
		int tongBuyCount = player.getTongAdd().getTongBuyCount();
		int ret = 0;
		ExchangedataTemplate et = DataManager.ins().ExchangeData.getTemplate(2+"_"+type);
		if(et == null){
			ret = ErrorCode.ERROR;
		}else{
			if(tongBuyCount>=3){
				ret = ErrorCode.BUY_COUNT_NOT_ENOUGH;
			}else{
				if(player.getDiamond() < et.getGem()){
					ret = ErrorCode.GOLD_NOT_ENOUGH;
				}else{
					ResourceService.ins().addTongRes(player, et.getExchange_value());
					ResourceService.ins().addDiamond(player, 0-et.getGem());
					player.getTongAdd().setTongBuyCount(player.getTongAdd().getTongBuyCount() +1);
					tongBuyCount = player.getTongAdd().getTongBuyCount();
				}
			}
		}

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("type", type);
		vo.addData("tongBuyCount", tongBuyCount);

		send(MProtrol.toStringProtrol(MProtrol.TONGHUAINFO_BUY), vo, user);
	}

	
}
