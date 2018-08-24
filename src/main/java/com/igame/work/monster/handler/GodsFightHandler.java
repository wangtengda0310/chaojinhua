package com.igame.work.monster.handler;


import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.monster.dto.Gods;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class GodsFightHandler extends ReconnectedHandler {


	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);
		
		int teamId = jsonObject.getInt("teamId");
		int godsType = jsonObject.getInt("godsType");
		Gods gods = player.getGods().get(godsType);

		if(gods == null || player.getTeams().get(teamId) == null){
			return error(ErrorCode.ERROR);
		}else{
			 player.getTeams().get(teamId).setTeamGod(godsType);
			 MessageUtil.notifyTeamChange(player,player.getTeams().get(teamId));
		}

		RetVO vo = new RetVO();

		vo.addData("teamId", teamId);
		vo.addData("godsType", godsType);

		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.GODS_FIGHT;
	}
}
