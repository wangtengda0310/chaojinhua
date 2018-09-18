package com.igame.work.checkpoint.mingyunZhiMen.handler;


import com.google.common.collect.Lists;
import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.work.checkpoint.mingyunZhiMen.GateDto;
import com.igame.work.checkpoint.mingyunZhiMen.GateService;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public class GateHandler extends ReconnectedHandler {
	@Inject GateService gateService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		//校验等级
//		if(player.getPlayerLevel() <18){
//			return error(ErrorCode.LEVEL_NOT);
//		}

		//校验是否可挑战
		if(player.getFateData().getGetReward() == 1){
			return error(ErrorCode.GATE_NOT);
		}

		//合法性校验
		if(player.getFateData().getTempBoxCount() != -1){
			return error(ErrorCode.ERROR);
		}


		List<GateDto> ls = Lists.newArrayList();
        ls = gateService.getGateDtos(player, ls);
        if(player.getFateData().getTempBoxCount() > 0){
			MessageUtil.notifyDeInfoChange(player);
		}
		if(ls.isEmpty()){//说明已经达到昨天最高门,但都没有随机到特殊门
			ls = gateService.createGate(player);
		}
		player.getFateData().setGate(ls);

		gateService.getMingZheng(player).clear();

		// todo extract method
		long[] teamMonster = player.getTeams().get(player.getCurTeam()).getTeamMonster();
		for (int i = 0; i < teamMonster.length; i++) {
			Monster m = player.getMonsters().get(teamMonster[i]);
			MatchMonsterDto mto = new MatchMonsterDto(m, i);
			mto.reCalGods(player.callFightGods(), null);
			gateService.getMingZheng(player).put(mto.getObjectId(),mto);
		}

		vo.addData("gateInfo", ls);

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.GATE_INFO;
	}

	public void afterPlayerLogin(Player player) {
		player.getFateData().setTodayFateLevel(1);
		player.getFateData().setTodayBoxCount(0);
		player.getFateData().setTempBoxCount(-1);
		player.getFateData().setTempSpecialCount(0);
		player.getFateData().setAddRate(0);
	}
}
