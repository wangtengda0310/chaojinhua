package com.igame.work.checkpoint.handler.worldEvent;



import java.util.List;

import com.igame.util.MyUtil;
import com.igame.work.checkpoint.GuanQiaDataManager;
import net.sf.json.JSONObject;

import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.work.checkpoint.data.WorldEventTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.util.GameMath;
import com.igame.work.checkpoint.dto.RewardDto;
import com.igame.work.checkpoint.dto.WordEventDto;
import com.igame.work.checkpoint.service.CheckPointService;
import com.igame.work.monster.dto.Monster;
import com.igame.work.quest.service.QuestService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class WordEventEndHandler extends BaseHandler{
	

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
		int win = jsonObject.getInt("win");

		vo.addData("eventType", eventType);
		vo.addData("level", level);
		vo.addData("win", win);

		//防作弊校验
		if (!(eventType+"_"+level).equals(player.getEnterWordEventId())){
			sendError(ErrorCode.CHECKPOINT_END_ERROR,MProtrol.toStringProtrol(MProtrol.WWORDEVENT_END), vo, user);
			return;
		}

		WordEventDto wd = player.getWordEvent().get(eventType);
		WorldEventTemplate wt = GuanQiaDataManager.WordEventData.getTemplate(eventType+"_"+level);
		if(wd == null ||wt ==null){
			sendError(ErrorCode.CHECKPOINT_END_ERROR,MProtrol.toStringProtrol(MProtrol.WWORDEVENT_END), vo, user);
			return;
		}

		if(win != 1){
			sendSucceed(MProtrol.toStringProtrol(MProtrol.WWORDEVENT_END), vo, user);
			return;
		}

		if (!wd.getLevel().contains(String.valueOf(level))){	//首次通关
			String newLevel = MyUtil.isNullOrEmpty(wd.getLevel()) ? String.valueOf(level) : wd.getLevel() + "," + level;
			wd.setLevel(newLevel);
		}

		//增加挑战次数
		wd.setCount(wd.getCount() + 1);
		wd.setDtate(2);

		//处理任务埋点
		QuestService.processTask(player, 3, 1);

		//增加奖励
		RewardDto rt = ResourceService.ins().getRewardDto(wt.getDrop(), wt.getRate());
		String[] golds = wt.getGold().split("-");
		if(golds.length >= 2){
			rt.setGold(rt.getGold() + GameMath.getRandomCount(Integer.parseInt(golds[0]), Integer.parseInt(golds[1])));
		}

		ResourceService.ins().addRewarToPlayer(player, rt);

		//增加人物经验
		int playerExp = wt.getPhysical() * 5;
		ResourceService.ins().addExp(player,playerExp);

		//增加怪物经验
		List<Monster> ll = Lists.newArrayList();
		String monsterExpStr = "";
		for (long mid : player.getTeams().get(player.getCurTeam()).getTeamMonster()) {
			if(-1 != mid){
				Monster mm = player.getMonsters().get(mid);
				if(mm != null){
					int mmExp = CheckPointService.getTotalExp(mm, wt.getPhysical() * 5);
					monsterExpStr += mid;
					if(ResourceService.ins().addMonsterExp(player, mid, mmExp, false) == 0){
						ll.add(mm);
						monsterExpStr += ("," + mmExp +";");
					}else{
						monsterExpStr += ",0;";
					}
				}
			}
		}

		MessageUtil.notiyMonsterChange(player, ll);

		//怪物经验字符串
		if(monsterExpStr.lastIndexOf(";") >0){
			monsterExpStr = monsterExpStr.substring(0,monsterExpStr.lastIndexOf(";"));
		}

		//奖励字符串
		String reward = ResourceService.ins().getRewardString(rt);

		vo.addData("playerExp", playerExp);
		vo.addData("monsterExp", monsterExpStr);
		vo.addData("reward", reward);
		vo.addData("count", wd.getCount());
		vo.addData("levelInfo", wd.getLevel());

		sendSucceed(MProtrol.toStringProtrol(MProtrol.WWORDEVENT_END), vo, user);
	}

	
}
