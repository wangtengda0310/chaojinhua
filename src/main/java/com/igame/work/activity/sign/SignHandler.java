package com.igame.work.activity.sign;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 玩家创建账号开始算登录时间
 * 签完30天再读下一个30天的配置
 * 累积签到奖励如果没有领取，到下一个签到周期数据会被清掉
 */
public class SignHandler extends BaseHandler {
    @Override
    public void handleClientRequest(User user, ISFSObject params) {

        RetVO vo = new RetVO();
        if (reviceMessage(user, params, vo)) {
            return;
        }

        Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
        if (player == null) {
            this.getLogger().error(this.getClass().getSimpleName(), " get player failed Name:" + user.getName());
            return;
        }

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        int index = jsonObject.getInt("index");

        if (index == 0) {
            String date = player.getActivityData().getSignData().signToday();
            if (date == null) {
                sendError(ErrorCode.PACK_PURCHASED, MProtrol.toStringProtrol(MProtrol.SIGN), vo, user);
                return;
            }
            vo.addData("date", date);
        } else if (index < 5) {
            int ind = player.getActivityData().getSignData().signTotal(index);
            if (ind != index) {
                sendError(ErrorCode.PACK_PURCHASED, MProtrol.toStringProtrol(MProtrol.SIGN), vo, user);
                return;
            }
            vo.addData("total", ind);
        } else {
            sendError(ErrorCode.PARAMS_INVALID, MProtrol.toStringProtrol(MProtrol.SIGN), vo, user);
            return;
        }

        sendSucceed(MProtrol.toStringProtrol(MProtrol.SIGN), vo, user);
    }
}
