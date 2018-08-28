package com.igame.work.checkpoint.tansuo.handler;

import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.checkpoint.tansuo.TansuoDto;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TansuoMonsterHandler extends ReconnectedHandler {
	

	@Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int sid = jsonObject.getInt("sid");	
		long objectId = jsonObject.getLong("objectId");

        TansuoDto dto = player.getTangSuo().get(sid);
		if(dto == null || dto.getState() > 0){
			return error(ErrorCode.ERROR);
		}else{
			Monster mm = player.getMonsters().get(objectId);
			if(mm == null){
				return error(ErrorCode.MONSTER_NOT);
			}else{
				boolean can = true;
				for(Map.Entry<Integer, TansuoDto> m : player.getTangSuo().entrySet()){
					if(m.getKey() == sid){
						continue;
					}
					if(m.getValue().getMons().contains(String.valueOf(objectId))){
						can = false;
						break;
					}
				}
				if(!can){
					return error(ErrorCode.TANG_MON_YET);//此怪物已经在其他探索地图上阵
				}else{
					if(dto.getMons().contains(String.valueOf(objectId))){//下阵
						dto.setMons(dto.getMons().replace(String.valueOf(objectId), "0"));
					}else{//上阵
						String[] ls = dto.getMons().split(",");
						boolean ful = true;
						for(String l :ls){
							if("0".equals(l)){
								ful = false;
								break;
							}
						}
						if(ful){
							return error(ErrorCode.TANG_MON_FULL);//请先下阵再上阵
						}else{
							for(int i =0;i < ls.length;i++){
								if("0".equals(ls[i])){
									ls[i] = String.valueOf(objectId);
									break;
								}
							}
							dto.setMons(MyUtil.toString(ls, ","));
						}
					}
				}
			}
		}

        vo.addData("sid", sid);
		vo.addData("objectId", objectId);
		vo.addData("mons", dto.getMons());

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.TANGSUO_MONSTER;
	}

}
