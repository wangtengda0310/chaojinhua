package com.igame.work.checkpoint.xinmo;


import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.RobotDto;
import com.igame.work.user.service.RobotService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class XinmoEnterHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int chapterId = jsonObject.getInt("chapterId");
		if(player.getXinMo().get(chapterId) == null){
			return error(ErrorCode.ERROR);
		}else if(player.getXinMo().get(chapterId) != null && player.getXinMo().get(chapterId).calLeftTime(System.currentTimeMillis()) <= 0){
			return error(ErrorCode.XINGMO_LEAVEL);
		}else{
			Map<String,RobotDto> ro = RobotService.ins().getRobot().get(player.getSeverId());
			if(ro == null || ro.get(player.getXinMo().get(chapterId).getMid()) == null){
				return error(ErrorCode.ERROR);
			}			
		}

		vo.addData("chapterId", chapterId);

		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.XINGMO_ENTER;
	}

}
