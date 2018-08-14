package com.igame.work.item.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.ItemGroupTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.item.dto.Item;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.Map;

/**
 * @author xym
 *
 * 道具合成
 */
public class ItemGroupHandler extends BaseHandler {

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

        vo.addData("itemId",itemId);

        //校验背包上限
        Map<Integer, Item> items = player.getItems();
		if (items.size() >= player.getBagSpace()){
			sendError(ErrorCode.BAGSPACE_ALREADY_FULL, MProtrol.toStringProtrol(MProtrol.ITEM_GROUP),vo,user);
			return;
		}

        //校验道具是否存在
        Item item = items.get(itemId);
        if (item == null){
            sendError(ErrorCode.PARAMS_INVALID, MProtrol.toStringProtrol(MProtrol.ITEM_GROUP),vo,user);
            return;
        }

        //检验道具是否可召唤怪兽
        ItemGroupTemplate template = DataManager.itemGroupData.getTemplate(itemId);
        if (template == null){
            sendError(ErrorCode.PARAMS_INVALID, MProtrol.toStringProtrol(MProtrol.ITEM_GROUP),vo,user);
            return;
        }

        //校验碎片是否满足数量
        if (item.getUsableCount(-1) < template.getNum()){
            sendError(ErrorCode.PARAMS_INVALID, MProtrol.toStringProtrol(MProtrol.ITEM_GROUP),vo,user);
            return;
        }

        //新增道具
        Item addItem = ResourceService.ins().addItem(player, template.getGroupId(), 1, true);
        if (addItem == null){
            sendError(ErrorCode.ERROR, MProtrol.toStringProtrol(MProtrol.ITEM_GROUP),vo,user);
            return;
        }

        //减少道具数量
        ResourceService.ins().addItem(player,itemId,-template.getNum(),true);

        vo.addData("groupItemId",addItem.getItemId());
        sendSucceed(MProtrol.toStringProtrol(MProtrol.ITEM_GROUP),vo,user);

    }

}
