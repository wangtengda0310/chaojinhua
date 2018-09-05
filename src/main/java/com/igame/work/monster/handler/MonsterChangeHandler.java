package com.igame.work.monster.handler;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.item.dto.Item;
import com.igame.work.monster.MonsterDataManager;
import com.igame.work.monster.data.MonsterTemplate;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MonsterChangeHandler extends ReconnectedHandler {

	private ResourceService resourceService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		long mid1 = jsonObject.getLong("mid1");
		long mid2 = jsonObject.getLong("mid2");
		Monster mm1 = player.getMonsters().get(mid1);
		Monster mm2 = player.getMonsters().get(mid2);
		if(mm1 == null || mm2 == null){
			return error(ErrorCode.MONSTER_NOT);//没有此怪物
		}else{
			MonsterTemplate mont1 = MonsterDataManager.MONSTER_DATA.getMonsterTemplate(mm1.getMonsterId());
			MonsterTemplate mont2 = MonsterDataManager.MONSTER_DATA.getMonsterTemplate(mm2.getMonsterId());
			if(mont1 == null || mont2 == null){
				return error(ErrorCode.MONSTER_NOT);//没有此怪物
			}else{
				if(mont1.getMonster_rarity() != mont2.getMonster_rarity() || (mont1.getSkill() != null && mont2.getSkill() != null && mont1.getSkill().split(",").length != mont2.getSkill().split(",").length)){
					return error(ErrorCode.MONSTER_RA_NOTE);//品质不同
				}else{
					if(player.getItems().get(300001) == null || player.getItems().get(300001).getUsableCount(-1) < 1){
						return error(ErrorCode.ITEM_NOT_ENOUGH);//道具不足
					}else{
						String breaklv1 = mm1.getBreaklv();
						String breaklv2 = mm2.getBreaklv();
						mm1.setBreaklv(breaklv2);
						mm2.setBreaklv(breaklv1);
						int lv1 = mm1.getLevel();
						int exp1 = mm1.getExp();
						int lv2 = mm2.getLevel();
						int exp2 = mm2.getExp();
						mm1.setLevel(lv2);
						mm1.setExp(exp2);
						mm2.setLevel(lv1);
						mm2.setExp(exp1);
						
						if(mont1.getSkill() != null && mont2.getSkill() != null){
							String[] skill1 = mont1.getSkill().split(",");
							String[] skill2 = mont2.getSkill().split(",");
							if(skill1.length == skill2.length){
								Map<Integer,Integer>  sm1 = Maps.newHashMap();//1怪物技能
								Map<Integer,Integer>  sm2 = Maps.newHashMap();//2怪物技能
								for(Map.Entry<Integer, Integer> m : mm1.getSkillMap().entrySet()){
									sm1.put(m.getKey(), m.getValue());
								}
								for(Map.Entry<Integer, Integer> m : mm2.getSkillMap().entrySet()){
									sm2.put(m.getKey(), m.getValue());
								}
								for(int i = 0;i< skill1.length;i++ ){
									if(mm1.getSkillMap().get(Integer.parseInt(skill1[i])) != null){
										mm2.getSkillMap().put(Integer.parseInt(skill2[i]), sm1.get(Integer.parseInt(skill1[i])) == null ? 1 : sm1.get(Integer.parseInt(skill1[i])));
									}
								}
								for(int i = 0;i< skill2.length;i++ ){
									if(mm2.getSkillMap().get(Integer.parseInt(skill2[i])) != null){
										mm1.getSkillMap().put(Integer.parseInt(skill1[i]), sm2.get(Integer.parseInt(skill2[i])) == null ? 1 : sm2.get(Integer.parseInt(skill2[i])));
									}
								}
								mm1.initSkillString();
								mm2.initSkillString();
							}
						}
						
						List<Item> items = Lists.newArrayList();
						String[] eqs = mm1.getEquip().split(",");
						addItem(player, items, eqs);
						mm1.setEquip(MyUtil.toString(eqs, ","));
						
						String[] eqs2 = mm2.getEquip().split(",");
						addItem(player, items, eqs2);
						mm2.setEquip(MyUtil.toString(eqs2, ","));
						
						mm1.reCalculate(player, true);
						mm1.setDtate(2);
						mm2.reCalculate(player, true);
						mm2.setDtate(2);
						List<Monster> ll = Lists.newArrayList();
						ll.add(mm1);
						ll.add(mm2);
						MessageUtil.notifyMonsterChange(player, ll);

						Item xiao = resourceService.addItem(player, 300001, -1, false);//消耗道具
						items.add(xiao);
						MessageUtil.notifyItemChange(player, items);
						
					}
				}
				
			}
		}

		vo.addData("mid1", mid1);
		vo.addData("mid2", mid2);

		return vo;
	}

	private void addItem(Player player, List<Item> items, String[] eqs) {
		for(int i = 0;i < eqs.length;i++){
			if(!"-1".equals(eqs[i]) && !"0".equals(eqs[i])){
				Item old = resourceService.addItem(player, Integer.parseInt(eqs[i]), 1, false);
				items.add(old);
				eqs[i] = "0";

			}
		}
	}

	@Override
    public int protocolId() {
		return MProtrol.MONSTER_CHANGE;
	}

}
