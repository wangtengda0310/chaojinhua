package com.igame.work.checkpoint.guanqia.handler;



import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.work.checkpoint.guanqia.GuanQiaDataManager;
import com.igame.work.checkpoint.guanqia.data.CheckPointTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.core.log.GoldLog;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.checkpoint.guanqia.CheckPointService;
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
public class CheckSaoDangHandler extends BaseHandler{
	

	@Override
	public void handleClientRequest(User user, ISFSObject params) {

		RetVO vo = new RetVO();
		if(reviceMessage(user,params,vo)){
			return;
		}

		Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
		if(player == null){
			this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
			return;
		}

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int chapterId = jsonObject.getInt("chapterId");
		vo.addData("chapterId", chapterId);

		//入参校验
		CheckPointTemplate ct = GuanQiaDataManager.CheckPointData.getTemplate(chapterId);
		if(ct == null || !MyUtil.hasCheckPoint(player.getCheckPoint(), String.valueOf(chapterId))){
	    	GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
	    			+"#act:cheat" + "#type:saoC#chapterId:"+chapterId);
			sendError(ErrorCode.CHECKPOINT_END_ERROR,MProtrol.toStringProtrol(MProtrol.CHECKPOINT_SAO), vo, user);
			return;
		}

		//扫荡券校验
		if(player.getSao() < 1){
			sendError(ErrorCode.SAO_NOT_ENOUGH,MProtrol.toStringProtrol(MProtrol.CHECKPOINT_SAO), vo, user);
			return;
		}

		//体力校验
		if(player.getPhysical() < ct.getPhysical()){
			sendError(ErrorCode.PHYSICA_NOT_ENOUGH,MProtrol.toStringProtrol(MProtrol.CHECKPOINT_SAO), vo, user);
			return;
		}

		//校验挑战次数
		if (player.getPlayerCount().getCheckPoint(ct.getChapterType(),chapterId) <= 0){
			sendError(ErrorCode.CHECKCOUNT_NOT_ENOUGH,MProtrol.toStringProtrol(MProtrol.CHECKPOINT_ENTER), vo, user);//该关卡今日挑战次数不足
			return;
		}

		//扣除扫荡券
		ResourceService.ins().addSao(player,1);

		//扣除体力
		ResourceService.ins().addPhysica(player, 0-ct.getPhysical());

		//减少可挑战次数
		player.getPlayerCount().addCheckPoint(player,ct,-1);

		//增加奖励
		RewardDto reward = CheckPointService.getReward(player, chapterId, 1,false, 0);
		ResourceService.ins().addRewarToPlayer(player,reward);

		//增加人物经验
		int playerExp = reward.getExp();
		ResourceService.ins().addExp(player, playerExp);

		//增加怪兽经验
		List<Monster> ll = Lists.newArrayList();
		String monsterExpStr = "";
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

		//怪物经验字符串
		if(monsterExpStr.lastIndexOf(";") >0){
			monsterExpStr = monsterExpStr.substring(0,monsterExpStr.lastIndexOf(";"));
		}

		//奖励字符串
		String rr = ResourceService.ins().getRewardString(reward);

		vo.addData("playerExp", playerExp);
		vo.addData("monsterExp", monsterExpStr);
		vo.addData("reward", rr);

		sendSucceed(MProtrol.toStringProtrol(MProtrol.CHECKPOINT_SAO), vo, user);
	}

	
}
