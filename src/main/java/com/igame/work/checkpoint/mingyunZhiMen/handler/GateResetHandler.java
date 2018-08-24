package com.igame.work.checkpoint.mingyunZhiMen.handler;


import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.mingyunZhiMen.GateDto;
import com.igame.work.checkpoint.mingyunZhiMen.GateService;
import com.igame.work.fight.dto.FightData;
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
public class GateResetHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		List<GateDto> ls;
//		if(player.getPlayerLevel() <18){
//			ret = ErrorCode.LEVEL_NOT;
//		}else{
			if(player.getFateData().getGetReward() == 1){
				return error(ErrorCode.GATE_NOT);
			}else{
				player.getFateData().setTodayFateLevel(1);		
				player.getFateData().setTempBoxCount(-1);
				player.getFateData().setTempSpecialCount(0);
				player.getFateData().setAddRate(0);
				ls = GateService.creatGate(player);
				player.getFateData().setGate(ls);
//				MessageUtil.notifyDeInfoChange(player);
				MessageUtil.notifyGateChange(player);
				
				FightData fd = new FightData(player);
				player.getMingZheng().clear();
		    	for(Monster m : fd.getMonsters().values()){
		    		MatchMonsterDto mto = new MatchMonsterDto(m);
					mto.reCalGods(player.callFightGods(),null);
		    		player.getMingZheng().put(mto.getObjectId(),mto);
		    	}
				
			}

//		}

		return new RetVO();
	}

	@Override
	protected int protocolId() {
		return MProtrol.GATE_RESET;
	}

}
