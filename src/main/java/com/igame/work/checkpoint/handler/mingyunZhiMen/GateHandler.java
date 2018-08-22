package com.igame.work.checkpoint.handler.mingyunZhiMen;


import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.checkpoint.dto.GateDto;
import com.igame.work.checkpoint.service.CheckPointService;
import com.igame.work.fight.dto.FightData;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public class GateHandler extends BaseHandler{
	

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

		//校验等级
//		if(player.getPlayerLevel() <18){
//			sendError(ErrorCode.LEVEL_NOT,MProtrol.toStringProtrol(MProtrol.GATE_INFO), vo, user);
//			return;
//		}

		//校验是否可挑战
		if(player.getFateData().getGetReward() == 1){
			sendError(ErrorCode.GATE_NOT,MProtrol.toStringProtrol(MProtrol.GATE_INFO), vo, user);
			return;
		}

		//合法性校验
		if(player.getFateData().getTempBoxCount() != -1){
			sendError(ErrorCode.ERROR,MProtrol.toStringProtrol(MProtrol.GATE_INFO), vo, user);
			return;
		}


		List<GateDto> ls = Lists.newArrayList();
		for(int level = player.getFateData().getTodayFateLevel();level <= player.getFateData().getFateLevel();level++){
			List<GateDto> temp = CheckPointService.creatGate(player);
			boolean special = false;
			for(GateDto gto : temp){
				if(gto.getType() != 0){//怪物关卡直接获得宝箱
					special = true;//随机到特殊关卡就展示门
					break;
				}
			}
			if(special){
				ls = temp;
			}else{
				player.getFateData().addTempBoxCount(2);
			}
			player.getFateData().setTodayFateLevel(level);
			if(!ls.isEmpty()){//随机到特殊关卡就展示门
				break;
			}
		}
		if(player.getFateData().getTempBoxCount() > 0){
			MessageUtil.notiyDeInfoChange(player);
		}
		if(ls.isEmpty()){//说明已经达到昨天最高门,但都没有随机到特殊门
			ls = CheckPointService.creatGate(player);
		}
		player.getFateData().setGate(ls);

		FightData fd = new FightData(player);
		player.getMingZheng().clear();
		for(Monster m : fd.getMonsters().values()){
			MatchMonsterDto mto = new MatchMonsterDto(m);
			mto.reCalGods(player.callFightGods(), null);
			player.getMingZheng().put(mto.getObjectId(),mto);
		}

		vo.addData("gateInfo", ls);

		sendSucceed(MProtrol.toStringProtrol(MProtrol.GATE_INFO), vo, user);
	}

	
}
