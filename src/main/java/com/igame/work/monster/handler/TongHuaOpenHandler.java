package com.igame.work.monster.handler;



import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
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
public class TongHuaOpenHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		long now = System.currentTimeMillis();
		int index = jsonObject.getInt("index");
		String info;
		int timeIndex = 0;
		long leftTime = 0;
		String unlocks = "";
		TongHuaDto tdo = player.getTonghua();
		if(tdo == null){
			return error(ErrorCode.ERROR);
		}else{
			String[] tss = tdo.getTongStr().split(";");
			if(index< 1 || index>tss.length){
				return error(ErrorCode.ERROR);
			}else{
				String ts = tss[index-1];//"type,state,rewardtype,rewardId,count"
				String[] t = ts.split(",");
				if(!"0".equals(t[1]) || (tdo.getStartTime() > 0 && tdo.calLeftTime() >0)){
					return error(ErrorCode.TONGHUA_NOTOPEN);//该点不可打开
				}else{
					if(player.getTongRes() < 1){
						return error(ErrorCode.TONGHUA_NOT_ENOUGH);//同化资源不够
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
						StrengthenRouteTemplate srt = MonsterDataManager.StrengthenRouteData.getTemplate(tdo.getSid());
						unlocks = TongHuaCDHandler.getString(index, unlocks, tdo, tss, t, srt);
						tss[index-1] = MyUtil.toString(t, ",");
						info = tss[index-1];
						tdo.setTongStr(MyUtil.toString(tss, ";"));
//						MessageUtil.notiyTongHuaDtoChange(player);
						ResourceService.ins().addTongRes(player, -1);
					}

				}
			}
		}

		vo.addData("index", index);
		vo.addData("info", info);
		vo.addData("timeIndex", timeIndex);
		vo.addData("leftTime", leftTime);
		vo.addData("unlock", unlocks);

		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.TONGHUA_OPEN;
	}

}
