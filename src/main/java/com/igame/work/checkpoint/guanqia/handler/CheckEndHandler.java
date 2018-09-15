package com.igame.work.checkpoint.guanqia.handler;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.core.log.GoldLog;
import com.igame.util.MyUtil;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.work.checkpoint.guanqia.CheckPointService;
import com.igame.work.checkpoint.guanqia.GuanQiaDataManager;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.checkpoint.guanqia.data.CheckPointTemplate;
import com.igame.work.checkpoint.tansuo.TansuoDataManager;
import com.igame.work.checkpoint.tansuo.TansuoDto;
import com.igame.work.checkpoint.tansuo.TansuoTemplate;
import com.igame.work.checkpoint.worldEvent.WorldEventDataManager;
import com.igame.work.checkpoint.worldEvent.WorldEventDto;
import com.igame.work.checkpoint.worldEvent.WorldEventTemplate;
import com.igame.work.monster.dto.Monster;
import com.igame.work.quest.service.QuestService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.PlayerService;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author Marcus.Z
 *
 */
public class CheckEndHandler extends ReconnectedHandler {
	private CheckPointService checkPointService;
	private ResourceService resourceService;
	private QuestService questService;
	private PlayerService playerService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int chapterId = jsonObject.getInt("chapterId");
		int win = jsonObject.getInt("win");

		vo.addData("chapterId", chapterId);
		vo.addData("win", win);

		CheckPointTemplate ct = GuanQiaDataManager.CheckPointData.getTemplate(chapterId);
		if(ct == null || chapterId != checkPointService.getEnterCheckpointId(player)){
			GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
					+"#act:cheat" + "#type:endC#chapterId:"+chapterId);
			return error(ErrorCode.CHECKPOINT_END_ERROR);
		}

		if(player.getXinMo().get(chapterId) != null && player.getXinMo().get(chapterId).calLeftTime(System.currentTimeMillis()) > 0){
			return error(ErrorCode.XINGMO_EXIT);
		}

		if (win != 1){
			return vo;
		}

		boolean first = false;
		if(!player.hasCheckPoint(String.valueOf(chapterId))){	//首次通关
			first = true;
		}

		//增加奖励
		RewardDto reward = checkPointService.getReward(player, chapterId, win,first, 0);
		resourceService.addRewarToPlayer(player,reward);

		//增加人物经验
		int playerExp = reward.getExp();
		resourceService.addExp(player, reward.getExp());

		//增加怪物经验
		String monsterExpStr = "";
		List<Monster> ll = Lists.newArrayList();
        monsterExpStr = checkPointService.getString(player, reward, ll, monsterExpStr);

        MessageUtil.notifyMonsterChange(player, ll);

		player.setLastCheckpointId(chapterId);

		//处理任务埋点
		questService.processTask(player, 2, 1);

		if(first){

			if(MyUtil.isNullOrEmpty(player.getCheckPoint())){
				player.setCheckPoint(String.valueOf(chapterId));
			}else{
				player.setCheckPoint(player.getCheckPoint()+"," +String.valueOf(chapterId));
			}

			notiyUnLockCheck(player, ct.getUnlock(),chapterId);//推送解锁关卡

			for(TansuoTemplate ts : TansuoDataManager.TansuoData.getAll()){//解锁探索关卡
				if(chapterId ==  ts.getUnlock() && player.getTangSuo().get(ts.getNum()) == null){
					player.getTangSuo().put(ts.getNum(), new TansuoDto(ts));
				}
			}

			for(WorldEventTemplate ts : WorldEventDataManager.WorldEventData.getAll()){//解锁世界事件
				if(ts.getLevel() == 1 && chapterId ==  ts.getUnlock()){
					WorldEventDto wet = new WorldEventDto(player.getPlayerId(), ts.getEvent_type(), "", 0,1);
					player.getWordEvent().put(ts.getEvent_type(), wet);
				}
			}

			playerService.checkDrawData(player, true);//检测造物台数据

			questService.processTask(player, 17, 0);//击杀BOSS关卡
			questService.processTask(player, 23, 0);//占领金矿数
			questService.processTask(player, 25, 0);//城市占领完成度

		}


		//怪物经验字符串
		if(monsterExpStr.lastIndexOf(";") >0){
			monsterExpStr = monsterExpStr.substring(0,monsterExpStr.lastIndexOf(";"));
		}

		//奖励字符串
		String rr = resourceService.getRewardString(reward);

		vo.addData("playerExp", playerExp);
		vo.addData("monsterExp", monsterExpStr);
		vo.addData("checkReward", rr);

		removeNianyaBuff(player);
		return vo;
	}

	private void removeNianyaBuff(Player player) {
		long[] teamMonster = player.getTeams().get(player.getCurTeam()).getTeamMonster();
		Arrays.stream(teamMonster).forEach(monsterId->{

			Monster monster = player.getMonsters().get(monsterId);
			if (monster != null) {
				monster.setHp(monster.getHp()/2);
				monster.setAttack(monster.getAttack()*10/15);
			}
		});
	}

	@Override
    public int protocolId() {
		return MProtrol.CHECKPOINT_END;
	}


	private void notiyUnLockCheck(Player player,String unlock,int newchapterId){

		StringBuilder str = new StringBuilder();
		StringBuilder strC = new StringBuilder();
		StringBuilder checkPoint = new StringBuilder(String.valueOf(newchapterId));
		if(!MyUtil.isNullOrEmpty(unlock)){
			String[] us = unlock.split(",");
			Set<Integer> ccs = Sets.newHashSet();
			Set<Integer> newccs = Sets.newHashSet();
			if(player.getCheckPoint() != null && !MyUtil.isNullOrEmpty(player.getCheckPoint())){//已过章节
				String[] cc = player.getCheckPoint().split(",");
				for(String c : cc){
					CheckPointTemplate ct = GuanQiaDataManager.CheckPointData.getTemplate(Integer.parseInt(c));
					if(ct != null){
						ccs.add(ct.getCityId());
					}
				}
			}
			for(String u : us){
				CheckPointTemplate ct = GuanQiaDataManager.CheckPointData.getTemplate(Integer.parseInt(u));
				if(!player.hasCheckPoint(u)){//真正第一次已过关卡更新
					if(ct.getChapterType()!=2){
						str.append(",").append(u);
					}

					if(ct.getChapterType()==2 && !MyUtil.isNullOrEmpty(ct.getDropPoint())){
						if(player.getTimeResCheck().get(ct.getChapterId()) == null){//资源关卡
							checkPoint.append(",").append(ct.getChapterId());
							player.setCheckPoint(player.getCheckPoint()+"," +String.valueOf(ct.getChapterId()));
							player.getTimeResCheck().put(ct.getChapterId(), ct.getMaxTime() * 60);
							RewardDto dto = resourceService.getResRewardDto(ct.getDropPoint(), ct.getMaxTime() * 60, ct.getMaxTime() * 60);
							MessageUtil.notifyTimeResToPlayer(player,ct.getChapterId(), dto);    //推送金币关卡 第一次满
						}
					}
				}

				if(ct != null && !ccs.contains(ct.getCityId())){
					newccs.add(ct.getCityId());
				}
			}
			if(!newccs.isEmpty()){
				for(Integer ii : newccs){
					strC.append(",").append(ii);
				}
			}
			if(strC.length()>0){
				strC = new StringBuilder(strC.substring(1));
			}
			if(str.length()>0){
				str = new StringBuilder(str.substring(1));
				if(isTwoRound(str.toString())){
					if(player.getRound() == 1){
						player.setRound(2);
					}
				}
			}


		}
		RetVO vo = new RetVO();
		vo.addData("unlock", str.toString());
		vo.addData("unlockCity", strC.toString());
		vo.addData("checkPoint", checkPoint.toString());
		vo.addData("round", player.getRound());
		MessageUtil.sendMessageToPlayer(player, MProtrol.CHECK_UNLOCK, vo);


	}

	private boolean isTwoRound(String checkpoint){
		if (checkpoint == null) {
			return false;
		}

		String[] ssc = checkpoint.split(",");
		for(String ss : ssc){
			CheckPointTemplate ct = GuanQiaDataManager.CheckPointData.getTemplate(Integer.parseInt(ss));
			if(ct.getRound() == 2){
				return true;
			}
		}
		return false;

	}

}
