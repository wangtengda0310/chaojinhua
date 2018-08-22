package com.igame.work.monster.handler;





import com.igame.work.monster.MonsterDataManager;
import net.sf.json.JSONObject;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.work.monster.data.StrengthenRouteTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TongHuaInfoHandler extends BaseHandler{
	

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
		String unlocks = "";
		int ret = 0;

		if(player.getTonghua() == null){
			ret = ErrorCode.LEVEL_NOT_ENOUGH;
		}else{
			player.getTonghua().calRefLeftTime();
			if(player.getTonghua().getTimeIndex() >0 && player.getTonghua().getStartTime() >0 && player.getTonghua().calLeftTime() <= 0){
				String[] tss = player.getTonghua().getTongStr().split(";");
				String[] t = tss[player.getTonghua().getTimeIndex()-1].split(",");
				t[1] = "2";
				tss[player.getTonghua().getTimeIndex()-1] = MyUtil.toString(t, ",");
				
				
				StrengthenRouteTemplate srt = MonsterDataManager.StrengthenRouteData.getTemplate(player.getTonghua().getTimeIndex());
				if(srt != null && !"1".equals(t[0])){//怪物关卡或者是倒计时还在，不能解锁下面
					String points = null;
					for(String temp : srt.getCoordinate().split(";")){
						if(temp.split(":")[0].equals(String.valueOf(player.getTonghua().getTimeIndex() -1))){
							points = temp;
							break;
						}
					}
					if(points != null){
						String unlock = points.split(":")[2];
						if(unlock != null){
							String[] unls = unlock.split(",");
							for(String un : unls){
								String[] t1 = tss[Integer.parseInt(un)].split(",");
								if("-1".equals(t1[1])){
									t1[1] = "0";
									tss[Integer.parseInt(un)] = MyUtil.toString(t1, ",");
									unlocks += ("," + (Integer.parseInt(un) +1));
								}
							}
						}
					}
					if(unlocks.length() > 0){
						unlocks = unlocks.substring(1);
					}
				}
				player.getTonghua().setTongStr(MyUtil.toString(tss, ";"));
				player.getTonghua().setStartTime(0);
				player.getTonghua().setTimeCount(0);
				player.getTonghua().setTimeIndex(0);
				
			}
		}
		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("tongInfo", player.getTonghua());

		send(MProtrol.toStringProtrol(MProtrol.TONGHUA_INFO), vo, user);
	}

	
}
