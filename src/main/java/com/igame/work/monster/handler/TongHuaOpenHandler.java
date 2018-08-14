package com.igame.work.monster.handler;



import net.sf.json.JSONObject;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.StrengthenRouteTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.TongHuaDto;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TongHuaOpenHandler extends BaseHandler{
	

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
		long now = System.currentTimeMillis();
		int index = jsonObject.getInt("index");
		String info = "";
		int timeIndex = 0;
		long leftTime = 0;
		int ret = 0;
		String unlocks = "";
		TongHuaDto tdo = player.getTonghua();
		if(tdo == null){
			ret = ErrorCode.ERROR;
		}else{
			String[] tss = tdo.getTongStr().split(";");
			if(index< 1 || index>tss.length){
				ret = ErrorCode.ERROR;
			}else{
				String ts = tss[index-1];//"type,state,rewardtype,rewardId,count"
				String[] t = ts.split(",");
				if(!"0".equals(t[1]) || (tdo.getStartTime() > 0 && tdo.calLeftTime() >0)){
					ret = ErrorCode.TONGHUA_NOTOPEN;//该点不可打开
				}else{
					if(player.getTongRes() < 1){
						ret = ErrorCode.TONGHUA_NOT_ENOUGH;//同化资源不够
					}else{
						if("0".equals(t[0])){//普通
							t[1] = "2";
						}else if("1".equals(t[0])){//怪物
							tdo.setStartTime(now);
							tdo.setTimeCount(60);
							t[1] = "1";
							tdo.setTimeIndex(index);
							timeIndex = index;
							leftTime = 60 * 60;
						}else{
							tdo.setStartTime(now);
							tdo.setTimeCount(120);
							t[1] = "1";
							tdo.setTimeIndex(index);
							timeIndex = index;
							leftTime = 120 * 60;
						}
						StrengthenRouteTemplate srt = DataManager.ins().StrengthenRouteData.getTemplate(tdo.getSid());
						if(srt != null && !"1".equals(t[0]) && tdo.getTimeIndex() == 0){//怪物关卡或者是倒计时还在，不能解锁下面
							String points = null;
							for(String temp : srt.getCoordinate().split(";")){
								if(temp.split(":")[0].equals(String.valueOf(index -1))){
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
						tss[index-1] = MyUtil.toString(t, ",");
						info = tss[index-1];
						tdo.setTongStr(MyUtil.toString(tss, ";"));
//						MessageUtil.notiyTongHuaDtoChange(player);
						ResourceService.ins().addTongRes(player, -1);
					}

				}
			}
		}
		
		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("index", index);
		vo.addData("info", info);
		vo.addData("timeIndex", timeIndex);
		vo.addData("leftTime", leftTime);
		vo.addData("unlock", unlocks);

		send(MProtrol.toStringProtrol(MProtrol.TONGHUA_OPEN), vo, user);
	}

	
}
