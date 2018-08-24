package com.igame.work.activity.sign;

import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.RetVO;
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
public class SignHandler extends ActivityHandler {
    @Override
    protected int activityId() {
        return MProtrol.SIGN;
    }

    @Override
    public RetVO handleClientRequest(Player player, PlayerActivityData activityData, ISFSObject params) {

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        int index = jsonObject.getInt("index");

        if(player.getActivityData().getSign()==null) {
            player.getActivityData().setSign(new SignData());
        }

        RetVO vo = new RetVO();

        if (index == 0) {
            String date = player.getActivityData().getSign().signToday(player);
            if (date == null) {
                return error(ErrorCode.PACK_PURCHASED);
            }
            vo.addData("date", date);
        } else if (index < 5) {
            int ind = player.getActivityData().getSign().signTotal(player,index);
            if (ind != index) {
                return error(ErrorCode.PACK_PURCHASED);
            }
            vo.addData("total", ind);
        } else {
            return error(ErrorCode.PARAMS_INVALID);
        }

        return vo;
    }
}
