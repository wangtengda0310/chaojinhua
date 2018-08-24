package com.igame.work.turntable.handler;

import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * @author xym
 *
 * 获取玩家幸运大转盘
 */
public class TurntableGetHandler extends ReconnectedHandler {

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        //校验等级
        if (player.getPlayerLevel() < 15 || player.getTurntable() == null){
            return error(ErrorCode.LEVEL_NOT);
        }

        vo.addData("turntable",player.transTurntableVo());
        return vo;
    }

    @Override
    protected int protocolId() {
        return MProtrol.LUCKTABLE_GET;
    }

}
