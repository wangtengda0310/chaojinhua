package com.igame.work.checkpoint.handler.mingyunZhiMen;








import java.util.List;

import net.sf.json.JSONObject;

import com.google.common.collect.Lists;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.system.RankService;
import com.igame.work.system.Ranker;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class GateRankHandler extends BaseHandler{
	

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
		int ret = 0;
		int myMrank = 0;
		int myScore = 0;
//		if(player.getPlayerLevel() <18){
//			ret = ErrorCode.LEVEL_NOT;
//		}else{
		myMrank = RankService.ins().getMRank(player);
		myScore = player.getFateData().getTodayBoxCount();

//		}

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("myMrank", myMrank);
		vo.addData("myScore", myScore);
		List<Ranker> mr = RankService.ins().getRankList().get(player.getSeverId());
		if(mr == null){
			mr =Lists.newArrayList();
		}
		vo.addData("mRank", mr);

		send(MProtrol.toStringProtrol(MProtrol.GATE_RANKS), vo, user);
	}

	
}
