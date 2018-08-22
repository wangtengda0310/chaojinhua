package com.igame.work.fight.handler;



import java.util.Collections;
import java.util.List;

import net.sf.json.JSONObject;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.fight.dto.AreaRanker;
import com.igame.work.fight.service.ArenaService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class AreaEndHandler extends BaseHandler{
	

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

		int win = jsonObject.getInt("win");
		vo.addData("win", win);

		//异常校验
		if(player.getTempAreaPlayerId() == 0){
			sendError(ErrorCode.ERROR,MProtrol.toStringProtrol(MProtrol.AREA_END), vo, user);
			return;
		}

		//处理排行信息
		int myRank = player.getMyRank();
		List<AreaRanker> rank = ArenaService.ins().getRank(player.getAreaType(), player.getSeverId());
		if(win == 1){
			synchronized (ArenaService.ins().getLockByPlayer(player.getSeverId())) {

				AreaRanker self = null;
				AreaRanker oter = null;
				for(AreaRanker ar : player.getTempOpponent()){
					if(ar.getPlayerId() == player.getTempAreaPlayerId()){
						oter = ar;
						break;
					}
				}

				for(AreaRanker ar : rank){
					if(ar.getPlayerId() == player.getPlayerId()){
						self = ar;
						break;
					}
				}

				if(oter != null){
					int selfRank = player.getMyRank();
					int otherRank = oter.getRank();
					if(self == null){
						self = new AreaRanker(player.getPlayerId(), selfRank, player.getNickname(), player.getTeams().get(6).getFightValue(),
								player.getSeverId());
						rank.add(self);
					}
					self.setRank(otherRank);
					oter.setRank(selfRank);
					player.setMyRank(self.getRank());
					myRank = player.getMyRank();

					Collections.sort(rank);
					ArenaService.ins().addPlayerRobotDto(player,false);

				}
				ArenaService.ins().setUp(true);
			}
		}

		vo.addData("playerId", player.getTempAreaPlayerId());
		vo.addData("myRank", myRank);

		sendSucceed(MProtrol.toStringProtrol(MProtrol.AREA_END), vo, user);
	}

	
}
