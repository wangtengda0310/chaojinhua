package com.igame.work.checkpoint.worldEvent.handler;


import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.checkpoint.worldEvent.WorldEventDataManager;
import com.igame.work.checkpoint.worldEvent.WorldEventDto;
import com.igame.work.checkpoint.worldEvent.WorldEventTemplate;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class WorldEventListHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		for(WorldEventTemplate ts : WorldEventDataManager.WorldEventData.getAll()){
			if(ts.getLevel() == 1 &&  MyUtil.hasCheckPoint(player.getCheckPoint(), String.valueOf(ts.getUnlock()))){
				player.getWordEvent().computeIfAbsent(ts.getEvent_type(),wet -> new WorldEventDto(player.getPlayerId(), ts.getEvent_type(), "", 0,1));
			}
		}

		RetVO vo = new RetVO();
		vo.addData("world", player.getWordEvent().values());

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.WWORDEVENT_LIST;
	}

}
