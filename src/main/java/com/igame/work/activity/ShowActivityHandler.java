package com.igame.work.activity;

import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.MProtrol;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShowActivityHandler extends ReconnectedHandler {
    @Override
    public int protocolId() {
        return MProtrol.ACTICITY;
    }

    @Override
    public RetVO handleClientRequest(Player player, ISFSObject params) {

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        List<JSONObject> data = new ArrayList<>();
        if (player.getActivityData() == null) {
            player.setActivityData(new PlayerActivityData());
        }

        if(player.getActivityData().getSign()!=null) {
            data.add(player.getActivityData().getSign().toClientData());
        }
        if(player.getActivityData().getMeiriLiangfa()!=null) {
            data.add(player.getActivityData().getMeiriLiangfa().toClientData());
        }
        if (player.getActivityData().getTansuo() != null) {
            data.add(player.getActivityData().getTansuo().toClientData());
        }

        RetVO vo = new RetVO();
        vo.addData("activitiesData", data);
        return vo;
    }

}
