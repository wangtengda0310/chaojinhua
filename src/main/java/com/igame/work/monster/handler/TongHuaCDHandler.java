package com.igame.work.monster.handler;


import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.monster.MonsterDataManager;
import com.igame.work.monster.data.StrengthenRouteTemplate;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.TongHuaDto;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TongHuaCDHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int index = jsonObject.getInt("index");
		int type = jsonObject.getInt("type");
		String info;
		int timeIndex;
		long leftTime;
		String unlocks = "";
		/*
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
			return error(ErrorCode.ERROR);
		}else{
			String[] tss = tdo.getTongStr().split(";");
			if(index< 1 || index>tss.length){
				return error(ErrorCode.ERROR);
			}else{
				String[] t = tss[index-1].split(",");//"type,state,rewardtype,rewardId,count"
//				if(tdo.getTimeIndex() == 0){
//					ret = ErrorCode.TONGHUA_NOTCD;
//				}else{
					if(type == 1 && player.getDiamond() < 120 || type == 2 && tdo.calLeftTime() > 0){
						return error(ErrorCode.DIAMOND_NOT_ENOUGH);
					}else{
						if(index != tdo.getTimeIndex() && tdo.getTimeIndex()>0){
							return error(ErrorCode.TONGHUA_NOTCD);
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
							StrengthenRouteTemplate srt = MonsterDataManager.StrengthenRouteData.getTemplate(tdo.getSid());
							unlocks = getString(index, unlocks, tdo, tss, t, srt);
							tdo.setTongStr(MyUtil.toString(tss, ";"));
						}
					}
//				}

			}
		}

		vo.addData("index", index);
		vo.addData("type", type);
		vo.addData("info", info);
		vo.addData("timeIndex", timeIndex);
		vo.addData("leftTime", leftTime);
		vo.addData("unlock", unlocks);

		return vo;
	}

	static String getString(int index, String unlocks, TongHuaDto tdo, String[] tss, String[] t, StrengthenRouteTemplate srt) {
		if(srt != null && !"1".equals(t[0]) && tdo.getTimeIndex() == 0){//怪物关卡或者是倒计时还在，不能解锁下面
			String points = null;
			for(String temp : srt.getCoordinate().split(";")){
				if(temp.split(":")[0].equals(String.valueOf(index -1))){
					points = temp;
					break;
				}
			}
unlocks = TongHuaFightHandler.getString(unlocks, tss, points);
if(unlocks.length() > 0){
				unlocks = unlocks.substring(1);
			}
		}
		return unlocks;
	}

	@Override
	protected int protocolId() {
		return MProtrol.TONGHUA_CD_BUY;
	}

}
