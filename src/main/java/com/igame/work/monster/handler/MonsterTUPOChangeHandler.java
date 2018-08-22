package com.igame.work.monster.handler;



import com.igame.work.monster.MonsterDataManager;
import net.sf.json.JSONObject;

import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.work.monster.data.MonsterBreakTemplate;
import com.igame.work.monster.data.MonsterTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.core.log.GoldLog;
import com.igame.dto.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.monster.dto.JiyinType;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 * 怪物基因单个更换
 *
 */
public class MonsterTUPOChangeHandler extends BaseHandler{
	

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
		
		long objectId = jsonObject.getLong("objectId");
		int rank = jsonObject.getInt("rank");
		int costType = jsonObject.getInt("costType");
		
		int ret = 0;
		String type = "-1";
		Monster mm = player.getMonsters().get(objectId);
		MonsterTemplate mont = MonsterDataManager.MONSTER_DATA.getMonsterTemplate(mm.getMonsterId());
		if(mm == null || mont == null){
			ret = ErrorCode.MONSTER_NOT;//没有此怪物
		}else{
			String[] jiying = mm.getBreaklv().split(",");
			if(rank <=0 || rank > MonsterDataManager.MonsterBreakData.size()){
				ret = ErrorCode.MONSTER_CHANGE_NOT;//此阶无法突破
			}else{
				if("-1".equals(jiying[rank - 1])){
					ret = ErrorCode.MONSTER_TUPO_NOT;//此阶尚未突破
				}else{
					MonsterBreakTemplate mt = MonsterDataManager.MonsterBreakData.getTemplate(rank);
					if(costType == 1){//gold
						if(player.getGold() < mt.getChange_gold()){
							ret = ErrorCode.GOLD_NOT_ENOUGH;
						}
					}else if(costType == 2){//diam
						if(player.getDiamond() < mt.getDiamond()){
							ret = ErrorCode.DIAMOND_NOT_ENOUGH;
						}
					}else{
						ret = ErrorCode.ERROR;
					}
	
					if(ret != 0){
					}else{
						type = JiyinType.getRandType(rank, mont.getAtk_type() == 1 ? true : false);
						jiying[rank - 1] = type;
						if(costType == 1){//gold
							ResourceService.ins().addGold(player, 0-mt.getChange_gold());
						}else if(costType == 2){//diam
							ResourceService.ins().addDiamond(player, 0-mt.getDiamond());
						}						
						mm.setBreaklv(MyUtil.toString(jiying, ","));
						mm.reCalculate(player,true);
						mm.setDtate(2);
						List<Monster> mons = Lists.newArrayList();
						mons.add(mm);
						MessageUtil.notiyMonsterChange(player, mons);
				    	GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
				    			+"#act:monsterTUPOChange" + "#objectId:" + objectId+"#rank:"+rank+"#type:"+type);
						
					}
				}
			}
			
		}

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("objectId", objectId);
		vo.addData("rank", rank);
		vo.addData("type", Integer.parseInt(type));

		send(MProtrol.toStringProtrol(MProtrol.MONSTER_TUPO_C), vo, user);
	}

	
}
