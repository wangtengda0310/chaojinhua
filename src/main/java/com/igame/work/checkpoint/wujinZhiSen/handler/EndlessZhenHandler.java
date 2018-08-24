package com.igame.work.checkpoint.wujinZhiSen.handler;


import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.fight.dto.FightData;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class EndlessZhenHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		if(!player.getWuZheng().isEmpty()){
			return error(ErrorCode.WUZHENG_ERROR);
		}else{
			FightData fd = new FightData(player);
			if(fd.getMonsters().size() < 4){
				return error(ErrorCode.WUZHENG_COUNT_ERROR);
			}else{
		    	for(Monster m : fd.getMonsters().values()){
		    		MatchMonsterDto mto = new MatchMonsterDto(m);
					mto.reCalGods(player.callFightGods(), null);
		    		player.getWuZheng().put(mto.getObjectId(),mto);
		    	}
			}
		}

		MessageUtil.notiyWuZhengChange(player);

		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.WUZHENG_YES;
	}

}
