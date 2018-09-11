package com.igame.work.user.handler;


import com.igame.core.di.Inject;
import com.igame.work.MProtrol;
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
public class MiaSendHandler extends ReconnectedHandler {


	@Inject private MailService mailService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		long playerId = jsonObject.getLong("playerId");
		String title = jsonObject.getString("title");
		String content = jsonObject.getString("content");
		if(title.length() > 50){
			title = title.substring(0, 50);
		}
		if(content.length() > 300){
			content = content.substring(0, 300);
		}

		mailService.senderMail(player.getSeverId(), playerId, 2, 2, player.getUsername(), title, content, null);

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.MAIL_NEW;
	}

}
