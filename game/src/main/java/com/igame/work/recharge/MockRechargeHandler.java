package com.igame.work.recharge;

import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.MProtrol;
import com.igame.work.PlayerEvents;
import com.igame.work.gm.service.GMService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

public class MockRechargeHandler extends ReconnectedHandler {
    private GMService gmService;

    @Override
    public int protocolId() {
        return MProtrol.MOCK_RECHARGE;
    }

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {
        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        int id = jsonObject.getInt("id");

        if (id == 1) {
            gmService.processGM(player, "1,2,1");
            fireEvent(player, PlayerEvents.RECHARGE, 1);
        } else if (id == 2) {
            gmService.processGM(player, "1,2,5");
            fireEvent(player, PlayerEvents.RECHARGE, 5);
        } else if (id == 3) {
            gmService.processGM(player, "1,2,10");
            fireEvent(player, PlayerEvents.RECHARGE, 10);
        }

        return new RetVO();
    }

}
