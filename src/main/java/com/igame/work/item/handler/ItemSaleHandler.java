package com.igame.work.item.handler;


import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.item.dto.Item;
import com.igame.work.user.PlayerDataManager;
import com.igame.work.user.data.ItemTemplate;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author xym
 *
 * 	道具出售
 *
 */
public class ItemSaleHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		String items = jsonObject.getString("items");
		vo.addData("items",items);

		int gold = 0;
		List<Integer> failed = new ArrayList<>();
		String[] itemsArr = items.split(";");
		for (String itemStr : itemsArr) {

			String[] itemArr = itemStr.split(",");

			int itemId = Integer.parseInt(itemArr[0]);
			int count = Integer.parseInt(itemArr[1]);

			//校验道具是否存在
			Item item = player.getItems().get(itemId);
			if (item == null){
				failed.add(itemId);
				break;
			}

			//校验道具数量
			int itemCount = item.getUsableCount(-1);
			if (itemCount < count){
				failed.add(itemId);
				break;
			}

			//减少道具
			ResourceService.ins().addItem(player,itemId,-count,true);

			//增加金币
			ItemTemplate template = PlayerDataManager.ItemData.getTemplate(itemId);
			gold += template.getSale() * count;
		}

		if (gold != 0)
			ResourceService.ins().addGold(player,gold);

		vo.addData("reward","1,1,"+gold);
		vo.addData("failed",failed);
		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.ITEM_SALE;
	}

}
