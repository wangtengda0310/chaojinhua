package com.igame.work.checkpoint.handler.tansuo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import net.sf.json.JSONObject;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.checkpoint.dto.TangSuoDto;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TangStartHandler extends BaseHandler{
	

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
		int sid = jsonObject.getInt("sid");	
		int state = jsonObject.getInt("state");
		int type = jsonObject.getInt("type");
		String mons = jsonObject.getString("mons");
		int ret = 0;
		long leftTime = 0;
		
		TangSuoDto dto = player.getTangSuo().get(sid);
		if(dto == null || (state != 0 && state != 2 && state != 6 && state != 10)  || (type != 1 && type != 2)){
			ret = ErrorCode.ERROR;
		}else{
			if(type == 1){
				if(dto.getState() != 0){
					ret = ErrorCode.ERROR;
				}else{
					String[] ls = mons.split(",");
					boolean can = false;
					for(String l :ls){
						if(!"0".equals(l) && !"-1".equals(l)){
							can = true;
							if(player.getMonsters().get(Long.parseLong(l)) == null){
								can = false;
								break;
							}else{
								player.getMonsters();
								can = player.getTangSuo().entrySet().stream()
								.filter((entry)->{return entry.getKey()!=sid;})	// 其他地圖
								.filter(entry->entry.getValue().getState()!=0)	// 探索狀態
								.map(entry->new HashSet<String>(Arrays.asList(entry.getValue().getMons().split(","))))
								.noneMatch(monset->monset.contains(l))	// 不包含當前怪
								;
//								for(Map.Entry<Integer, TangSuoDto> m : player.getTangSuo().entrySet()){
//									if(m.getKey() == sid){
//										continue;
//									}
//									if(m.getValue().getMons().indexOf(l) != -1){
//										can = false;
//										break;
//									}
//								}
								if(can == false){
									break;
								}
							}
						}
					}
					if(!can){
						ret = ErrorCode.TANG_MON_ONE;//请上阵怪物
					}else{
						long now = System.currentTimeMillis();
						dto.setState(state);
						dto.setStartTime(now);
						dto.setMons(mons);
						leftTime = state * 3600;
					}
				}

			}else{
				if(dto.getState() == 0){
					ret = ErrorCode.ERROR;
				}else{
					dto.setState(0);
					dto.setStartTime(0);
					dto.clearhelp();
					state = 0;
					leftTime = 0;
				}
			}
		
		}
		
		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("sid", sid);
		vo.addData("leftTime", leftTime);
		vo.addData("state", state);
		vo.addData("mons", mons);
		vo.addData("type", type);

		send(MProtrol.toStringProtrol(MProtrol.TANGSUO_START), vo, user);
	}

	
}
