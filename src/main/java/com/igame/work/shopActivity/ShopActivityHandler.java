package com.igame.work.shopActivity;

import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.gm.service.GMService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

public class ShopActivityHandler extends ReconnectedHandler {
    private ShopActivityService shopActivityService;
    private GMService gmService;

    @Override
    public int protocolId() {
        return MProtrol.SHOP_ACTICITY_REWARD;
    }

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {
        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        int id = jsonObject.getInt("id");

        ShopActivityDataTemplate config = ShopActivityDataManager.Configs.getTemplate(id);
        if (config == null) {
            return error(ErrorCode.CAN_NOT_RECEIVE);
        }

        if(!shopActivityService.dto.containsKey(id)
        ||!shopActivityService.dto.get(id).players.containsKey(player.getPlayerId())) {
            return error(ErrorCode.CAN_NOT_RECEIVE);
        }

        ShopActivityPlayerDto dto = shopActivityService.dto.get(id).players.remove(player.getPlayerId());
        shopActivityService.save(player);
        if (dto.openMillis + config.getTime_limit() * ShopActivityService.HOUR_MILLIS < System.currentTimeMillis()) {
            return error(ErrorCode.ACTIVITY_CLOSED);
        }

        gmService.processGM(player, config.getDrop_value());

        return new RetVO();
    }
}
