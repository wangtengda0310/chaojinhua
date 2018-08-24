package com.igame.work.item.handler;


import com.google.common.collect.Lists;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.core.log.GoldLog;
import com.igame.util.GameMath;
import com.igame.work.item.ItemDataManager;
import com.igame.work.item.data.PropGroupTemplate;
import com.igame.work.item.dto.Item;
import com.igame.work.item.service.ItemService;
import com.igame.work.monster.dto.Monster;
import com.igame.work.quest.service.QuestService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public class ItemHeChengHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int itemId = jsonObject.getInt("itemId");
		int type = jsonObject.getInt("type");

		vo.addData("itemId", itemId);
		vo.addData("type", type);

		//入参校验
		if(type<1 || type >3){
			return error(ErrorCode.ERROR);
		}

		//校验道具
		Item item = player.getItems().get(itemId);
		if(item == null){
			return error(ErrorCode.ITEM_NOT_EXIT);
		}

		PropGroupTemplate pt = ItemDataManager.PropGroupData.getTemplate(itemId);
		if(pt == null){
			return error(ErrorCode.EQ_HECHENG_CANT);
		}

		//校验纹章可用数量
		if(item.getUsableCount(player.getCurTeam()) < 3){
			return error(ErrorCode.ITEM_NOT_ENOUGH);
		}

		int res;
		int newItemId = 0;
		List<Item> ll = Lists.newArrayList();
		List<Monster> mm = Lists.newArrayList();
		if(type == 1){//金币

			if(player.getGold() < pt.getGold()){
				return error(ErrorCode.GOLD_NOT_ENOUGH);
			}

			if(GameMath.hitRate(pt.getRate() * 100)){//成功
				ResourceService.ins().addGold(player, 0-pt.getGold());
				Item old = ResourceService.ins().addItem(player, itemId, -3, false);
				Item newI = ResourceService.ins().addItem(player, pt.getGroup(), 1, false);
				List<Monster> monsters = ItemService.ins().processItemGroupSucceed(player, old);
				mm.addAll(monsters);
				ll.add(old);
				ll.add(newI);
				res = 1;
				newItemId = newI.getItemId();
			}else{
				ResourceService.ins().addGold(player, 0-pt.getGold());
				if(pt.getItem() > 0){
					Item newI = ResourceService.ins().addItem(player, 300001,  pt.getGetItem(), false);//返回一定粉尘
					ll.add(newI);
				}
				res = 2;
			}
		}else if(type == 2){//钻石

			if(pt.getGem() == 0){
				return error(ErrorCode.EQ_HECHENG_TYPECANT);
			}

			if(player.getDiamond() < pt.getGem()){
				return error(ErrorCode.DIAMOND_NOT_ENOUGH);
			}

			ResourceService.ins().addDiamond(player, 0-pt.getGem());
			Item old = ResourceService.ins().addItem(player, itemId, -3, false);
			Item newI = ResourceService.ins().addItem(player, pt.getGroup(), 1, false);
			List<Monster> monsters = ItemService.ins().processItemGroupSucceed(player, old);
			mm.addAll(monsters);
			ll.add(old);
			ll.add(newI);
			res = 1;
			newItemId = newI.getItemId();
		}else {//粉尘

			if(pt.getItem() == 0){
				return error(ErrorCode.EQ_HECHENG_TYPECANT);
			}

			Item xiao = player.getItems().get(300001);
			if(xiao == null || xiao.getUsableCount(-1) < pt.getItem()){
				return error(ErrorCode.ITEM_NOT_ENOUGH);
			}

			xiao = ResourceService.ins().addItem(player, xiao.getItemId(), 0-pt.getItem(), false);
			Item old = ResourceService.ins().addItem(player, itemId, -3, false);
			Item newI = ResourceService.ins().addItem(player, pt.getGroup(), 1, false);
			List<Monster> monsters = ItemService.ins().processItemGroupSucceed(player, old);
			mm.addAll(monsters);
			ll.add(xiao);
			ll.add(old);
			ll.add(newI);
			res = 1;
			newItemId = newI.getItemId();
		}

		GoldLog.info(player.getSeverId(), player.getUserId(), player.getPlayerId(), GoldLog.HECHENG, "#itemId:" + itemId+"#type:"+type+"#res:"+res+"#newItemId:"+newItemId);

		if (mm.size() > 0)
			MessageUtil.notifyMonsterChange(player,mm);

		MessageUtil.notifyItemChange(player, ll);

		QuestService.processTask(player, 9, 1);

		vo.addData("res", res);
		vo.addData("newItemId", newItemId);

		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.ITEM_HE;
	}

}
