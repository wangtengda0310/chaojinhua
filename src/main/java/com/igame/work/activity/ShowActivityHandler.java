package com.igame.work.activity;

import com.igame.core.MProtrol;
import com.igame.core.handler.RetVO;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShowActivityHandler extends ActivityHandler {
    @Override
    public RetVO handleClientRequest(Player player, PlayerActivityData activityData, ISFSObject params) {

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

    @Override
    protected int activityId() {
        return MProtrol.ACTICITY;
    }

}
