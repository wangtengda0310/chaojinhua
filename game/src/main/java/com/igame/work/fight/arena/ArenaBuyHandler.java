package com.igame.work.fight.arena;


import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author xym
 *
 * 	购买竞技场挑战次数
 *
 */
public class ArenaBuyHandler extends ReconnectedHandler {

	private ResourceService resourceService;

	@Override
    public int protocolId() {
		return MProtrol.AREA_BUY;
	}

	@Override
	public RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		//校验钻石
		int diamond = player.getDiamond();
		if (diamond < 50){
			return error(ErrorCode.DIAMOND_NOT_ENOUGH);
		}

		//减少钻石
		resourceService.addDiamond(player,-25);

		//减少已挑战
		player.addAreaCount(-5);

		vo.addData("areaCount",player.getAreaCount());
		return vo;
	}

	
}
