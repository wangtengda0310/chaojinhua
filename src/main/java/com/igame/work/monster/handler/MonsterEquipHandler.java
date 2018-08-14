package com.igame.work.monster.handler;


import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.ItemTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.fight.service.ComputeFightService;
import com.igame.work.item.dto.Item;
import com.igame.work.item.service.ItemService;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MonsterEquipHandler extends BaseHandler{
	

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
		
		long mid = jsonObject.getLong("mid");
		int itemId = jsonObject.getInt("itemId");
		int location = jsonObject.getInt("location");
		int type = jsonObject.getInt("opeType");
		int teamId = jsonObject.getInt("teamId");

		vo.addData("mid", mid);
		vo.addData("itemId", itemId);
		vo.addData("location", location);
		vo.addData("opeType", type);
		vo.addData("teamId", teamId);

		//入参校验
		if (location < 1 || 4 < location || type < 0 || 1 < type || teamId < 1 || 5 < teamId){
			sendError(ErrorCode.ERROR,MProtrol.toStringProtrol(MProtrol.ITEM_EQ), vo, user);
			return;
		}

		//校验怪兽
		Monster mm = player.getMonsters().get(mid);
		if(mm == null || DataManager.MONSTER_DATA.getMonsterTemplate(mm.getMonsterId()) == null){//没有此怪物
			sendError(ErrorCode.MONSTER_NOT,MProtrol.toStringProtrol(MProtrol.ITEM_EQ), vo, user);
			return;
		}

		//校验位置
		String[] eqs = mm.getEquip().split(",");
		if("-1".equals(eqs[location-1])){
			sendError(ErrorCode.EQ_LOCATION_NOT,MProtrol.toStringProtrol(MProtrol.ITEM_EQ), vo, user);
			return;
		}

		List<Item> items = new ArrayList<>();
		Map<Long, Map<Integer, Integer>> teamEquip = player.getTeams().get(teamId).getTeamEquip();
		Map<Integer, Integer> monsterEquip = teamEquip.computeIfAbsent(mid, map -> new ConcurrentHashMap<>());
		if(type == 0){//卸下

			if(monsterEquip.get(location) == null){//无装备可卸载
				sendError(ErrorCode.EQ_NOT_XIE,MProtrol.toStringProtrol(MProtrol.ITEM_EQ), vo, user);
				return;
			}

			ItemService.ins().unsnatch(player, teamId, location, monsterEquip, items);
			eqs[location-1] = "0";

		}else{//穿上

			//校验道具
			Item item = player.getItems().get(itemId);
			ItemTemplate it = DataManager.ItemData.getTemplate(itemId);
			if(item == null || item.getUsableCount(teamId) < 0 ||it == null || it.getItemType() != 1){
				sendError(ErrorCode.ITEM_NOT_EXIT,MProtrol.toStringProtrol(MProtrol.ITEM_EQ), vo, user);
				return;
			}

			//校验道具类型与装备位置
			if(location == 4 && it.getSubtype() != 2 || location != 4 && it.getSubtype() != 1){
				sendError(ErrorCode.EQ_LOCATION_ZHUANG,MProtrol.toStringProtrol(MProtrol.ITEM_EQ), vo, user);
				return;
			}

			ItemService.ins().dress(player,teamId,location,itemId,monsterEquip, items);
			eqs[location-1] = String.valueOf(itemId);

		}

		//推送道具更新
		MessageUtil.notiyItemChange(player,items);

		//如果更新怪兽装备及战力,并推送
		List<Monster> mms = Lists.newArrayList();

		mm.setEquip(MyUtil.toString(eqs,","));
		mm.setTeamEquip(player.getTeams().values());
		mm.reCalculate(player, true);
		mm.setDtate(2);

		mms.add(mm);
		MessageUtil.notiyMonsterChange(player, mms);

		//如果更新怪兽为当前阵容出战怪兽,更新阵容战力
		if (Arrays.asList(player.getTeams().get(teamId).getTeamMonster()).contains(mid)){
			ComputeFightService.ins().computeTeamFight(player,teamId);
		}
		MessageUtil.notiyTeamChange(player,player.getTeams().get(teamId));

		sendSucceed(MProtrol.toStringProtrol(MProtrol.ITEM_EQ), vo, user);
	}


}
