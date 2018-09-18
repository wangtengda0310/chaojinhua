package com.igame.work.monster.handler;


import com.google.common.collect.Lists;
import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.item.dto.Item;
import com.igame.work.monster.data.MonsterTemplate;
import com.igame.work.monster.data.NewMonsterTemplate;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.service.MonsterService;
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
public class MonsterNewHandler extends ReconnectedHandler {

	@Inject
	private MonsterService monseterService;
	@Inject
	private ResourceService resourceService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int newMonster = jsonObject.getInt("newMonster");
		NewMonsterTemplate nt = monseterService.newMonsterData.getTemplate(newMonster);
		MonsterTemplate mt = monseterService.MONSTER_DATA.getMonsterTemplate(newMonster);
		if(nt == null || mt == null){
			return error(ErrorCode.ERROR);
		}else{
			String[] cons = nt.getCondition().split(",");
			boolean can = true;
			StringBuilder sb = new StringBuilder();
			for(Monster mm : player.getMonsters().values()){
				sb.append(mm.getMonsterId()).append(",");
			}
			for(String con : cons){
				if(!sb.toString().contains(con)){
					can = false;
					break;
				}
			}
			if(!can){
				return error(ErrorCode.MONSTER_CON_NOT);
			}else{
				RewardDto rt = resourceService.getRewardDto(nt.getItem(), "100");
				if(rt != null ){
					if(rt.getGold() > 0 && player.getGold() < rt.getGold()){
						return error(ErrorCode.GOLD_NOT_ENOUGH);
					}
					if(!rt.getItems().isEmpty()){//道具不足
						for(Map.Entry<Integer, Integer> m :rt.getItems().entrySet()){
							if(player.getItems().get(m.getKey()) == null || player.getItems().get(m.getKey()).getUsableCount(-1) < m.getValue()){
								return error(ErrorCode.ITEM_NOT_ENOUGH);
							}
						}
					}
					//生成新怪物
					resourceService.addMonster(player, newMonster, 1, true);
					if(rt.getGold()>0){
						resourceService.addGold(player, 0-rt.getGold());
					}
					if(!rt.getItems().isEmpty()){
						List<Item> ll = Lists.newArrayList();
						for(Map.Entry<Integer, Integer> m :rt.getItems().entrySet()){
							ll.add(resourceService.addItem(player, m.getKey(), 0-m.getValue(), false));
						}
						MessageUtil.notifyItemChange(player, ll);
					}

				}
			}
		}

		vo.addData("newMonster", newMonster);

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.MONSTER_NEW;
	}

}