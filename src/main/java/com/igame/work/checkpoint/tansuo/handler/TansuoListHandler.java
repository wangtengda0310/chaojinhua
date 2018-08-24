package com.igame.work.checkpoint.tansuo.handler;


import com.igame.core.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.checkpoint.tansuo.TansuoDataManager;
import com.igame.work.checkpoint.tansuo.TansuoDto;
import com.igame.work.checkpoint.tansuo.TansuoTemplate;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TansuoListHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		for(TansuoTemplate ts : TansuoDataManager.TansuoData.getAll()){
			if(MyUtil.hasCheckPoint(player.getCheckPoint(), String.valueOf(ts.getUnlock())) && player.getTangSuo().get(ts.getNum()) == null){
				player.getTangSuo().put(ts.getNum(), new TansuoDto(ts));
			}
		}	
		

		long now = System.currentTimeMillis();
		player.getTangSuo().values().forEach(e -> e.calLeftTime(now));
		vo.addData("tang", player.getTangSuo().values());

		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.TANGSUO_LIST;
	}

}
