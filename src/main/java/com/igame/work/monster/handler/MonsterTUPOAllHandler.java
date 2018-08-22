package com.igame.work.monster.handler;



import com.igame.work.monster.MonsterDataManager;
import net.sf.json.JSONObject;

import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.work.monster.data.MonsterTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.core.log.GoldLog;
import com.igame.dto.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.monster.dto.JiyinType;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.dto.RandoRes;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 * 怪物基因全部更换
 *
 */
public class MonsterTUPOAllHandler extends BaseHandler{
	

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
		
		long objectId = jsonObject.getLong("objectId");//怪物ID
		int rank = jsonObject.getInt("rank");//RANK
		int type = jsonObject.getInt("type");//期望改造的基因
		int costType = jsonObject.getInt("costType");//消耗方式 1-金币 2-钻石
		
		int ret = 0;
		String rtype = "-1";
		int result = -1;
		long cost = 0;
		Monster mm = player.getMonsters().get(objectId);
		MonsterTemplate mont = MonsterDataManager.MONSTER_DATA.getMonsterTemplate(mm.getMonsterId());
		if(mm == null || mont == null){
			ret = ErrorCode.MONSTER_NOT;//没有此怪物
		}else{
			String[] jiying = mm.getBreaklv().split(",");				

			if(rank <=0 || rank > MonsterDataManager.MONSTER_DATA.size()){
				ret = ErrorCode.MONSTER_CHANGE_NOT;//此阶无法改造
			}else{
				if("-1".equals(jiying[rank - 1])){
					ret = ErrorCode.MONSTER_TUPO_NOT;//此阶尚未突破
				}else{
					int rankType = 1;
					if(rank%5 == 0 && rank >=5){
						rankType = 2;
					}
					RandoRes res = JiyinType.getRandType(player, rankType, type, costType, rank,  mont.getAtk_type() == 1 ? true : false);
					if(res.getRes() == 0 || res.getRes() == 1){//此次改造完毕
						result = res.getRes();
						rtype = res.getType();
						cost = res.getTotal();
						jiying[rank - 1] = rtype;
						mm.setBreaklv(MyUtil.toString(jiying, ","));
						mm.reCalculate(player,true);
						mm.setDtate(2);
						List<Monster> mons = Lists.newArrayList();
						mons.add(mm);
						MessageUtil.notiyMonsterChange(player, mons);
				    	GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
				    			+"#act:monsterTUPOAll" + "#objectId:" + objectId+"#rank:"+rank+"#type:"+rtype);
					}else{
						if(res.getRes() == -1){//异常出错
							ret = 1;
						}else{//其他错误码
							ret = res.getRes();
						}
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
		vo.addData("type", Integer.parseInt(rtype));
		vo.addData("result", result);
		vo.addData("cost", cost);

		send(MProtrol.toStringProtrol(MProtrol.MONSTER_TUPO_A), vo, user);
	}

	
}
