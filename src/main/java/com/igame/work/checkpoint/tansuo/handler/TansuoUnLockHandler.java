package com.igame.work.checkpoint.tansuo.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.work.checkpoint.tansuo.TansuoDataManager;
import com.igame.work.checkpoint.tansuo.TansuoTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.checkpoint.tansuo.TansuoDto;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TansuoUnLockHandler extends BaseHandler{
	

	@Override
	public void handleClientRequest(User user, ISFSObject params) {
		RetVO vo = new RetVO();
		if(reviceMessage(user,params,vo)){
			return;
		}

		Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
		if(player == null){
			this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
			return;
		}

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int sid = jsonObject.getInt("sid");	
		int index = jsonObject.getInt("index");	
		int ret = 0;
		
		TansuoDto dto = player.getTangSuo().get(sid);
		TansuoTemplate ts = TansuoDataManager.TansuoData.getTemplate(sid);
		if(dto == null || index < 1 || index > 5 || ts == null || dto.getState()> 0){
			ret = ErrorCode.ERROR;
		}else{
			String[] ls = dto.getMons().split(",");
			if(!"-1".equals(ls[index -1])){//已经解锁
				ret = ErrorCode.ERROR;
			}else{
				if(index > 1 && "-1".equals(ls[index -2])){
					ret = ErrorCode.TANG_MON_UN_PRE;//请先解锁前一个位置
				}else{
					String site = ts.getSite(index);
					if(!"-1".equals(site)){
						String[] need = site.split(",");
						if(need != null && need.length >= 2){
							if("1".equals(need[0])){//金币
								if(player.getGold() < Long.parseLong(need[1])){
									ret = ErrorCode.GOLD_NOT_ENOUGH;
								}else{
									ls[index -1] = "0";
									ResourceService.ins().addGold(player, 0-Long.parseLong(need[1]));
									dto.setMons(MyUtil.toString(ls,","));
								}
							}else if("2".equals(need[0])){
								if(player.getDiamond() < Integer.parseInt(need[1])){
									ret = ErrorCode.DIAMOND_NOT_ENOUGH;
								}else{
									ls[index -1] = "0";
									ResourceService.ins().addDiamond(player, 0-Integer.parseInt(need[1]));
									dto.setMons(MyUtil.toString(ls,","));
								}
							}
						}
					}
				}
			}
		}

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("sid", sid);
		vo.addData("index", index);

		send(MProtrol.toStringProtrol(MProtrol.TANGSUO_MONSTER_UN), vo, user);
	}

	
}
