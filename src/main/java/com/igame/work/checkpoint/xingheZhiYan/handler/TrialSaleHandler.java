package com.igame.work.checkpoint.xingheZhiYan.handler;


import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author xym
 *
 * 	购买星能(即星核之眼挑战次数)
 *
 */
public class TrialSaleHandler extends BaseHandler{
	

	@Override
	public void handleClientRequest(User user, ISFSObject params) {

		RetVO vo = new RetVO();
		if(reviceMessage(user,params,vo)){
			return;
		}

		Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
		if(player == null){
			this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
			return;
		}

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		//校验钻石
		if (player.getDiamond() < 50){
			sendError(ErrorCode.DIAMOND_NOT_ENOUGH, MProtrol.toStringProtrol(MProtrol.TRIAL_SALE),vo,user);
			return;
		}

		//扣除钻石
		ResourceService.ins().addDiamond(player,-50);

		//增加星能
		ResourceService.ins().addXing(player,5);

		vo.addData("xing",player.getXing());
		sendSucceed(MProtrol.toStringProtrol(MProtrol.TRIAL_SALE),vo,user);
	}

	
}
