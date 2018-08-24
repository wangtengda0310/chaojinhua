package com.igame.work.gm.handler;


import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.gm.service.GMService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;


/**
 * 
 * @author Marcus.Z
 *
 */
public class GMHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		
		RetVO vo = new RetVO();
		
		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		String gm = jsonObject.getString("gm");
		boolean succ = GMService.processGM(player, gm);

		if(!succ){
			vo.setState(1);
			vo.setErrCode(1);
		}
		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.GM;
	}

}
