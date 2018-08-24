package com.igame.work.item.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.item.dto.Item;
import com.igame.work.user.PlayerDataManager;
import com.igame.work.user.data.ItemGroupTemplate;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.Map;

/**
 * @author xym
 *
 * 道具合成
 */
public class ItemGroupHandler extends ReconnectedHandler {

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        int itemId = jsonObject.getInt("itemId");

        RetVO vo = new RetVO();

        vo.addData("itemId",itemId);

        //校验背包上限
        Map<Integer, Item> items = player.getItems();
		if (items.size() >= player.getBagSpace()){
			return error(ErrorCode.BAGSPACE_ALREADY_FULL);
		}

        //校验道具是否存在
        Item item = items.get(itemId);
        if (item == null){
            return error(ErrorCode.PARAMS_INVALID);
        }

        //检验道具是否可召唤怪兽
        ItemGroupTemplate template = PlayerDataManager.itemGroupData.getTemplate(itemId);
        if (template == null){
            return error(ErrorCode.PARAMS_INVALID);
        }

        //校验碎片是否满足数量
        if (item.getUsableCount(-1) < template.getNum()){
            return error(ErrorCode.PARAMS_INVALID);
        }

        //新增道具
        Item addItem = ResourceService.ins().addItem(player, template.getGroupId(), 1, true);
        if (addItem == null){
            return error(ErrorCode.ERROR);
        }

        //减少道具数量
        ResourceService.ins().addItem(player,itemId,-template.getNum(),true);

        vo.addData("groupItemId",addItem.getItemId());
        return vo;

    }

    @Override
    protected int protocolId() {
        return MProtrol.ITEM_GROUP;
    }

}
