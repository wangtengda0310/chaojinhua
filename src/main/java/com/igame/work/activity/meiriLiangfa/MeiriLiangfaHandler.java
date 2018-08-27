package com.igame.work.activity.meiriLiangfa;

import com.igame.core.handler.RetVO;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.activity.ActivityHandler;
import com.igame.work.activity.PlayerActivityData;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 玩家创建账号开始算登录时间
 * 签完30天再读下一个30天的配置
 * 累积签到奖励如果没有领取，到下一个签到周期数据会被清掉
 */
public class MeiriLiangfaHandler extends ActivityHandler {
    @Override
    public RetVO handleClientRequest(Player player, PlayerActivityData activityData, ISFSObject params) {

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        int index = jsonObject.getInt("index");

        if(player.getActivityData().getMeiriLiangfa()==null) {
            player.getActivityData().setMeiriLiangfa(new MeiriLiangfaData());
        }

        String date = player.getActivityData().getMeiriLiangfa().receive(player, index);

        if (date == null) {
            return error(ErrorCode.PACK_PURCHASED);
        }

        RetVO vo = new RetVO();

        vo.addData("date", date);

        return vo;
    }

    @Override
    protected int activityId() {
        return 1004;
    }

    @Override
    protected int protocolId() {
        return MProtrol.MEIRI_LIANGFA;
    }
}
