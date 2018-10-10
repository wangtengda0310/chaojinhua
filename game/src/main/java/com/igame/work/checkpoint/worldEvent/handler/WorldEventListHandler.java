package com.igame.work.checkpoint.worldEvent.handler;


import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.MProtrol;
import com.igame.work.checkpoint.guanqia.CheckPointService;
import com.igame.work.checkpoint.worldEvent.WorldEventDto;
import com.igame.work.checkpoint.worldEvent.WorldEventService;
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
	@Inject
	private CheckPointService checkPointService;
	@Inject
	private WorldEventService worldEventService;
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		for(WorldEventTemplate ts : worldEventService.worldEventData.getAll()){
			if(ts.getDifficulty() == 1 &&  player.hasCheckPoint(String.valueOf(ts.getUnlock()))){
				player.getWordEvent()
						.computeIfAbsent(ts.getEventType()
								,wet -> new WorldEventDto(player.getPlayerId(), ts.getEventType(), "", 0,1));
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
