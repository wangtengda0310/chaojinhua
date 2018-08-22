package com.igame.work.checkpoint.handler.worldEvent;



import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.work.checkpoint.GuanQiaDataManager;
import com.igame.work.checkpoint.data.WorldEventTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.util.GameMath;
import com.igame.work.checkpoint.dto.RewardDto;
import com.igame.work.checkpoint.dto.WordEventDto;
import com.igame.work.checkpoint.service.CheckPointService;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public class WordEventSaoHandler extends BaseHandler{
	

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
		int ret = 0;
		String reward = null;
		int playerExp = 0;
		String monsterExpStr = "";
		
		WordEventDto wd = player.getWordEvent().get(eventType);
		WorldEventTemplate wt = GuanQiaDataManager.WordEventData.getTemplate(eventType+"_"+level);
		if(wd == null ||wt ==null|| wd.getLevel().indexOf(String.valueOf(level)) == -1){
			ret = ErrorCode.CHECKPOINT_END_ERROR;
		}else{
			
			if(player.getPhysical() < wt.getPhysical()){
				ret = ErrorCode.PHYSICA_NOT_ENOUGH;
			}else{
				if(wd.getCount() >= wt.getTimes()){
					ret = ErrorCode.TODAY_COUNT_NOTENOUGH;
				}else{
					RewardDto rt = new RewardDto();
					rt = ResourceService.ins().getRewardDto(wt.getDrop(), wt.getRate());
					String[] golds = wt.getGold().split("-");
					if(golds != null && golds.length >= 2){
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
					if(monsterExpStr.lastIndexOf(";") >0){
						monsterExpStr = monsterExpStr.substring(0,monsterExpStr.lastIndexOf(";"));
					}
				}
			}

		}

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("eventType", eventType);
		vo.addData("level", level);
		vo.addData("count", wd.getCount());
		vo.addData("playerExp", playerExp);
		vo.addData("monsterExp", monsterExpStr);
		vo.addData("reward", reward);

		send(MProtrol.toStringProtrol(MProtrol.WWORDEVENT_SAO), vo, user);
	}

	
}
