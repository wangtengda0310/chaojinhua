package com.igame.work.checkpoint.xingheZhiYan.handler;


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
 * 	购买星能(即星核之眼挑战次数)
 *
 */
public class TrialSaleHandler extends ReconnectedHandler {

	private ResourceService resourceService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		//校验钻石
		if (player.getDiamond() < 50){
			return error(ErrorCode.DIAMOND_NOT_ENOUGH);
		}

		//扣除钻石
		resourceService.addDiamond(player,-50);

		//增加星能
		resourceService.addXing(player,5);

		vo.addData("xing",player.getXing());

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.TRIAL_SALE;
	}

}