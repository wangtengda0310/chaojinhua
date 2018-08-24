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
public class MonsterEquipDownAllHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);
		
		long mid = jsonObject.getLong("mid");
//		int teamId = jsonObject.getInt("tid");
		int teamId = player.getCurTeam();

		vo.addData("mid", mid);
		vo.addData("tid", teamId);

		//入参校验
		if (teamId < 1 || 6 < teamId){
			return error(ErrorCode.TEAM_NOT);
		}

		//校验怪兽
		Monster mm = player.getMonsters().get(mid);
		if(mm == null || MonsterDataManager.MONSTER_DATA.getMonsterTemplate(mm.getMonsterId()) == null){//没有此怪物
			return error(ErrorCode.MONSTER_NOT);
		}

		String[] eqs = mm.getEquip().split(",");

		List<Item> items = new ArrayList<>();
		Map<Long, Map<Integer, Integer>> teamEquip = player.getTeams().get(teamId).getTeamEquip();
		Map<Integer, Integer> monsterEquip = teamEquip.computeIfAbsent(mid, map -> new ConcurrentHashMap<>());
		for(int i = 0; i < eqs.length; i++){//卸下
			if("-1".equals(eqs[i])){continue;}

			if(monsterEquip.get(i+1)/*下标从1开始*/ == null){//无装备可卸载
				continue;
			}

			ItemService.ins().unsnatch(player, teamId, i+1/*下标从1开始*/, monsterEquip, items);
			eqs[i] = "0";

		}

		//推送道具更新
		MessageUtil.notiyItemChange(player,items);

		//更新怪兽装备及战力,并推送
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
		return MProtrol.ITEM_EQ_ALL;
	}

}
