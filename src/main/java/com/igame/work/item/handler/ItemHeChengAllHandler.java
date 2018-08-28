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
import com.igame.work.user.PlayerDataManager;
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
public class ItemHeChengAllHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		String effects = jsonObject.getString("effect");
		int type = jsonObject.getInt("type");

		//入参校验
		if(effects == null || "".equals(effects)){
			return error(ErrorCode.ERROR);
		}


		int end = 1;//是否全部结束
		int res = 2;// 1-成功  2-失败
		int newItemId = 0;
		int currItem = 0;
		int nextItem = 0;
		List<Monster> mm = Lists.newArrayList();
		List<Item> list = PlayerDataManager.ItemData.getByEquip(player.getItems().values(), type, effects);//筛选出符合条件的装备
		if(list == null || list.isEmpty()){
			end = 1;//已经全部合成完毕
		}else{
			Item item = null;
			PropGroupTemplate pt = null;
			for(Item ii : list){
				pt = ItemDataManager.PropGroupData.getTemplate(ii.getItemId());
				if(pt != null){
					item = ii;
					break;
				}
			}

			if(item == null){//没有符合条件的合成纹章，结束
				end = 1;
				res = 2;
			}else{
				currItem = item.getItemId();

				List<Item> ll = Lists.newArrayList();

				if(player.getGold() < pt.getGold()){
					end = 3;
					res = 2;
				}else{
					if(GameMath.hitRate(pt.getRate() * 100)){//成功
						Item old = ResourceService.ins().addItem(player, item.getItemId(), -3, false);
						Item newI = ResourceService.ins().addItem(player, pt.getGroup(), 1, false);
						List<Monster> monsters = ItemService.ins().processItemGroupSucceed(player, old);
						mm.addAll(monsters);
						ll.add(old);
						ll.add(newI);
						res = 1;
						newItemId = newI.getItemId();
					}else{
						if(pt.getItem() > 0){
							Item newI = ResourceService.ins().addItem(player, 300001, pt.getGetItem(), false);//返回一定粉尘
							ll.add(newI);
						}
						res = 2;
					}

					ResourceService.ins().addGold(player, 0-pt.getGold());
					QuestService.processTask(player, 9, 1);

					//判断下步是否有可合成的
					list = PlayerDataManager.ItemData.getByEquip(player.getItems().values(), type, effects);
					if(list == null || list.isEmpty()){
						end = 1;//已经全部合成完毕
					}else{
						end = 2;//还可以继续合
						nextItem = list.get(0).getItemId();
					}

				}
				MessageUtil.notifyItemChange(player, ll);
			}
		}

		if (mm.size() > 0)
			MessageUtil.notifyMonsterChange(player,mm);
		
		GoldLog.info(player.getSeverId(), player.getUserId(), player.getPlayerId(), GoldLog.HECHENG, "#effects:" + effects+"#end:"+end+"#res:"+res+"#newItemId:"+newItemId);

		vo.addData("end", end);
		vo.addData("res", res);
		vo.addData("newItemId", newItemId);
//		vo.addData("currItem", currItem);
//		vo.addData("nextItem", nextItem);

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.ITEM_HE_ALL;
	}

}
