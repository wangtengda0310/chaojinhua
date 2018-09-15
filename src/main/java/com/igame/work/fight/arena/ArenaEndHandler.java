package com.igame.work.fight.arena;


import com.google.common.collect.Maps;
import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.PlayerEvents;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class ArenaEndHandler extends ReconnectedHandler {
	@Inject private ArenaService arenaService;

	private Map<Integer, Object> lock = Maps.newHashMap();

	private Object getLockByPlayer(int severId) {

		return lock.computeIfAbsent(severId, k -> new Object());
	}


	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();
		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int win = jsonObject.getInt("win");
		vo.addData("win", win);

		//异常校验
		if(arenaService.getTempAreaPlayerId(player) == 0){
			return error(ErrorCode.ERROR);
		}

		//处理排行信息
		int myRank = arenaService.getPlayerRank(player.getPlayerId());
		List<ArenaRanker> rank = arenaService.getRank(player);
		if(win == 1){
			synchronized (getLockByPlayer(player.getSeverId())) {

				ArenaRanker self = null;
				ArenaRanker oter = null;
				for(ArenaRanker ar : arenaService.getTempOpponent(player)){
					if(ar.getPlayerId() == arenaService.getTempAreaPlayerId(player)){
						oter = ar;
						break;
					}
				}

				for(ArenaRanker ar : rank){
					if(ar.getPlayerId() == player.getPlayerId()){
						self = ar;
						break;
					}
				}

				if(oter != null){
					int selfRank = arenaService.getPlayerRank(player.getPlayerId());
					int otherRank = oter.getRank();
					if(self == null){
						self = new ArenaRanker(player.getPlayerId(), selfRank, player.getNickname(), player.getTeams().get(6).getFightValue());
						rank.add(self);
					}
					self.setRank(otherRank);
					oter.setRank(selfRank);
					arenaService.setPlayerRank(player.getPlayerId(), self.getRank());
					myRank = arenaService.getPlayerRank(player.getPlayerId());

					Collections.sort(rank);
					fireEvent(player, PlayerEvents.ARENA_RANK, null);

				}
				ArenaService.setUp(true);
			}
		}

		vo.addData("playerId", arenaService.getTempAreaPlayerId(player));
		vo.addData("myRank", myRank);

		return vo;
	}

	@Override
	public int protocolId() {
		return MProtrol.AREA_END;
	}

}
