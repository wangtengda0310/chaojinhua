package com.igame.work.mail;


import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.MProtrol;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MaiSysSendHandler extends ReconnectedHandler {


	@Inject private MailService mailService;

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

		mailService.senderMail(player.getSeverId(), playerId, type, 1, "系统", title, content, attach);

		return new RetVO();
	}

	@Override
    public int protocolId() {
		return MProtrol.MAIL_NEW_SYS;
	}

}