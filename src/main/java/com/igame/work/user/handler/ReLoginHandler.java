package com.igame.work.user.handler;


import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;


/**
 * 
 * @author Marcus.Z
 *
 */
public class ReLoginHandler extends BaseHandler{
	

	@Override
	public void handleClientRequest(User user, ISFSObject params) {


		Player player =  SessionManager.ins().getSession(Long.parseLong(user.getName()));
		if(player != null){
			User old = getApi().getUserById(player.getUser().getId());
			player.setUser(user);
		}
		RetVO vo = new RetVO();
		sendClient(MProtrol.toStringProtrol(MProtrol.RE_CONN), vo, user);
	}

}
