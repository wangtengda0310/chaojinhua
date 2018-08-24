package com.igame.work.checkpoint.mingyunZhiMen.handler;


import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.mingyunZhiMen.FateDto;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class DeInfoHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		if(player.getPlayerLevel() <18){
//			ret = ErrorCode.ERROR;
		}else{


		}

		vo.addData("desInfo", FateDto.creatFateDto(player.getFateData()));

		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.DE_INFO;
	}

}
