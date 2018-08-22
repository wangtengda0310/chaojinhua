package com.igame.work.monster.handler;





import net.sf.json.JSONObject;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.PlayerService;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TongHuaRefHandler extends BaseHandler{
	

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
		int ret = 0;
		if(type< 1 || type>2){
			ret = ErrorCode.ERROR;
		}else{
			if(type == 1){
				if(player.getTonghua().calRefLeftTime() >0){
					ret = ErrorCode.TONGHUA_CD_NOT;
				}else{
					player.setTonghua(PlayerService.getRandomTongHuaDto());
					player.getTonghua().setStartRefTime(System.currentTimeMillis());
				}
			}else{
				if(player.getDiamond() < 100){
					ret = ErrorCode.DIAMOND_NOT_ENOUGH;
				}else{
					ResourceService.ins().addDiamond(player, -100);
					player.setTonghua(PlayerService.getRandomTongHuaDto());
					player.getTonghua().setStartRefTime(System.currentTimeMillis());
				}
			}
		}

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}else{
			MessageUtil.notiyTongHuaAddChange(player);
		}
		player.getTonghua().calLeftTime();
		player.getTonghua().calRefLeftTime();
		vo.addData("type", type);
		vo.addData("tongInfo", player.getTonghua());

		send(MProtrol.toStringProtrol(MProtrol.TONGHUA_CD_REF), vo, user);
	}

	
}
