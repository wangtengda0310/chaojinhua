package com.igame.work.item.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * @author xym
 *
 *  增加背包上限
 *
 */
public class ItemAddToplimitHandler extends ReconnectedHandler {

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        //校验钻石
        if (player.getDiamond() < 50){
            return error(ErrorCode.DIAMOND_NOT_ENOUGH);
        }

        //增加背包上限
        player.addBagSpace(5);

        //扣除钻石
        ResourceService.ins().addDiamond(player,-50);

        RetVO vo = new RetVO();
        vo.addData("bagSpace",player.getBagSpace());
        return vo;
    }

    @Override
    protected int protocolId() {
        return MProtrol.ITEM_ADD_TOPLIMIT;
    }

}
