package com.igame.work.user.handler;

import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.gm.service.GMService;
import com.igame.work.user.PlayerDataManager;
import com.igame.work.user.data.VipPackTemplate;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import static com.igame.work.user.VIPConstants.KEY_DAY_PACK;

/**
 * @author xym
 *
 * 会员领取每日礼包
 */
public class VipRecDayPackHandler extends ReconnectedHandler {

    private GMService gmService;

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

        //校验vip等级
        int vip = player.getVip();
        if (vip <= 0){
            return error(ErrorCode.VIP_LV_LACK);
        }

        //校验是否已领取
        int flag = (int) player.getVipPrivileges().get(KEY_DAY_PACK);
        if (flag == 1){
            return error(ErrorCode.PACK_PURCHASED);
        }

        VipPackTemplate template = PlayerDataManager.vipPackData.getTemplate(vip);
        if (template == null){
            return error(ErrorCode.PARAMS_INVALID);
        }

        String dayPack = template.getDayPack();

        gmService.processGM(player,dayPack);

        //标记已领取
        flag = 1;
        player.getVipPrivileges().put(KEY_DAY_PACK,flag);

        RetVO vo = new RetVO();

        vo.addData("checkReward",dayPack);
        return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.VIP_DAY_PACK;
    }

}
