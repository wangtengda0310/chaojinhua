package com.igame.work.user.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.work.user.PlayerDataManager;
import com.igame.work.user.data.VipPackTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.gm.service.GMService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import static com.igame.work.user.VIPConstants.KEY_DAY_PACK;

/**
 * @author xym
 *
 * 会员领取每日礼包
 */
public class VipRecDayPackHandler extends BaseHandler{

    @Override
    public void handleClientRequest(User user, ISFSObject params) {

        RetVO vo = new RetVO();

        if(reviceMessage(user,params,vo)){
            return;
        }

        Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
        if(player == null){
            this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
            return;
        }

        //校验vip等级
        int vip = player.getVip();
        if (vip <= 0){
            sendError(ErrorCode.VIP_LV_LACK,MProtrol.toStringProtrol(MProtrol.VIP_DAY_PACK),vo,user);
            return;
        }

        //校验是否已领取
        int flag = (int) player.getVipPrivileges().get(KEY_DAY_PACK);
        if (flag == 1){
            sendError(ErrorCode.PACK_PURCHASED,MProtrol.toStringProtrol(MProtrol.VIP_FRIST_PACK),vo,user);
            return;
        }

        VipPackTemplate template = PlayerDataManager.vipPackData.getTemplate(vip);
        if (template == null){
            sendError(ErrorCode.PARAMS_INVALID,MProtrol.toStringProtrol(MProtrol.VIP_FRIST_PACK),vo,user);
            return;
        }

        String dayPack = template.getDayPack();

        GMService.processGM(player,dayPack);

        //标记已领取
        flag = 1;
        player.getVipPrivileges().put(KEY_DAY_PACK,flag);

        vo.addData("reward",dayPack);
        sendSucceed(MProtrol.toStringProtrol(MProtrol.VIP_DAY_PACK),vo,user);
    }
}
