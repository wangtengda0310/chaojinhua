package com.igame.work.item.handler;



import java.util.List;

import com.igame.work.item.service.ItemService;
import com.igame.work.monster.dto.Monster;
import net.sf.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.PropGroupTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.core.log.GoldLog;
import com.igame.dto.RetVO;
import com.igame.util.GameMath;
import com.igame.work.item.dto.Item;
import com.igame.work.quest.service.QuestService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

/**
 * 
 * @author Marcus.Z
 *
 */
public class ItemHeChengHandler extends BaseHandler{
	

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

		int itemId = jsonObject.getInt("itemId");
		int type = jsonObject.getInt("type");

		vo.addData("itemId", itemId);
		vo.addData("type", type);

		//入参校验
		if(type<1 || type >3){
			sendError(ErrorCode.ERROR,MProtrol.toStringProtrol(MProtrol.ITEM_HE), vo, user);
			return;
		}

		//校验道具
		Item item = player.getItems().get(itemId);
		if(item == null){
			sendError(ErrorCode.ITEM_NOT_EXIT,MProtrol.toStringProtrol(MProtrol.ITEM_HE), vo, user);
			return;
		}

		PropGroupTemplate pt = DataManager.PropGroupData.getTemplate(itemId);
		if(pt == null){
			sendError(ErrorCode.EQ_HECHENG_CANT,MProtrol.toStringProtrol(MProtrol.ITEM_HE), vo, user);
			return;
		}

		//校验纹章可用数量
		if(item.getUsableCount(player.getCurTeam()) < 3){
			sendError(ErrorCode.ITEM_NOT_ENOUGH,MProtrol.toStringProtrol(MProtrol.ITEM_HE), vo, user);
			return;
		}

		int res;
		int newItemId = 0;
		List<Item> ll = Lists.newArrayList();
		List<Monster> mm = Lists.newArrayList();
		if(type == 1){//金币

			if(player.getGold() < pt.getGold()){
				sendError(ErrorCode.GOLD_NOT_ENOUGH,MProtrol.toStringProtrol(MProtrol.ITEM_HE), vo, user);
				return;
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
				sendError(ErrorCode.EQ_HECHENG_TYPECANT,MProtrol.toStringProtrol(MProtrol.ITEM_HE), vo, user);
				return;
			}

			if(player.getDiamond() < pt.getGem()){
				sendError(ErrorCode.DIAMOND_NOT_ENOUGH,MProtrol.toStringProtrol(MProtrol.ITEM_HE), vo, user);
				return;
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
				sendError(ErrorCode.EQ_HECHENG_TYPECANT,MProtrol.toStringProtrol(MProtrol.ITEM_HE), vo, user);
				return;
			}

			Item xiao = player.getItems().get(300001);
			if(xiao == null || xiao.getUsableCount(-1) < pt.getItem()){
				sendError(ErrorCode.ITEM_NOT_ENOUGH,MProtrol.toStringProtrol(MProtrol.ITEM_HE), vo, user);
				return;
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
			MessageUtil.notiyMonsterChange(player,mm);

		MessageUtil.notiyItemChange(player, ll);

		QuestService.processTask(player, 9, 1);

		vo.addData("res", res);
		vo.addData("newItemId", newItemId);

		sendSucceed(MProtrol.toStringProtrol(MProtrol.ITEM_HE), vo, user);
	}

	
}
