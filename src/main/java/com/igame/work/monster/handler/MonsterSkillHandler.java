package com.igame.work.monster.handler;


import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.SkillLvTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.item.dto.Item;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author xym
 *
 * 	怪兽技能升级
 *
 */
public class MonsterSkillHandler extends BaseHandler{
	

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
		String items = jsonObject.getString("items");
		vo.addData("objectId",objectId);
		vo.addData("items",items);

		if (items.isEmpty()){
			sendError(ErrorCode.PARAMS_INVALID,MProtrol.toStringProtrol(MProtrol.MONSTER_SKILL), vo, user);
			return;
		}

		Monster monster = player.getMonsters().get(objectId);
		if (monster == null){
			sendError(ErrorCode.MONSTER_NOT,MProtrol.toStringProtrol(MProtrol.MONSTER_SKILL), vo, user);
			return;
		}

		List<Integer> failed = new ArrayList<>();
		List<Integer> skillIds = new ArrayList<>();
		String[] itemsArr = items.split(";");
		for (String itemStr : itemsArr) {

			int skillId = 0;
			SkillLvTemplate template = null;

			String[] itemArr = itemStr.split(",");

			int itemId = Integer.parseInt(itemArr[0]);
			int count = Integer.parseInt(itemArr[1]);

			Set<Integer> set = monster.getSkillMap().keySet();
			for (Integer tempSkillId : set) {
				SkillLvTemplate skillLvTemplate = DataManager.SkillLvData.getTemplate(tempSkillId);
				if (skillLvTemplate.getUseItem().contains(String.valueOf(itemId))){
					template = skillLvTemplate;
					skillId = tempSkillId;
				}
			}

			if (template == null || skillId == 0){
				failed.add(itemId);
				break;
			}

			//校验道具是否存在
			Item item = player.getItems().get(itemId);
			if (item == null){
				failed.add(itemId);
				break;
			}

			//校验道具数量
			if (item.getUsableCount(-1) < count){
				failed.add(itemId);
				break;
			}

			//记录道具下标
			int index = -1;
			String[] useItems = template.getUseItem().split(",");
			for (int i = 0; i < useItems.length; i++) {
				if (itemId == Integer.parseInt(useItems[i])){
					index = i;
				}
			}

			//校验道具是否可用于此技能升级
			if (index == -1){
				failed.add(itemId);
				break;
			}

			Integer curExp = monster.getSkillExp().get(skillId);
			Integer curLv = monster.getSkillMap().get(skillId);

			//增加经验
			int addExp = Integer.parseInt(template.getGetExp().split(",")[index]) * count;
			curExp += addExp;
			while (curExp >= template.getSkillExp()){	//升级
				curExp -= template.getSkillExp();
				curLv ++;
			}

			//未升级
			monster.getSkillMap().put(skillId,curLv);
			monster.getSkillExp().put(skillId,curExp);

			monster.setDtate(2);

			//减少道具
			ResourceService.ins().addItem(player,itemId,-count,true);

			skillIds.add(skillId);
		}

		List<String> skillStr = new ArrayList<>();
		for (Integer skillId : skillIds) {
			String skill = skillId + ","+ monster.getSkillMap().get(skillId) +","+ monster.getSkillExp().get(skillId);
			skillStr.add(skill);
		}

		vo.addData("skill",skillStr);
		vo.addData("failed",failed);
		sendSucceed(MProtrol.toStringProtrol(MProtrol.MONSTER_SKILL), vo, user);
	}
	
}
