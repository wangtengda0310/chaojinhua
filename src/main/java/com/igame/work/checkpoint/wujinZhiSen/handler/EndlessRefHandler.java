package com.igame.work.checkpoint.wujinZhiSen.handler;





import net.sf.json.JSONObject;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.guanqia.CheckPointService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class EndlessRefHandler extends BaseHandler{
	

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

		int ret = 0;
		if(player.getWuReset() >= 1){//免费次数已用完
			if(player.getDiamond()<100){
				ret = ErrorCode.DIAMOND_NOT_ENOUGH;
			}else{
				ret = CheckPointService.refEndlessRef(player);
				ResourceService.ins().addDiamond(player, -100);
			}
		}else{
			ret = CheckPointService.refEndlessRef(player);
			player.setWuReset(1);
			MessageUtil.notiyWuResetChange(player);
		}

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}else{
			MessageUtil.notiyWuChange(player);
			MessageUtil.notiyWuZhengChange(player);
			MessageUtil.notiyWuNaiChange(player);
			MessageUtil.notiyWuBufferChange(player,player.getWuEffect());
			MessageUtil.notiyWuResetChange(player);
		}

		send(MProtrol.toStringProtrol(MProtrol.WU_REF), vo, user);
	}

	
}
