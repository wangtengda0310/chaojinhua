package com.igame.work.user.handler;


import com.igame.core.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.service.MailService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MiaSysSendHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

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

		MailService.ins().senderMail(player.getSeverId(), playerId, type, 1, "系统", title, content, attach);

		return new RetVO();
	}

	@Override
	protected int protocolId() {
		return MProtrol.MAIL_NEW_SYS;
	}

}
