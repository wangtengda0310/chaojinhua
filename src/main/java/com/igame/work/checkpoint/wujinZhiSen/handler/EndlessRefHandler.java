package com.igame.work.checkpoint.wujinZhiSen.handler;


import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.guanqia.CheckPointService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class EndlessRefHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int ret;
		if(player.getWuReset() >= 1){//免费次数已用完
			if(player.getDiamond()<100){
				return vo;
			}else{
				ret = CheckPointService.refEndlessRef(player);
				ResourceService.ins().addDiamond(player, -100);
			}
		}else{
			ret = CheckPointService.refEndlessRef(player);
			player.setWuReset(1);
			MessageUtil.notifyWuResetChange(player);
		}

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}else{
			MessageUtil.notifyWuChange(player);
			MessageUtil.notifyWuZhengChange(player);
			MessageUtil.notifyWuNaiChange(player);
			MessageUtil.notifyWuBufferChange(player,player.getWuEffect());
			MessageUtil.notifyWuResetChange(player);
		}

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.WU_REF;
	}

}
