package com.igame.work.activity;

import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityHandler extends BaseHandler {
    @Override
    public void handleClientRequest(User user, ISFSObject params) {

        RetVO vo = new RetVO();
        if(reviceMessage(user,params,vo)){
            return;
        }

        Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
        if(player == null){
            this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
            return;
        }

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        List<JSONObject> data = new ArrayList<>();
        if (player.getActivityData() == null) {
            player.setActivityData(new PlayerActivityData(player));
        }

        player.getActivityData().all().forEach(
                activity -> data.add(activity.toClientData())
        );

        vo.addData("activitiesData", data);
        sendSucceed(MProtrol.toStringProtrol(MProtrol.ACTICITY), vo, user);
    }
}
