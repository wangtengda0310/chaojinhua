package com.igame.work.checkpoint.handler.guanqia;



import java.util.List;

import com.igame.work.checkpoint.GuanQiaDataManager;
import net.sf.json.JSONObject;

import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.work.checkpoint.data.CheckPointTemplate;
import com.igame.work.checkpoint.data.TangSuoTemplate;
import com.igame.work.checkpoint.data.WorldEventTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.core.log.GoldLog;
import com.igame.dto.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.checkpoint.dto.RewardDto;
import com.igame.work.checkpoint.dto.TangSuoDto;
import com.igame.work.checkpoint.dto.WordEventDto;
import com.igame.work.checkpoint.service.CheckPointService;
import com.igame.work.monster.dto.Monster;
import com.igame.work.quest.service.QuestService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.PlayerService;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class CheckEndHandler extends BaseHandler{
	

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

		int chapterId = jsonObject.getInt("chapterId");
		int win = jsonObject.getInt("win");

		vo.addData("chapterId", chapterId);
		vo.addData("win", win);

		CheckPointTemplate ct = GuanQiaDataManager.CheckPointData.getTemplate(chapterId);
		if(ct == null || chapterId != player.getEnterCheckpointId()){
			sendError(ErrorCode.CHECKPOINT_END_ERROR,MProtrol.toStringProtrol(MProtrol.CHECKPOINT_END), vo, user);
	    	GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
	    			+"#act:cheat" + "#type:endC#chapterId:"+chapterId);
	    	return;
		}

		if(player.getXinMo().get(chapterId) != null && player.getXinMo().get(chapterId).calLeftTime(System.currentTimeMillis()) > 0){
			sendError(ErrorCode.XINGMO_EXIT,MProtrol.toStringProtrol(MProtrol.CHECKPOINT_END), vo, user);
			return;
		}

		if (win != 1){
			sendSucceed(MProtrol.toStringProtrol(MProtrol.CHECKPOINT_END), vo, user);
			return;
		}

		boolean first = false;
		if(!MyUtil.hasCheckPoint(player.getCheckPoint(), String.valueOf(chapterId))){	//首次通关
			first = true;
		}

		//增加奖励
		RewardDto reward = CheckPointService.getReward(player, chapterId, win,first, 0);
		ResourceService.ins().addRewarToPlayer(player,reward);

		//增加人物经验
		int playerExp = reward.getExp();
		ResourceService.ins().addExp(player, reward.getExp());

		//增加怪物经验
		String monsterExpStr = "";
		List<Monster> ll = Lists.newArrayList();
		for(long mid : player.getTeams().get(player.getCurTeam()).getTeamMonster()){
			if(-1 != mid){
				Monster mm = player.getMonsters().get(mid);
				if(mm != null){
					int mmExp = CheckPointService.getTotalExp(mm, reward.getExp());
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

		player.setLastCheckpointId(chapterId);

		//处理任务埋点
		QuestService.processTask(player, 2, 1);

		if(first){

			if(MyUtil.isNullOrEmpty(player.getCheckPoint())){
				player.setCheckPoint(String.valueOf(chapterId));
			}else{
				player.setCheckPoint(player.getCheckPoint()+"," +String.valueOf(chapterId));
			}

			MessageUtil.notiyUnLockCheck(player, ct.getUnlock(),chapterId);//推送解锁关卡

			for(TangSuoTemplate ts : GuanQiaDataManager.TangSuoData.getAll()){//解锁探索关卡
				if(chapterId ==  ts.getUnlock() && player.getTangSuo().get(ts.getNum()) == null){
					player.getTangSuo().put(ts.getNum(), new TangSuoDto(ts));
				}
			}

			for(WorldEventTemplate ts : GuanQiaDataManager.WordEventData.getAll()){//解锁世界事件
				if(ts.getLevel() == 1 && chapterId ==  ts.getUnlock()){
					WordEventDto wet = new WordEventDto(player.getPlayerId(), ts.getEvent_type(), "", 0,1);
					player.getWordEvent().put(ts.getEvent_type(), wet);
				}
			}

			PlayerService.checkDrawData(player, true);//检测造物台数据

			QuestService.processTask(player, 17, 0);//击杀BOSS关卡
			QuestService.processTask(player, 23, 0);//占领金矿数
			QuestService.processTask(player, 25, 0);//城市占领完成度

		}


		//怪物经验字符串
		if(monsterExpStr.lastIndexOf(";") >0){
			monsterExpStr = monsterExpStr.substring(0,monsterExpStr.lastIndexOf(";"));
		}

		//奖励字符串
		String rr = ResourceService.ins().getRewardString(reward);

		vo.addData("playerExp", playerExp);
		vo.addData("monsterExp", monsterExpStr);
		vo.addData("reward", rr);

		sendSucceed(MProtrol.toStringProtrol(MProtrol.CHECKPOINT_END), vo, user);
	}

	
}
