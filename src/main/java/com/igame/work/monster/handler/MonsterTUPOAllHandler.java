package com.igame.work.monster.handler;


import com.google.common.collect.Lists;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.core.log.GoldLog;
import com.igame.util.MyUtil;
import com.igame.work.monster.MonsterDataManager;
import com.igame.work.monster.data.MonsterTemplate;
import com.igame.work.monster.dto.JiyinType;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.dto.RandoRes;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 * 怪物基因全部更换
 *
 */
public class MonsterTUPOAllHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);
		
		long objectId = jsonObject.getLong("objectId");//怪物ID
		int rank = jsonObject.getInt("rank");//RANK
		int type = jsonObject.getInt("type");//期望改造的基因
		int costType = jsonObject.getInt("costType");//消耗方式 1-金币 2-钻石

		String rtype;
		int result;
		long cost;
		Monster mm = player.getMonsters().get(objectId);
		MonsterTemplate mont = MonsterDataManager.MONSTER_DATA.getMonsterTemplate(mm.getMonsterId());
		if(mont == null){
			return error(ErrorCode.MONSTER_NOT);//没有此怪物
		}else{
			String[] jiying = mm.getBreaklv().split(",");				

			if(rank <=0 || rank > MonsterDataManager.MONSTER_DATA.size()){
				return error(ErrorCode.MONSTER_CHANGE_NOT);//此阶无法改造
			}else{
				if("-1".equals(jiying[rank - 1])){
					return error(ErrorCode.MONSTER_TUPO_NOT);//此阶尚未突破
				}else{
					int rankType = 1;
					if(rank%5 == 0 && rank >=5){
						rankType = 2;
					}
					RandoRes res = JiyinType.getRandType(player, rankType, type, costType, rank, mont.getAtk_type() == 1);
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
						MessageUtil.notifyMonsterChange(player, mons);
				    	GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
				    			+"#act:monsterTUPOAll" + "#objectId:" + objectId+"#rank:"+rank+"#type:"+rtype);
					}else{
						if(res.getRes() == -1){//异常出错
							return error(ErrorCode.ERROR);
						}else{//其他错误码
							return error(res.getRes());
						}
					}
				}
			}

			
		}

		vo.addData("objectId", objectId);
		vo.addData("rank", rank);
		vo.addData("type", Integer.parseInt(rtype));
		vo.addData("result", result);
		vo.addData("cost", cost);

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.MONSTER_TUPO_A;
	}

}
