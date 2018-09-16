package com.igame.work.checkpoint.guanqia.handler;


import com.google.common.collect.Lists;
import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.core.log.GoldLog;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.work.checkpoint.guanqia.CheckPointService;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.checkpoint.guanqia.data.CheckPointTemplate;
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
public class CheckSaoDangHandler extends ReconnectedHandler {
	@Inject private CheckPointService checkPointService;
	@Inject private ResourceService resourceService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int chapterId = jsonObject.getInt("chapterId");
		vo.addData("chapterId", chapterId);

		//入参校验
		CheckPointTemplate ct = checkPointService.checkPointData.getTemplate(chapterId);
		if(ct == null || !player.hasCheckPoint(String.valueOf(chapterId))){
	    	GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
	    			+"#act:cheat" + "#type:saoC#chapterId:"+chapterId);
			return error(ErrorCode.CHECKPOINT_END_ERROR);
		}

		//扫荡券校验
		if(player.getSao() < 1){
			return error(ErrorCode.SAO_NOT_ENOUGH);
		}

		//体力校验
		if(player.getPhysical() < ct.getPhysical()){
			return error(ErrorCode.PHYSICA_NOT_ENOUGH);
		}

		//校验挑战次数
		if (player.getPlayerCount().getCheckPoint(ct.getChapterType(),chapterId) <= 0){
			return error(ErrorCode.CHECKCOUNT_NOT_ENOUGH);//该关卡今日挑战次数不足
		}

		//扣除扫荡券
		resourceService.addSao(player,1);

		//扣除体力
		resourceService.addPhysica(player, 0-ct.getPhysical());

		//减少可挑战次数
		player.getPlayerCount().addCheckPoint(player,ct,-1);

		//增加奖励
		RewardDto reward = checkPointService.getReward(player, chapterId, 1,false, 0);
		resourceService.addRewarToPlayer(player,reward);

		//增加人物经验
		int playerExp = reward.getExp();
		resourceService.addExp(player, playerExp);

		//增加怪兽经验
		List<Monster> ll = Lists.newArrayList();
		String monsterExpStr = "";
		monsterExpStr = checkPointService.getString(player, reward, ll, monsterExpStr);

		MessageUtil.notifyMonsterChange(player, ll);

		//怪物经验字符串
		if(monsterExpStr.lastIndexOf(";") >0){
			monsterExpStr = monsterExpStr.substring(0,monsterExpStr.lastIndexOf(";"));
		}

		//奖励字符串
		String rr = resourceService.getRewardString(reward);

		vo.addData("playerExp", playerExp);
		vo.addData("monsterExp", monsterExpStr);
		vo.addData("checkReward", rr);

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.CHECKPOINT_SAO;
	}

}
