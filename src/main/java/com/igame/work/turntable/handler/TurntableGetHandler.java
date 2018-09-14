package com.igame.work.turntable.handler;

import com.igame.core.di.Inject;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.turntable.service.TurntableService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * @author xym
 *
 * 获取玩家幸运大转盘
 */
public class TurntableGetHandler extends ReconnectedHandler {
    @Inject
    private TurntableService turntableService;

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        //校验等级
        if (player.getPlayerLevel() < 15 || turntableService.getTurntable(player) == null){
            return error(ErrorCode.LEVEL_NOT);
        }

        vo.addData("rewardsStr",turntableService.transTurntableVo(player).getRewardsStr());
        return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.LUCKTABLE_GET;
    }

}
