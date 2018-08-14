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
import com.igame.work.checkpoint.dto.RewardDto;
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
public class TongHuaCDHandler extends BaseHandler{
	

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

		int index = jsonObject.getInt("index");
		int type = jsonObject.getInt("type");
		String info = "";
		int timeIndex = 0;
		long leftTime = 0;
		String unlocks = "";
		int ret = 0;
		/**
		 *"type,state,rewardtype,rewardId,count;";//如1,0,1,1,1;2,-1,3,200005,100
		 *
		 * type:0-普通 1-怪物 2-强化袍子 3-进化袍子 4-神性袍子
	     * state:-1-未解锁 0-已解锁/可开启 1-倒计时/进行中 2-可领取 3-已领取/已完结	
		 * rewardtype:1-怪物关卡  3-道具 5-同化经验值
		 * rewardId:当rewardtype为1 对应strengthenmonster.xml中的num 
		 *  当rewardtype为3 对应道具ID
		 *  当rewardtype为5 为5
		 * count:对应的数量
		 */
		TongHuaDto tdo = player.getTonghua();
		if(tdo == null){
			ret = ErrorCode.ERROR;
		}else{
			String[] tss = tdo.getTongStr().split(";");
			if(index< 1 || index>tss.length){
				ret = ErrorCode.ERROR;
			}else{
				String[] t = tss[index-1].split(",");//"type,state,rewardtype,rewardId,count"
//				if(tdo.getTimeIndex() == 0){
//					ret = ErrorCode.TONGHUA_NOTCD;
//				}else{
					if(type == 1 && player.getDiamond() < 120 || type == 2 && tdo.calLeftTime() > 0){
						ret = ErrorCode.DIAMOND_NOT_ENOUGH;
					}else{
						timeIndex = tdo.getTimeIndex();
						leftTime = tdo.calLeftTime();
						if(index != tdo.getTimeIndex() && tdo.getTimeIndex()>0){
							ret = ErrorCode.TONGHUA_NOTCD;
						}else{
							
							t[1] = "2";
							tdo.setStartTime(0);
							tdo.setTimeCount(0);
							tdo.setTimeIndex(0);
							tss[index-1] = MyUtil.toString(t, ",");
							info = tss[index-1];
							
							timeIndex = tdo.getTimeIndex();
							leftTime = tdo.calLeftTime();
							if(type == 1){
								ResourceService.ins().addDiamond(player, -120);
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
							tdo.setTongStr(MyUtil.toString(tss, ";"));
						}
					}
//				}

			}
		}

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("index", index);
		vo.addData("type", type);
		vo.addData("info", info);
		vo.addData("timeIndex", timeIndex);
		vo.addData("leftTime", leftTime);
		vo.addData("unlock", unlocks);

		send(MProtrol.toStringProtrol(MProtrol.TONGHUA_CD_BUY), vo, user);
	}

	
}
