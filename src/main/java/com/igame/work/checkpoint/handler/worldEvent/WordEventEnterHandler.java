package com.igame.work.checkpoint.handler.worldEvent;



import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.GuanQiaDataManager;
import com.igame.work.checkpoint.data.WorldEventTemplate;
import com.igame.work.checkpoint.dto.WordEventDto;
import com.igame.work.fight.dto.FightBase;
import com.igame.work.fight.dto.FightData;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.fight.service.FightUtil;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
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
public class WordEventEnterHandler extends BaseHandler{
	

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

		int eventType = jsonObject.getInt("eventType");
		int level = jsonObject.getInt("level");
		vo.addData("eventType", eventType);
		vo.addData("level", level);

		//入参校验
		WordEventDto wd = player.getWordEvent().get(eventType);
		WorldEventTemplate wt = GuanQiaDataManager.WordEventData.getTemplate(eventType+"_"+level);
		if (wd == null || wt ==null){
			sendError(ErrorCode.CHECKPOINT_ENTER_ERROR,MProtrol.toStringProtrol(MProtrol.WWORDEVENT_ENTER), vo, user);
			return;
		}

		//校验前置难度是否通过, level = 1 时 wd.getLevel() 为 空字符串
		if (level != 1 && !wd.getLevel().contains(String.valueOf(level-1))){
			sendError(ErrorCode.CHECKPOINT_ENTER_ERROR,MProtrol.toStringProtrol(MProtrol.WWORDEVENT_ENTER), vo, user);
			return;
		}

		//校验背包
		if (player.getItems().size() >= player.getBagSpace()){
			sendError(ErrorCode.BAGSPACE_ALREADY_FULL,MProtrol.toStringProtrol(MProtrol.WWORDEVENT_ENTER), vo, user);
			return;
		}

		//校验体力
		if(player.getPhysical() < wt.getPhysical()){
			sendError(ErrorCode.PHYSICA_NOT_ENOUGH,MProtrol.toStringProtrol(MProtrol.WWORDEVENT_ENTER), vo, user);
			return;
		}

		//校验挑战次数
		if(wd.getCount() >= wt.getTimes()){
			sendError(ErrorCode.TODAY_COUNT_NOTENOUGH,MProtrol.toStringProtrol(MProtrol.WWORDEVENT_ENTER), vo, user);
			return;
		}

		//扣除体力
		ResourceService.ins().addPhysica(player, 0-wt.getPhysical());

		//防作弊
		player.setEnterWordEventTime(System.currentTimeMillis());
		player.setEnterWordEventId(eventType+"_"+level);

		//生成怪兽
		FightBase fb  = new FightBase(player.getPlayerId(),new FightData(player),new FightData(null,FightUtil.createMonster(wt.getMonsterId(), wt.getMlevel(), wt.getSite(),"","")));
		player.setFightBase(fb);
		List<MatchMonsterDto> lb = Lists.newArrayList();
		for(Monster m : fb.getFightB().getMonsters().values()){
			MatchMonsterDto mto = new MatchMonsterDto(m);
			mto.reCalGods(player.callFightGods(), null);
			lb.add(mto);
		}

		vo.addData("m", lb);

		Map<String, Object> param = new HashMap<>();
		param.put("battleType", 2);
		param.put("eventType", eventType);
		param.put("level", level);
		player.setLastBattleParam(param);

		send(MProtrol.toStringProtrol(MProtrol.WWORDEVENT_ENTER), vo, user);
	}

	
}
