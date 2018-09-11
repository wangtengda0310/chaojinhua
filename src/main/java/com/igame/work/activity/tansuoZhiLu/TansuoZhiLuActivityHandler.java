package com.igame.work.activity.tansuoZhiLu;

import com.igame.core.handler.RetVO;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.activity.ActivityConfigTemplate;
import com.igame.work.activity.ActivityHandler;
import com.igame.work.activity.PlayerActivityData;
import com.igame.work.gm.service.GMService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.Optional;

/**
 * 玩家创建账号开始算登录时间
 * 签完30天再读下一个30天的配置
 * 累积签到奖励如果没有领取，到下一个签到周期数据会被清掉
 */
public class TansuoZhiLuActivityHandler extends ActivityHandler {
    private GMService gmService;
    @Override
    public RetVO handleClientRequest(Player player, PlayerActivityData activityData, ISFSObject params) {

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        int level = jsonObject.getInt("level");

        if(player.getActivityData().getTansuo()==null) {
            player.getActivityData().setTansuo(new TanSuoZhiLuActivityData());
        }

        Optional<ActivityConfigTemplate> config = TanSuoZhiLuActivityData.configs.stream().filter(c -> String.valueOf(level).equals(c.getGet_value())).findAny();
        if (!config.isPresent()) {
            return error(ErrorCode.CAN_NOT_RECEIVE);
        }

        String levelLimit = config.get().getGet_value();
        if (player.getPlayerLevel() < Integer.valueOf(levelLimit)) {
            return error(ErrorCode.CAN_NOT_RECEIVE);
        }

        TanSuoZhiLuActivityData tansuo = player.getActivityData().getTansuo();
        String receivedLevel = tansuo.getReceivedLevels();
        if (receivedLevel.contains("," + levelLimit + ",")) {
            return error(ErrorCode.CAN_NOT_RECEIVE);
        }
        tansuo.setReceivedLevels(receivedLevel + level + ",");
        String reward = config.get().getActivity_drop();

        gmService.processGM(player, reward);

        RetVO retVO = new RetVO();
        retVO.addData("d", tansuo.clientData(player));
        retVO.addData("level", level);
        return retVO;
    }

    @Override
    protected int activityId() {
        return 1005;
    }

    @Override
    public int protocolId() {
        return MProtrol.TANSUO_ZHI_LU;
    }
}
