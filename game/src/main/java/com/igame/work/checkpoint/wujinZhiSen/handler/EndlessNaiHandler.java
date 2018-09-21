package com.igame.work.checkpoint.wujinZhiSen.handler;


import com.igame.core.di.Inject;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.guanqia.CheckPointService;
import com.igame.work.checkpoint.wujinZhiSen.EndlessService;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class EndlessNaiHandler extends ReconnectedHandler {

	@Inject
	private ResourceService resourceService;
	@Inject
	private EndlessService endlessService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		if(player.getWuNai() >= 1){
			return error(ErrorCode.NAI_ERROR);
		}else{
			if(player.getDiamond() < 50){
				return error(ErrorCode.DIAMOND_NOT_ENOUGH);
			}else{
				if(endlessService.isFullWuHp(player)){
					return error(ErrorCode.WUZHENG_HPFULL);
				}else{
					player.setWuNai(1);
					resourceService.addDiamond(player, -50);
			    	for(MatchMonsterDto mto : player.getWuZheng().values()){
			    		mto.setHp(mto.getHpInit());
			    	}
				}

			}

		}

		endlessService.notifyWuNaiChange(player);
		endlessService.notifyWuZhengChange(player);

		return new RetVO();
	}

	@Override
    public int protocolId() {
		return MProtrol.WU_NAI;
	}

}
