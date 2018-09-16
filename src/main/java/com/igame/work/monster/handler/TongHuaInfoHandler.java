package com.igame.work.monster.handler;


import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.monster.data.StrengthenRouteTemplate;
import com.igame.work.monster.service.MonsterService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TongHuaInfoHandler extends ReconnectedHandler {
	@Inject
	private MonsterService monseterService;
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		String unlocks = "";

		if(player.getTonghua() == null){
			return error(ErrorCode.LEVEL_NOT_ENOUGH);
		}else{
			player.getTonghua().calRefLeftTime();
			if(player.getTonghua().getTimeIndex() >0 && player.getTonghua().getStartTime() >0 && player.getTonghua().calLeftTime() <= 0){
				String[] tss = player.getTonghua().getTongStr().split(";");
				String[] t = tss[player.getTonghua().getTimeIndex()-1].split(",");
				t[1] = "2";
				tss[player.getTonghua().getTimeIndex()-1] = MyUtil.toString(t, ",");
				
				
				StrengthenRouteTemplate srt = monseterService.strengthenRouteData.getTemplate(player.getTonghua().getTimeIndex());
				if(srt != null && !"1".equals(t[0])){//怪物关卡或者是倒计时还在，不能解锁下面
					for(String temp : srt.getCoordinate().split(";")){
						if(temp.split(":")[0].equals(String.valueOf(player.getTonghua().getTimeIndex() -1))){
							break;
						}
					}
				}
				player.getTonghua().setTongStr(MyUtil.toString(tss, ";"));
				player.getTonghua().setStartTime(0);
				player.getTonghua().setTimeCount(0);
				player.getTonghua().setTimeIndex(0);
				
			}
		}
		vo.addData("tongInfo", player.getTonghua());

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.TONGHUA_INFO;
	}

}
