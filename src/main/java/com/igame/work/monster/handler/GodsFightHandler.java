package com.igame.work.monster.handler;



import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.GodsdataTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.core.log.GoldLog;
import com.igame.dto.RetVO;
import com.igame.work.checkpoint.dto.RewardDto;
import com.igame.work.monster.dto.Gods;
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
public class GodsFightHandler extends BaseHandler{
	

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
		
		int teamId = jsonObject.getInt("teamId");
		int godsType = jsonObject.getInt("godsType");
		int ret = 0;
		Gods gods = player.getGods().get(godsType);

		if(gods == null || player.getTeams().get(teamId) == null){
			ret = ErrorCode.ERROR;
		}else{
			 player.getTeams().get(teamId).setTeamGod(godsType);
			 MessageUtil.notiyTeamChange(player,player.getTeams().get(teamId));
		}

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("teamId", teamId);
		vo.addData("godsType", godsType);

		send(MProtrol.toStringProtrol(MProtrol.GODS_FIGHT), vo, user);
	}

	
}
