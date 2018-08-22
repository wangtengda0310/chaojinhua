package com.igame.work.monster.handler;






import com.igame.work.monster.MonsterDataManager;
import net.sf.json.JSONObject;

import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TuJianHandler extends BaseHandler{
	

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
		String meetM = jsonObject.getString("meetM");
		int ret = 0;
		if(!MyUtil.isNullOrEmpty(meetM)){
			boolean change = false;
			for(String id :meetM.split(",")){
				int mid = Integer.parseInt(id);
				if(MonsterDataManager.MONSTER_DATA.getMonsterTemplate(mid) != null && !player.getMeetM().contains(mid)){
					player.getMeetM().add(mid);
					change = true;
				}
			}
			if(change){
				MessageUtil.notiyMeetM(player);
			}
		}

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}

		send(MProtrol.toStringProtrol(MProtrol.MEET_NEW_MONSTER), vo, user);
	}

	
}
