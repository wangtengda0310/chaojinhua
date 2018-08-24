package com.igame.work.checkpoint.tansuo.handler;

import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.tansuo.TansuoDto;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TansuoStartHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int sid = jsonObject.getInt("sid");	
		int state = jsonObject.getInt("state");
		int type = jsonObject.getInt("type");
		String mons = jsonObject.getString("mons");
		long leftTime = 0;
		
		TansuoDto dto = player.getTangSuo().get(sid);
		if(dto == null || (state != 0 && state != 2 && state != 6 && state != 10)  || (type != 1 && type != 2)){
			return error(ErrorCode.ERROR);
		}else{
			if(type == 1){
				if(dto.getState() != 0){
					return error(ErrorCode.ERROR);
				}else{
					String[] ls = mons.split(",");
					boolean can = true;
					for(String l :ls){
						if(!"0".equals(l) && !"-1".equals(l)){
							if(player.getMonsters().get(Long.parseLong(l)) == null){
								can = false;
								break;
							}else{
								can = player.getTangSuo().entrySet().stream()
								.filter((entry)-> entry.getKey()!=sid)	// 其他地圖
								.filter(entry->entry.getValue().getState()!=0)	// 探索狀態
								.map(entry-> new HashSet<>(Arrays.asList(entry.getValue().getMons().split(","))))
								.noneMatch(monset->monset.contains(l))	// 不包含當前怪
								;

								if(!can){
									break;
								}
							}
						}
					}
					if(!can){
						return error(ErrorCode.TANG_MON_ONE);//请上阵怪物
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
					return error(ErrorCode.ERROR);
				}else{
					dto.setState(0);
					dto.setStartTime(0);
					dto.clearhelp();
					state = 0;
				}
			}
		
		}

		vo.addData("sid", sid);
		vo.addData("leftTime", leftTime);
		vo.addData("state", state);
		vo.addData("mons", mons);
		vo.addData("type", type);

		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.TANGSUO_START;
	}

}
