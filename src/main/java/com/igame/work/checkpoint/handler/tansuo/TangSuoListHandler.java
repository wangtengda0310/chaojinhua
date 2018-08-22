package com.igame.work.checkpoint.handler.tansuo;





import com.igame.work.checkpoint.GuanQiaDataManager;
import net.sf.json.JSONObject;

import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.work.checkpoint.data.TangSuoTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.checkpoint.dto.TangSuoDto;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TangSuoListHandler extends BaseHandler{
	

	@Override
	public void handleClientRequest(User user, ISFSObject params) {
		RetVO vo = new RetVO();
		if(reviceMessage(user,params,vo)){
			return;
		}
		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);
		Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
		if(player == null){
			this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
			return;
		}
		
		for(TangSuoTemplate ts : GuanQiaDataManager.TangSuoData.getAll()){
			if(MyUtil.hasCheckPoint(player.getCheckPoint(), String.valueOf(ts.getUnlock())) && player.getTangSuo().get(ts.getNum()) == null){
				player.getTangSuo().put(ts.getNum(), new TangSuoDto(ts));
			}
		}	
		

		long now = System.currentTimeMillis();
		player.getTangSuo().values().forEach(e -> e.calLeftTime(now));
		vo.addData("tang", player.getTangSuo().values());

		send(MProtrol.toStringProtrol(MProtrol.TANGSUO_LIST), vo, user);
	}

	
}
