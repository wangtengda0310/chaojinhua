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
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.item.dto.Item;
import com.igame.work.monster.dto.JiyinType;
import com.igame.work.monster.dto.Monster;
import com.igame.work.quest.service.QuestService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 * 怪物基因单个突破
 *
 */
public class MonsterTUPOSHandler extends BaseHandler{
	

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
		
		int ret = 0;
		String type = "-1";
		Monster mm = player.getMonsters().get(objectId);
		MonsterTemplate mont = MonsterDataManager.MONSTER_DATA.getMonsterTemplate(mm.getMonsterId());
		if(mm == null || mont == null){
			ret = ErrorCode.MONSTER_NOT;//没有此怪物
		}else{
			String[] jiying = mm.getBreaklv().split(",");
			if(rank <=0 || rank > MonsterDataManager.MonsterBreakData.size() || (rank >=2 && "-1".equals(jiying[rank - 2]))){
				ret = ErrorCode.MONSTER_RANK_ERROR;//此阶无法突破
			}else{
				if(!"-1".equals(jiying[rank - 1])){
					ret = ErrorCode.MONSTER_RANK_EXIT;//此阶已经突破（如需更换请选择换一个）
				}else{
					MonsterBreakTemplate mt = MonsterDataManager.MonsterBreakData.getTemplate(rank);
					if(player.getGold() < mt.getGold()){
						ret = ErrorCode.GOLD_NOT_ENOUGH;
					}else{
						List<Item> items = Lists.newArrayList();
						if(!MyUtil.isNullOrEmpty(mt.getItem())){
							int itemId = Integer.parseInt(mt.getItem().split(",")[1]);
							int count = Integer.parseInt(mt.getItem().split(",")[2]);
							Item item = player.getItems().get(itemId);
							if(item == null || item.getUsableCount(-1) < count){
								ret = ErrorCode.ITEM_NOT_ENOUGH;
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
						if(ret == 0){
							type = JiyinType.getRandType(rank, mont.getAtk_type() == 1 ? true : false);
							jiying[rank - 1] = type;
							ResourceService.ins().addGold(player, 0-mt.getGold());
							mm.setBreaklv(MyUtil.toString(jiying, ","));
							mm.reCalculate(player,true);
							mm.setDtate(2);
							List<Monster> mons = Lists.newArrayList();
							mons.add(mm);
							MessageUtil.notiyItemChange(player, items);
							MessageUtil.notiyMonsterChange(player, mons);
					    	GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
					    			+"#act:monsterTUPO" + "#objectId:" + objectId+"#rank:"+rank+"#type:"+type);
					    	QuestService.processTask(player, 7, 1);
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
		vo.addData("type", Integer.parseInt(type));

		send(MProtrol.toStringProtrol(MProtrol.MONSTER_TUPO_S), vo, user);
	}

	
}
