package com.igame.work.monster.handler;



import net.sf.json.JSONObject;

import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MonsterLockHandler extends BaseHandler{
	

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
		
		long objectId = jsonObject.getLong("objectId");
		int lock = jsonObject.getInt("lock");
		int ret = 0;
		Monster mm = player.getMonsters().get(objectId);
		if(mm == null){
			ret = 1;
		}else{
			if(lock == 1){
				mm.setIsLock(true);
			}else{
				mm.setIsLock(false);
			}
			mm.setDtate(2);
		}
//		List<Monster> ll = Lists.newArrayList();
//		ll.add(mm);
//		MessageUtil.notiyMonsterChange(player, ll);
		
		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("objectId", objectId);
		vo.addData("lock", mm.getIsLock());

		send(MProtrol.toStringProtrol(MProtrol.MONSTER_LOCK), vo, user);
	}

	
}
