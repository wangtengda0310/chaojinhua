package com.igame.work.monster.handler;


import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.fight.service.ComputeFightService;
import com.igame.work.item.dto.Item;
import com.igame.work.item.service.ItemService;
import com.igame.work.monster.MonsterDataManager;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.PlayerDataManager;
import com.igame.work.user.data.ItemTemplate;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MonsterEquipHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);
		
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
			return error(ErrorCode.ERROR);
		}

		//校验怪兽
		Monster mm = player.getMonsters().get(mid);
		if(mm == null || MonsterDataManager.MONSTER_DATA.getMonsterTemplate(mm.getMonsterId()) == null){//没有此怪物
			return error(ErrorCode.MONSTER_NOT);
		}

		//校验位置
		String[] eqs = mm.getEquip().split(",");
		if("-1".equals(eqs[location-1])){
			return error(ErrorCode.EQ_LOCATION_NOT);
		}

		List<Item> items = new ArrayList<>();
		Map<Long, Map<Integer, Integer>> teamEquip = player.getTeams().get(teamId).getTeamEquip();
		Map<Integer, Integer> monsterEquip = teamEquip.computeIfAbsent(mid, map -> new ConcurrentHashMap<>());
		if(type == 0){//卸下

			if(monsterEquip.get(location) == null){//无装备可卸载
				return error(ErrorCode.EQ_NOT_XIE);
			}

			ItemService.ins().unsnatch(player, teamId, location, monsterEquip, items);
			eqs[location-1] = "0";

		}else{//穿上

			//校验道具
			Item item = player.getItems().get(itemId);
			ItemTemplate it = PlayerDataManager.ItemData.getTemplate(itemId);
			if(item == null || item.getUsableCount(teamId) < 0 ||it == null || it.getItemType() != 1){
				return error(ErrorCode.ITEM_NOT_EXIT);
			}

			//校验道具类型与装备位置
			if(location == 4 && it.getSubtype() != 2 || location != 4 && it.getSubtype() != 1){
				return error(ErrorCode.EQ_LOCATION_ZHUANG);
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

		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.ITEM_EQ;
	}

}
