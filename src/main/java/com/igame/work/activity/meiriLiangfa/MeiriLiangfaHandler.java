package com.igame.work.activity.meiriLiangfa;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 玩家创建账号开始算登录时间
 * 签完30天再读下一个30天的配置
 * 累积签到奖励如果没有领取，到下一个签到周期数据会被清掉
 */
public class MeiriLiangfaHandler extends BaseHandler {
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

        if(player.getActivityData().all().noneMatch(activities -> activities.getType()==1004)) {
            player.getActivityData().add(new MeiriLiangfaData(player));
        }

        String date = player.getActivityData().all()
                .filter(activity -> activity.getType() == 1)
                .map(activity->{
                    if(activity instanceof MeiriLiangfaData){
                        return ((MeiriLiangfaData)activity).receive(index);
                    }
                    return null;
                }).findAny().orElse(null);

        if (date == null) {
            sendError(ErrorCode.PACK_PURCHASED, MProtrol.toStringProtrol(MProtrol.MEIRI_LIANGFA), vo, user);
            return;
        }
        vo.addData("date", date);

        sendSucceed(MProtrol.toStringProtrol(MProtrol.MEIRI_LIANGFA), vo, user);
    }
}
