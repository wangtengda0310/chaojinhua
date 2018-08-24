package com.igame.work.checkpoint.mingyunZhiMen.handler;


import com.google.common.collect.Lists;
import com.igame.core.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.system.RankService;
import com.igame.work.system.Ranker;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public class GateRankHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int myMrank;
		int myScore;
//		if(player.getPlayerLevel() <18){
//			ret = ErrorCode.LEVEL_NOT;
//		}else{
		myMrank = RankService.ins().getMRank(player);
		myScore = player.getFateData().getTodayBoxCount();

//		}

		vo.addData("myMrank", myMrank);
		vo.addData("myScore", myScore);
		List<Ranker> mr = RankService.ins().getRankList().get(player.getSeverId());
		if(mr == null){
			mr =Lists.newArrayList();
		}
		vo.addData("mRank", mr);

		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.GATE_RANKS;
	}

}
