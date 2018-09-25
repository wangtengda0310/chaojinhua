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
import com.igame.work.quest.service.QuestService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public class GateEnterHandler extends ReconnectedHandler {

	@Inject
	private QuestService questService;
	@Inject
	private GateService gateService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int gateId = jsonObject.getInt("gateId");

		//校验等级
//		if(player.getPlayerLevel() <18){
//			return error(ErrorCode.LEVEL_NOT);
//		}

		//校验是否领取
		if(player.getFateData().getGetReward() == 1){
			return error(ErrorCode.GATE_NOT);
		}

		//异常校验
		GateDto gto = player.getFateData().getGate(gateId);
		if(gto == null){
			return error(ErrorCode.GATE_NOT);
		}

		player.getFateData().setGateId(gateId);
		List<GateDto> ls = Lists.newArrayList();
		List<MatchMonsterDto> lb = Lists.newArrayList();
		int act = 0;
		if(player.getFateData().getTodayFateLevel() < player.getFateData().getFateLevel()){//还在快速选门之中，未达到最高门
			ls = gateService.getGateDtos(player, ls);
			if(player.getFateData().getTempBoxCount() > 0){
				MessageUtil.notifyDeInfoChange(player);
			}
			if(ls.isEmpty()){//说明已经达到昨天最高门,但都没有随机到特殊门
				ls = gateService.createGate(player);
			}
			player.getFateData().setGate(ls);
		}else{

			if(gto.getType() != 0){//buffer
				act = 1;
				player.getFateData().addTempBoxCount(gto.getBoxCount());//加宝箱
				player.getFateData().addTodayFateLevel();//到下一层
				List<GateDto> gls = gateService.createGate(player);//创建新的门
				player.getFateData().setGate(gls);
				MessageUtil.notifyDeInfoChange(player);
				MessageUtil.notifyGateChange(player);
				questService.processTask(player, 10, 1);
			}else {//普通怪物
				act = 0;

				// todo extract method
				gto.getMons().forEach((mid, m) -> {
					int i = mid.intValue();
					MatchMonsterDto mto = new MatchMonsterDto(m, i);
					mto.reCalGods(player.currentFightGods(), null);
					lb.add(mto);
				});

			}
		}

		int type = gto.getType();

		vo.addData("act", act);
		vo.addData("type", type);
		vo.addData("m", lb);
		if(act == 0){
			vo.addData("a", gateService.getMingZheng(player).values());
		}else{
			vo.addData("a", Lists.newArrayList());
		}

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.GATE_ENTER;
	}

}
