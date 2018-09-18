package com.igame.work.user.handler;

import com.igame.core.di.Inject;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.user.PlayerService;
import com.igame.work.user.data.HeadTemplate;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * @author xym
 *
 * 更换头像
 */
public class ChangeHeadHandler extends ReconnectedHandler {

    @Inject private PlayerService playerService;

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        int headId = jsonObject.getInt("headId");

        vo.addData("headId",headId);

        //校验头像是否存在
        HeadTemplate template = playerService.headData.getTemplate(headId);
        if (template == null){
            return error(ErrorCode.PARAMS_INVALID);
        }

        //校验头像是否解锁
        if (!player.getUnlockHead().contains(headId)){
            return error(ErrorCode.HEAD_UNLOCK);
        }

        //更换头像
        player.setPlayerHeadId(headId);

        return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.HEAD_CHANGE;
    }

}