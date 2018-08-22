package com.igame.work.user.handler;






import net.sf.json.JSONObject;

import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.user.dto.Mail;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MialReadHandler extends BaseHandler{
	

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
		String id = jsonObject.getString("id");
		int ret = 0;
		if(!MyUtil.isNullOrEmpty(id)){
			String[] ids = id.split(",");
			if(ids != null){
				for(String temp : ids){
					Mail mail = player.getMail().get(Integer.parseInt(temp));
					if(mail != null){
						mail.setState(1);
						mail.setDtate(2);						
					}
				}
			}
		}

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("id", id);

		send(MProtrol.toStringProtrol(MProtrol.MAIL_READ), vo, user);
	}

	
}
