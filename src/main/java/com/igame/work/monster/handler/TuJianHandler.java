package com.igame.work.monster.handler;


import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.monster.MonsterDataManager;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TuJianHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		String meetM = jsonObject.getString("meetM");
		if(!MyUtil.isNullOrEmpty(meetM)){
			boolean change = false;
			change = isChange(player, meetM, change);
			if(change){
				MessageUtil.notifyMeetM(player);
			}
		}

		return vo;
	}

	public static boolean isChange(Player player, String meetM, boolean change) {
		for(String id :meetM.split(",")){
			int mid = Integer.parseInt(id);
			if(MonsterDataManager.MONSTER_DATA.getMonsterTemplate(mid) != null && !player.getMeetM().contains(mid)){
				player.getMeetM().add(mid);
				change = true;
			}
		}
		return change;
	}

	@Override
	protected int protocolId() {
		return MProtrol.MEET_NEW_MONSTER;
	}

}
