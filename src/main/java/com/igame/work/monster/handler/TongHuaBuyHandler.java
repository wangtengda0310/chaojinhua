package com.igame.work.monster.handler;


import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.monster.data.ExchangedataTemplate;
import com.igame.work.monster.service.MonsterService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TongHuaBuyHandler extends ReconnectedHandler {

	@Inject
	private MonsterService monseterService;
	@Inject
	private ResourceService resourceService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int type = jsonObject.getInt("type");
		int tongBuyCount = player.getTongAdd().getTongBuyCount();

		ExchangedataTemplate et = monseterService.exchangeData.getTemplate(2+"_"+type);
		if(et == null){
			return error(ErrorCode.ERROR);
		}else{
			if(tongBuyCount>=3){
				return error(ErrorCode.BUY_COUNT_NOT_ENOUGH);
			}else{
				if(player.getDiamond() < et.getGem()){
					return error(ErrorCode.GOLD_NOT_ENOUGH);
				}else{
					resourceService.addTongRes(player, et.getExchange_value());
					resourceService.addDiamond(player, 0-et.getGem());
					player.getTongAdd().setTongBuyCount(player.getTongAdd().getTongBuyCount() +1);
					tongBuyCount = player.getTongAdd().getTongBuyCount();
				}
			}
		}

		vo.addData("type", type);
		vo.addData("tongBuyCount", tongBuyCount);

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.TONGHUAINFO_BUY;
	}

}
