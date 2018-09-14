package com.igame.work.turntable.handler;

import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.turntable.dto.Turntable;
import com.igame.work.turntable.service.TurntableService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * @author xym
 *
 * 刷新幸运大转盘
 */
public class TurntableReloadHandler extends ReconnectedHandler {
    private ResourceService resourceService;
    @Inject
    private TurntableService turntableService;

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        //校验等级
        Turntable turntable = turntableService.getTurntable(player);
        if (player.getPlayerLevel() < 15 || turntable == null){
            return error(ErrorCode.LEVEL_NOT);
        }

        //校验钻石
        if (player.getDiamond() < 20){
            return error(ErrorCode.DIAMOND_NOT_ENOUGH);
        }

        //扣除钻石
        resourceService.addDiamond(player,-20);

        //刷新大转盘
        turntableService.reloadTurntable(player, turntable);

        RetVO vo = new RetVO();

        vo.addData("rewardsStr",turntableService.transTurntableVo(player).getRewardsStr());
        return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.LUCKTABLE_RELOAD;
    }

}
