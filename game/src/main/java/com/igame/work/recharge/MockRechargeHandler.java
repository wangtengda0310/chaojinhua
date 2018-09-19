package com.igame.work.recharge;

import com.igame.core.di.Inject;
import com.igame.core.event.EventType;
import com.igame.core.event.PlayerEventObserver;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.MProtrol;
import com.igame.work.PlayerEvents;
import com.igame.work.gm.service.GMService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

public class MockRechargeHandler extends ReconnectedHandler {
    @Inject private GMService gmService;
    @Inject private ResourceService resourceService;

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
            resourceService.addMoney(player, 1);
        } else if (id == 2) {
            resourceService.addMoney(player, 5);
        } else if (id == 3) {
            resourceService.addMoney(player, 10);
        }

        return new RetVO();
    }

    @Override
    protected PlayerEventObserver playerObserver() {
        return new PlayerEventObserver() {
            @Override
            public EventType interestedType() {
                return PlayerEvents.RECHARGE;
            }

            @Override
            public void observe(Player player, EventType eventType, Object event) {

                Object[] params = (Object[]) event;
                long timeMillis = (long)params[0];
                int money = (int) params[1];
                if (money == 1) {
                    gmService.processGM(player, "1,2,1");
                    fireEvent(player, PlayerEvents.RECHARGE, 1);
                } else if (money == 2) {
                    gmService.processGM(player, "1,2,5");
                    fireEvent(player, PlayerEvents.RECHARGE, 5);
                } else if (money == 3) {
                    gmService.processGM(player, "1,2,10");
                    fireEvent(player, PlayerEvents.RECHARGE, 10);
                }

            }
        };
    }
}
