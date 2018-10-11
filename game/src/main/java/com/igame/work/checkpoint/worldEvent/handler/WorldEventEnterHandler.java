package com.igame.work.checkpoint.worldEvent.handler;


import com.google.common.collect.Lists;
import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.GameMath;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.checkpoint.guanqia.CheckPointService;
import com.igame.work.checkpoint.worldEvent.WorldEventDto;
import com.igame.work.checkpoint.worldEvent.WorldEventService;
import com.igame.work.checkpoint.worldEvent.WorldEventTemplate;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.service.MonsterService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.igame.work.user.service.RobotService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class WorldEventEnterHandler extends ReconnectedHandler {

	@Inject private ResourceService resourceService;
	@Inject private CheckPointService checkPointService;
	@Inject private WorldEventService worldEventService;
	@Inject private MonsterService monsterService;
    @Inject private RobotService robotService;

    @Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int eventType = jsonObject.getInt("eventType");
		int level = jsonObject.getInt("level");
		vo.addData("eventType", eventType);
		vo.addData("level", level);

		//入参校验
		WorldEventDto wd = player.getWordEvent().get(eventType);
		WorldEventTemplate wt = worldEventService.worldEventData.getTemplate(eventType+"_"+level);
		if (wd == null || wt ==null){
			return error(ErrorCode.CHECKPOINT_ENTER_ERROR);
		}

		//校验前置难度是否通过, level = 1 时 wd.getLevel() 为 空字符串
		if (level != 1 && !wd.getLevel().contains(String.valueOf(level-1))){
			return error(ErrorCode.CHECKPOINT_ENTER_ERROR);
		}

		//校验背包
		if (player.getItems().size() >= player.getBagSpace()){
			return error(ErrorCode.BAGSPACE_ALREADY_FULL);
		}

		//校验体力
		if(player.getPhysical() < wt.getPhysical()){
			return error(ErrorCode.PHYSICA_NOT_ENOUGH);
		}

		//校验挑战次数
		if(wd.getCount() >= wt.getTimes()){
			return error(ErrorCode.TODAY_COUNT_NOTENOUGH);
		}

		//扣除体力
		resourceService.addPhysica(player, 0-wt.getPhysical());

		//防作弊
		checkPointService.setEnterWordEventTime(player);
		worldEventService.setEnterWordEventId(player,eventType+"_"+level);

		List<MatchMonsterDto> lb = monsterService.createMonsterDtoOfAll(robotService.randomOne(wt.getMonsterset(), "\\|"));

		vo.addData("m", lb);

		Map<String, Object> param = new HashMap<>();
		param.put("battleType", 2);
		param.put("eventType", eventType);
		param.put("level", level);
		checkPointService.setLastBattleParam(player.getPlayerId(), param);

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.WWORDEVENT_ENTER;
	}

}
