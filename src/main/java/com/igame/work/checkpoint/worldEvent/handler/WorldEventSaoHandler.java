package com.igame.work.checkpoint.worldEvent.handler;


import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.GameMath;
import com.igame.work.checkpoint.guanqia.CheckPointService;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.checkpoint.worldEvent.WorldEventDataManager;
import com.igame.work.checkpoint.worldEvent.WorldEventDto;
import com.igame.work.checkpoint.worldEvent.WorldEventTemplate;
import com.igame.work.monster.dto.Monster;
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
public class WorldEventSaoHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int eventType = jsonObject.getInt("eventType");
		int level = jsonObject.getInt("level");
		String reward;
		int playerExp;
		String monsterExpStr = "";
		
		WorldEventDto wd = player.getWordEvent().get(eventType);
		WorldEventTemplate wt = WorldEventDataManager.WorldEventData.getTemplate(eventType+"_"+level);
		if(wd == null ||wt ==null|| !wd.getLevel().contains(String.valueOf(level))){
			return error(ErrorCode.CHECKPOINT_END_ERROR);
		}else{
			
			if(player.getPhysical() < wt.getPhysical()){
				return error(ErrorCode.PHYSICA_NOT_ENOUGH);
			}else{
				if(wd.getCount() >= wt.getTimes()){
					return error(ErrorCode.TODAY_COUNT_NOTENOUGH);
				}else{
					RewardDto rt = ResourceService.ins().getRewardDto(wt.getDrop(), wt.getRate());
					String[] golds = wt.getGold().split("-");
					if(golds.length >= 2){
						rt.setGold(rt.getGold() + GameMath.getRandomCount(Integer.parseInt(golds[0]), Integer.parseInt(golds[1])));
					}
					ResourceService.ins().addRewarToPlayer(player, rt);
					reward = ResourceService.ins().getRewardString(rt);
					wd.setCount(wd.getCount() + 1);
					wd.setDtate(2);
					
					ResourceService.ins().addExp(player, wt.getPhysical() * 5);
					playerExp = wt.getPhysical() * 5;
					List<Monster> ll = Lists.newArrayList();
					/*String[] monsters = player.getTeams()[0].split(",");
					for(String mid : monsters){
						if(!"-1".equals(mid)){
							Monster mm = player.getMonsters().get(Long.parseLong(mid));
							if(mm != null){
								int mmExp = CheckPointService.getTotalExp(mm, wt.getPhysical() * 5);
								monsterExpStr += mid;
								if(ResourceService.ins().addMonsterExp(player, Long.parseLong(mid), mmExp, false) == 0){
									ll.add(mm);
									monsterExpStr += ("," + mmExp +";");
								}else{
									monsterExpStr += ",0;";
								}
							}
						}
					}	*/
					long[] teamMonster = player.getTeams().get(player.getCurTeam()).getTeamMonster();
					for (long mid : teamMonster) {
						monsterExpStr = getString(player, monsterExpStr, wt, ll, mid);
					}
					MessageUtil.notiyMonsterChange(player, ll);
					if(monsterExpStr.lastIndexOf(";") >0){
						monsterExpStr = monsterExpStr.substring(0,monsterExpStr.lastIndexOf(";"));
					}
				}
			}

		}

		vo.addData("eventType", eventType);
		vo.addData("level", level);
		vo.addData("count", wd.getCount());
		vo.addData("playerExp", playerExp);
		vo.addData("monsterExp", monsterExpStr);
		vo.addData("reward", reward);

		return vo;
	}

	static String getString(Player player, String monsterExpStr, WorldEventTemplate wt, List<Monster> ll, long mid) {
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
		return monsterExpStr;
	}

	@Override
	protected int protocolId() {
		return MProtrol.WWORDEVENT_SAO;
	}

}
