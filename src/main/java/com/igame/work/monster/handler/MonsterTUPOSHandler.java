package com.igame.work.monster.handler;


import com.google.common.collect.Lists;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.core.log.GoldLog;
import com.igame.util.MyUtil;
import com.igame.work.item.dto.Item;
import com.igame.work.monster.MonsterDataManager;
import com.igame.work.monster.data.MonsterBreakTemplate;
import com.igame.work.monster.data.MonsterTemplate;
import com.igame.work.monster.dto.JiyinType;
import com.igame.work.monster.dto.Monster;
import com.igame.work.quest.service.QuestService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 * 怪物基因单个突破
 *
 */
public class MonsterTUPOSHandler extends ReconnectedHandler {

	private ResourceService resourceService;
	private QuestService questService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);
		
		long objectId = jsonObject.getLong("objectId");
		int rank = jsonObject.getInt("rank");

		String type = "-1";
		Monster mm = player.getMonsters().get(objectId);
		MonsterTemplate mont = MonsterDataManager.MONSTER_DATA.getMonsterTemplate(mm.getMonsterId());
		if(mont == null){
			return error(ErrorCode.MONSTER_NOT);//没有此怪物
		}else{
			String[] jiying = mm.getBreaklv().split(",");
			if(rank <=0 || rank > MonsterDataManager.MonsterBreakData.size() || (rank >=2 && "-1".equals(jiying[rank - 2]))){
				return error(ErrorCode.MONSTER_RANK_ERROR);//此阶无法突破
			}else{
				if(!"-1".equals(jiying[rank - 1])){
					return error(ErrorCode.MONSTER_RANK_EXIT);//此阶已经突破（如需更换请选择换一个）
				}else{
					MonsterBreakTemplate mt = MonsterDataManager.MonsterBreakData.getTemplate(rank);
					if(player.getGold() < mt.getGold()){
						return error(ErrorCode.GOLD_NOT_ENOUGH);
					}else{
						List<Item> items = Lists.newArrayList();
						if(!MyUtil.isNullOrEmpty(mt.getItem())){
							int itemId = Integer.parseInt(mt.getItem().split(",")[1]);
							int count = Integer.parseInt(mt.getItem().split(",")[2]);
							Item item = player.getItems().get(itemId);
							if(item == null || item.getUsableCount(-1) < count){
								return error(ErrorCode.ITEM_NOT_ENOUGH);
							}else{
								item.setCount(item.getCount() - count);
								item.setDtate(2);
								if(item.getCount() <= 0){
									item.setDtate(3);
									item.setCount(0);
									player.getRemoves().add(player.getItems().remove(itemId));
								}
								items.add(item);
							}
						}
						type = JiyinType.getRandType(rank, mont.getAtk_type() == 1);
						jiying[rank - 1] = type;
						resourceService.addGold(player, 0-mt.getGold());
						mm.setBreaklv(MyUtil.toString(jiying, ","));
						mm.reCalculate(player,true);
						mm.setDtate(2);
						List<Monster> mons = Lists.newArrayList();
						mons.add(mm);
						MessageUtil.notifyItemChange(player, items);
						MessageUtil.notifyMonsterChange(player, mons);
						GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
								+"#act:monsterTUPO" + "#objectId:" + objectId+"#rank:"+rank+"#type:"+type);
						questService.processTask(player, 7, 1);

					}
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
		return MProtrol.MONSTER_TUPO_S;
	}

}
