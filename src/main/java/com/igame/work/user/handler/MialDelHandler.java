package com.igame.work.user.handler;


import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.user.dto.Mail;
import com.igame.work.user.dto.Player;
import com.igame.work.user.service.MailService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MialDelHandler extends ReconnectedHandler {
	@Inject MailService mailService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int id = jsonObject.getInt("id");
		Mail mail = mailService.getMails(player).get(id);
		if(mail == null){
			return error(ErrorCode.ERROR);
		}else{
			mail.setDtate(3);
		}

		vo.addData("id", id);

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.MAIL_DEL;
	}

}
