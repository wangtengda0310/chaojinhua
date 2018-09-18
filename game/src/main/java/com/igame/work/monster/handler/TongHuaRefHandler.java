package com.igame.work.monster.handler;


import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.work.user.PlayerService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TongHuaRefHandler extends ReconnectedHandler {

	@Inject private ResourceService resourceService;
	@Inject private PlayerService playerService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int type = jsonObject.getInt("type");
		if(type< 1 || type>2){
			return error(ErrorCode.ERROR);
		}else{
			if(type == 1){
				if(player.getTonghua().calRefLeftTime() >0){
					return error(ErrorCode.TONGHUA_CD_NOT);
				}else{
					player.setTonghua(playerService.getRandomTongHuaDto());
					player.getTonghua().setStartRefTime(System.currentTimeMillis());
				}
			}else{
				if(player.getDiamond() < 100){
					return error(ErrorCode.DIAMOND_NOT_ENOUGH);
				}else{
					resourceService.addDiamond(player, -100);
					player.setTonghua(playerService.getRandomTongHuaDto());
					player.getTonghua().setStartRefTime(System.currentTimeMillis());
				}
			}
		}

		MessageUtil.notifyTongHuaAddChange(player);
		player.getTonghua().calLeftTime();
		player.getTonghua().calRefLeftTime();
		vo.addData("type", type);
		vo.addData("tongInfo", player.getTonghua());

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.TONGHUA_CD_REF;
	}

}
