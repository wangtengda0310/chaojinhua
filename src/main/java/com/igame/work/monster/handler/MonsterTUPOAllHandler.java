package com.igame.work.monster.handler;


import com.google.common.collect.Lists;
import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.core.log.GoldLog;
import com.igame.util.MyUtil;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.work.monster.data.MonsterBreakTemplate;
import com.igame.work.monster.data.MonsterTemplate;
import com.igame.work.monster.dto.JiyinType;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.dto.RandoRes;
import com.igame.work.monster.service.MonsterService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
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


	@Inject
	private MonsterService monsterService;
	@Inject
	private ResourceService resourceService;

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
		MonsterTemplate mont = monsterService.MONSTER_DATA.getMonsterTemplate(mm.getMonsterId());
		if(mont == null){
			return error(ErrorCode.MONSTER_NOT);//没有此怪物
		}else{
			String[] jiying = mm.getBreaklv().split(",");				

			if(rank <=0 || rank > monsterService.MONSTER_DATA.size()){
				return error(ErrorCode.MONSTER_CHANGE_NOT);//此阶无法改造
			}else{
				if("-1".equals(jiying[rank - 1])){
					return error(ErrorCode.MONSTER_TUPO_NOT);//此阶尚未突破
				}else{
					int rankType = 1;
					if(rank%5 == 0 && rank >=5){
						rankType = 2;
					}
					RandoRes res = getRandType(player, rankType, type, costType, rank, mont.getAtk_type() == 1);
					if(res.getRes() == 0 || res.getRes() == 1){//此次改造完毕
						result = res.getRes();
						rtype = res.getType();
						cost = res.getTotal();
						jiying[rank - 1] = rtype;
						mm.setBreaklv(MyUtil.toString(jiying, ","));
						monsterService.reCalculate(player, mm.getMonsterId(), mm,true);
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

	/**
	 *
	 * @param rankType 1-小基因  2-大基因
	 * @param type 期望类型 1-20
	 * @param costType 货币类型 1-金币 2-钻石
	 * @param rank  当前改造阶数
	 * @param jingzhan 是否近战
	 */
	private RandoRes getRandType(Player player,int rankType,int type,int costType,int rank,boolean jingzhan){
		RandoRes res = new RandoRes();
		if(costType < 1 || costType > 2){//货币错误
			return new RandoRes(-1,"-1");
		}
		String qitype = String.valueOf(type);//期望类型
		List<String> randow = null;
		if(rankType == 2){//大基因
			if(jingzhan){
				if(!JiyinType.bigNotJing.contains(qitype)){
					return new RandoRes(ErrorCode.MONSTER_JIYINGTYPE_ERROR,"-1");
				}
				randow = JiyinType.bigNotJing;
			}else{
				if(!JiyinType.big.contains(qitype)){
					return new RandoRes(ErrorCode.MONSTER_JIYINGTYPE_ERROR,"-1");
				}
				randow = JiyinType.big;
			}
		}else{//小基因
			if(!JiyinType.small.contains(qitype)){
				return new RandoRes(ErrorCode.MONSTER_JIYINGTYPE_ERROR,"-1");
			}
			randow = JiyinType.small;
		}
		if(randow != null){

			MonsterBreakTemplate mt = monsterService.monsterBreakData.getTemplate(rank);
			if(costType == 1){//金币改造
				long totalCost = 0;//总花销
				if(player.getGold() < mt.getChange_gold()){
					return new RandoRes(ErrorCode.GOLD_NOT_ENOUGH,"-1");
				}
				String getType = "-1";

				getType = JiyinType.getRandType(rank, jingzhan);
				totalCost += mt.getChange_gold();
				res.setType(getType);
				res.setTotal(totalCost);
				if(!getType.equals(String.valueOf(type))){
					res.setRes(1);
				}

				resourceService.addGold(player, 0-totalCost);
				return res;
			}else{//钻石改造
				int totalCost = 0;
				if(player.getDiamond() < mt.getDiamond()){
					return new RandoRes(ErrorCode.DIAMOND_NOT_ENOUGH,"-1");
				}
				String getType = "-1";

				getType = JiyinType.getRandType(rank, jingzhan);
				totalCost += mt.getDiamond();
				res.setType(getType);
				res.setTotal(totalCost);
				if(!getType.equals(String.valueOf(type))){
					res.setRes(1);
				}
				resourceService.addDiamond(player, 0-totalCost);
				return res;
			}

		}else{
			return new RandoRes(-1,"-1");
		}

	}


}
