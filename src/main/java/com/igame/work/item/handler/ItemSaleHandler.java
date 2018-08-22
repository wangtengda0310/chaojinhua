package com.igame.work.item.handler;


import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.work.user.PlayerDataManager;
import com.igame.work.user.data.ItemTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.item.dto.Item;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
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
public class ItemSaleHandler extends BaseHandler{
	

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
		sendSucceed(MProtrol.toStringProtrol(MProtrol.ITEM_SALE),vo,user);
	}

	
}
