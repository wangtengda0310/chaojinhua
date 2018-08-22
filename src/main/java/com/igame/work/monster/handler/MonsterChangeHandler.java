package com.igame.work.monster.handler;



import java.util.List;
import java.util.Map;

import com.igame.work.monster.MonsterDataManager;
import net.sf.json.JSONObject;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.work.monster.data.MonsterTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.item.dto.Item;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MonsterChangeHandler extends BaseHandler{
	

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
		int ret = 0;
		long mid1 = jsonObject.getLong("mid1");
		long mid2 = jsonObject.getLong("mid2");
		Monster mm1 = player.getMonsters().get(mid1);
		Monster mm2 = player.getMonsters().get(mid2);
		if(mm1 == null || mm2 == null){
			ret = ErrorCode.MONSTER_NOT;//没有此怪物
		}else{
			MonsterTemplate mont1 = MonsterDataManager.MONSTER_DATA.getMonsterTemplate(mm1.getMonsterId());
			MonsterTemplate mont2 = MonsterDataManager.MONSTER_DATA.getMonsterTemplate(mm2.getMonsterId());
			if(mont1 == null || mont2 == null){
				ret = ErrorCode.MONSTER_NOT;//没有此怪物
			}else{
				if(mont1.getMonster_rarity() != mont2.getMonster_rarity() || (mont1.getSkill() != null && mont2.getSkill() != null && mont1.getSkill().split(",").length != mont2.getSkill().split(",").length)){
					ret = ErrorCode.MONSTER_RA_NOTE;//品质不同
				}else{
					if(player.getItems().get(300001) == null || player.getItems().get(300001).getUsableCount(-1) < 1){
						ret = ErrorCode.ITEM_NOT_ENOUGH;//道具不足
					}else{
						String breaklv1 =  new String(mm1.getBreaklv());
						String breaklv2 =  new String(mm2.getBreaklv());
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
							if(skill1 != null && skill2 != null && skill1.length == skill2.length){
								Map<Integer,Integer>  sm1 = Maps.newHashMap();//1怪物技能
								Map<Integer,Integer>  sm2 = Maps.newHashMap();//2怪物技能
								for(Map.Entry<Integer, Integer> m : mm1.getSkillMap().entrySet()){
									sm1.put(m.getKey().intValue(), m.getValue().intValue());
								}
								for(Map.Entry<Integer, Integer> m : mm2.getSkillMap().entrySet()){
									sm2.put(m.getKey().intValue(), m.getValue().intValue());
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
						for(int i = 0;i < eqs.length;i++){
							if(!"-1".equals(eqs[i]) && !"0".equals(eqs[i])){
								Item old = ResourceService.ins().addItem(player, Integer.parseInt(eqs[i]), 1, false);
								items.add(old);
								eqs[i] = "0";
								
							}
						}
						mm1.setEquip(MyUtil.toString(eqs, ","));
						
						String[] eqs2 = mm2.getEquip().split(",");
						for(int i = 0;i < eqs2.length;i++){
							if(!"-1".equals(eqs2[i]) && !"0".equals(eqs2[i])){
								Item old = ResourceService.ins().addItem(player, Integer.parseInt(eqs2[i]), 1, false);
								items.add(old);
								eqs2[i] = "0";
								
							}
						}
						mm2.setEquip(MyUtil.toString(eqs2, ","));
						
						mm1.reCalculate(player, true);
						mm1.setDtate(2);
						mm2.reCalculate(player, true);
						mm2.setDtate(2);
						List<Monster> ll = Lists.newArrayList();
						ll.add(mm1);
						ll.add(mm2);
						MessageUtil.notiyMonsterChange(player, ll);

						Item xiao = ResourceService.ins().addItem(player, 300001, -1, false);//消耗道具
						items.add(xiao);
						MessageUtil.notiyItemChange(player, items);
						
					}
				}
				
			}
		}

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("mid1", mid1);
		vo.addData("mid2", mid2);

		send(MProtrol.toStringProtrol(MProtrol.MONSTER_CHANGE), vo, user);
	}

	
}
