package com.igame.work.monster.service;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.work.monster.MonsterDataManager;
import com.igame.work.monster.data.MonsterEvolutionTemplate;
import com.igame.work.monster.data.MonsterTemplate;
import com.igame.work.monster.data.PokedexdataTemplate;
import com.igame.core.log.GoldLog;
import com.igame.dto.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.item.dto.Item;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MonsterService {
	
	/**
	 * 
	 * @param player
	 * @param objectId
	 * @param nextObject
	 * @return
	 */
	public static int monsterEV(Player player,long objectId,int nextObject){
		
		int ret = 0;
		Monster mm = player.getMonsters().get(objectId);
		if(mm == null){
			return ErrorCode.MONSTER_NOT;//玩家没有此怪物
		}
		MonsterEvolutionTemplate mt = MonsterDataManager.MonsterEvolutionData.getTemplate(mm.getMonsterId());
		if(mt == null){
			return ErrorCode.MONSTER_NOT;//玩家没有此怪物
		}
		if(MyUtil.isNullOrEmpty(mt.getMonsterObject())){
			return ErrorCode.MONSTER_EV_MAX;//已进化到最大品级。
		}
		if(mt.getMonsterObject().indexOf(String.valueOf(nextObject)) == -1
				|| MonsterDataManager.MONSTER_DATA.getMonsterTemplate(nextObject) == null){
			return ErrorCode.NOT_NEXT_OBJECT;//无法进化到此品质
		}
		if(mm.getLevel() < mt.getLevelLimit()){
			return ErrorCode.LEVEL_NOT_ENOUGH;//等级不够。
		}
		if(player.getGold() < mt.getGold()){
			return ErrorCode.GOLD_NOT_ENOUGH;//金币不足。
		}
		if(!MyUtil.isNullOrEmpty(mt.getItem())){
			String[] items = mt.getItem().split(";");
			for(String item :items){
				String[] it = item.split(",");
				if(player.getItems().get(Integer.parseInt(it[1])) == null 
						|| player.getItems().get(Integer.parseInt(it[1])).getUsableCount(-1) < Integer.parseInt(it[2])){
					return ErrorCode.ITEM_NOT_ENOUGH;//道具不足
				}
			} 
		}		
		
		mm.setMonsterId(nextObject);
		MonsterTemplate nt = MonsterDataManager.MONSTER_DATA.getMonsterTemplate(nextObject);
		if(nt.getSkill() != null){//解锁新的技能
			String[] skills = nt.getSkill().split(",");
			if(skills != null){
				for(String skill : skills){
					if(!mm.getSkillMap().containsKey(Integer.parseInt(skill))){
						mm.getSkillMap().put(Integer.parseInt(skill), 1);
					}
				}
			}
		}
		mm.initSkillString();
//		mm.setHp(DataManager.MONSTER_DATA.getMonsterTemplate(nextObject).getMonster_hp() * 10);
		mm.reCalculate(player,true);
		mm.setDtate(2);
		
		MonsterService.reCalMonsterExtPre(player, true);
		
		ResourceService.ins().addGold(player, 0-mt.getGold());
		
		List<Item> chitems = Lists.newArrayList();
		
		if(!MyUtil.isNullOrEmpty(mt.getItem())){
			String[] items = mt.getItem().split(";");
			for(String item :items){
				String[] it = item.split(",");
				Item ii = player.getItems().get(Integer.parseInt(it[1]));
				ii.setCount(ii.getCount() - Integer.parseInt(it[2]));
				ii.setDtate(2);
				if(ii.getCount() <= 0){
					ii.setCount(0);
					ii.setDtate(3);
					player.getRemoves().add(player.getItems().remove(ii.getItemId()));
				}
				chitems.add(ii);
			} 
		}
		
		List<Monster> mmos = Lists.newArrayList();
		mmos.add(mm);
		RetVO vo1 = new RetVO();		
    	vo1.addData("monsters", mmos);
		MessageUtil.sendMessageToPlayer(player, MProtrol.MONSTER_UPDATE, vo1);

		if(!chitems.isEmpty()){
			RetVO vo = new RetVO();
    		vo.addData("items", chitems);
    		MessageUtil.sendMessageToPlayer(player, MProtrol.ITEM_UPDATE, vo);
		}
		
    	GoldLog.info("#serverId:"+player.getSeverId()+"#userId:"+player.getUserId()+"#playerId:"+player.getPlayerId()
    			+"#act:monsterEV" + "#objectId:" + objectId+"#nextObject:"+nextObject);
		
		return ret;
	}
	
	/**
	 * 计算图鉴增加属性
	 */
	public static void reCalMonsterExtPre(Player player,boolean reCalMonsterTotalValue){
		
		for(Monster mm : player.getMonsters().values()){
			mm.hpExt = 0;
			mm.attackExt = 0;
			mm.damgeRedExt = 0;
			mm.damgeAddExt = 0;
		}
		
		for(PokedexdataTemplate pt  : MonsterDataManager.PokedexData.getAll()){
			if(!MyUtil.isNullOrEmpty(pt.getTakeMonster()) && !MyUtil.isNullOrEmpty(pt.getEffectId()) && !MyUtil.isNullOrEmpty(pt.getValue())){
				String[] takeMonster = pt.getTakeMonster().split(",");
				List<Monster> plus100 = getMonster(player,pt.getPlus100());//加100%属性的怪物
				List<Monster> plus50 = getMonster(player,pt.getPlus50());//加50%属性的怪物
				List<Monster> plus25 = getMonster(player,pt.getPlus25());//加25%属性的怪物
				for(int i = 0;i < takeMonster.length;i++){
					int mid = Integer.parseInt(takeMonster[i]);
					if(containsMonster(player,mid)){//包含此怪物
						String[] effects = pt.getEffectId().split(",");
						String[] values = pt.getValue().split(",");
						switch (Integer.parseInt(effects[i])){
							case 103:
								for(Monster mm : plus100){
									mm.hpExt += Integer.parseInt(values[i]);
								}
								for(Monster mm : plus50){
									mm.hpExt += (int)Math.round(Integer.parseInt(values[i]) * 0.5);
								}
								for(Monster mm : plus25){
									mm.hpExt += (int)Math.round(Integer.parseInt(values[i]) * 0.25);
								}
								break;
							case 106:
								for(Monster mm : plus100){
									mm.attackExt += Integer.parseInt(values[i]);
								}
								for(Monster mm : plus50){
									mm.attackExt += (int)Math.round(Integer.parseInt(values[i]) * 0.5);
								}
								for(Monster mm : plus25){
									mm.attackExt += (int)Math.round(Integer.parseInt(values[i]) * 0.25);
								}
								break;
							case 102:
								for(Monster mm : plus100){
									mm.damgeRedExt += Integer.parseInt(values[i]);
								}
								for(Monster mm : plus50){
									mm.damgeRedExt += (int)Math.round(Integer.parseInt(values[i]) * 0.5);
								}
								for(Monster mm : plus25){
									mm.damgeRedExt += (int)Math.round(Integer.parseInt(values[i]) * 0.25);
								}
								break;
							case 112:
								for(Monster mm : plus100){
									mm.damgeAddExt += Integer.parseInt(values[i]);
								}
								for(Monster mm : plus50){
									mm.damgeAddExt += (int)Math.round(Integer.parseInt(values[i]) * 0.5);
								}
								for(Monster mm : plus25){
									mm.damgeAddExt += (int)Math.round(Integer.parseInt(values[i]) * 0.25);
								}
								break;
							default:
								break;
						}
					}
				}
			}
		}
		if(reCalMonsterTotalValue){
			for(Monster mm : player.getMonsters().values()){
				mm.reCalculate(player, true);
			}
		}
		
		
	}
	
	public static boolean containsMonster(Player player,int mid){
		for(Monster mm : player.getMonsters().values()){
			if(mm.getMonsterId() == mid){
				return true;
			}
		}
		return false;
	}
	
	
	public static List<Monster> getMonster(Player player,String mids){
		List<Monster> mms = Lists.newArrayList();
		if(!MyUtil.isNullOrEmpty(mids)){
			Set<Integer> set = Sets.newHashSet();
			for(String mid : mids.split(",")){
				set.add(Integer.parseInt(mid));
			}
			for(Monster mm : player.getMonsters().values()){
				if(set.contains(mm.getMonsterId())){
					mms.add(mm);
				}
				
			}
		}

		return mms;
	}

}
