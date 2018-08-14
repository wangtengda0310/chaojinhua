package com.igame.work.monster.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.fight.service.ComputeFightService;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MonsterHandler extends BaseHandler{
	

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

		int teamId = jsonObject.getInt("teamId");
		int index = jsonObject.getInt("index");
		long objectId = jsonObject.getLong("objectId");
		boolean isFight = jsonObject.getBoolean("isFight");

		vo.addData("teamId", teamId);
		vo.addData("index", index);
		vo.addData("objectId", objectId);
		vo.addData("isFight", isFight);

		//入参校验
		if(teamId < 1 || teamId > 6){//不存在的组
			sendError(ErrorCode.TEAM_NOT,MProtrol.toStringProtrol(MProtrol.CHANGE_TEAM), vo, user);
			return;
		}
		if(index < 0 || index > 4){//不存在的位置
			sendError(ErrorCode.INDEX_NOT,MProtrol.toStringProtrol(MProtrol.CHANGE_TEAM), vo, user);
			return;
		}

		Monster m = player.getMonsters().get(objectId);
		if(m == null){//玩家没有此怪物
			sendError(ErrorCode.MONSTER_NOT,MProtrol.toStringProtrol(MProtrol.CHANGE_TEAM), vo, user);
			return;
		}

		/*if(!cangPutIndex(player, index)) {//位置未解锁
			sendError(ErrorCode.INDEX_NOT_UNLOCK,MProtrol.toStringProtrol(MProtrol.CHANGE_TEAM), vo, user);
			return;
		}*/

		//String steam = player.getTeams()[teamId - 1];
		//String[] team = steam.split(",");
		long[] team = player.getTeams().get(teamId).getTeamMonster();
		if (team[index] == -1){//位置未解锁
			sendError(ErrorCode.INDEX_NOT_UNLOCK,MProtrol.toStringProtrol(MProtrol.CHANGE_TEAM), vo, user);
			return;
		}

		if(isFight){//如果是出战

			if(team[index] != 0){//此位置上已有怪物，要换怪物请先下阵
				sendError(ErrorCode.MONSTER_EXIT_INDEX,MProtrol.toStringProtrol(MProtrol.CHANGE_TEAM), vo, user);
				return;
			}

			team[index] = objectId;
			//player.getTeams()[teamId - 1] = MyUtil.toString(team, ",");

		}else{//下阵

			if(team[index] == 0){//此位置上无怪物可下阵
				sendError(ErrorCode.MONSTER_NOTEXIT_INDEX,MProtrol.toStringProtrol(MProtrol.CHANGE_TEAM), vo, user);
				return;
			}

			int total = 0;
			for(long te : team){
				if(0 == te || -1 == te){
					total += 1;
				}
			}
			if(total >= 4){//至少一个上阵
				sendError(ErrorCode.LAST_ONE_UP,MProtrol.toStringProtrol(MProtrol.CHANGE_TEAM), vo, user);
				return;
			}

			team[index] = 0;
			//player.getTeams()[teamId - 1] = MyUtil.toString(team, ",");
		}

		//更新阵容战力
		ComputeFightService.ins().computeTeamFight(player,teamId);
		MessageUtil.notiyTeamChange(player,player.getTeams().get(teamId));

		if(teamId == player.getCurTeam()){
			ComputeFightService.ins().computePlayerFight(player);
		}

		sendSucceed(MProtrol.toStringProtrol(MProtrol.CHANGE_TEAM), vo, user);
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
