package com.igame.work.recharge;

import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.MProtrol;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RechargeListHandler extends ReconnectedHandler {
    @Override
    protected int protocolId() {
        return MProtrol.RECHARGE_LIST;
    }

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {
        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        RetVO vo = new RetVO();
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String, Object> item1 = new HashMap<>();
        item1.put("id", 1);
        item1.put("rmb", 1);
        item1.put("diamond", 1);
        Map<String, Object> item2 = new HashMap<>();
        item2.put("id", 2);
        item2.put("rmb", 5);
        item2.put("diamond", 5);
        Map<String, Object> item3 = new HashMap<>();
        item3.put("id", 3);
        item3.put("rmb", 10);
        item3.put("diamond", 10);
        list.add(item1);
        list.add(item2);
        list.add(item3);
        vo.addData("list",list);
        return vo;
    }
}
