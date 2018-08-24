package com.igame.work.monster.handler;


import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MonsterLockHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);
		
		long objectId = jsonObject.getLong("objectId");
		int lock = jsonObject.getInt("lock");
		Monster mm = player.getMonsters().get(objectId);
		if(mm == null){
			return error(ErrorCode.ERROR);
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
//		MessageUtil.notifyMonsterChange(player, ll);

		vo.addData("objectId", objectId);
		vo.addData("lock", mm.getIsLock());

		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.MONSTER_LOCK;
	}

}
