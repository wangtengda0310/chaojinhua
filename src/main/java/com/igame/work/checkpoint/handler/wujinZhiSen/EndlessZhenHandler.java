package com.igame.work.checkpoint.handler.wujinZhiSen;







import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.fight.dto.FightData;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class EndlessZhenHandler extends BaseHandler{
	

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
		int ret = 0;
		if(!player.getWuZheng().isEmpty()){
			ret = ErrorCode.WUZHENG_ERROR;
		}else{
			FightData fd = new FightData(player);
			if(fd.getMonsters().size() < 4){
				ret = ErrorCode.WUZHENG_COUNT_ERROR;
			}else{
		    	for(Monster m : fd.getMonsters().values()){
		    		MatchMonsterDto mto = new MatchMonsterDto(m);
					mto.reCalGods(player.callFightGods(), null);
		    		player.getWuZheng().put(mto.getObjectId(),mto);
		    	}
			}
		}

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}else{
			MessageUtil.notiyWuZhengChange(player);
		}

		send(MProtrol.toStringProtrol(MProtrol.WUZHENG_YES), vo, user);
	}

	
}
