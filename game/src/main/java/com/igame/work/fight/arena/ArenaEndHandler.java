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

		long opponentPlayerId = arenaService.getOpponent(player);
		//异常校验
		if(opponentPlayerId == 0){
			return error(ErrorCode.ERROR);
		}

		//处理排行信息
		int myRank = arenaService.getPlayerRank(player.getPlayerId());
		if(win == 1){	// 挑战失败吧玩家的当前排位返回给客户端
            ArenaRanker self = arenaService.getRankInfo(player);

			ArenaRanker opponent = arenaService.getChallenge(player).stream()
					.filter(r->r.getPlayerId()== opponentPlayerId)
					.findAny().orElse(null);

			synchronized (getLockByPlayer(player.getSeverId())) {

				if(opponent != null){
					int opponentRank = opponent.getRank();	// todo arenaService.getPlayerRank(opponent.getPlayerId());
					self.setRank(opponentRank);
					opponent.setRank(myRank);

					arenaService.setPlayerRank(player.getPlayerId(), self.getRank());
					myRank = self.getRank();

					fireEvent(player, PlayerEvents.ARENA_RANK, null);

				}
				ArenaService.setPrepareToSave(true);
			}
		}

		vo.addData("playerId", opponentPlayerId);
		vo.addData("myRank", myRank);

		return vo;
	}

	@Override
	public int protocolId() {
		return MProtrol.AREA_END;
	}

}
