package com.igame.work.monster.handler;


import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.work.monster.service.MonsterService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TuJianHandler extends ReconnectedHandler {
	@Inject private MonsterService monseterService;
	@Inject private MonsterService monsterService;


	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		String meetM = jsonObject.getString("meetM");
		if(!MyUtil.isNullOrEmpty(meetM)){
			boolean change = monsterService.isChange(player, meetM);
			if(change){
				MessageUtil.notifyMeetM(player);
			}
		}

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.MEET_NEW_MONSTER;
	}

}
