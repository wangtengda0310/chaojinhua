package com.igame.work.monster.handler;


import com.igame.core.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.monster.service.MonsterService;
import com.igame.work.quest.service.QuestService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MonsterEVHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();
		
		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);
		
		long objectId = jsonObject.getLong("objectId");
		int nextObject = jsonObject.getInt("nextObject");

		int ret = MonsterService.monsterEV(player, objectId, nextObject);
		
		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}else{
			QuestService.processTask(player, 8, 1);
		}
		vo.addData("objectId", objectId);
		vo.addData("nextObject", nextObject);

		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.MONSTER_EV;
	}

}
