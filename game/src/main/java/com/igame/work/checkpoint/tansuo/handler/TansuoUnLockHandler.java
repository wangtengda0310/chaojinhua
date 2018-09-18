package com.igame.work.checkpoint.tansuo.handler;

import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.checkpoint.guanqia.CheckPointService;
import com.igame.work.checkpoint.tansuo.TansuoDto;
import com.igame.work.checkpoint.tansuo.TansuoTemplate;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TansuoUnLockHandler extends ReconnectedHandler {

	@Inject private CheckPointService checkPointService;
	@Inject private ResourceService resourceService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int sid = jsonObject.getInt("sid");	
		int index = jsonObject.getInt("index");

		TansuoDto dto = player.getTangSuo().get(sid);
		TansuoTemplate ts = checkPointService.tansuoData.getTemplate(sid);
		if(dto == null || index < 1 || index > 5 || ts == null || dto.getState()> 0){
			return error(ErrorCode.ERROR);
		}else{
			String[] ls = dto.getMons().split(",");
			if(!"-1".equals(ls[index -1])){//已经解锁
				return error(ErrorCode.ERROR);
			}else{
				if(index > 1 && "-1".equals(ls[index -2])){
					return error(ErrorCode.TANG_MON_UN_PRE);//请先解锁前一个位置
				}else{
					String site = ts.getSite(index);
					if(!"-1".equals(site)){
						String[] need = site.split(",");
						if(need.length >= 2){
							if("1".equals(need[0])){//金币
								if(player.getGold() < Long.parseLong(need[1])){
									return error(ErrorCode.GOLD_NOT_ENOUGH);
								}else{
									ls[index -1] = "0";
									resourceService.addGold(player, 0-Long.parseLong(need[1]));
									dto.setMons(MyUtil.toString(ls,","));
								}
							}else if("2".equals(need[0])){
								if(player.getDiamond() < Integer.parseInt(need[1])){
									return error(ErrorCode.DIAMOND_NOT_ENOUGH);
								}else{
									ls[index -1] = "0";
									resourceService.addDiamond(player, 0-Integer.parseInt(need[1]));
									dto.setMons(MyUtil.toString(ls,","));
								}
							}
						}
					}
				}
			}
		}

		vo.addData("sid", sid);
		vo.addData("index", index);

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.TANGSUO_MONSTER_UN;
	}

}
