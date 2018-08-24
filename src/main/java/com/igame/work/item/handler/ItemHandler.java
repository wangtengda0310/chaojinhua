package com.igame.work.item.handler;


import com.igame.core.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class ItemHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		
		RetVO vo = new RetVO();
		
		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int itemId = jsonObject.getInt("itemId");
		int count = jsonObject.getInt("count");
		int targetType = jsonObject.getInt("targetType");
		long targetId = jsonObject.getLong("targetId");
		
		int ret = ResourceService.ins().useItem(player, itemId, count, targetType, targetId);
		
		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("itemId", itemId);
		vo.addData("count", count);
		vo.addData("targetType", targetType);
		vo.addData("targetId", targetId);
//		vo.addData("test", player.getMonsters().get(targetId).ff);

		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.ITEM_USE;
	}
}
