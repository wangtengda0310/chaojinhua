package com.igame.work.checkpoint.wujinZhiSen.handler;


import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.checkpoint.wujinZhiSen.EndlessService;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.Arrays;

/**
 * 
 * @author Marcus.Z
 *
 */
public class EndlessZhenHandler extends ReconnectedHandler {


	@Inject private EndlessService endlessService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		if(!player.getWuZheng().isEmpty()){
			return error(ErrorCode.WUZHENG_ERROR);
		}else{
			long count = Arrays.stream(player.getTeams().get(player.getCurTeam()).getTeamMonster()).filter(mid -> mid > 0).count();
			if (count < 4) {
				return error(ErrorCode.WUZHENG_COUNT_ERROR);
			}

			// todo extract method
			long[] teamMonster = player.getTeams().get(player.getCurTeam()).getTeamMonster();
			for (int i = 0; i < teamMonster.length; i++) {
				Monster m = player.getMonsters().get(teamMonster[i]);
				MatchMonsterDto mto = new MatchMonsterDto(m, i);
				mto.reCalGods(player.currentFightGods(), null);
				player.getWuZheng().put(mto.getObjectId(),mto);
			}

		}

		endlessService.notifyWuZhengChange(player);

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.WUZHENG_YES;
	}

}
