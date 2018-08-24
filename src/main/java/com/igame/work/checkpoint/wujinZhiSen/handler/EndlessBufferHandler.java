package com.igame.work.checkpoint.wujinZhiSen.handler;


import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.monster.dto.WuEffect;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class EndlessBufferHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		String buffer = jsonObject.getString("buffer");
		String[] buffers = buffer.split(",");
		if(player.getWuEffect().size() == 0 || !MyUtil.vlaidString(buffer, "101,105,127,107")){
			return error(ErrorCode.ERROR);
		}else{
			if(buffers.length != player.getWuEffect().size()){
				return error(ErrorCode.WUBUFFER_ERROR);
			}else{
				if(player.getDiamond() < 20){
					return error(ErrorCode.DIAMOND_NOT_ENOUGH);
				}else{
					ResourceService.ins().addDiamond(player, -20);
					player.getWuEffect().clear();
					for(String bu : buffers){
						player.getWuEffect().add(new WuEffect(Integer.parseInt(bu)));
					}
				}

			}
		}

		MessageUtil.notifyWuBufferChange(player,player.getWuEffect());

		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.WU_BUFFER;
	}

}
