package com.igame.work.recharge;

import com.igame.core.event.EventType;
import com.igame.core.event.PlayerEventObserver;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.MProtrol;
import com.igame.work.PlayerEvents;
import com.igame.work.activity.ActivityConfigTemplate;
import com.igame.work.activity.ActivityDataManager;
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

    @Override
    protected PlayerEventObserver playerObserver() {
        return new PlayerEventObserver() {
            @Override
            public EventType interestedType() {
                return PlayerEvents.RECHARGE;
            }

            @Override
            public void observe(Player player, EventType eventType, Object event) {
                // TODO 这块是终极馈赠的活动 是新建个活动类移到活动模块还是放在这里
                ActivityConfigTemplate template = ActivityDataManager.activityConfig.getTemplate(1003);
                if (template == null) {
                    return;
                }
                int rechargeLimit = Integer.parseInt(template.getGet_value());
                if ((int) event > rechargeLimit) {
                    gmService.processGM(player, template.getActivity_drop());
                }
            }
        };
    }
}
