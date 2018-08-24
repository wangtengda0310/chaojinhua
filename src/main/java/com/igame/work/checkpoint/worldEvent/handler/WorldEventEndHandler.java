package com.igame.work.checkpoint.worldEvent.handler;


import com.google.common.collect.Lists;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.GameMath;
import com.igame.util.MyUtil;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.checkpoint.worldEvent.WorldEventDataManager;
import com.igame.work.checkpoint.worldEvent.WorldEventDto;
import com.igame.work.checkpoint.worldEvent.WorldEventTemplate;
import com.igame.work.monster.dto.Monster;
import com.igame.work.quest.service.QuestService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public class WorldEventEndHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int eventType = jsonObject.getInt("eventType");
		int level = jsonObject.getInt("level");
		int win = jsonObject.getInt("win");

		vo.addData("eventType", eventType);
		vo.addData("level", level);
		vo.addData("win", win);

		//防作弊校验
		if (!(eventType+"_"+level).equals(player.getEnterWordEventId())){
			return error(ErrorCode.CHECKPOINT_END_ERROR);
		}

		WorldEventDto wd = player.getWordEvent().get(eventType);
		WorldEventTemplate wt = WorldEventDataManager.WorldEventData.getTemplate(eventType+"_"+level);
		if(wd == null ||wt ==null){
			return error(ErrorCode.CHECKPOINT_END_ERROR);
		}

		if(win != 1){
			return vo;
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
            monsterExpStr = WorldEventSaoHandler.getString(player, monsterExpStr, wt, ll, mid);
        }

		MessageUtil.notifyMonsterChange(player, ll);

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

		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.WWORDEVENT_END;
	}

}
