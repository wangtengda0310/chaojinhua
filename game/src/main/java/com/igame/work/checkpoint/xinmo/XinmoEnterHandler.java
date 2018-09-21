package com.igame.work.checkpoint.xinmo;


import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
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


	private RobotService robotService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int chapterId = jsonObject.getInt("chapterId");
		XingMoDto xingMoDto = player.getXinMo().get(chapterId);
		if(xingMoDto == null){
			return error(ErrorCode.ERROR);
		}else if(xingMoDto.calLeftTime(System.currentTimeMillis()) <= 0){
			return error(ErrorCode.XINGMO_LEAVEL);
		}else{
			Map<String,RobotDto> ro = robotService.getRobot();
			if(ro == null || ro.get(xingMoDto.getMid()) == null){
				return error(ErrorCode.ERROR);
			}			
		}

		vo.addData("chapterId", chapterId);

		return vo;
	}

	@Override
	public int protocolId() {
		return MProtrol.XINGMO_ENTER;
	}

}
