package com.igame.work.activity.tansuoZhiLu;

import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.RetVO;
import com.igame.work.activity.ActivityConfigTemplate;
import com.igame.work.activity.ActivityHandler;
import com.igame.work.activity.PlayerActivityData;
import com.igame.work.gm.service.GMService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 玩家创建账号开始算登录时间
 * 签完30天再读下一个30天的配置
 * 累积签到奖励如果没有领取，到下一个签到周期数据会被清掉
 */
public class TansuoZhiLuActivityHandler extends ActivityHandler {
    @Override
    public RetVO handleClientRequest(Player player, PlayerActivityData activityData, ISFSObject params) {

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        int id = jsonObject.getInt("id");

        if(player.getActivityData().getTansuo()==null) {
            player.getActivityData().setTansuo(new TanSuoZhiLuActivityData());
        }

        ActivityConfigTemplate config = TanSuoZhiLuActivityData.configs.get(id);
        if(config==null) {
            return error(ErrorCode.CAN_NOT_RECEIVE);
        }
        String levelLimit = config.getGet_limit();

        if (player.getPlayerLevel() < Integer.valueOf(levelLimit)) {
            return error(ErrorCode.CAN_NOT_RECEIVE);
        }

        if(player.getActivityData().getTansuo().getReveivedLeve().contains(levelLimit)) {
            return error(ErrorCode.CAN_NOT_RECEIVE);
        }
        String reward = config.getGet_value();

        GMService.processGM(player, reward);

        return new RetVO();
    }

    @Override
    protected int activityId() {
        return MProtrol.TANSUO_ZHI_LU;
    }

}
