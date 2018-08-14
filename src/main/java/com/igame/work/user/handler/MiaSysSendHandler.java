package com.igame.work.user.handler;






import net.sf.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.ExchangedataTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.core.log.GoldLog;
import com.igame.dto.RetVO;
import com.igame.work.user.dto.Mail;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.igame.work.user.service.MailService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MiaSysSendHandler extends BaseHandler{
	

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
		long playerId = jsonObject.getLong("playerId");
		int type = jsonObject.getInt("type");
		String title = jsonObject.getString("title");
		String content = jsonObject.getString("content");
		String attach = jsonObject.getString("attach");
		if(title.length() > 50){
			title = title.substring(0, 50);
		}
		if(content.length() > 300){
			content = content.substring(0, 300);
		}
		int ret = 0;
		Mail mail = MailService.ins().senderMail(player.getSeverId(), playerId, type, 1, "系统", title, content, attach);

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}

		send(MProtrol.toStringProtrol(MProtrol.MAIL_NEW_SYS), vo, user);
	}

	
}
