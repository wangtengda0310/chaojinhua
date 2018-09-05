package com.igame.work.item.handler;


import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.item.dto.Item;
import com.igame.work.monster.MonsterDataManager;
import com.igame.work.monster.data.MonsterGroupTemplate;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author xym
 *
 * 	召唤怪兽
 *
 */
public class ItemSummonHandler extends ReconnectedHandler {

	private ResourceService resourceService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int itemId = jsonObject.getInt("itemId");

		vo.addData("itemId",itemId);

		//校验背包上限
		Map<Integer, Item> items = player.getItems();
		/*if (items.size() >= player.getBagSpace()){
			return error(ErrorCode.BAGSPACE_ALREADY_FULL);
		}*/

		//校验道具是否存在
		Item item = items.get(itemId);
		if (item == null){
			return error(ErrorCode.PARAMS_INVALID);
		}

		//检验道具是否可召唤怪兽
		MonsterGroupTemplate template = MonsterDataManager.monsterGroupData.getTemplate(itemId);
		if (template == null){
			return error(ErrorCode.PARAMS_INVALID);
		}

		//校验碎片是否满足数量
		if (item.getUsableCount(-1) < template.getNum()){
			return error(ErrorCode.PARAMS_INVALID);
		}

		//新增怪兽
		List<Monster> monsters = resourceService.addMonster(player, template.getMonsterId(), 1, true);
		if (monsters.isEmpty()){
			return error(ErrorCode.ERROR);
		}

		//减少道具数量
		resourceService.addItem(player,itemId,-template.getNum(),true);

		vo.addData("objectId",monsters.get(0).getObjectId());
		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.ITEM_SUMMON;
	}

}
