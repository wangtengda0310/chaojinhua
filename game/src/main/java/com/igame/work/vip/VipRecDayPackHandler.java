package com.igame.work.vip;

import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.DateUtil;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.gm.service.GMService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import static com.igame.work.vip.VIPConstants.KEY_DAY_PACK;

/**
 * @author xym
 *
 * 会员领取每日礼包
 */
public class VipRecDayPackHandler extends ReconnectedHandler {

    @Inject private GMService gmService;
    @Inject private VIPService vipService;

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

        //校验vip等级
        int vip = player.getVip();
        if (vipService.vipPackData.getAll().stream().noneMatch(c->c.getVipLv()==vip)){
            return error(ErrorCode.VIP_LV_LACK);
        }

        VipDto dto = vipService.getVipData(player);
        //校验是否已领取
        String today = DateUtil.formatToday();
        if (today.equals(dto.lastDailyPack)){
            return error(ErrorCode.PACK_PURCHASED);
        }

        VipPackTemplate template = vipService.vipPackData.getTemplate(vip);
        if (template == null){
            return error(ErrorCode.PARAMS_INVALID);
        }

        String dayPack = template.getDayPack();

        gmService.processGM(player,dayPack);

        //标记已领取
        dto.lastDailyPack = today;

        RetVO vo = new RetVO();

        vo.addData("reward",dayPack);
        vo.addData("d", vipService.getFirstPackStatus(dto));
        return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.VIP_DAY_PACK;
    }

}
