package com.igame.work.user.handler;


import com.igame.core.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.user.dto.Mail;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MialReadHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		String id = jsonObject.getString("id");

		if(!MyUtil.isNullOrEmpty(id)){
			String[] ids = id.split(",");
			for(String temp : ids){
				Mail mail = player.getMail().get(Integer.parseInt(temp));
				if(mail != null){
					mail.setState(1);
					mail.setDtate(2);
				}
			}
		}

		vo.addData("id", id);

		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.MAIL_READ;
	}

}
