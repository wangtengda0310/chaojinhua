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
import com.igame.work.monster.service.MonsterService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 * 怪物基因单个更换
 *
 */
public class MonsterTUPOChangeHandler extends ReconnectedHandler {

	@Inject
	private MonsterService monseterService;
	@Inject
	private ResourceService resourceService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);
		
		long objectId = jsonObject.getLong("objectId");
		int rank = jsonObject.getInt("rank");
		int costType = jsonObject.getInt("costType");

		String type;
		Monster mm = player.getMonsters().get(objectId);
		MonsterTemplate mont = monseterService.MONSTER_DATA.getMonsterTemplate(mm.getMonsterId());
		if(mont == null){
			return error(ErrorCode.MONSTER_NOT);//没有此怪物
		}else{
			String[] jiying = mm.getBreaklv().split(",");
			if(rank <=0 || rank > monseterService.monsterBreakData.size()){
				return error(ErrorCode.MONSTER_CHANGE_NOT);//此阶无法突破
			}else{
				if("-1".equals(jiying[rank - 1])){
					return error(ErrorCode.MONSTER_TUPO_NOT);//此阶尚未突破
				}else{
					MonsterBreakTemplate mt = monseterService.monsterBreakData.getTemplate(rank);
					if(costType == 1){//gold
						if(player.getGold() < mt.getChange_gold()){
							return error(ErrorCode.GOLD_NOT_ENOUGH);
						}
					}else if(costType == 2){//diam
						if(player.getDiamond() < mt.getDiamond()){
							return error(ErrorCode.DIAMOND_NOT_ENOUGH);
						}
					}else{
						return error(ErrorCode.ERROR);
					}

					type = JiyinType.getRandType(rank, mont.getAtk_type() == 1);
					jiying[rank - 1] = type;
					if(costType == 1){//gold
						resourceService.addGold(player, 0-mt.getChange_gold());
					}else {//diam
						resourceService.addDiamond(player, 0-mt.getDiamond());
					}
					mm.setBreaklv(MyUtil.toString(jiying, ","));
					monseterService.reCalculate(player, mm.getMonsterId(), mm,true);
					mm.setDtate(2);
					List<Monster> mons = Lists.newArrayList();
					mons.add(mm);
					MessageUtil.notifyMonsterChange(player, mons);
					GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
							+"#act:monsterTUPOChange" + "#objectId:" + objectId+"#rank:"+rank+"#type:"+type);

				}
			}
			
		}

		vo.addData("objectId", objectId);
		vo.addData("rank", rank);
		vo.addData("type", Integer.parseInt(type));

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.MONSTER_TUPO_C;
	}

}