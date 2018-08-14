package com.igame.work.monster.handler;

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
import com.igame.work.monster.dao.MonsterDAO;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dao.PlayerDAO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.PlayerLoad;
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
public class MonsterDefendHandler extends BaseHandler{
	

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
		long objectId = jsonObject.getLong("objectId");
		boolean isFight = jsonObject.getBoolean("isFight");
//		if(!jsonObject.containsKey("teamId") || !jsonObject.containsKey("index") 
//				|| !jsonObject.containsKey("objectId")|| !jsonObject.containsKey("isFight")){
//			
//		}
		
		ObjectMapper mapper = new ObjectMapper();
		String json = null;
		ISFSObject res = new SFSObject();
		if(index < 0 || index > 4){//不存在的位置
			vo.setState(1);
			vo.setErrCode(ErrorCode.INDEX_NOT);
		}else{
			/*Monster m = player.getMonsters().get(objectId);
			if(m == null){//玩家没有此怪物
				vo.setState(1);
				vo.setErrCode(ErrorCode.MONSTER_NOT);
			}else{
				String[] team = player.getDefendTeam().split(",");
				if(isFight){//如果是出战
					
					if(!team[index].equals("-1")){//此位置上已有怪物，要换怪物请先下阵
						vo.setState(1);
						vo.setErrCode(ErrorCode.MONSTER_EXIT_INDEX);
					}else{
						if(!cangPutIndex(player, index)){//位置未解锁
							vo.setState(1);
							vo.setErrCode(ErrorCode.INDEX_NOT_UNLOCK);
						}else{
							team[index] = Long.toString(objectId);
							player.setDefendTeam(MyUtil.toString(team, ","));
						}
					}
				}else{//下阵
					if(team[index].equals("-1")){//此位置上无怪物可下阵
						vo.setState(1);
						vo.setErrCode(ErrorCode.MONSTER_NOTEXIT_INDEX);
					}else{
						int total = 0;
						for(String te : team){
							if("-1".equals(te)){
								total += 1;
							}
						}
						if(total >= 4){//至少一个上阵
							vo.setState(1);
							vo.setErrCode(ErrorCode.LAST_ONE_UP);
						}else{
							team[index] = "-1";
							player.setDefendTeam(MyUtil.toString(team, ","));
						}
					}
				}
			}*/
			
		}
		if(vo.getState() == 0){
			vo.addData("index", index);
			vo.addData("objectId", objectId);
			vo.addData("isFight", isFight);
		}

		send(MProtrol.toStringProtrol(MProtrol.JINGJI_ZHENG), vo, user);
	}
	
	private boolean cangPutIndex(Player player,int index){
//		if(player.getPlayerLevel()<2 && (index == 1 || index == 2 || index == 3 || index == 4)){
//			return false;
//		}
		if(player.getPlayerLevel()<10 && (index == 2 || index == 3 || index == 4)){
			return false;
		}
		if(player.getPlayerLevel()<19 && (index == 3 || index == 4)){
			return false;
		}
		if(player.getPlayerLevel()<23 && index == 4){
			return false;
		}
		return true;
	}
	
}
