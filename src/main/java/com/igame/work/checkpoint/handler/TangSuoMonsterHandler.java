package com.igame.work.checkpoint.handler;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.data.DataManager;
import com.igame.core.db.DBManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.log.GoldLog;
import com.igame.dto.IDFactory;
import com.igame.dto.RetVO;
import com.igame.dto.ServerInfo;
import com.igame.util.MyUtil;
import com.igame.work.checkpoint.dto.TangSuoDto;
import com.igame.work.monster.dao.MonsterDAO;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dao.PlayerDAO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.PlayerLoad;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSErrorCode;
import com.smartfoxserver.v2.exceptions.SFSErrorData;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TangSuoMonsterHandler extends BaseHandler{
	

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
		long objectId = jsonObject.getLong("objectId");	
		int ret = 0;
		
		TangSuoDto dto = player.getTangSuo().get(sid);
		if(dto == null || dto.getState() > 0){
			ret = ErrorCode.ERROR;
		}else{
			Monster mm = player.getMonsters().get(objectId);
			if(mm == null){
				ret = ErrorCode.MONSTER_NOT;
			}else{
				boolean can = true;
				for(Map.Entry<Integer, TangSuoDto> m : player.getTangSuo().entrySet()){
					if(m.getKey() == sid){
						continue;
					}
					if(m.getValue().getMons().indexOf(String.valueOf(objectId)) != -1){
						can = false;
						break;
					}
				}
				if(!can){
					ret = ErrorCode.TANG_MON_YET;//此怪物已经在其他探索地图上阵
				}else{
					if(dto.getMons().indexOf(String.valueOf(objectId)) != -1){//下阵
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
							ret = ErrorCode.TANG_MON_FULL;//请先下阵再上阵
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
		
		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("sid", sid);
		vo.addData("objectId", objectId);
		vo.addData("mons", dto.getMons());

		send(MProtrol.toStringProtrol(MProtrol.TANGSUO_MONSTER), vo, user);
	}

	
}
