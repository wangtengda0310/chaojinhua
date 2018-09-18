package com.igame.work.activity;

import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.MProtrol;
import com.igame.work.activity.denglu.DengluService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

public class ActivityHandler extends ReconnectedHandler {
    @Inject private ActivityService activityService;
    @Inject private DengluService dengluService;
    @Override
    public RetVO handleClientRequest(Player player, ISFSObject params) {

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        int activityId = jsonObject.getInt("activityId");
        int order = jsonObject.getInt("order");

        activityService.reward(player, activityId, order);

        RetVO retVO = new RetVO();
        ActivityOrderDto orderData = activityService.dtos.get(activityId).getOrderData().get(player.getPlayerId());
        retVO.addData("state", activityService.join(orderData.state));
        retVO.addData("activityId", activityId);
        retVO.addData("order", order);
        return retVO;
    }

    @Override
    public int protocolId() {
        return MProtrol.ACTIVITY;
    }
}